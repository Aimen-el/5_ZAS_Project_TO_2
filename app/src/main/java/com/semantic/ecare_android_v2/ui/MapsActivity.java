package com.semantic.ecare_android_v2.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.semantic.ecare_android_v2.R;

import static com.semantic.ecare_android_v2.R.id.map;
import static com.semantic.ecare_android_v2.ui.NoteDialogActivity.ARRAY_LAT_LNG_PATIENT;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latLng = new LatLng(ARRAY_LAT_LNG_PATIENT[0], ARRAY_LAT_LNG_PATIENT[1]);
        LatLng patientLocation = new LatLng(ARRAY_LAT_LNG_PATIENT[0], ARRAY_LAT_LNG_PATIENT[1]);
        mMap.addMarker(new MarkerOptions().position(patientLocation).title("Patient"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19));

    }
}
