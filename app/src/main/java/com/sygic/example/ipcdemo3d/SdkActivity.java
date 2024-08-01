package com.sygic.example.ipcdemo3d;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TabHost;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.sygic.example.ipcdemo3d.fragments.ItinFragment;
import com.sygic.example.ipcdemo3d.fragments.LocationFragment;
import com.sygic.example.ipcdemo3d.fragments.NaviFragment;
import com.sygic.example.ipcdemo3d.fragments.PoisFragment;
import com.sygic.example.ipcdemo3d.fragments.RouteFragment;
import com.sygic.example.ipcdemo3d.fragments.RouteInfoFragment;
import com.sygic.example.ipcdemo3d.fragments.SearchFragment;
import com.sygic.example.ipcdemo3d.fragments.SoundFragment;
import com.sygic.sdk.remoteapi.Api;
import com.sygic.sdk.remoteapi.ApiCallback;
import com.sygic.sdk.remoteapi.events.ApiEvents;
import com.sygic.sdk.remoteapi.exception.GeneralException;

import java.util.HashMap;

/**
 * Main activity for the application, decides which layout to use depending on the orientation,
 * we implement a callback form the menu fragment to receive on items selections.
 * On activity launch we set up the views, connect with the service through IPC, and initApi on connection.
 * The initApi() will launch Navigation on background if it is not already running
 */
public class SdkActivity extends FragmentActivity implements ActivityResolver {

    private static final int ITIN_MENU_INDEX = 4;
    public static int tabId = 0;

