package com.semantic.ecare_android_v2.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.widget.Toast;

/**
 * Created by zakaria_afir on 22/04/2017.
 */

public class Proximity extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String key = LocationManager.KEY_PROXIMITY_ENTERING;
        Boolean entering = intent.getBooleanExtra(key, false);
        if (entering) {
            Toast.makeText(context,"you're entering the zone",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context,"you're out of the zone",Toast.LENGTH_SHORT).show();
        }
    }
}