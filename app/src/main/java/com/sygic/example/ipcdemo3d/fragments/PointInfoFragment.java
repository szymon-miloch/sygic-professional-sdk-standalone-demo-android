package com.sygic.example.ipcdemo3d.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.sygic.example.ipcdemo3d.R;
import com.sygic.example.ipcdemo3d.SdkApplication;
import com.sygic.sdk.remoteapi.ApiItinerary;
import com.sygic.sdk.remoteapi.exception.GeneralException;
import com.sygic.sdk.remoteapi.model.StopOffPoint;

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

    private void fillView(View view) {
        try {
            mPoint = ApiItinerary.getItineraryList(mItinerary, SdkApplication.MAX).get(mPos);
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