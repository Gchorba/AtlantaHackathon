package com.example.digitallifesampleandroidapp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONObject;
import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class DeviceListActivity extends Activity {
    private static final String TAG = "ExampleSportsActivity";
    static long axis;
    private PebbleKit.PebbleDataReceiver dataHandler = null;
    private final static UUID PEBBLE_APP_UUID = UUID.fromString("05f44315-6d87-46f6-9a50-a65a0a399c9c");

	private List<DigitalLifeDevice> devices; 
	private DigitalLifeController dlc; 
	private SimpleAdapter deviceAdapter; 
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_device_list);

        dlc = DigitalLifeController.getInstance(); 
        dlc.init("VE_6485B0A98964AECA_1", "https://systest.digitallife.att.com");
        try {
            dlc.login( "553474466", "NO-PASSWD");
        } catch (Exception e) {
        	System.out.println("Logout Failed");
            e.printStackTrace();
            return;
        }

        devices = dlc.fetchDevices();
        populateListView( devices);
        
        new Thread(new eServiceClient( this)).start();
        PebbleKit.registerPebbleConnectedReceiver(getApplicationContext(), new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(getLocalClassName(), "Pebble connected!");
            }
        });

        PebbleKit.registerPebbleDisconnectedReceiver(getApplicationContext(), new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(getLocalClassName(), "Pebble disconnected!");
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Always deregister any Activity-scoped BroadcastReceivers when the Activity is paused
        if (dataHandler != null) {
            unregisterReceiver(dataHandler);
            dataHandler = null;
        }
    }
    public void populateListView( List<DigitalLifeDevice> deviceList) {
        String[] keys = new String[] { "icon", "secondRow", "firstRow"};
        int[] uiComponents = { R.id.icon, R.id.firstLine, R.id.secondLine };
    	
        List<HashMap<String, Object>> deviceData = new ArrayList<HashMap<String, Object>>(deviceList.size());

        // build the data for the table view. 
        for (DigitalLifeDevice device : deviceList) {
            HashMap<String, Object> d =  new HashMap<String, Object>();
            d.put("icon", device.getResourceID());
            d.put("firstRow", device.getName());
            d.put("secondRow", device.getStatus());
            deviceData.add(d);
		}
        
        deviceAdapter = new SimpleAdapter(this, deviceData, R.layout.row_layout, keys, uiComponents);
        final ListView deviceView = (ListView)findViewById(R.id.device_list);
        deviceView.setAdapter(deviceAdapter);
        
        deviceView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(devices!=null) {
					DigitalLifeDevice device = devices.get(position);
					
					ArrayList<String> editableDeviceList = DigitalLifeViewConfiguration.editableDeviceList;

					if(!editableDeviceList.contains(device.getDeviceType())) {
						// this is not something that can be controlled.
						return; 
					}

					// retrieve the potential values this device can be set to.  ON/OFF, LOCKED/UNLOCKED, etc...
					String[] potentialValues = device.getValues();
					String pendingValue = null; 
					if(potentialValues.length>1) {
						if(potentialValues[0].equalsIgnoreCase(device.getStatus())) {
							pendingValue = potentialValues[1];
						} else {
							pendingValue = potentialValues[0];
						}
					}
					
					Toast.makeText(getApplicationContext(), device.getName() + " getting set to:  " + pendingValue, Toast.LENGTH_SHORT).show();
		            try {
						dlc.updateDevice(device.getDeviceID(), device.getAction(), pendingValue);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

    }
    @Override
    protected void onResume() {
        super.onResume();
        final Handler handler = new Handler();
        final int REQUEST_IMAGE_CAPTURE = 1;
        final int CAMERA_REQUEST = 1888;
        final int TAKE_PHOTO_CODE = 0;
        dataHandler = new PebbleKit.PebbleDataReceiver(PEBBLE_APP_UUID) {
            @Override
            public void receiveData(final Context context, final int transactionId, final PebbleDictionary data) {

                PebbleKit.sendAckToPebble(context, transactionId);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //updateUi();

                        axis = data.getUnsignedIntegerAsLong(1);
                        Log.w("axis", ""+axis);
                        //0 = x
                        //1 = y
                        //2 = z

                        if(axis == 0) {
	                    		/*IntentService intent = new IntentService("android.media.action.IMAGE_CAPTURE");
	                    		startService(intent);*/

//	                    		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//	                    	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//	                    	        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//	                    	    }

                            dlc.updateDevice("DL00000003", "lock", "unlock");

/*	                            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
	                            preview.addView(mPreview);
	                            mCamera.takePicture(null, null, mPicture);*/

                        } else if(axis == 1) {
/*	                    		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	                    		fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
	                    	    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
	                    	    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);*/
                            //Intent i = new Intent(Intent.ACTION_VIEW);
                            // Android 2.2+
                           // i.setData(Uri.parse("content://com.android.calendar/time"));
                           // startActivity(i);
                            dlc.updateDevice("DL00000003", "lock", "unlock");
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    dlc.updateDevice("PE00000002", "switch", "on");
                                }
                            }, 5000);
                           dlc.updateDevice("DL00000003", "lock", "unlock");



                           // dlc.updateAlarm();
                        } else if(axis == 2){
                            dlc.updateDevice("DL00000003", "lock", "unlock");
                     dlc.updateAlarm();

                        }
                    }
                });
            }
        };
        PebbleKit.registerReceivedDataHandler(this, dataHandler);
    }
    public void updateDeviceWithEvent(final JSONObject event) {
    	String devID = (String) event.get("dev");
    	String attributeId = (String) event.get("label");
		String value = (String) event.get("value");

    	if(attributeId!=null) {
	    	for (DigitalLifeDevice device : devices) {
				if(device.getDeviceID().equalsIgnoreCase(devID)  && (value != null)) { 
					if(value == null || value.equals("")){
						System.out.println("empty value for device:  " + event);
						//this is only here to
					}
					device.setStatus( value);
					
					DigitalLifeController.decorateDevice( device);
					
			    	updateDisplay();
			    	return; 
				}
			}
    	}
    }

	private void updateDisplay() {
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
//				deviceAdapter.notifyDataSetChanged();		
				populateListView(devices);
			}
		};
    	runOnUiThread(r);
	}
}