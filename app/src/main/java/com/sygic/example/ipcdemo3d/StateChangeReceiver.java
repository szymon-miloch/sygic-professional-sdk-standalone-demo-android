package com.sygic.example.ipcdemo3d;

import com.sygic.sdk.remoteapi.Api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;

public class StateChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
    	if (intent.getAction().equals(SdkApplication.INTENT_ACTION_APP_STARTED)) {
    		Intent i = new Intent();
    		i.setAction(SdkApplication.INTENT_ACTION_APP_STARTED_LOCAL);
    		context.sendBroadcast(i);
    	} else if (intent.getAction().equals(SdkApplication.INTENT_ACTION_AM_WAKEUP)) {
    		//Toast.makeText(SdkActivity.this, "Sun rise", Toast.LENGTH_LONG).show();
			Api.getInstance().bringApplicationToBackground();
    	}
    }
}
