package com.sygic.example.ipcdemo3d;

import android.util.Log;

import com.sygic.sdk.remoteapi.OnSoundListener;
import com.sygic.sdk.remoteapi.OnTtsListener;

public class SygicSoundListener implements OnSoundListener, OnTtsListener {

	private static final String LOG_TAG = SygicSoundListener.class.getSimpleName();
	
	@Override
	public void onSound(boolean arg0) {
		Log.d(LOG_TAG, "onSound " + arg0);
	}

	@Override
	public void onTts(String arg0) {
		Log.d(LOG_TAG, "onTts " + arg0);
	}

}
