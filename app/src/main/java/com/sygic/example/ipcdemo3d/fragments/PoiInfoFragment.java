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
import com.sygic.sdk.remoteapi.ApiMaps;
import com.sygic.sdk.remoteapi.exception.GeneralException;
import com.sygic.sdk.remoteapi.model.Poi;

/**
 * shows info about the poi
 */
public class PoiInfoFragment extends Fragment {
    private Poi mPoi;

    /**
     * read the arguments to a POI class
     *
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mPoi = Poi.readBundle(getArguments());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_poi_info, container, false);
        view.findViewById(R.id.btn_show).setOnClickListener(view1 -> {
            if (mPoi != null) {
                try {
                    ApiMaps.showCoordinatesOnMap(mPoi.getLocation(), SdkApplication.ZOOM, SdkApplication.MAX);
                } catch (GeneralException e) {
                    e.printStackTrace();
                }
            }
        });


        fillView(view);
        return view;
    }

    /**
     * show fields
     *
     * @param view
     */
    private void fillView(View view) {

        String empty = "empty";

        if (mPoi != null) {
            TextView tv = (TextView) view.findViewById(R.id.tv1);
            tv.setText(mPoi.getName().equals("") ? empty : mPoi.getName());

            tv = (TextView) view.findViewById(R.id.tv2);
            tv.setText(mPoi.getAddress().equals("") ? empty : mPoi.getAddress());

            tv = (TextView) view.findViewById(R.id.tv3);
            tv.setText(mPoi.getCategory().equals("") ? empty : mPoi.getCategory());

            tv = (TextView) view.findViewById(R.id.tv4);
            tv.setText("Lon:" + mPoi.getLocation().getX() + " Lat:" + mPoi.getLocation().getY());

            tv = (TextView) view.findViewById(R.id.tv5);
            tv.setText(mPoi.isSearchAddress() ? "true" : "false");
        }
    }
}