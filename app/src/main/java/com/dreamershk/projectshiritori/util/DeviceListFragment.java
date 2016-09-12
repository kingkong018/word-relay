package com.dreamershk.projectshiritori.util;

/**
 * Created by Windows7 on 2/7/2016.
 */

import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dreamershk.projectshiritori.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A ListFragment that displays available peers on discovery and requests the
 * parent activity to handle user interaction events
 */
public class DeviceListFragment extends ListFragment implements PeerListListener {

    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    ProgressDialog progressDialog = null;
    View mContentView = null;
    private WifiP2pDevice device;
    boolean havingCustomName;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.setListAdapter(new WiFiPeerListAdapter(getActivity(), R.layout.row_device, peers));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.device_list, null);
        return mContentView;
    }

    public WifiP2pDevice getDevice() {
        return device;
    }

    private static String getDeviceStatus(int deviceStatus) {
        String status = "";
        switch (deviceStatus) {
            case WifiP2pDevice.AVAILABLE:
                status =  "可以連線";
                break;
            case WifiP2pDevice.INVITED:
                status =  "已發送邀請";
                break;
            case WifiP2pDevice.CONNECTED:
                status =  "已成功連線";
                break;
            case WifiP2pDevice.FAILED:
                status =  "連線失敗";
                break;
            case WifiP2pDevice.UNAVAILABLE:
                status =  "無法連線";
                break;
            default:
                status =  "不詳";
        }
        Log.d(WiFiDirectActivity.TAG, "Peer status :" + status);
        return status;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        WifiP2pDevice device = (WifiP2pDevice) getListAdapter().getItem(position);
        //((DeviceActionListener) getActivity()).showDetails(device);
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(getActivity(), "請稍侯",
                "正在連接 :" + device.deviceAddress, true, true
        );
        ((DeviceListFragment.DeviceActionListener) getActivity()).connect(config);
    }

        /**
         * Array adapter for ListFragment that maintains WifiP2pDevice list.
         */
    private class WiFiPeerListAdapter extends ArrayAdapter<WifiP2pDevice> {
        private List<WifiP2pDevice> items;
        public WiFiPeerListAdapter(Context context, int textViewResourceId, List<WifiP2pDevice> objects) {
            super(context, textViewResourceId, objects);
            items = objects;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.row_device, null);
            }
            WifiP2pDevice device = items.get(position);
            if (device != null) {
                TextView top = (TextView) v.findViewById(R.id.device_name);
                TextView bottom = (TextView) v.findViewById(R.id.device_details);
                if (top != null) {
                    top.setText(device.deviceName);
                }
                if (bottom != null) {
                    bottom.setText(getDeviceStatus(device.status));
                }
            }
            return v;
        }
    }
        /**
         * Update UI for this device.
         */
    public void updateThisDevice(WifiP2pDevice device) {
        this.device = device;
        TextView view = (TextView) mContentView.findViewById(R.id.my_name);
        if (!havingCustomName) {
            view.setText(device.deviceName);
            WiFiDirectActivity.myDeviceName = device.deviceName;
        }
        view = (TextView) mContentView.findViewById(R.id.my_status);
        view.setText(getDeviceStatus(device.status));
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        peers.clear();
        if (device.status != WifiP2pDevice.CONNECTED){
            //Only Group Owner are shown.
            for (WifiP2pDevice device : peerList.getDeviceList()){
                if (device.isGroupOwner()) peers.add(device);
            }
        }else{
            peers.addAll(peerList.getDeviceList());
        }
        ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
        if (peers.size() == 0) {
            Log.d(WiFiDirectActivity.TAG, "No devices/rooms found");
            return;
        }else{
            Log.d(WiFiDirectActivity.TAG, "Number of devices/rooms found: " + peers.size());
        }
    }

    public void clearPeers() {
        peers.clear();
        ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
    }
    /**
     *
     */
    public void onInitiateDiscovery() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(getActivity(), "搜索中", "點擊視窗以外範圍取消搜索。", true,
                true, new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        //WifiP2pManager manager = (WifiP2pManager) getActivity().getSystemService(Context.WIFI_P2P_SERVICE);
                        //manager.stopPeerDiscovery();
                    }
                });
    }

    /**
     * An interface-callback for the activity to listen to fragment interaction
     * events.
     */
    public interface DeviceActionListener {
        void showDetails(WifiP2pDevice device);
        void cancelDisconnect();
        void connect(WifiP2pConfig config);
        void disconnect();
    }
}
