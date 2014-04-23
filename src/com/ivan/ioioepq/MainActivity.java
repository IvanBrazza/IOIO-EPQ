package com.ivan.ioioepq;

//import java.io.IOException;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.util.Set;
//import java.util.UUID;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;
import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothSocket;
//import android.content.BroadcastReceiver;
//import android.content.Context;
import android.content.Intent;
//import android.content.IntentFilter;
import android.os.Bundle;
//import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends IOIOActivity {
//	TODO: Get the goddamn pairing working...
//	int mIOIOPaired = 0;
//	private BroadcastReceiver mReceiver = null;
	/*
	 * onCreate is called when the activity is first created. 
	 * It creates the layout defined in res/layout/activity_main.xml.
	 * Also set here is an OnChangeListener for the SeekBar
	 * so that the value of the SeekBar is set as the value for
	 * the speed. Since the SeekBar value is from 0-100, the value
	 * is divided by 100 to get a floating point value between
	 * 0 and 1 as this is the requirement to set the duty cycle
	 * for the PWM of the motors. 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		if (!mBluetoothAdapter.isEnabled()) {
//			// If there are paired devices
//			if (pairedDevices.size() > 0) {
//				// Loop through paired devices
//				for (BluetoothDevice device : pairedDevices) {
//					if (device.getName() == "IOIO (83:A2)") {
//						mIOIOPaired = 1;
//					}
//				}
//			}
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, 1);
//		} else if (mBluetoothAdapter.isEnabled()) {
//			// If there are paired devices
//			if (pairedDevices.size() > 0) {
//				// Loop through paired devices
//				for (BluetoothDevice device : pairedDevices) {
//					if (device.getName() == "IOIO (83:A2)") {
//						mIOIOPaired = 1;
//					}
//				}
//			} 
//			if (mIOIOPaired == 0) {
//				Log.d("IOIOEPQ", "Discovery Started");
//				mBluetoothAdapter.startDiscovery();
//			}
		}
//		BroadcastReceiver mReceiver = new BroadcastReceiver() {
//		    public void onReceive(Context context, Intent intent) {
//		        String action = intent.getAction();
//		        // When discovery finds a device
//		        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//		            // Get the BluetoothDevice object from the Intent
//		            BluetoothDevice discoveredDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//		            Log.d("IOIOEPQ", "Device found! Name: " + discoveredDevice.getName() + ", Address:" + discoveredDevice.getAddress());
//		            if (discoveredDevice.getAddress() == "00:02:72:C5:83:A2") {
//		            	Log.d("IOIOEPQ", "Attemping to pair with IOIO");
//		            	/*BluetoothDevice ioiodevice = mBluetoothAdapter.getRemoteDevice("00:02:72:C5:83:A2");
//						BluetoothSocket tmp = null;
//						try {
//							tmp = ioiodevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
//							Method m = ioiodevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
//							tmp = (BluetoothSocket) m.invoke(ioiodevice, 1);
//							tmp.connect();
//						} catch (IOException e) {
//							e.printStackTrace();
//						} catch (NoSuchMethodException e) {
//							e.printStackTrace();
//						} catch (IllegalArgumentException e) {
//							e.printStackTrace();
//						} catch (IllegalAccessException e) {
//							e.printStackTrace();
//						} catch (InvocationTargetException e) {
//							e.printStackTrace();
//						}
//						try {
//							tmp.close();
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//						recreate();*/
//		            }
//		        }
//		    }
//		};
//		// Register the BroadcastReceiver
//		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//		registerReceiver(mReceiver, filter);
	}
	
	
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
	    if (resultCode == 0) {
	    	Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, 1);
	    } else {
//	    	if (mIOIOPaired == 1) {
	        	recreate();
//	    	}
	    }
	}
	
	/*
	 * This is the thread on which all the IOIO activity happens. It will be run
	 * every time the application is resumed and aborted when it is paused. The
	 * method setup() will be called right after a connection with the IOIO has
	 * been established (which might happen several times!). Then, loop() will
	 * be called repetitively until the IOIO gets disconnected.
	 */
	class Looper extends BaseIOIOLooper {
		ImageButton BTN_LEFT = (ImageButton)findViewById(R.id.btn_left); // Define an object for the left button
		ImageButton BTN_RIGHT = (ImageButton)findViewById(R.id.btn_right); // Define an object for the right button
		ImageButton BTN_UP = (ImageButton)findViewById(R.id.btn_up); // Define an object for the up button
		ImageButton BTN_DOWN = (ImageButton)findViewById(R.id.btn_down); // Define an object for the down button
		TextView STATUS = (TextView)findViewById(R.id.dynamic_status_text); // Define an object for the status text
		PwmOutput ENA; // Define an object for the enable pin for motor A as a PWM output
		PwmOutput ENB; // Define an object for the enable pin for motor B as a PWM output
		DigitalOutput IN1; // Define an object for first input pin for motor A as a digital output
		DigitalOutput IN2; // Define an object for second input pin for motor A as a digital output
		DigitalOutput IN3; // Define an object for first input pin for motor B as a digital output
		DigitalOutput IN4; // Define an object for second input pin for motor B as a digital output
		DigitalOutput STAT; // Define an object for the IOIO's on-board LED as a digital output
		
		
		// Connection established! Let's let the user know with a toast and open some pins
		protected void setup() throws ConnectionLostException {
			// Shows a "connected" toast and changes the status text to "Connected"
			MainActivity.this.runOnUiThread(new Runnable() {
				public void run() {
				    STATUS.setText("Connected");
				}
			});
			ENA = ioio_.openPwmOutput(1, 1000); // Open the ENA pin for PWM output on IOIO pin 1 with a frequency of 1kHz
			ENB = ioio_.openPwmOutput(2, 1000); // Open the ENB pin for PWM output on IOIO pin 2 with a frequency of 1kHz
			IN1 = ioio_.openDigitalOutput(31); // Open the IN1 pin for digital output on IOIO pin 31
			IN2 = ioio_.openDigitalOutput(32); // Open the IN2 pin for digital output on IOIO pin 32
			IN3 = ioio_.openDigitalOutput(33); // Open the IN3 pin for digital output on IOIO pin 33
			IN4 = ioio_.openDigitalOutput(34); // Open the IN4 pin for digital output on IOIO pin 34
			STAT = ioio_.openDigitalOutput(0, true); // Open the IOIO's on-board  LED pin for digital output
		}
		
		/* loop() is called repetitively while IOIO is connected 
		 * and throws a ConnectionLostException when IOIO is
		 * disconnected.
		 * -----------------------------------------------------------
		 * | ENA/ENB | IN1/IN3 | IN2/IN4 | Description               |
		 * |---------------------------------------------------------|
		 * |    0    |   N/A   |   N/A   | Motor is off              |
		 * |    1    |    0    |    0    | Motor is off (floating)   |
		 * |    1    |    0    |    1    | Motor is on, backwards    |
		 * |    1    |    1    |    0    | Motor is on, forwards     |
		 * |    1    |    1    |    1    | Motor is stopped (brakes) |
		 * |---------------------------------------------------------|
		 * |                   false = 0, true = 1                   |
		 * -----------------------------------------------------------
		 */
		public void loop() throws ConnectionLostException {
			/* Set an onTouchListener for the left button. When the
			 * button is pressed, the right motor will spin forwards
			 * and the left motor will spin backwards, resulting
			 * in the car turning left.
			 */
			BTN_LEFT.setOnTouchListener(new OnTouchListener() {
				@Override
				// Called when a button is touched
				public boolean onTouch(View v, MotionEvent event) {
					/* If the button was pressed down, try to activate the motors.
					 * If the motors cannot be activated, a ConnectionLostException
					 * is thrown.
					 */
					if (event.getAction() == MotionEvent.ACTION_DOWN ) {
	                    try {
							ENA.setDutyCycle(1); // Set the duty cycle of motor A as the value of the SeekBar
							ENB.setDutyCycle(1); // Set the duty cycle of motor B as the value of the SeekBar
							IN1.write(false);
							IN2.write(true);
							IN3.write(true);
							IN4.write(false);
						} catch (ConnectionLostException e) {
							/* On the UI thread, show a disconnected toast
							 * and set the status text to "Disconnected". 
							 */
							MainActivity.this.runOnUiThread(new Runnable() {
								public void run() {
									STATUS.setText("Disconnected");
								}
							});
						}
	                    return true;
	                /* If the button was released, try to stop the motors.
					 * If the motors cannot be stopped, a ConnectionLostException
					 * is thrown.
					 */
	                } else if (event.getAction() == MotionEvent.ACTION_UP) {
	                	try {
	                		ENA.setDutyCycle(0);
	                		ENB.setDutyCycle(0);
	                	} catch (ConnectionLostException e) {
	                		/* On the UI thread, show a disconnected toast
							 * and set the status text to "Disconnected". 
							 */
	                		MainActivity.this.runOnUiThread(new Runnable() {
								public void run() {
									STATUS.setText("Disconnected");
								}
							});
	                	}
	                }
					
					return false;
				}								
			});
			BTN_RIGHT.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_DOWN ) {
	                    try {
							ENA.setDutyCycle(1);
							ENB.setDutyCycle(1);
							IN1.write(true);
							IN2.write(false);
							IN3.write(false);
							IN4.write(true);
						} catch (ConnectionLostException e) {
							MainActivity.this.runOnUiThread(new Runnable() {
								public void run() {
									STATUS.setText("Disconnected");
								}
							});
						}
	                    return true;
	                } else if (event.getAction() == MotionEvent.ACTION_UP) {
	                	try {
	                		ENA.setDutyCycle(0);
	                		ENB.setDutyCycle(0);
	                	} catch (ConnectionLostException e) {
	                		MainActivity.this.runOnUiThread(new Runnable() {
								public void run() {
									STATUS.setText("Disconnected");
								}
							});
	                	}
	                }
					return false;
				}								
			});
			BTN_UP.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_DOWN ) {
	                    try {
	                    	ENA.setDutyCycle((float) 0.75);
							ENB.setDutyCycle((float) 0.75);
							IN1.write(true);
							IN2.write(false);
							IN3.write(true);
							IN4.write(false);
						} catch (ConnectionLostException e) {
							MainActivity.this.runOnUiThread(new Runnable() {
								public void run() {
									STATUS.setText("Disconnected");
								}
							});
						}
	                    return true;
	                } else if (event.getAction() == MotionEvent.ACTION_UP) {
	                	try {
	                		ENA.setDutyCycle(0);
	                		ENB.setDutyCycle(0);
	                	} catch (ConnectionLostException e) {
	                		MainActivity.this.runOnUiThread(new Runnable() {
								public void run() {
									STATUS.setText("Disconnected");
								}
							});
	                	}
	                }
					
					return false;
				}								
			});
			BTN_DOWN.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_DOWN ) {
	                    try {
	                    	ENA.setDutyCycle((float) 0.75);
							ENB.setDutyCycle((float) 0.75);
							IN1.write(false);
							IN2.write(true);
							IN3.write(false);
							IN4.write(true);
						} catch (ConnectionLostException e) {
							MainActivity.this.runOnUiThread(new Runnable() {
								public void run() {
									STATUS.setText("Disconnected");
								}
							});
						}
	                    return true;
	                } else if (event.getAction() == MotionEvent.ACTION_UP) {
	                	try {
	                		ENA.setDutyCycle(0);
	                		ENB.setDutyCycle(0);
	                	} catch (ConnectionLostException e) {
	                		MainActivity.this.runOnUiThread(new Runnable() {
								public void run() {
									STATUS.setText("Disconnected");
								}
							});
	                	}
	                }
					
					return false;
				}
			});
			/* Flash the on-board LED to let the user know that
			 * we are connected and active. A ConnectionLostException
			 * is thrown if the connection between the device and IOIO
			 * is lost. An InterruptedException is thrown if another thread
			 * interrupted the current thread while it is sleeping. This
			 * could happen if the app is closed while it is sleeping.
			 */
			try {
				Thread.sleep(1500); // Wait 1500ms/1.5s
				STAT.write(false); // Turn on the on-board LED
				Thread.sleep(150); // Wait 150ms/0.15s
				STAT.write(true); // Turn off the on-board LED
			} catch (ConnectionLostException e) {
				/* On the UI thread, show a disconnected toast
				 * and set the status text to "Disconnected". 
				 */
				MainActivity.this.runOnUiThread(new Runnable() {
					public void run() {
						STATUS.setText("Disconnected");
					}
				});
			} catch (InterruptedException e) {
				// On the UI thread, show a disconnected toast
				MainActivity.this.runOnUiThread(new Runnable() {
					public void run() {
						STATUS.setText("Disconnected");
					}
				});
			}
		}
	}
	
	/*
	 * A method that creates the IOIO thread to
	 * establish a connection using any of the 3
	 * connections (in order of priority):
	 *  1. ADB
	 *  2. Bluetooth
	 *  3. OpenAccessory
	 */
	@Override
	protected IOIOLooper createIOIOLooper() {
		return new Looper();
	}
	
//	protected void onDestroy() {
//		if (mReceiver != null) {
//			unregisterReceiver(mReceiver);
//		}
//		super.onDestroy();
//	}
}
