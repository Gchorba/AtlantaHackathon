package com.example.digitallifesampleandroidapp;

import java.util.ArrayList;

public class DigitalLifeViewConfiguration {

	public static ArrayList<String> viewableDeviceList = new ArrayList<String>();
	
	public static ArrayList<String> editableDeviceList = new ArrayList<String>();
	
	static {

		viewableDeviceList.add("water-sensor");
		viewableDeviceList.add("smart-plug");
		viewableDeviceList.add("door-lock");
        viewableDeviceList.add("thermostat");
        viewableDeviceList.add("garage-door-controller");
        viewableDeviceList.add("camera");
        editableDeviceList.add("garage-door-controller");

		editableDeviceList.add("smart-plug");
		editableDeviceList.add("door-lock");	
	}
	
}
