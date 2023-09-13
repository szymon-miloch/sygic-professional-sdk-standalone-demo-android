package com.sygic.example.ipcdemo3d;


import com.sygic.sdk.remoteapi.ApiCallback;

public interface ActivityResolver {
	public boolean isAppStarted(int timeOut);
	public boolean isServiceConnected();
	public void setTabsState(boolean enabled);
	public void addItin(int startLon, int startLat, int stopLon, int stopLat);
	public void addVisibleViapoint(int startLon, int startLat);
	public void addInvisibleViapoint(int viaLon, int viaLat);
	public void bringToBackg(long millis);
	public ApiCallback getApiCallback();
}
