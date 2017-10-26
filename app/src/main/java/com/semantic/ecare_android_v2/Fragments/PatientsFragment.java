package com.semantic.ecare_android_v2.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import com.semantic.ecare_android_v2.R;
import com.semantic.ecare_android_v2.adapters.PatientsListAdapter;
import com.semantic.ecare_android_v2.models.PatientModel;

/**
 * A simple {@link Fragment} subclass.
 */

                        public class PatientsFragment extends Fragment {

    protected RecyclerView patientsRecyclerView;
    protected PatientsListAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected ArrayList<PatientModel> mDataset;
    protected View view;

    public PatientsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_patients, container, false);

        patientsRecyclerView = view.findViewById(R.id.patients_recyclerview);
        initDataSet();
mAdapter= new PatientsListAdapter(mDataset);
        mLayoutManager= new LinearLayoutManager(getContext());
        patientsRecyclerView.setLayoutManager(mLayoutManager);
        patientsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        patientsRecyclerView.setAdapter(mAdapter);

        return view;
    }
    protected void initDataSet(){
        mDataset = new ArrayList<PatientModel>();
        mDataset.add(new PatientModel("Azir","Shurima"));
        mDataset.add(new PatientModel("Amumu","Shurima"));
        mDataset.add(new PatientModel("Xerath","Shurima"));
        mDataset.add(new PatientModel("taliah","Shurima"));
        mDataset.add(new PatientModel("Gankplank","Bilgewater"));
        mDataset.add(new PatientModel("Graves","Bilgewater"));
        mDataset.add(new PatientModel("MissFortune","Bilgewater"));
        mDataset.add(new PatientModel("Ahri","Ionia"));
        mDataset.add(new PatientModel("Kayn","Ionia"));
        mDataset.add(new PatientModel("Ass","Freljord"));
        mDataset.add(new PatientModel("Trynda die Please","Freljord"));
        mDataset.add(new PatientModel("Braum","Freljord"));
        mDataset.add(new PatientModel("Chop Chop Olaf","Freljord"));
        mDataset.add(new PatientModel("it's the Troll","Freljord"));
        mDataset.add(new PatientModel("Dyrus","Noxus"));
        mDataset.add(new PatientModel("Katanira","Noxus"));
        mDataset.add(new PatientModel("Kled","Noxus"));
        mDataset.add(new PatientModel("Cassiopeia","Noxus"));
        mDataset.add(new PatientModel("LeBlanc","Noxus"));

        }

}