package com.sygic.example.ipcdemo3d.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.sygic.example.ipcdemo3d.R;
import com.sygic.example.ipcdemo3d.SdkApplication;
import com.sygic.sdk.remoteapi.ApiItinerary;
import com.sygic.sdk.remoteapi.exception.GeneralException;
import com.sygic.sdk.remoteapi.model.StopOffPoint;

import java.util.ArrayList;

/**
 * show current route, possibility to save/load route to .rsv file ( no computing on load )
 */
public class RouteFragment extends Fragment {
    private Activity mActivity;
    private ArrayAdapter<String> mListAdapter;

    public RouteFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_list_item_1);
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
        View view = inflater.inflate(R.layout.fragment_route, container, false);

        registerListView(view);
        refreshList();
        return view;
    }


    /**
     * show point details on click
     *
     * @param rootView
     */
    private void registerListView(View rootView) {
        ListView list = (ListView) rootView.findViewById(R.id.waypoints);
        list.setAdapter(mListAdapter);
        list.setOnItemClickListener((adapterView, view, i, l) -> {
            Fragment f = new PointInfoFragment();
            Bundle b = new Bundle();
            b.putInt("position", i);
            b.putString("name", "default");
            f.setArguments(b);
            getFragmentManager().beginTransaction().add(RouteFragment.this.getId(), f)
                    .addToBackStack(null).hide(RouteFragment.this).commit();
        });
    }

    private void refreshList() {
        mListAdapter.clear();
        try {
            ArrayList<StopOffPoint> itinerary = ApiItinerary.getItineraryList("default", SdkApplication.MAX);
            if (itinerary != null) {
                for (StopOffPoint p : itinerary) {
                    String str = p.isVisited() ? "V  " : "U  ";
                    str += p.getCaption() != null ? p.getCaption() : p.getAddress();
                    mListAdapter.add(str);
                }
            }
        } catch (GeneralException e) {
            e.printStackTrace();
        }

        mListAdapter.notifyDataSetChanged();
    }
}