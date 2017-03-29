package com.semantic.ecare_android_v2.core.listener;

import java.util.ArrayList;

import com.semantic.ecare_android_v2.object.Patient;



public interface BuilderPatientListListener {
	
	public abstract void endOfUpdatePatientList(ArrayList<Patient> list); //End of updating patient list (with Internet)
	public abstract void endOfGetPatientListFromLocal(ArrayList<Patient> list); //End of building patient List from local DB (without Internet)
	public abstract void errorNoPatientData(); //No Internet connection, and No data in the local DB
	public abstract void runFireGetPatientListFromInternet(); // get patient list from the internet
	public abstract void endOfUpdatePatientListFromInternet(ArrayList<Patient> list);
}
