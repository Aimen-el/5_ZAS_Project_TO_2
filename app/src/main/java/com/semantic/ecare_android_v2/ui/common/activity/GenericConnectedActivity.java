package com.semantic.ecare_android_v2.ui.common.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.semantic.ecare_android_v2.R;
import com.semantic.ecare_android_v2.core.listener.ServiceEcareListener;
import com.semantic.ecare_android_v2.object.NoteModel;
import com.semantic.ecare_android_v2.object.Patient;
import com.semantic.ecare_android_v2.ui.ErrorActivity;
import com.semantic.ecare_android_v2.ui.MainActivity;
import com.semantic.ecare_android_v2.ui.NoteDialogActivity;
import com.semantic.ecare_android_v2.util.Constants;
import com.semantic.ecare_android_v2.util.FunctionsUI;

import net.newel.android.Log;

import java.util.Locale;

public abstract class GenericConnectedActivity extends GenericActivity implements ServiceEcareListener {
	private String CLASSNAME=this.getClass().getName();
	private String patientName="Pas de patient renseign !";
	private Context context = this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    }
    
    
    protected abstract void affichage_before_binding();
    
    
    protected void affichage(){//this method is called ONLY after service binding !
    	super.affichage();
    	
    	//Generic display for View

    	
    	if(mBoundService.getSelectedPatient()==null){
    		Log.e("TestDB","ICIIII");
    		//finish();
    	}else{		
	        Patient patient = mBoundService.getSelectedPatient();
	        patientName = FunctionsUI.genderIntToString(patient.getGender())+" "+patient.getSurname()+" "+patient.getName().toUpperCase(Locale.getDefault());
	        
	        
		    if(mBoundService.getSelectedPatient()!=null){
		    	//adding patientName in the header
		    	RelativeLayout lPatientName = (RelativeLayout) findViewById(R.id.lPatientName);
			    TextView tvPatientName = (TextView) findViewById(R.id.tvPatientName);
			    Button noteButton = (Button) findViewById(R.id.PatientNoteButton);
			    
			    Button btnOnGetMeasure = (Button) findViewById(R.id.tvNoticeActiveButton);
			    //Button btnGetMeasure = (Button) findViewById(R.id.tvNoticeGetButton);
			    TextView tvNotice = (TextView) findViewById(R.id.tvNotice);
			    
			    tvPatientName.setText(patientName);
			    lPatientName.setVisibility(View.VISIBLE);
			    
			    noteButton.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Patient p = mBoundService.getSelectedPatient();
						NoteModel model = new NoteModel(p.getNote(), p.getNoteDate());
						
						Intent intent = new Intent(context, NoteDialogActivity.class);
						intent.putExtra(Constants.NOTEMODEL_KEY, model);
						startActivityForResult(intent, Constants.REQ_CODE_NOTEDIALOG_SELECTED_PATIENT);
					}
				});  
		    }else{
		    	System.out.println("ATTENTION : affichage du panneau patient sans patient  afficher !");
		    	Log.w(Constants.TAG,CLASSNAME+" Displaying patient Activity without selecting patient, redirecting to error panel !");
		    	 
		 		Intent i = new Intent(getApplicationContext(), ErrorActivity.class);
				i.putExtra("errorTitle", "Pas de patient slectionn");
				i.putExtra("errorDetail", "Pour accder cette partie, vous devez slectionner un patient. Si le probleme persiste, contactez le support");
				i.putExtra("errorSummary", "Aucun patient n'a t slectionn !");
				startActivity(i);
		    }
    	}
    }
    

    
    protected void disconnectPatient() {
    	Log.i(Constants.TAG, CLASSNAME+" Demande manuelle de dconnexion depuis le menu");
    	 //if(patientName!=null){
    	//wheck if there is a connected service
    	while(true){
    		if(mBoundService!=null){
				//exit this panel, and disconnect from this patient !
				mBoundService.disconnectPatient();
				Intent i = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(i);
				finish();
				break;
    		}else{
    			System.out.println("ATTENTION : demande de dconnexion alors que mBoundService est nul !! Attente puis relance !");
    			try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		}
    	}
	}

    
	@Override
	public void disconnect() {
		Log.i(Constants.TAG, CLASSNAME+" Rception de dconnexion depuis le service (depuis la session)");
		//Receiving Disconnect from Service (from session!)
		Intent i = new Intent(getApplicationContext(), MainActivity.class);
		startActivity(i);
		
		this.finish();
	}
	/*
	private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
		  String action = intent.getAction();
		  if (BluetoothDevice.ACTION_FOUND.equals(action)) {
		    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		    Toast.makeText(context, "New Device = " + device.getName(), Toast.LENGTH_SHORT).show();
		  }
		}
	};
	*/
	/*public void openmaps(View view) {
		Intent intent = new Intent(this, MapsActivity.class);
		startActivity(intent);
	}*/
    
}
