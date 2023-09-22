package com.sygic.example.ipcdemo3d;


import com.sygic.sdk.remoteapi.ApiCallback;

public interface ActivityResolver {
    boolean isAppStarted(int timeOut);

    boolean isServiceConnected();

    void setTabsState(boolean enabled);

    void addItinerary(int startLon, int startLat, int stopLon, int stopLat);

    void addVisibleViapoint(int startLon, int startLat);

    void addInvisibleViapoint(int viaLon, int viaLat);

    void bringToBackg(long millis);

    ApiCallback getApiCallback();
}
