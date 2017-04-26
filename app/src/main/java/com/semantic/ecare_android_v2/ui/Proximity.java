package com.semantic.ecare_android_v2.ui;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;
import android.support.v4.app.NotificationCompat;


import com.semantic.ecare_android_v2.R;

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
                    Log.d("you're in the zone ",ARRAY_LAT_LNG_PATIENT.toString());
                    Log.d("Vous êtes arrivé chez le "+mBoundService.getPatientList().get(i).getName(),ARRAY_LAT_LNG_PATIENT.toString());
                    Toast.makeText(context,"Vous êtes arrivé chez le "+mBoundService.getPatientList().get(i).getName(),Toast.LENGTH_LONG).show();
                    break;
                }
            }
            addNotification(context);
        } else {
            Log.d("you're out of the zone ",ARRAY_LAT_LNG_PATIENT.toString());
            Toast.makeText(context,"you're out of the zone",Toast.LENGTH_SHORT).show();
        }
    }

    private void addNotification(Context context)
    {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.icon_error)
                        .setContentTitle("C'est votre arrivée")
                        .setContentText("Vous êtes arrivés chez le patient ");

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

}