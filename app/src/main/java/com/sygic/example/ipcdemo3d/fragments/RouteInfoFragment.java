package com.sygic.example.ipcdemo3d.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sygic.example.ipcdemo3d.SdkApplication;
import com.sygic.sdk.remoteapi.ApiItinerary;
import com.sygic.sdk.remoteapi.ApiNavigation;
import com.sygic.sdk.remoteapi.exception.GeneralException;
import com.sygic.sdk.remoteapi.model.RouteInfo;
import com.sygic.sdk.remoteapi.model.StopOffPoint;
import com.sygic.example.ipcdemo3d.R;

/**
 * show the info about current route
 */
public class RouteInfoFragment extends Fragment {
    private View mRoot;
    private Handler mHandler;
    private ArrayList<StopOffPoint> mRoute;
    private int mWpIndex;

    public RouteInfoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
    }

    /**
     * inflate the view for this fragment, and register buttons
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.fragment_route_info, container, false);
        return mRoot;
    }

    /**
     * start refreshing the view if it is visible, can be resumed but not visible on orientation change
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mRoot.isShown()) {
            refresh();
        }
    }

    /**
     * stop refreshing
     */
    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(refresh);
    }

    Runnable refresh = new Runnable() {
        @Override
        public void run() {
            refresh();
        }
    };

    private void refresh() {
        getRouteInfo();
        mHandler.postDelayed(refresh, 2000);
    }

    /**
     * retrieve the current route and find first unvisited point
     *
     * @return current StopOffPoint from the loaded route
     */
    private StopOffPoint getCurrent() {
        mWpIndex = -1;
        try {
            mRoute = ApiItinerary.getItineraryList("default", SdkApplication.MAX);
        } catch (GeneralException e) {
            e.printStackTrace();
        }
        StopOffPoint current = new StopOffPoint();
        if (mRoute != null) {
            for (StopOffPoint aMRoute : mRoute) {
                current = aMRoute;
                if (!current.isVisited()) {
                    break;
                }
                mWpIndex++;
            }
        }
        return current;
    }


    private void getRouteInfo() {
        try {
            RouteInfo info = ApiNavigation.getRouteInfo(true, SdkApplication.MAX);
            if (info != null) {
                fillInfo(info);
            }
        } catch (GeneralException e) {
            e.printStackTrace();
        }
    }


    private String formatDate(int seconds) {
        int day = seconds / 60 / 60 / 24;
        int hour = seconds / 60 / 60 - 24 * day;
        int min = seconds / 60 - 60 * hour;

        if (day == 0) {
            return String.format("%d h %d min", hour, min);
        } else {
            return String.format("%d days, %d h %d min", day, hour, min);
        }
    }

    private void fillInfo(RouteInfo info) {
        String str;

        TextView tv = (TextView) mRoot.findViewById(R.id.tv1);
        tv.setText(info.getTotalDistance() + " m");

        tv = (TextView) mRoot.findViewById(R.id.tv2);
        tv.setText(formatDate(info.getTotalTime()));

        tv = (TextView) mRoot.findViewById(R.id.tv3);
        tv.setText(info.getRemainingDistance() + " m");

        tv = (TextView) mRoot.findViewById(R.id.tv4);
        tv.setText(formatDate(info.getRemainingTime()));

        tv = (TextView) mRoot.findViewById(R.id.tv5);
        tv.setText(/*R.string.none*/"None" + info.getStatus());

        tv = (TextView) mRoot.findViewById(R.id.tv6);
        RouteInfo.RouteInfoTime time = info.getEstimatedTimeArrival();
        tv.setText(time.getHour() + ":" + time.getMinute() + ":" + time.getSecond() + " " + time.getDay() + "." + time.getMonth() + ". " + time.getYear());
    }
}
