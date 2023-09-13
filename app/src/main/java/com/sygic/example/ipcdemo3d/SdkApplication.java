package com.sygic.example.ipcdemo3d;


import java.io.File;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

/**
 * Used to save global state and to provide application general constants
 */
public class SdkApplication extends Application {
	private static Context mAppContext;
	private static final String PREFS = "com.sygic.preferences";
	private static final String PREFS_PATH_DRIVE = "com.sygic.preferences.DRIVE_PATH";
    public static String PATH_GPS_LOG;
    public static String PATH_ROUTE;
    public static String PATH_ITINERARY;
    public static String PATH_BTIMAP;
    public static String PATH_GF;
    public static String PATH_VOICES_2D;
    public static String PATH_VOICES_PERSON_2D;
    public static String PATH_LANGS;
    public static final int MAX = 10000;

    private static final String PACKAGE_NAME = "com.sygic";
    public static final String INTENT_EXTRA_EVENT_MSG = PACKAGE_NAME + ".intent.extra.actionMsg";
    public static final String INTENT_ACTION_NO_SDCARD = PACKAGE_NAME + ".intent.action.noSdcard";
    public static final String INTENT_ACTION_NO_DRIVE = PACKAGE_NAME + ".intent.action.noDrive";
    public static final String INTENT_ACTION_GPS_CLOSED = PACKAGE_NAME + ".intent.action.closedGps";
    public static final String INTENT_ACTION_SERVICE_DESTROYED = PACKAGE_NAME + ".intent.action.onDestroy";
    public static final String INTENT_ACTION_SERVICE_RESTARTED = PACKAGE_NAME + ".intent.action.restarted";
    public static final String INTENT_ACTION_APP_STARTED = PACKAGE_NAME + ".intent.action.app_started";
    public static final String INTENT_ACTION_APP_STARTED_LOCAL = PACKAGE_NAME + ".intent.action.app_started_local";
    public static final String INTENT_CHANGE_STATE = PACKAGE_NAME + ".intent.change.state";
    public static final String INTENT_CHANGE_MENU_STATE = PACKAGE_NAME + ".intent.change_menu.state";
    public static final String INTENT_ACTION_AM_WAKEUP = PACKAGE_NAME + ".intent.action.am_wakeup";

    public static final int NAVIGATION = 0;
    public static final int ROUTE = 1;
    public static final int GPS = 2;
    public static final int FAVOURITES = 3;
    public static final int LOCATION = 4;
    public static final int JOBS = 5;
    public static final int ROUTE_INFO = 6;

    public static final int ZOOM = 5000;

    public static boolean sRunning = false;
    private static boolean mService = false;
    
    
    private static String getDriveRoot() {
    	String pathToDrive = null;
        SharedPreferences prefs = mAppContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String s = prefs.getString(PREFS_PATH_DRIVE, null);
        File extDir = new File(s, "Drive");
        if (s == null || !extDir.exists()) {
        	pathToDrive = Util.getDriveRoot(new String[] {
                    s, null
            });
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(PREFS_PATH_DRIVE, pathToDrive);
            editor.commit();
        } else {
        	pathToDrive = s;
        }
        return pathToDrive;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mAppContext = this;
        PATH_GPS_LOG = Environment.getExternalStorageDirectory() + "/SygicTruck/Res/gpslogs";
        PATH_ROUTE = Environment.getExternalStorageDirectory() + "/SygicTruck/Res/routes";
        PATH_ITINERARY = Environment.getExternalStorageDirectory() + "/SygicTruck/Res/itinerary";
        PATH_BTIMAP = Environment.getExternalStorageDirectory() + "/SygicTruck/Res/icons/rupi";
        PATH_GF = Environment.getExternalStorageDirectory() + "/SygicTruck/Res/geofiles";
        PATH_VOICES_2D = Environment.getExternalStorageDirectory() + "/SygicTruck/Res/voices";
        PATH_VOICES_PERSON_2D = Environment.getExternalStorageDirectory() + "/LoquendoTTS/modules";
        PATH_LANGS = Environment.getExternalStorageDirectory() + "/SygicTruck/Res/skin/langs";
    }

    public static boolean isService() {
        return mService;
    }

    public static void setService(boolean isService) {
        mService = isService;
    }
}
