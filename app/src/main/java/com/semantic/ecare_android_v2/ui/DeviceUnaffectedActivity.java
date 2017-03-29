package com.semantic.ecare_android_v2.ui;



import com.semantic.ecare_android_v2.R;

import com.semantic.ecare_android_v2.core.ServiceEcare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class DeviceUnaffectedActivity extends Activity {
	private ServiceEcare serviceEcare;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_unaffected);
				
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		Log.e("Testing","unaffected called");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.rafraichir_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()){
		case R.id.menu_rafraichir:
				Intent ig = new Intent(getApplicationContext(), SplashScreen.class);
				startActivity(ig);
				finish();
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	
}
