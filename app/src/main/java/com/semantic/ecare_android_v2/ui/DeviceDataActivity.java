package com.semantic.ecare_android_v2.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.semantic.ecare_android_v2.R;
import com.semantic.ecare_android_v2.object.ScannedDevice;
import com.semantic.ecare_android_v2.ui.common.adapter.DeviceAdapter;

import java.util.ArrayList;

/*
import com.ecare.newel.testoxyble.BleUtil.BleUtil;
import com.ecare.newel.testoxyble.BleUtil.DeviceAdapter;
import com.ecare.newel.testoxyble.BleUtil.ScannedDevice;
import com.ecare.newel.testoxyble.util.Constants;
*/

@SuppressLint("NewApi")
public class DeviceDataActivity extends Activity {
   

	private SwipeRefreshLayout swipeLayout;
	private ProgressBar progressBar;
    public static final String TAG = "DeviceListActivity";
    

    private BluetoothAdapter mBluetoothAdapter;
    private DeviceAdapter mDeviceAdapter;
    
    private static final long SCAN_PERIOD = 40000;
    private Handler mHandler;
    private boolean mScanning;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        
        Log.d(TAG, "onCreate");

        setContentView(R.layout.popup_device_data);
        android.view.WindowManager.LayoutParams layoutParams = this.getWindow().getAttributes();
        layoutParams.gravity= Gravity.TOP;
        layoutParams.y = 200;
        
        
        mHandler = new Handler();
        
       
        // BLE check
        if (!isBLESupported(this)) {
            Toast.makeText(this, "Bluetooth Low Energy not supported", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // BT check
        BluetoothManager manager = getManager(this);
        if (manager != null) {
        	mBluetoothAdapter = manager.getAdapter();
        }
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth unavailable", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }   
        populateList();
        
        swipeLayout = (SwipeRefreshLayout) this.findViewById(R.id.swipe_refresh);
		swipeLayout.setOnRefreshListener(new mOnRefreshListener());
        swipeLayout.setColorScheme(android.R.color.holo_red_light, android.R.color.holo_green_light,
     				android.R.color.holo_blue_bright, android.R.color.holo_orange_light);

     	progressBar =(ProgressBar)findViewById(R.id.progressBar);
     	progressBar.setVisibility(View.GONE);
     	progressBar.setVisibility(View.VISIBLE);
     	
     	
        Button cancelButton = (Button) findViewById(R.id.btn_cancel);
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            		finish();
            }
        });

    }
	public class mOnRefreshListener implements OnRefreshListener {
	public void onRefresh() {
		new Handler().postDelayed(new Runnable() {
			public void run() {
				swipeLayout.setRefreshing(false);
				 mDeviceAdapter.clear();
	            	if (mScanning==false) {
	            		progressBar.setVisibility(View.VISIBLE);
	            		scanLeDevice(true); 
	            	}
			}
		}, 500);
		
	}
	}
    private void populateList() {
        /* Initialize device list container */
        Log.d(TAG, "populateList");
        
        ListView deviceListView = (ListView) findViewById(R.id.new_devices);
        mDeviceAdapter = new DeviceAdapter(this, R.layout.listitem_device,new ArrayList<ScannedDevice>());
        deviceListView.setAdapter(mDeviceAdapter);
        deviceListView.setOnItemClickListener(mDeviceClickListener);

        scanLeDevice(true);

    }
    
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
    	
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ScannedDevice item = mDeviceAdapter.getItem(position);
            BluetoothDevice device=item.getDevice();
            if(device!=null)
            {
	            mBluetoothAdapter.stopLeScan(mLeScanCallback);
	            Bundle b = new Bundle();
	            b.putString(BluetoothDevice.EXTRA_DEVICE, device.getAddress());
	
	            Intent result = new Intent();
	            result.putExtras(b);
	            setResult(Activity.RESULT_OK, result);
	            finish();
            }
        }
    };
    
    
    private void scanLeDevice(final boolean enable) {
        final Button cancelButton = (Button) findViewById(R.id.btn_cancel);
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
					mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    progressBar.setVisibility(View.GONE);
                }
            }, SCAN_PERIOD);
            
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);  
        } else {
            mScanning = false;
            
            mBluetoothAdapter.stopLeScan(mLeScanCallback);   
        }

    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice newDeivce, final int newRssi,  final byte[] newScanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            mDeviceAdapter.update(newDeivce, newRssi, newScanRecord);



                          /*  if (newDeivce.getAddress().equals(Constants.MAC_OXY) ||newDeivce.getAddress().equals(Constants.MAC_THERMO)) {
                                Bundle b = new Bundle();
                                b.putString(BluetoothDevice.EXTRA_DEVICE, newDeivce.getAddress());

                                Intent result = new Intent();
                                result.putExtras(b);
                                setResult(Activity.RESULT_OK, result);
                                finish();
                            }*/


                        }
                    });
                }
            });
        }
    };
    
    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
    }

    @Override
    public void onStop() {
        super.onStop();
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothAdapter.stopLeScan(mLeScanCallback);  
    }

    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
    }
    
    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    
    //-------
    /** check if BLE Supported device */
    public static boolean isBLESupported(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    /** get BluetoothManager */
    public static BluetoothManager getManager(Context context) {
        return (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
    }
    
    
}
