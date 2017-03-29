package com.semantic.ecare_android_v2.core;

import android.app.Service;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;

import com.semantic.ecare_android_v2.core.listener.BuilderPatientListListener;
import com.semantic.ecare_android_v2.object.Patient;
import com.semantic.ecare_android_v2.object.UserConstant;
import com.semantic.ecare_android_v2.util.Constants;
import com.semantic.ecare_android_v2.util.DataBaseConnector;
import com.semantic.ecare_android_v2.util.NetworkStatus;

import net.newel.android.Log;

import java.util.ArrayList;
import java.util.Collection;

public class BuilderPatientList {
	private Handler handler;
	private final Collection<BuilderPatientListListener> builderPatientListListeners = new ArrayList<BuilderPatientListListener>();
	private Service service;
	private NetworkStatus networkStatus;
	private ArrayList<Patient> patients = new ArrayList<Patient>();
	private String CLASSNAME=this.getClass().getName();
	
	private Runnable runFireGetPatientListFromInternet = new Runnable(){
		@Override
		public void run(){
			fireRunFireGetPatientListFromInternet();
		}
	};
	
	private Runnable runFireEndOfUpdate = new Runnable(){  
		@Override
		public void run(){
			fireEndOfUpdate(patients);
		}  
	}; 
	private Runnable runFireEndOfGetListFromLocal = new Runnable(){  
		@Override
		public void run(){
			fireEndOfGetListFromLocal(patients);
		}  
	};  
	private Runnable runFireErrorNodata = new Runnable(){  
		@Override
		public void run(){
			fireErrorNoData();
		}  
	};  
	

	public BuilderPatientList(Service service) {
		handler = new Handler();
		this.service=service;
		
	}

	public void get(){
		//get list from Internet or from local sqLite database, and returns it with listener once done.
		Log.i(Constants.TAG,CLASSNAME+" Build (update from internet or get from local SQLite DB) patient list");
		
		new Thread(){
			public void run(){
				Log.i(Constants.TAG,CLASSNAME+" Internet Nok, searching data in local SQLite DB");
				//Check if there is data from local DB

				getPatientFromDataBase();
			}
		}.start();
	}
	
	private void getPatientFromDataBase(){
		networkStatus = new NetworkStatus(service);
		int networkState = networkStatus.checkNetworkStatus();
		
		DataBaseConnector dbc = new DataBaseConnector(service);
		SQLiteDatabase db = dbc.openRead();
		if(db!=null){
			Cursor cursor_patient = db.query(Constants.TABLE_PATIENT, new String[] { "id", "idUser", "gender", "name", "surname", "symptome", "note", "noteDate" }, null, null, null, null, "name ASC");
			if (cursor_patient != null){
				if(cursor_patient.getCount()>=0){
					Log.i(Constants.TAG, CLASSNAME + " pas de problem, on charge depuis la base");

					patients = buildFromDBD(cursor_patient);
				}
				cursor_patient.close();
			}
			db.close();
		}
		dbc.close();
		if(patients.size()>0){
			handler.post(runFireEndOfGetListFromLocal);
		}else if ((networkState == NetworkStatus.WIFI) || (networkState == NetworkStatus.MOBILE)) {

				Log.i(Constants.TAG,CLASSNAME+" Internet OK, initialisation de la liste des patients");
				handler.post(runFireGetPatientListFromInternet);
				
			}
			else
			{

			}
	}
	
	
	private void fireRunFireGetPatientListFromInternet()
	{
		for (BuilderPatientListListener listener : builderPatientListListeners) {
			listener.runFireGetPatientListFromInternet();
		}
	}
	
	private ArrayList<Patient> buildFromDBD(Cursor cursor){
		Log.i(Constants.TAG, CLASSNAME + " build from dbd");
		ArrayList<Patient> patientList = new ArrayList<Patient>();
		if(cursor.getCount()>0){
			cursor.moveToFirst();
		    do{
		    	Patient patient = new Patient(cursor.getInt(0),cursor.getString(1), cursor.getInt(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7));
		    	patientList.add(patient);
		    } while(cursor.moveToNext());
		}
	    return patientList;
	}
	
	
	private void addUserConstantToPatient(Patient patient, Cursor cursor){
		//For this patient, adding all the user constants contained by this cursor
		if(cursor.moveToFirst()){
			do{
				UserConstant userConstant = new UserConstant(cursor.getString(0),cursor.getDouble(1),cursor.getInt(2));
				int sensor = cursor.getInt(1);
				
				if(patient.getUserConstants().get(sensor)!=null){
					//getting the user constant List for this key and add the current user constant to it
					patient.getUserConstants().get(sensor).add(userConstant);
				}else{
					//else, creating a user constant list, adding this user constant, and inserting to the MainList
					ArrayList<UserConstant> listeUserConstants = new ArrayList<UserConstant>();
					listeUserConstants.add(userConstant);
					
					patient.getUserConstants().put(sensor, listeUserConstants);
				}
			} while(cursor.moveToNext());
		}
	}	
	
	public void addListener(BuilderPatientListListener listener){
		builderPatientListListeners.add(listener);
	}
	
	private void fireEndOfUpdate(ArrayList<Patient> list) {
		for (BuilderPatientListListener listener : builderPatientListListeners) {
			listener.endOfUpdatePatientList(list);
		}
	}
	private void fireEndOfGetListFromLocal(ArrayList<Patient> list) {
		for (BuilderPatientListListener listener : builderPatientListListeners) {
			listener.endOfGetPatientListFromLocal(list);
		}
	}
	private void fireErrorNoData() {
		for (BuilderPatientListListener listener : builderPatientListListeners) {
			listener.errorNoPatientData();
		}
	}
}
