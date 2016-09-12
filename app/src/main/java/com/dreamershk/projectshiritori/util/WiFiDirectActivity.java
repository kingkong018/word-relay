package com.dreamershk.projectshiritori.util;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dreamershk.projectshiritori.GameWindow;
import com.dreamershk.projectshiritori.MultiPlayerGameManager;
import com.dreamershk.projectshiritori.MultiPlayerGameWindow;
import com.dreamershk.projectshiritori.R;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity that uses WiFi Direct APIs to discover and connect with available
 * devices. WiFi Direct APIs are asynchronous and rely on callback mechanism
 * using interfaces to notify the application of operation success or failure.
 * The application should also register a BroadcastReceiver for notification of
 * WiFi state related events.
 */

public class WiFiDirectActivity extends AppCompatActivity implements ChannelListener, DeviceListFragment.DeviceActionListener, WifiP2pManager.GroupInfoListener, WifiP2pManager.ConnectionInfoListener {

    public static final String TAG = "SocialMode";
    public static String myDeviceName;

    private List<WifiP2pDevice> roomPlayersList = new ArrayList<WifiP2pDevice>();
    private WifiP2pManager manager;
    private MenuItem atn_direct_enable;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;

    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver = null;

    Button button_search_peer, button_back, button_create_room, button_start_game, button_exit_room;
    LinearLayout linearLayout_before_connection, linearLayout_after_connection, room_owner, linear_layout_wi_fi_direct_bottom_bar_before_connection, linear_layout_wi_fi_direct_bottom_bar_after_connection;
    ListView list_room_players;
    RoomPlayerListAdapter roomPlayerListAdapter;

