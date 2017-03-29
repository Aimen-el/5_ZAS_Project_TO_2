package com.semantic.ecare_android_v2.ui;


import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.achartengine.GraphicalView;

import com.semantic.ecare_android_v2.R;
import com.semantic.ecare_android_v2.core.ServiceAntidoteClient;
import com.semantic.ecare_android_v2.object.Alert;
import com.semantic.ecare_android_v2.object.CompoundMeasure;
import com.semantic.ecare_android_v2.object.Measure;
import com.semantic.ecare_android_v2.object.NoteModel;
import com.semantic.ecare_android_v2.object.Patient;
import com.semantic.ecare_android_v2.object.Tools;
import com.semantic.ecare_android_v2.object.UartServiceJumper;
import com.semantic.ecare_android_v2.ui.chart.SensorValuesChartWait;
import com.semantic.ecare_android_v2.ui.common.activity.GenericConnectedActivity;
import com.semantic.ecare_android_v2.ui.common.adapter.LastMeasuresListAdapter;
import com.semantic.ecare_android_v2.ui.common.adapter.SensorListAdapter;
import com.semantic.ecare_android_v2.util.Constants;
import com.semantic.ecare_android_v2.util.FunctionsUI;
import com.semantic.ecare_android_v2.util.MySQLiteHelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.view.View.OnClickListener;
import net.newel.android.Log;

@SuppressLint("NewApi")
public class WaitActivity extends GenericConnectedActivity {

	private String CLASSNAME=this.getClass().getName();
    private Handler handler;
	private ListView LastMeasures;
	private Spinner spinnerSensor;
    private GraphicalView gview;
    private LinearLayout view;
    private TextView tvNotice;
    private int sensor;
    private Timer timerUpdateLastMeasures=null;
    private Activity self = this;
    
    private ArrayAdapter<String> listAdapter;
    private Handler mHandler;
    private boolean mScanning;
    
    private BluetoothAdapter mBtAdapter = null;
    
    //----------------------
    public static final String TAG = "tools UART";
    private int mState = UART_PROFILE_DISCONNECTED;
    private UartServiceJumper mServiceJumper;
    private BluetoothDevice mDevice = null;
    
    private static final int REQUEST_SELECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    //    private static final int UART_PROFILE_READY = 10;
    private static final int UART_PROFILE_CONNECTED = 20;
    private static final int UART_PROFILE_DISCONNECTED = 21;
    
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.i(Constants.TAG, CLASSNAME+" Oncreate Class");
        
        handler = new Handler();
        LastMeasures = (ListView) findViewById(R.id.listViewLastMeasures);
        
        sensor=Measure.SENSOR_OXY;
        
       // Toast.makeText(WaitActivity.this, "On passe ici",Toast.LENGTH_SHORT).show();
        // binding the note buttons
        
	    RelativeLayout lNotice = (RelativeLayout) findViewById(R.id.lNotice); //banner header
	    TextView tvNotice = (TextView) findViewById(R.id.tvNotice); //error summary in the header banner
	    Button btnConnectMeasure = (Button) findViewById(R.id.tvNoticeActiveButton);
	    
	    lNotice.setVisibility(View.VISIBLE);
	    
	  //m-rv : test connectivity
	   
