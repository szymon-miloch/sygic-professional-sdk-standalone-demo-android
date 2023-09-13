package com.sygic.example.ipcdemo3d.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sygic.example.ipcdemo3d.SdkApplication;
import com.sygic.sdk.remoteapi.ApiLocation;
import com.sygic.sdk.remoteapi.ApiNavigation;
import com.sygic.sdk.remoteapi.exception.GeneralException;
import com.sygic.sdk.remoteapi.exception.InvalidLocationException;
import com.sygic.sdk.remoteapi.exception.NavigationException;
import com.sygic.sdk.remoteapi.model.Position;
import com.sygic.sdk.remoteapi.model.RoadInfo;
import com.sygic.sdk.remoteapi.model.WayPoint;
import com.sygic.example.ipcdemo3d.R;

/**
 * geocoding
 */
public class LocationFragment extends Fragment {
    private View mRoot;
    private EditText mPosX, mPosY, mAddress;
    private TextView mText;

    public LocationFragment() {
    }

    /**
     * inflate corresponding view, register views...
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.fragment_location, container, false);

        registerButtons(mRoot);
        registerFields(mRoot);
        
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
    	int coords[] = {sharedPref.getInt("locStopLon", 0), sharedPref.getInt("locStopLat", 0)};
    	String address = sharedPref.getString("locAddress", "");
        
        mPosX.setText(coords[0] == 0 ? "" : Integer.toString(coords[0]));
        mPosY.setText(coords[1] == 0 ? "" : Integer.toString(coords[1]));
        mAddress.setText(address);
        
        return mRoot;
    }

    @Override
	public void onDestroyView() {
    	SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putInt("locStopLon", mPosX.getText().toString().equals("") ? 0 : Integer.parseInt(mPosX.getText().toString()));
		editor.putInt("locStopLat", mPosY.getText().toString().equals("") ? 0 : Integer.parseInt(mPosY.getText().toString()));
		editor.putString("locAddress", mAddress.getText().toString());
		editor.commit();
		super.onDestroyView();
	}

	private void registerFields(View view) {
        mPosX = (EditText) view.findViewById(R.id.et1);
        mPosY = (EditText) view.findViewById(R.id.et2);
        mAddress = (EditText) view.findViewById(R.id.et3);
        mText = (TextView) view.findViewById(R.id.tv1);
    }

    private void registerButtons(View rootView) {
        final Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);

        Button btn = (Button) rootView.findViewById(R.id.btn_loc_address_info);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = null;
                try {
                    int x = 0, y = 0;
                    if (!mPosX.getText().toString().equals("")) {
                        x = Integer.parseInt(mPosX.getText().toString());
                    } else {
                        mPosX.startAnimation(shake);
                    }
                    if (!mPosY.getText().toString().equals("")) {
                        y = Integer.parseInt(mPosY.getText().toString());
                    } else {
                        mPosY.startAnimation(shake);
                    }
                    str = ApiLocation.getLocationAddressInfo(new Position(x, y), SdkApplication.MAX);
                } catch (InvalidLocationException e) {
                    e.printStackTrace();
                }
                if (str != null) {
                    mText.setText(str);
                }
            }
        });

        btn = (Button) rootView.findViewById(R.id.btn_loc_road_info);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RoadInfo info = null;
                try {
                    int x = 0, y = 0;
                    if (!mPosX.getText().toString().equals("")) {
                        x = Integer.parseInt(mPosX.getText().toString());
                    } else {
                        mPosX.startAnimation(shake);
                    }
                    if (!mPosY.getText().toString().equals("")) {
                        y = Integer.parseInt(mPosY.getText().toString());
                    } else {
                        mPosY.startAnimation(shake);
                    }
                    info = ApiLocation.getLocationRoadInfo(new Position(x, y), SdkApplication.MAX);
                } catch (InvalidLocationException e) {
                    e.printStackTrace();
                }
                if (info != null) {
                    mText.setText(info.toString());
                }
            }
        });

        btn = (Button) rootView.findViewById(R.id.btn_loc_navi_point);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    int x = 0, y = 0;
                    if (!mPosX.getText().toString().equals("")) {
                        x = Integer.parseInt(mPosX.getText().toString());
                    } else {
                        mPosX.startAnimation(shake);
                    }
                    if (!mPosY.getText().toString().equals("")) {
                        y = Integer.parseInt(mPosY.getText().toString());
                        String address = ApiLocation.getLocationAddressInfo(new Position(x, y), SdkApplication.MAX);
                        ApiNavigation.startNavigation(new WayPoint(address, x, y), 0, false, SdkApplication.MAX);
                    } else {
                        mPosY.startAnimation(shake);
                    }
                } catch (InvalidLocationException e) {
                    e.printStackTrace();
                } catch (NavigationException e) {
                    e.printStackTrace();
                }
            }
        });

        btn = (Button) rootView.findViewById(R.id.btn_loc_navi_add);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (mAddress.getText().length() == 0) {
                        mAddress.startAnimation(shake);
                    } else {
                        Position pos = ApiLocation.locationFromAddress(mAddress.getText().toString(), false, true, 0);
                        ApiNavigation.startNavigation(new WayPoint(mAddress.getText().toString(), pos.getX(), pos.getY()), 0, false, 0);
                    }
                } catch (NavigationException e) {
                    e.printStackTrace();
                } catch (GeneralException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}