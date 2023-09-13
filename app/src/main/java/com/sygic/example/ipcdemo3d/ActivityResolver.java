package com.sygic.example.ipcdemo3d;


import com.sygic.sdk.remoteapi.ApiCallback;

public interface ActivityResolver {
	public boolean isAppStarted(int timeOut);
	public boolean isServiceConnected();
	public void setTabsState(boolean enabled);
	public void addItin(int startLong, int startLat, int stopLong, int stopLat);
	public void addVisibleViapoint(int startLong, int startLat);
	public void addInvisibleViapoint(int viaLong, int viaLat);
	public void bringToBackg(long millis);
	public ApiCallback getApiCallback();
}
