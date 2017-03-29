package com.semantic.ecare_android_v2.core.listener;

public interface ServiceEcareUpdatingListener {
	public abstract void endOfPatientListBuild();//End Of building patient List (from Internet or from local DB)
	public abstract void endOfPatientListNoData(); //No data (from Internet or from local DB)
	public abstract void endOfConfigurationListBuild();//End Of building configuration List (from Internet or from local DB)
	public abstract void endOfConfigurationListNoData(); //No data (from Internet or from local DB)
	
}
