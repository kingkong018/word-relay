package com.dreamershk.projectshiritori.util;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dreamershk.projectshiritori.GameManager;
import com.dreamershk.projectshiritori.GameWindow;
import com.dreamershk.projectshiritori.MultiPlayerGameManager;
import com.dreamershk.projectshiritori.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A fragment that manages a particular peer and allows interaction with device
 * i.e. setting up network connection and transferring data.
 */
public class DeviceDetailFragment extends Fragment implements ConnectionInfoListener {

    protected static final int CHOOSE_FILE_RESULT_CODE = 20;
    private View mContentView = null;
    private WifiP2pDevice device;
    private WifiP2pInfo info;
    ProgressDialog progressDialog = null;

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        this.info = info;
        this.getView().setVisibility(View.VISIBLE);

        // The owner IP is now known.
        TextView view = (TextView) mContentView.findViewById(R.id.group_owner);
        view.setText(((info.isGroupOwner == true) ? "是"
                : "否"));
        view.setVisibility(View.VISIBLE);

        // InetAddress from WifiP2pInfo struct.
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText("房主嘅IP - " + info.groupOwnerAddress.getHostAddress());

        // After the group negotiation, we assign the group owner as the file
        // server. The file server is single threaded, single connection server
        // socket.
        if (info.groupFormed && info.isGroupOwner) {
            // important:::: server side creates a game manager and game view
//            new FileServerAsyncTask(getActivity(), mContentView.findViewById(R.id.status_text))
//                    .execute();
//            Intent intent = new Intent();
//            intent.setClass(getContext(), GameWindow.class);
//            intent.putExtra("server",true);
//            startActivity(intent);
            mContentView.findViewById(R.id.btn_start_server).setVisibility(View.VISIBLE);
        } else if (info.groupFormed) {
            // important:::: client side creates a game view and use a remoteGameActionHandler
            mContentView.findViewById(R.id.btn_start_client).setVisibility(View.VISIBLE);
//            ((TextView) mContentView.findViewById(R.id.status_text)).setText(getResources()
//                    .getString(R.string.client_text));

            //set to click button to join server
//            Intent intent = new Intent(getActivity(), GameWindow.class);
//            intent.putExtra("server",false);
//            intent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
//                    info.groupOwnerAddress.getHostAddress());
//            intent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
//            startActivity(intent);
        }

        // hide the connect button
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.device_detail, null);
        mContentView.findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                config.wps.setup = WpsInfo.PBC;
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                progressDialog = ProgressDialog.show(getActivity(), "禁返回取消",
                        "連緊去 :" + device.deviceAddress, true, true
//                        new DialogInterface.OnCancelListener() {
//
//                            @Override
//                            public void onCancel(DialogInterface dialog) {
//                                ((DeviceActionListener) getActivity()).cancelDisconnect();
//                            }
//                        }
                );
                ((DeviceListFragment.DeviceActionListener) getActivity()).connect(config);
            }
        });

        mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ((DeviceListFragment.DeviceActionListener) getActivity()).disconnect();
                    }
                });

        mContentView.findViewById(R.id.btn_start_client).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Allow user to pick an image from Gallery or other
                        // registered apps
//                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                        intent.setMessageType("image/*");
//                        startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE);
                        Intent intent = new Intent(getActivity(), GameWindow.class);
                        intent.putExtra("server",false);
                        intent.putExtra(MultiPlayerGameManager.EXTRAS_GROUP_OWNER_ADDRESS,
                                info.groupOwnerAddress.getHostAddress());
                        intent.putExtra(MultiPlayerGameManager.EXTRAS_GROUP_OWNER_PORT, 8988);
                        intent.putExtra("myDeviceName",WiFiDirectActivity.myDeviceName);
                        startActivity(intent);
                    }
                });

        mContentView.findViewById(R.id.btn_start_server).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // Allow user to pick an image from Gallery or other
                        // registered apps
//                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                        intent.setMessageType("image/*");
//                        startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE);
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), GameWindow.class);
                        intent.putExtra("server", true);
                        intent.putExtra("myDeviceName",WiFiDirectActivity.myDeviceName);
                        startActivity(intent);
                    }
                });

        return mContentView;
    }


    /**
     * Updates the UI with device data
     *
     * @param device the device to be displayed
     */
    public void showDetails(WifiP2pDevice device) {
        this.device = device;
        this.getView().setVisibility(View.VISIBLE);
//        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
//        view.setText(device.deviceAddress);
//        view = (TextView) mContentView.findViewById(R.id.device_info);
//        view.setText(device.toString());
    }

    /**
     * Clears the UI fields after a disconnect or direct mode disable operation.
     */
    public void resetViews() {
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText("nothing");
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText("nothing");
        view = (TextView) mContentView.findViewById(R.id.group_owner);
        view.setText("nothing");
        view.setVisibility(View.GONE);
//        view = (TextView) mContentView.findViewById(R.id.status_text);
//        view.setText(R.string.empty);
        mContentView.findViewById(R.id.btn_start_client).setVisibility(View.GONE);
        mContentView.findViewById(R.id.btn_start_server).setVisibility(View.GONE);
        this.getView().setVisibility(View.GONE);
    }

    public static boolean copyFile(InputStream inputStream, OutputStream out) {
        byte buf[] = new byte[1024];
        int len;
        long startTime=System.currentTimeMillis();

        try {
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            out.close();
            inputStream.close();
            long endTime=System.currentTimeMillis()-startTime;
            Log.v("","Time taken to transfer all bytes is : "+endTime);

        } catch (IOException e) {
            Log.d(WiFiDirectActivity.TAG, e.toString());
            return false;
        }
        return true;
    }
}
