package com.sygic.example.ipcdemo3d.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.sygic.example.ipcdemo3d.R;
import com.sygic.example.ipcdemo3d.SdkActivity;
import com.sygic.example.ipcdemo3d.SdkApplication;
import com.sygic.sdk.remoteapi.ApiPoi;
import com.sygic.sdk.remoteapi.exception.GeneralException;
import com.sygic.sdk.remoteapi.model.Poi;
import com.sygic.sdk.remoteapi.model.PoiCategory;
import com.sygic.sdk.remoteapi.model.Position;

import java.util.ArrayList;
import java.util.Collections;

/**
 * shows the POIs from the favorites category
 */
public class UpdatePoisFragment extends Fragment {
    private SdkActivity mActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (SdkActivity) activity;

    }

    /**
     * inflate the view for this fragment, register buttons, list view....
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_update_pois, container, false);

        registerButtons(rootView);
        return rootView;
    }

    private void registerButtons(View rootView) {
        Button btn = (Button) rootView.findViewById(R.id.btn_updatePois);
        btn.setOnClickListener(view -> {

            try {
                EditText tv = (EditText) rootView.findViewById(R.id.edit_updateCmd);
                String update = tv.getText().toString();

                ApiPoi.updatePois(update, ApiPoi.FORMAT_TEXT, 1000);

            } catch (GeneralException e) {
                Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }
}