	    mBtAdapter = BluetoothAdapter.getDefaultAdapter();
	    if (mBtAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
	    
	    if (!mBtAdapter.isEnabled()) {
            Log.i(TAG, "onClick - BT not enabled yet");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
	    /*
	    else {
            Intent newIntent = new Intent(getBaseContext(), DeviceDataActivity.class);
            startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);
            //btnConnectMeasure.setVisibility(View.VISIBLE);
        }
	    
	    btnConnectMeasure.setOnClickListener(new View.OnClickListener(){
	    	
	    	@Override
	    	public void onClick(View v){
	    		
	    		//Patient p = mBoundService.getSelectedPatient();
	    		//String p_name =p.getSurname();
        		//Toast.makeText(WaitActivity.this, "Activation et recuperation des donnees "+p_name,Toast.LENGTH_SHORT).show();
        		 //Intent myIntent = new Intent(getBaseContext(), GameModeActivity.class);    
 			     //startActivity(myIntent);
        		//Intent intent = new Intent(getBaseContext(), DeviceDataActivity.class);
				//intent.putExtra("patient", p_name);
				//startActivityForResult(intent, Constants.REQ_CODE_NOTEDIALOG_SELECTED_PATIENT);
				
	    		if (!mBtAdapter.isEnabled()) {
                    //Log.i(TAG, "onClick - BT not enabled yet");
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                } else {
                	//Toast.makeText(WaitActivity.this, "Je passe ucu",Toast.LENGTH_SHORT).show();
                	Intent newIntent = new Intent(getBaseContext(), DeviceDataActivity.class);
                    startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);
                }
        	} 	
        });
	    */
	    
	    service_init();
	    
	    Intent it = new Intent("pw.msdx.ACTION_SEND");
        LocalBroadcastManager.getInstance(WaitActivity.this).sendBroadcast(it) ;
	}
    
	@Override
	protected void affichage_before_binding() {
		 setContentView(R.layout.activity_wait);
	} //"98:7B:F3:6A:FD:17"
	
    @Override
    protected void affichage(){
    	super.affichage();
    	
    	//Here : display the UI elements (service binded here)
    	//Reset measures Context !
    	mBoundService.resetMeasuresListContext();
    	
		//Affichage du graphique
		spinnerSensor = (Spinner) findViewById(R.id.spinnerSensor);
		view = (LinearLayout) findViewById(R.id.viewGraph);
		
	       //initialisation du spinner du sensor
        String[] listeTypes = new String[4];
        listeTypes[0]=getResources().getString(Constants.SENSOR_LEGEND.get(Measure.SENSOR_OXY));
        listeTypes[1]=getResources().getString(Constants.SENSOR_LEGEND.get(Measure.SENSOR_POIDS));
        listeTypes[2]=getResources().getString(Constants.SENSOR_LEGEND.get(Measure.SENSOR_TENSION));
        listeTypes[3]=getResources().getString(Constants.SENSOR_LEGEND.get(Measure.SENSOR_CARDIO));
        spinnerSensor.setAdapter(new SensorListAdapter(getApplicationContext(), listeTypes));
        
        int lastMeasureSensor=mBoundService.getLastMeasureType();
        switch(lastMeasureSensor) {
			case Measure.SENSOR_OXY :
				spinnerSensor.setSelection(0);
				break;
			case Measure.SENSOR_POIDS :
				spinnerSensor.setSelection(1);
				break;
			case Measure.SENSOR_TENSION :
				spinnerSensor.setSelection(2);
				break;
			case Measure.SENSOR_CARDIO :
				spinnerSensor.setSelection(3);
				break;
		}
        //set the sensor visible
        sensor=lastMeasureSensor;
        
        spinnerSensor.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				//int sensor=Measure.SENSOR_OXY;

				switch((int)arg3) {
					case 0 :
						sensor=Measure.SENSOR_OXY;
						break;
					case 1 :
						sensor=Measure.SENSOR_POIDS;
						break;
					case 2 :
						sensor=Measure.SENSOR_TENSION;
						break;
					case 3 :
						sensor=Measure.SENSOR_CARDIO;
						break;
				}
				
				displayGraph(sensor);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
        	
        });
        
        timerUpdateLastMeasures = new Timer();
        TimerTask tt = new TimerTask(){
			@Override
			public void run() {
				handler.post(runDisplayLastMeasures);
			}
        };
       
