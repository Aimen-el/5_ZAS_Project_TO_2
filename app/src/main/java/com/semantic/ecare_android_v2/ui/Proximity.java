package com.semantic.ecare_android_v2.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import static com.semantic.ecare_android_v2.ui.NoteDialogActivity.ARRAY_LAT_LNG_PATIENT;
import static com.semantic.ecare_android_v2.ui.NoteDialogActivity.note;
import static com.semantic.ecare_android_v2.ui.common.activity.GenericActivity.mBoundService;

/**
 * Created by zakaria_afir on 22/04/2017.
 */

public class Proximity extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String key = LocationManager.KEY_PROXIMITY_ENTERING;
        Boolean entering = intent.getBooleanExtra(key, false);
        if (entering) {

            for(int i = 0; i<=mBoundService.getPatientList().size();i++){
                if(note.getAddress().equals(mBoundService.getPatientList().get(i).getAddress())){
                    Log.d("Vous êtes arrivé chez le "+mBoundService.getPatientList().get(i).getName(),ARRAY_LAT_LNG_PATIENT.toString());
                    Toast.makeText(context,mBoundService.getPatientList().get(i).getName(),Toast.LENGTH_LONG).show();
                    break;
                }
            }
        } else {
            Log.d("you're out of the zone ",ARRAY_LAT_LNG_PATIENT.toString());
            //Toast.makeText(context,"you're out of the zone",Toast.LENGTH_SHORT).show();
        }
    }
}