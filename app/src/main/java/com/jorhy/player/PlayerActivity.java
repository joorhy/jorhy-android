package com.jorhy.player;

/**
 * Created by cnjliu on 15-5-21.
 */

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class PlayerActivity extends Activity {	
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
		
		new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("XL_log", "PlayerActivity.nativeInit begin");
                PlayerActivity.nativeInit();
                Log.i("XL_log", "PlayerActivity.nativeInit end");
            }
        }).start();
    }
}