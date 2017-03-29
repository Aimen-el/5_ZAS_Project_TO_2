package com.semantic.ecare_android_v2.core.listener;

import com.semantic.ecare_android_v2.object.ConfigurationList;



public interface BuilderConfigurationListListener {
	
	public abstract void endOfUpdateConfigurationList(ConfigurationList list); //End of updating configuration list (with Internet)
	public abstract void endOfGetConfigurationListFromLocal(ConfigurationList list); //End of building configuration List from local DB (without Internet)
	public abstract void errorNoConfigurationData(); //No Internet connection, and No data in the local DB
	public abstract void runFireGetConfigListFromInternet(); // get configuration list from from the internet
	public abstract void runErrorNoInternet(); // Pas de connection internet
	
}
