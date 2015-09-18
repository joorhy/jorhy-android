package com.jorhy.player;

/**
 * Created by cnjliu on 15-5-21.
 */

import android.app.Activity;
import android.os.Bundle;

public class PlayerActivity extends Activity {	
	// Main components
    protected static MainTask mMainTask;
	
	// Load the .so
    static {
        System.loadLibrary("SDL2");
        System.loadLibrary("openh264");
        System.loadLibrary("XlPlayer");
    }
    
	 // C functions we call
    public static native void nativeInit();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
		
		mMainTask = new MainTask();
    }
}

class MainTask implements Runnable {
	@Override
	public void run() {
		PlayerActivity.nativeInit();
	}
}