    /**
     * @param isWifiP2pEnabled the isWifiP2pEnabled to set
     */
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wi_fi_direct);
        if (((WifiManager) this.getSystemService(Context.WIFI_SERVICE)).isWifiEnabled()){
            setIsWifiP2pEnabled(true);
        }
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        // add necessary intent values to be matched.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        //
        room_owner = (LinearLayout)findViewById(R.id.room_owner);
        //
        list_room_players = (ListView)findViewById(R.id.listView_room_player_list);
        roomPlayerListAdapter = new RoomPlayerListAdapter(this, R.layout.row_device, roomPlayersList);
        list_room_players.setAdapter(roomPlayerListAdapter);
        //
        linearLayout_after_connection = (LinearLayout)findViewById(R.id.linear_layout_multiplayer_bottom_bar_after_joined_room);
        linearLayout_before_connection = (LinearLayout)findViewById(R.id.linear_layout_multiplayer_bottom_bar_before_joined_room);
        linear_layout_wi_fi_direct_bottom_bar_before_connection = (LinearLayout)findViewById(R.id.linear_layout_wi_fi_direct_bottom_bar_before_connection);
        linear_layout_wi_fi_direct_bottom_bar_after_connection = (LinearLayout)findViewById(R.id.linear_layout_wi_fi_direct_bottom_bar_after_connection);
        //
        button_back = (Button)findViewById((R.id.button_back_wi_fi_direct));
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //
        button_create_room = (Button)findViewById(R.id.button_create_room);
        button_create_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isWifiP2pEnabled){
                    if (manager != null && channel != null){
                        new AsyncTask<Void, Void, Void>() {
                            private ProgressDialog progressDialog;

                            @Override
                            protected void onPreExecute() {
                                progressDialog = new ProgressDialog(WiFiDirectActivity.this);
                                progressDialog.setMessage("建立房間中");
                                progressDialog.show();
                            }

                            @Override
                            protected Void doInBackground(Void... params) {
                                manager.createGroup(channel, new WifiP2pManager.ActionListener(){
                                    @Override
                                    public void onSuccess() {

                                    }
                                    @Override
                                    public void onFailure(int reason) {
                                        Log.d(WiFiDirectActivity.TAG, "Create room failed: " + showFailureMessage(reason));
                                    }
                                });
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                progressDialog.dismiss();
                            }
                        }.execute();
                    }else{
                        Log.e(TAG, "channel or manager is null");
                        Toast.makeText(WiFiDirectActivity.this, "請重啓多人遊戲。", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(WiFiDirectActivity.this, "請開啓Wi-fi。", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //
        button_search_peer = (Button)findViewById(R.id.button_search_peer);
        button_search_peer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isWifiP2pEnabled) {
                    if (manager != null && channel != null) {
                        new AsyncTask<Void, Void, Void>() {
                            private ProgressDialog progressDialog;

                            @Override
                            protected void onPreExecute() {
                                progressDialog = new ProgressDialog(WiFiDirectActivity.this);
                                progressDialog.setMessage("連接中");
                                progressDialog.show();
                            }

                            @Override
                            protected Void doInBackground(Void... params) {
                                // Since this is the system wireless settings activity, it's
                                // not going to send us a result. We will be notified by
                                // WiFiDeviceBroadcastReceiver instead.
                                final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager().findFragmentById(R.id.frag_list);
                                fragment.onInitiateDiscovery();
                                manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                                    @Override
                                    public void onSuccess() {
                                        Toast.makeText(WiFiDirectActivity.this, "搜索中...",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    @Override
                                    public void onFailure(int reasonCode) {
                                        Log.d(WiFiDirectActivity.TAG, "Search peer failed: " + showFailureMessage(reasonCode));
                                    }
                                });
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                progressDialog.dismiss();
                            }
                        };
                    } else {
                        Log.e(TAG, "channel or manager is null");
                        Toast.makeText(WiFiDirectActivity.this, "請重啓多人遊戲。", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(WiFiDirectActivity.this, "請開啓Wi-fi。", Toast.LENGTH_SHORT).show();
                }
            }
        });
        button_exit_room = (Button)findViewById(R.id.button_back_multiplayer_room);
        button_exit_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
                linear_layout_wi_fi_direct_bottom_bar_before_connection.setVisibility(View.VISIBLE);
                linear_layout_wi_fi_direct_bottom_bar_after_connection.setVisibility(View.GONE);
            }
        });
        button_start_game = (Button)findViewById(R.id.button_start_multiplayer_game);

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null)actionBar.hide();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    private String showFailureMessage (int reasonCode){
        String message = "";
        switch(reasonCode){
            case WifiP2pManager.BUSY:
                message = "The framework is busy now. Please try again later.";
                break;
            case WifiP2pManager.ERROR:
                message = "Encounter internal error. Please try again.";
                break;
            case WifiP2pManager.P2P_UNSUPPORTED:
                message = "Wifi direct is not supported. Your device cannot play multiplayer game.";
                break;
            default:
                message = "Unknown error. Please try again.";
        }
        return message;
    }

    public void showDetails(WifiP2pDevice device) {
        DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager()
                .findFragmentById(R.id.frag_detail);
        fragment.showDetails(device);
    }


    public void connect(WifiP2pConfig config) {
        manager.connect(channel, config, new ActionListener() {

            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
            }

            @Override
            public void onFailure(int reason) {
                Log.d(WiFiDirectActivity.TAG, "Connection failed: " + showFailureMessage(reason));
            }
        });
    }


    public void disconnect() {
        final DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager()
                .findFragmentById(R.id.frag_detail);
        fragment.resetViews();
        manager.removeGroup(channel, new ActionListener() {

            @Override
            public void onFailure(int reasonCode) {
                Log.d(TAG, "Disconnect failed: " + showFailureMessage(reasonCode));
            }

            @Override
            public void onSuccess() {
                fragment.getView().setVisibility(View.GONE);
            }

        });
    }

    @Override
    public void onChannelDisconnected() {
        // we will try once more
        if (manager != null && !retryChannel) {
            Toast.makeText(this, "Channel lost. Trying again", Toast.LENGTH_LONG).show();
            resetData();
            retryChannel = true;
            manager.initialize(this, getMainLooper(), this);
        } else {
            Toast.makeText(this,
                    "Severe! Channel is probably lost premanently. Try Disable/Re-Enable P2P.",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void cancelDisconnect() {

        /*
         * A cancel abort request by user. Disconnect i.e. removeGroup if
         * already connected. Else, request WifiP2pManager to abort the ongoing
         * request
         */
        if (manager != null) {
            final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager()
                    .findFragmentById(R.id.frag_list);
            if (fragment.getDevice() == null
                    || fragment.getDevice().status == WifiP2pDevice.CONNECTED) {
                disconnect();
            } else if (fragment.getDevice().status == WifiP2pDevice.AVAILABLE
                    || fragment.getDevice().status == WifiP2pDevice.INVITED) {

                manager.cancelConnect(channel, new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                        Toast.makeText(WiFiDirectActivity.this, "Aborting connection",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reasonCode) {
                        Toast.makeText(WiFiDirectActivity.this,
                                "Connect abort request failed. Reason Code: " + reasonCode,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

    }

    /**
     * Remove all peers and clear all fields. This is called on
     * BroadcastReceiver receiving a state change event.
     */
    public void resetData() {
        DeviceListFragment fragmentList = (DeviceListFragment) getFragmentManager()
                .findFragmentById(R.id.frag_list);
        DeviceDetailFragment fragmentDetails = (DeviceDetailFragment) getFragmentManager()
                .findFragmentById(R.id.frag_detail);
        if (fragmentList != null) {
            fragmentList.clearPeers();
        }
        if (fragmentDetails != null) {
            fragmentDetails.resetViews();
        }
    }

    @Override
    public void onGroupInfoAvailable(WifiP2pGroup group) {
        if (roomPlayersList.size() == 0){
            linearLayout_before_connection.setVisibility(View.GONE);
            linearLayout_after_connection.setVisibility(View.VISIBLE);
            //
            WifiP2pDevice device = group.getOwner();
            TextView name = (TextView) room_owner.findViewById(R.id.device_name);
            name.setText(device.deviceName);
            TextView address = (TextView) room_owner.findViewById(R.id.device_details);
            address.setText(device.deviceAddress);
            //
            button_exit_room.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    disconnect();
                    linearLayout_before_connection.setVisibility(View.VISIBLE);
                    linearLayout_after_connection.setVisibility(View.GONE);
                }
            });
        }
        roomPlayersList.clear();
        roomPlayersList.addAll(group.getClientList());
        roomPlayerListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        if (info.groupFormed){
            linear_layout_wi_fi_direct_bottom_bar_before_connection.setVisibility(View.GONE);
            linear_layout_wi_fi_direct_bottom_bar_after_connection.setVisibility(View.VISIBLE);
            if (info.isGroupOwner){
                button_start_game.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Disallow discovery.
                        manager.stopPeerDiscovery(channel, new ActionListener() {
                            @Override
                            public void onSuccess() {

                            }
                            @Override
                            public void onFailure(int reason) {
                                Log.e(TAG, "Stop Peer Discovery Failed: " + showFailureMessage(reason));
                            }
                        });
                        //start activity
                        Intent intent = new Intent();
                        intent.setClass(WiFiDirectActivity.this, MultiPlayerGameWindow.class);
                        intent.putExtra("server", true);
                        intent.putExtra("myDeviceName",WiFiDirectActivity.myDeviceName);
                        startActivity(intent);
                    }
                });
            }else{
                button_start_game.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //start activity
                        Intent intent = new Intent(WiFiDirectActivity.this, MultiPlayerGameWindow.class);
                        intent.putExtra("server",false);
                        intent.putExtra(MultiPlayerGameManager.EXTRAS_GROUP_OWNER_ADDRESS, info.groupOwnerAddress.getHostAddress());
                        intent.putExtra(MultiPlayerGameManager.EXTRAS_GROUP_OWNER_PORT, 8988);
                        intent.putExtra("myDeviceName",WiFiDirectActivity.myDeviceName);
                        startActivity(intent);
                    }
                });
            }
        }else{
            linear_layout_wi_fi_direct_bottom_bar_before_connection.setVisibility(View.VISIBLE);
            linear_layout_wi_fi_direct_bottom_bar_after_connection.setVisibility(View.GONE);
        }

    }

    private class RoomPlayerListAdapter extends ArrayAdapter<WifiP2pDevice> {
        private List<WifiP2pDevice> items;
        public RoomPlayerListAdapter(Context context, int viewResourceId, List<WifiP2pDevice> objects){
            super(context, viewResourceId, objects);
            items = objects;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                    bottom.setText(device.deviceAddress);
                }
            }
            return v;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
