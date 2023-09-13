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

import java.util.ArrayList;
import java.util.Collections;

/**
 * shows the POIs from the favorites category
 */
public class PoisFragment extends Fragment {
    private SdkActivity mActivity;
    private ArrayAdapter<String> mListAdapter, mSpinnerAdapter;
    private Spinner mSpin;
    private ArrayList<Poi> mPois;

    public PoisFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (SdkActivity) activity;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_list_item_1);
        mSpinnerAdapter = new ArrayAdapter<>(mActivity, R.layout.spinner_item);
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

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
        View rootView = inflater.inflate(R.layout.fragment_pois, container, false);

        registerButtons(rootView);
        registerListView(rootView);
        registerSpinner(rootView);
        populateSpinner();
        return rootView;
    }

    private void populateSpinner() {
        mSpinnerAdapter.clear();
        ArrayList<PoiCategory> list = null;
        try {
            list = ApiPoi.getPoiCategoryList(SdkApplication.MAX);
        } catch (GeneralException e) {
            e.printStackTrace();
        }

        Collections.sort(list, (arg0, arg1) -> {
            try {
                Integer defCatA = Integer.parseInt(arg0.getName());
                Integer defCatB = Integer.parseInt(arg1.getName());
                return defCatA.compareTo(defCatB);
            } catch (NumberFormatException e) {
                return arg0.getName().compareToIgnoreCase(arg1.getName());
            }
        });
        if (list != null && !list.isEmpty()) {
            for (PoiCategory cat : list) {
                mSpinnerAdapter.add(cat.getName());
            }
        }
        mSpinnerAdapter.notifyDataSetChanged();
    }

    private void registerSpinner(final View v) {
        mSpin = (Spinner) v.findViewById(R.id.spin1);
        mSpin.setAdapter(mSpinnerAdapter);
        mSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                refreshList(false);
                ((TextView) v.findViewById(R.id.tv_category)).setText("Category: " + (String) adapterView.getItemAtPosition(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void registerButtons(View rootView) {
        Button btn = (Button) rootView.findViewById(R.id.btn_nearby);
        btn.setOnClickListener(view -> {

            /**
             * create a new dialog for adding the poi
             */
            LayoutInflater inflater = mActivity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_add_poi, null);
            final EditText captionEt = ((EditText) dialogView.findViewById(R.id.et1));
            captionEt.setVisibility(View.GONE);
            final EditText countEt = ((EditText) dialogView.findViewById(R.id.et2));
            countEt.setHint("max count");
            countEt.setInputType(InputType.TYPE_CLASS_NUMBER);
            final EditText locXEt = ((EditText) dialogView.findViewById(R.id.et3));
            final EditText locYEt = ((EditText) dialogView.findViewById(R.id.et4));
            final Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);

            final AlertDialog dialog = new AlertDialog.Builder(mActivity)
                    .setTitle("Nearby pois")
                    .setView(dialogView)
                    .setPositiveButton("OK", (dialog12, which) -> {
                    })
                    .setNegativeButton("Cancel", (dialog1, which) -> dialog1.cancel())
                    .create();
            dialog.setOnDismissListener(dialogInterface -> refreshList(true));
            dialog.setOnShowListener(dialogInterface -> {
                Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(view1 -> {
                    boolean dismiss = true;
                    String count = countEt.getText().toString();
                    String locX = locXEt.getText().toString();
                    String locY = locYEt.getText().toString();

                    if (count.length() == 0) {
                        countEt.startAnimation(shake);
                    }
                    int x = 0, y = 0, c = 0;
                    if (!locX.equals("")) {
                        x = Integer.parseInt(locX);
                    } else {
                        locXEt.startAnimation(shake);
                    }
                    if (!locY.equals("")) {
                        y = Integer.parseInt(locY);
                    } else {
                        locYEt.startAnimation(shake);
                    }
                    if (!count.equals("")) {
                        c = Integer.parseInt(count);
                    } else {
                        locYEt.startAnimation(shake);
                    }

                    int id;
                    String sel = (String) mSpin.getSelectedItem();
                    try {
                        id = Integer.parseInt(sel);
                    } catch (NumberFormatException e) {
                        id = ApiPoi.USERDEFINE;
                    }

                    try {
                        mPois = ApiPoi.findNearbyPoiList(id, sel, x, y, c, SdkApplication.MAX);
                    } catch (GeneralException e) {
                        dismiss = false;
                        Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    if (dismiss) {
                        dialog.dismiss();
                    }
                });
            });
            dialog.show();
        });
    }

    /**
     * show poi info on item clicked
     *
     * @param rootView
     */
    private void registerListView(View rootView) {
        ListView mList = (ListView) rootView.findViewById(R.id.pois_list);
        mList.setAdapter(mListAdapter);
        mList.setOnItemClickListener((adapterView, view, i, l) -> {
            Poi poi = mPois.get(i);
            Fragment fragment = new PoiInfoFragment();
            fragment.setArguments(Poi.writeBundle(poi));
            getFragmentManager().beginTransaction().add(PoisFragment.this.getId(), fragment)
                    .addToBackStack(null).detach(PoisFragment.this).commit();
        });
    }

    private void refreshList(boolean nearby) {
        if (!nearby) {
            try {
                mPois = ApiPoi.getPoiList((String) mSpin.getSelectedItem(), true, SdkApplication.MAX);
            } catch (GeneralException e) {
                e.printStackTrace();
            }
        }
        mListAdapter.clear();
        if (mPois != null) {
            for (Poi p : mPois) {
                mListAdapter.add(p.getName());
            }
        }
        mListAdapter.notifyDataSetChanged();
    }
}