package com.semantic.ecare_android_v2.core.listener;

import java.util.ArrayList;

import com.semantic.ecare_android_v2.object.CompoundMeasure;
import com.semantic.ecare_android_v2.object.SensorState;



public interface ServiceEcareListener {
	public void newSensorMeasure(ArrayList<CompoundMeasure> measure);
	public void newSensorState(SensorState state);
	public void disconnect();
}