    private ActivityReceiver mReceiver;
    private Handler mHandler;
    private final HashMap<Integer, String> mEvents = new HashMap<>();
    private boolean mReconnect = false;
    private String[] sMenu;
    private TabHost mTabHost;
    private ApiCallback mApiCallback;
    private SygicSoundListener mSoundListener;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sdk_tabs);

        //set up the screen
        setupScreen();

        //handler to post ui on the main thread
        mHandler = new Handler();

        mSoundListener = new SygicSoundListener();

        //create a hash map with string events associated with their ids
        String[] descs = getResources().getStringArray(R.array.event_descs);
        int[] ids = getResources().getIntArray(R.array.event_ids);
        for (int i = 0; i < ids.length; i++) {
            mEvents.put(ids[i], descs[i]);
        }

        // we create a intent filter we can use with our receiver
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(SdkApplication.INTENT_ACTION_GPS_CLOSED);
        mFilter.addAction(SdkApplication.INTENT_ACTION_SERVICE_DESTROYED);
        mFilter.addAction(SdkApplication.INTENT_ACTION_NO_DRIVE);
        mFilter.addAction(SdkApplication.INTENT_ACTION_NO_SDCARD);
        mFilter.addAction(SdkApplication.INTENT_ACTION_APP_STARTED);
        mFilter.addAction(SdkApplication.INTENT_ACTION_AM_WAKEUP);
        mReceiver = new ActivityReceiver();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            registerReceiver(mReceiver, mFilter, Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(mReceiver, mFilter);
        }

        //create a new Api Callback
        mApiCallback = new DemoApiCallback();

        // set the correct binding based on your installed navigation version
        Api.init(getApplicationContext(), BuildConfig.NAVI_PACKAGE, Api.CLASS_TRUCK, mApiCallback);
    }


    /**
     * activity has been destroyed, close the api and disconnect
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("SdkActivity", "onDestroy");
        try {
            Api.getInstance().disconnect();
        } catch (IllegalStateException e) {
            //no problem as we are finishing. Message in logcat is enough.
        }
        SdkApplication.setService(false);
        unregisterReceiver(mReceiver);
    }


    /**
     * start the receiver, reconnect if necessary
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (mReconnect) {
            Api.getInstance().connect();
        }
    }

    /**
     * Check if Truck navigation is running;
     *
     * @param timeOut Timeout in milliseconds to wait for getting result
     * @return true if application is running
     */
    @Override
    public boolean isAppStarted(int timeOut) {
        boolean appRunning = false;

        if (SdkApplication.isService()) {
            try {
                appRunning = Api.isApplicationRunning(timeOut);
            } catch (GeneralException e) {
                e.printStackTrace();
            }
        }

        return appRunning;
    }


    /**
     * Check if Sygic service is bound;
     *
     * @return true if Sygic service is bound
     */
    @Override
    public boolean isServiceConnected() {
        return SdkApplication.isService();
    }


    /**
     * Enable/Disable tabs;
     *
     * @param enabled enabled/disabled state for tabs 1..n
     */
    @Override
    public void setTabsState(boolean enabled) {
        int tabsCount = mTabHost.getTabWidget().getChildCount();
        for (int i = 1; i < tabsCount; i++) {
            mTabHost.getTabWidget().getChildTabViewAt(i).setEnabled(enabled);
            mTabHost.getTabWidget().getChildTabViewAt(i).setAlpha(enabled ? 1 : 0.5f);
            mTabHost.getTabWidget().getChildTabViewAt(i);
        }
    }


    private void setupScreen() {
        /**
         * set the content view, based on the orientation, activity_sdk references different layouts
         */
        setContentView(R.layout.activity_sdk_tabs);

        sMenu = getResources().getStringArray(R.array.menu);


        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup();

        TabManager mTabManager = new TabManager(this, mTabHost, R.id.realtabcontent);

        int n = 0;
        mTabManager.addTab(mTabHost.newTabSpec(sMenu[n]).setIndicator(sMenu[n++]),
                NaviFragment.class, null);
        mTabManager.addTab(mTabHost.newTabSpec(sMenu[n]).setIndicator(sMenu[n++]),
                RouteFragment.class, null);
        mTabManager.addTab(mTabHost.newTabSpec(sMenu[n]).setIndicator(sMenu[n++]),
                PoisFragment.class, null);
        mTabManager.addTab(mTabHost.newTabSpec(sMenu[n]).setIndicator(sMenu[n++]),
                LocationFragment.class, null);
        mTabManager.addTab(mTabHost.newTabSpec(sMenu[n]).setIndicator(sMenu[n++]),
                ItinFragment.class, null);
        mTabManager.addTab(mTabHost.newTabSpec(sMenu[n]).setIndicator(sMenu[n++]),
                RouteInfoFragment.class, null);
        mTabManager.addTab(mTabHost.newTabSpec(sMenu[n]).setIndicator(sMenu[n++]),
                SoundFragment.class, null);
        mTabManager.addTab(mTabHost.newTabSpec(sMenu[n]).setIndicator(sMenu[n++]),
                SearchFragment.class, null);
    }

    /**
     * Tabs Manager
     */
    public static class TabManager implements TabHost.OnTabChangeListener {
        private final FragmentActivity mActivity;
        private final TabHost mTabHost;
        private final int mContainerId;
        private final HashMap<String, TabInfo> mTabs = new HashMap<>();
        TabInfo mLastTab;

        static final class TabInfo {
            private final String tag;
            private final Class<?> clss;
            private final Bundle args;
            private Fragment fragment;

            TabInfo(String _tag, Class<?> _class, Bundle _args) {
                tag = _tag;
                clss = _class;
                args = _args;
            }
        }

        static class DummyTabFactory implements TabHost.TabContentFactory {
            private final Context mContext;

            public DummyTabFactory(Context context) {
                mContext = context;
            }

            @Override
            public View createTabContent(String tag) {
                View v = new View(mContext);
                v.setMinimumWidth(0);
                v.setMinimumHeight(0);
                return v;
            }
        }

        public TabManager(FragmentActivity activity, TabHost tabHost, int containerId) {
            mActivity = activity;
            mTabHost = tabHost;
            mContainerId = containerId;
            mTabHost.setOnTabChangedListener(this);
        }

        public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
            tabSpec.setContent(new DummyTabFactory(mActivity));
            String tag = tabSpec.getTag();

            TabInfo info = new TabInfo(tag, clss, args);

            info.fragment = mActivity.getSupportFragmentManager().findFragmentByTag(tag);
            if (info.fragment != null && info.fragment.getId() != mContainerId) {
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                ft.remove(info.fragment);
                ft.commit();
                info.fragment = null;
            }
            if (info.fragment != null && !info.fragment.isDetached()) {
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                ft.detach(info.fragment);
                ft.commit();
            }

            mTabs.put(tag, info);
            mTabHost.addTab(tabSpec);
        }

        @Override
        public void onTabChanged(String tabId) {
            mActivity.getSupportFragmentManager().popBackStack();
            TabInfo newTab = mTabs.get(tabId);
            if (mLastTab != newTab) {
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                if (mLastTab != null) {
                    if (mLastTab.fragment != null) {
                        ft.detach(mLastTab.fragment);
                    }
                }
                if (newTab != null) {
                    if (newTab.fragment == null) {
                        newTab.fragment = Fragment.instantiate(mActivity,
                                newTab.clss.getName(), newTab.args);
                        ft.add(mContainerId, newTab.fragment, newTab.tag);
                    } else {
                        ft.attach(newTab.fragment);
                    }
                }

                mLastTab = newTab;
                ft.commit();
                mActivity.getSupportFragmentManager().executePendingTransactions();
            }
        }
    }

    public class ActivityReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SdkApplication.INTENT_ACTION_APP_STARTED_LOCAL)) {
                setTabsState(true);
                NaviFragment naviFrag = (NaviFragment) getSupportFragmentManager().findFragmentByTag(sMenu[0]);
                naviFrag.refreshState(true, SdkApplication.isService());
            }
        }

    }


    /**
     * These functions are defined in Activity because of exchanging data between Dialogs (Dialog Fragments) and Fragments.
     */
    @Override
    public void addItinerary(int startLon, int startLat, int stopLon, int stopLat) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(Constants.INIT_START_LON, startLon);
        editor.putInt(Constants.INIT_START_LAT, startLat);
        editor.putInt(Constants.INIT_STOP_LON, stopLon);
        editor.putInt(Constants.INIT_STOP_LAT, stopLat);
        editor.commit();

        ItinFragment f = (ItinFragment) getSupportFragmentManager().findFragmentByTag(sMenu[ITIN_MENU_INDEX]);
        f.addItinerary(startLon, startLat, stopLon, stopLat);
    }

    @Override
    public ApiCallback getApiCallback() {
        return mApiCallback;
    }


    //Api.bringApplicationToBackground() can be invoked by some event from background service.
    //We used as an example AlarmManager to set wakeup event after @millis. (see also StateChangeReceiver)
    @Override
    public void bringToBackg(long millis) {
        AlarmManager am = (AlarmManager) (getSystemService(Context.ALARM_SERVICE));
        Intent amIntent = new Intent(this, StateChangeReceiver.class);
        amIntent.setAction(SdkApplication.INTENT_ACTION_AM_WAKEUP);

        PendingIntent pi = PendingIntent.getBroadcast(this, 0, amIntent, PendingIntent.FLAG_IMMUTABLE);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + millis, pi);
    }

    @Override
    public void addVisibleViapoint(int viaLon, int viaLat) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("itinViaLon", viaLon);
        editor.putInt("itinViaLat", viaLat);
        editor.commit();

        ItinFragment f = (ItinFragment) getSupportFragmentManager().findFragmentByTag(sMenu[ITIN_MENU_INDEX]);
        f.addVisibleViapoint(viaLon, viaLat);
    }

    @Override
    public void addInvisibleViapoint(int viaLon, int viaLat) {
        ItinFragment f = (ItinFragment) getSupportFragmentManager().findFragmentByTag(sMenu[ITIN_MENU_INDEX]);
        f.addInvisibleViapoint(viaLon, viaLat);
    }


    /**
     * Callback receiver from Navigation
     */
    private class DemoApiCallback implements ApiCallback {
        @Override
        public void onEvent(final int event, final String data) {
            boolean show = true;
            switch (event) {
                case ApiEvents.EVENT_APP_STARTED:
                    SdkApplication.sRunning = true;
                    break;
                case ApiEvents.EVENT_APP_EXIT:
                    SdkApplication.sRunning = false;
                    Api.getInstance().disconnect();
                    SdkApplication.setService(false);

                    Intent intent = new Intent();
                    intent.setAction(SdkApplication.INTENT_CHANGE_STATE);
                    sendBroadcast(intent);
                    break;
                case ApiEvents.EVENT_MAIN_MENU:
                    show = false;
                    break;
            }
            if (show)
                mHandler.post(() -> {
                    String str = mEvents.get(event);
                    str += data != null ? " " + data : "";
                    Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
                });
        }

        @Override
        public void onServiceConnected() {
            mReconnect = false;
            SdkApplication.setService(true);

            Intent intent = new Intent();
            intent.setAction(SdkApplication.INTENT_CHANGE_STATE);
            sendBroadcast(intent);
            setTabsState(true);

            Api.getInstance().setOnSoundListener(mSoundListener);
            Api.getInstance().setOnTtsListener(mSoundListener);
        }

        @Override
        public void onServiceDisconnected() {
            SdkApplication.setService(false);

            mReconnect = true;
            Intent intent = new Intent();
            intent.setAction(SdkApplication.INTENT_CHANGE_STATE);
            sendBroadcast(intent);
        }
    }

}