package com.semantic.ecare_android_v2.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.semantic.ecare_android_v2.Fragments.AproposFragment ;
import com.semantic.ecare_android_v2.Fragments.DashboardFragment ;
import com.semantic.ecare_android_v2.Fragments.PatientsFragment ;
import com.semantic.ecare_android_v2.R;

public class MainActivity2 extends AppCompatActivity {

    private TextView mTextMessage;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_dashboard:
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.main_container, new DashboardFragment(), "Dashboard");
                    fragmentTransaction.commit();
                    return true;
                case R.id.nav_patients:
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.main_container, new PatientsFragment(), "Patients");
                    fragmentTransaction.commit();
                    return true;
                case R.id.rootElement:
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.main_container, new AproposFragment(), "A propos");
                    fragmentTransaction.commit();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_2);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        // initiation du fragment dashboard par defaut au demarrage de l'application
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        DashboardFragment dash = new DashboardFragment();
        fragmentTransaction.add(R.id.main_container, dash, "Dashboard");
        fragmentTransaction.commit();
        // end
    }

}