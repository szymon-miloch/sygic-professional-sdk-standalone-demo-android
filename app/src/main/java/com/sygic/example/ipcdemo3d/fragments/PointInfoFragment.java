package com.sygic.example.ipcdemo3d.fragments;

import android.app.Activity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
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

import java.util.concurrent.TimeUnit;

/**
 * shows info about selected StopOffPoint
 */
public class PointInfoFragment extends Fragment {
    private int mPos;
    private String mItinerary;
    private StopOffPoint mPoint;

    /**
     * retrieve the arguments
     *
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mPos = getArguments().getInt("position");
        // if not set use the current itinerary ( default.itf )
        mItinerary = getArguments().getString("name", "default");
    }

    /**
     * inflate the view for this fragment, register buttons..
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_point_info, container, false);
        fillView(view);
        return view;
    }

    private String formatDate(int seconds) {
        int day = (int) TimeUnit.SECONDS.toDays(seconds);
        int hour = (int) TimeUnit.SECONDS.toHours(seconds) - 24 * day;
        int min = (int) TimeUnit.SECONDS.toMinutes(seconds) - 60 * hour;

        if (day == 0) {
            return String.format("%d h %d min", hour, min);
        } else {
            return String.format("%d days, %d h %d min", day, hour, min);
        }
    }

    private void fillView(View view) {
        RouteInfo info = null;
        try {
            mPoint = ApiItinerary.getItineraryList(mItinerary, SdkApplication.MAX).get(mPos);
            info = ApiNavigation.getRouteInfo(true, SdkApplication.MAX);
        } catch (GeneralException e) {
            e.printStackTrace();
        }

        String empty = "empty";

        if (mPoint != null) {
            TextView tv = (TextView) view.findViewById(R.id.tvAddress);
            tv.setText(mPoint.getCaption().equals("") ? empty : mPoint.getCaption());

            tv = (TextView) view.findViewById(R.id.tvISO);
            tv.setText(mPoint.getAddress().equals("") ? empty : mPoint.getIso());

            tv = (TextView) view.findViewById(R.id.tvPosition);
            tv.setText("Lon:" + mPoint.getLocation().getX() + " Lat:" + mPoint.getLocation().getY());

            tv = (TextView) view.findViewById(R.id.tvOffset);
            tv.setText("" + mPoint.getOffset());

            tv = (TextView) view.findViewById(R.id.tvPointType);
            String[] types = getActivity().getResources().getStringArray(R.array.point_type);
            tv.setText(types[mPoint.getPointType() > 0 ? mPoint.getPointType() - 1 : 0]);
        }
    }
}