        timerUpdateLastMeasures.schedule(tt, 10, 60000);
        
    }
    
	private Runnable runDisplayLastMeasures = new Runnable(){
		@Override
		public void run(){
			Log.i(Constants.TAG, CLASSNAME+" Raffraichissement de la vue WaitActivity");
			if(mBoundService.getSelectedPatient()!=null){
			    //TODO : Attention affichage de certaines mesures fait une scrollBar
				//TODO : Attention sur certaines mesures pas de "il y a.."
				//Build Last Measure List + Alert From BDD
				try{
					ArrayList<CompoundMeasure> mesures = mBoundService.getLastMeasures();
					
					// binding the note buttons
					
					Button noteButton = (Button) findViewById(R.id.lastMeasureNoteButton);
					//here
				    LastMeasures.setAdapter(new LastMeasuresListAdapter(getApplicationContext(), mesures, self));
				
				    LastMeasures.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
							int sensor=Measure.SENSOR_OXY;
							switch((int)arg3) {
							case 0 :
								sensor=Measure.SENSOR_OXY;
								spinnerSensor.setSelection(0);
								break;
							case 1 :
								sensor=Measure.SENSOR_POIDS;
								spinnerSensor.setSelection(1);
								break;
							case 2 :
								sensor=Measure.SENSOR_TENSION;
								spinnerSensor.setSelection(2);
								break;
							case 3 :
								sensor=Measure.SENSOR_CARDIO;
								spinnerSensor.setSelection(3);
								break;
							}
						
							displayGraph(sensor);
						}
					});
				}catch(Exception e){
					Log.e(Constants.TAG, e);
				}
			}//ELSE : on est en train de se dÃ©connecter !
		}
	
	};
	

	private void displayGraph(int sensor){
		Log.i(Constants.TAG, CLASSNAME+" Raffraichissement du graphique");
		ArrayList<ArrayList<Measure>> mesures=updateMeasureList(SensorValuesChartWait.PERIOD_WAIT,sensor);
		if(mesures.size()>0){
			Log.i(Constants.TAG, CLASSNAME + " mesures.size()>0");
			gview = new SensorValuesChartWait().execute(this, mesures, sensor);
			view.removeAllViews();
    		view.addView(gview);
		}else{
			Log.i(Constants.TAG, CLASSNAME + " mesures.size()=0");
    		view.removeAllViews();
    		TextView tv = new TextView(this);
    		tv.setText(R.string.text_no_mesure);
    		tv.setTextSize(20);

    		view.setGravity(Gravity.CENTER);
    		view.addView(tv);
    		
    		Log.i(Constants.TAG, CLASSNAME+" ATTENTION : pas de mesure ï¿½ afficher !");
		}
    }
	
	private Measure findMeasureById(int id){
		for(CompoundMeasure cm : mBoundService.getLastMeasures()){
			Measure m = cm.findMeasureById(id);
			if(m != null){
				return m;
			}
		}
		return null;
	}
    
	private ArrayList<ArrayList<Measure>> updateMeasureList(int period, int sensor) {
		mBoundService.sessionAction();
		return mBoundService.getMeasures(period,sensor);
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_wait, menu);
        return true;
    }
    
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		MenuItem itemTest = menu.findItem(R.id.mode_test);  
		if(mBoundService.isSaveMeasure())
		{
			itemTest.setIcon(null);
		}
		else
		{
			itemTest.setIcon(R.drawable.check_mode_test);
		}
		return true;
	}
    
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.mode_test:
			if(mBoundService.isSaveMeasure())
			{
				mBoundService.setSaveMeasure(false);
				item.setIcon(R.drawable.check_mode_test);
			}
			else
			{
				mBoundService.setSaveMeasure(true);
				item.setIcon(null);
			}
			break;
			
		case R.id.menu_deco:
			disconnectPatient();
			break;

			
		case R.id.menu_graph:
			Intent ig = new Intent(getApplicationContext(), ChartActivity.class);
			ig.putExtra("sensor", sensor);
			startActivity(ig);
			finish();
			break;
			
		case R.id.menu_alert:
			Intent ia = new Intent(getApplicationContext(), AlertActivity.class);
			startActivity(ia);
			finish();
			break;

		}
		return true;
	}
    
	@Override
	protected void newMeasureReceived(ArrayList<CompoundMeasure> mesures){
		//New measure comes : display MesureActivity
		Intent i = new Intent(getApplicationContext(), MeasureActivity.class);
		i.putExtra("mesures", mesures);
		startActivity(i);
		Log.i(Constants.TAG, CLASSNAME+" Envoi de "+mesures.size()+" CM mesures");
		this.finish();
	}
	
	@Override
	public void disconnect() {
		super.disconnect();
	}
	
	@Override
	protected void onKeyBack(){
		disconnectPatient();
	}
	
    @Override
    public void onStart() {
        super.onStart();
        scanLeDevice(true);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
        //--
        scanLeDevice(false);
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(UARTStatusChangeReceiver);
        } catch (Exception ignore) {
            Log.e(TAG, ignore.toString());
        }
        unbindService(mServiceConnection);
        mServiceJumper.stopSelf();
        mServiceJumper= null;
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
        scanLeDevice(false);
    }

    @Override
	public void onPause() {
        Log.d(TAG, "onPause");
        scanLeDevice(false);
        super.onPause();
    }
    
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }
    
    @Override
    public void onResume() {
        super.onResume();
        scanLeDevice(true);
        Log.d(TAG, "onResume");
        if (!mBtAdapter.isEnabled()) {
            Log.i(TAG, "onResume - BT not enabled yet");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    
    //-------------------- me
    
    private void service_init() {
        Intent bindIntent = new Intent(this, UartServiceJumper.class);
        bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

        LocalBroadcastManager.getInstance(this).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());

    }
    
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UartServiceJumper.ACTION_GATT_CONNECTED);
        intentFilter.addAction(UartServiceJumper.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(UartServiceJumper.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(UartServiceJumper.ACTION_DATA_Notification);
        intentFilter.addAction(UartServiceJumper.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(UartServiceJumper.DEVICE_DOES_NOT_SUPPORT_UART);
        intentFilter.addAction(UartServiceJumper.ACTION_GET_OXYMETRE);
        intentFilter.addAction(UartServiceJumper.ACTION_GET_TEMPERATURE);
        intentFilter.addAction("pw.msdx.ACTION_SEND");

        return intentFilter;
    }
    
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            mServiceJumper = ((UartServiceJumper.LocalBinder) rawBinder).getService();
            Log.d(TAG, "onServiceConnected mService= " + mServiceJumper);
            mServiceJumper.initialize();

            if (!mServiceJumper.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }

        }

        public void onServiceDisconnected(ComponentName classname) {
            mServiceJumper = null;
        }
    };
    
    private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {
    	
    	private int showResult = 0;
    	
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            //final Intent mIntent = intent;

            Log.e(TAG, " BroadcastReceiver:  "+action);

            if (action.equals(UartServiceJumper.ACTION_GATT_CONNECTED)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        //String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                        Log.d(TAG, "UART_CONNECT_MSG");
                        TextView tvNotice = (TextView) findViewById(R.id.tvNotice);
                        ImageView img = (ImageView) findViewById(R.id.ivNotice);
                        img.setImageResource(R.drawable.icon_info_white);
                        tvNotice.setText("Device - ready");
                        mState = UART_PROFILE_CONNECTED;
                    }
                });
            }
           
            /*GATT �Ͽ����ӹ㲥*/
            if (action.equals(UartServiceJumper.ACTION_GATT_DISCONNECTED)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                        Log.d(TAG, "UART_DISCONNECT_MSG");
                        //btnConnectDisconnect.setText("Connect");
                        //((TextView) findViewById(R.id.deviceName)).setText("Not Connected");
                        //listAdapter.add(currentDateTimeString+"-> Disconnected to: "+ mDevice.getName());
                        mState = UART_PROFILE_DISCONNECTED;
                        //mService.close();
                        scanLeDevice(true);
                    }
                });
            }


            if (action.equals(UartServiceJumper.ACTION_GATT_SERVICES_DISCOVERED)) {
                mServiceJumper.enableTXNotification();
            }
          
          // GATT ֪ͨ�㲥
            if (action.equals(UartServiceJumper.ACTION_DATA_Notification)) {

                final byte[] txValue = intent.getByteArrayExtra(UartServiceJumper.EXTRA_DATA);
                runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            String text = Tools.hex2HexString(txValue);
                            String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                            //listAdapter.add(currentDateTimeString+"-> receive:0x"+text.toUpperCase());
                            //messageListView.smoothScrollToPosition(listAdapter.getCount() - 1);
                        } catch (Exception e) {
                            Log.e(TAG," error1 :"+ e.toString());
                        }
                    }
                });
                //receive_process(txValue);
            }
            
            /*GATT ����ֵ��ݸı�㲥*/
            if (action.equals(UartServiceJumper.ACTION_DATA_AVAILABLE)) {

                final byte[] txValue = intent.getByteArrayExtra(UartServiceJumper.EXTRA_DATA);
                runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            String text = new String(txValue, "UTF-8");
                            String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                            //listAdapter.add(currentDateTimeString+"-> RX: "+text);
                            //messageListView.smoothScrollToPosition(listAdapter.getCount() - 1);

                        } catch (Exception e) {
                            Log.e(TAG, "error2: "+e.toString());
                        }
                    }
                });
            }
             
            //�豸��֧�ֹ㲥
            if (action.equals(UartServiceJumper.DEVICE_DOES_NOT_SUPPORT_UART)){
                //showMessage("Doesn't support!");
                mServiceJumper.disconnect();
            }

            if (action.equals(UartServiceJumper.ACTION_GET_OXYMETRE)){

                double sat= intent.getIntExtra(UartServiceJumper.EXTRA_SATURATION, 0);
                double fq= intent.getIntExtra(UartServiceJumper.EXTRA_FREQUENCE, 0);
                
                String listSat = " Sat: "+sat+" - Fr: "+fq;
                String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                Patient patient = mBoundService.getSelectedPatient();
                
                ImageView img = (ImageView) findViewById(R.id.ivNotice);
                img.setImageResource(R.drawable.check_mode_test);
                TextView tvNotice = (TextView) findViewById(R.id.tvNotice);
                tvNotice.setText("Data receive");
                
                //Measure.SENSOR_CARDIO
                Measure m_cardio = new Measure(Measure.SENSOR_CARDIO, new Date(), fq);
                Measure m_oxy = new Measure(Measure.SENSOR_OXY, new Date(), sat);
                save_data(patient,m_cardio);
                save_data(patient,m_oxy);
                
                mServiceJumper.disconnect();
                scanLeDevice(false);

                handler.post(runDisplayLastMeasures);
            }

            if (action.equals(UartServiceJumper.ACTION_GET_TEMPERATURE)){
                float temp= intent.getFloatExtra(UartServiceJumper.EXTRA_TEMPERATURE, 0);
            }
        }
    };
    
    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
    
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBtAdapter.stopLeScan(mLeScanCallback);
                }
            };
            mBtAdapter.startLeScan(mLeScanCallback);
        } else {
            mBtAdapter.stopLeScan(mLeScanCallback);
        }
    }
    
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice newDeivce, final int newRssi,  final byte[] newScanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                	Log.e(TAG, "new device: "+newDeivce.getName()+"   mac: "+newDeivce.getAddress());
                    if (newDeivce.getAddress().equals(Constants.MAC_OXY_AMINE) ||newDeivce.getAddress().equals(Constants.MAC_OXY_HERVE) || newDeivce.getAddress().equals(Constants.MAC_OXY_MOHA) ||newDeivce.getAddress().equals(Constants.MAC_THERMO_MOHA)) {
                        scanLeDevice(false);
                        mDevice= newDeivce;
                        mServiceJumper.connect(newDeivce.getAddress());
                    }
                }
            });
        }
    };
   
    private void save_data(Patient p,Measure m){
    	//insertion
    	//String patientName = FunctionsUI.genderIntToString(p.getGender())+" "+p.getSurname()+" "+p.getName().toUpperCase(Locale.getDefault());
    	
    	MySQLiteHelper db = new MySQLiteHelper(this);
    	db.addMeasure(m, p);
    	
    	//CompoundMeasure cm = mBoundService.getLastCompoundMeasure(p.getUid(), Measure.SENSOR_OXY);
    	//Toast.makeText(this, "j:"+j+" New "+m.getValue()+" Last "+ cm.get(0).getValue(), Toast.LENGTH_LONG).show();
    	
    	/*
    	if(j > -1){
    		Toast.makeText(this, "Saving data of "+p.getName()+ " | val "+ m.getValue(), Toast.LENGTH_LONG).show();
    	}
    	else{
    		Toast.makeText(this, "Error during the saving "+p.getName(), Toast.LENGTH_LONG).show();
    	}
    	*/
    }
	
}
