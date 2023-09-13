package com.sygic.example.ipcdemo3d.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.sygic.example.ipcdemo3d.R;
import com.sygic.example.ipcdemo3d.SdkApplication;
import com.sygic.sdk.remoteapi.ApiItinerary;
import com.sygic.sdk.remoteapi.exception.GeneralException;
import com.sygic.sdk.remoteapi.exception.NavigationException;
import com.sygic.sdk.remoteapi.model.StopOffPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * shows available itineraries
 */
public class ItinFragment extends Fragment {
    private ArrayAdapter<String> mListAdapter;
    private Spinner mSpin;

    private static class ViewHolder {
        int pos;
        TextView tvItem;
        Button btnDel;
    }

    public ItinFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_itin, container, false);

        registerButtons(rootView);
        registerListView(rootView);

        return rootView;
    }


    /**
     * compute the route from selected itinerary
     *
     * @param rootView
     */
    private void registerButtons(View rootView) {
        Button btnAccept = (Button) rootView.findViewById(R.id.btn_accept);
        btnAccept.setOnClickListener(view -> {
            try {
                //if 2d navigation the name must be without .itf extension
                ApiItinerary.setRoute("test1", 0, SdkApplication.MAX * 2);
            } catch (NavigationException e) {
                Toast.makeText(getActivity(), "No itinerary added!", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });
        Button btnAdd = (Button) rootView.findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(view -> {
            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            int[] coords = {sharedPref.getInt("itinStartLon", 0), sharedPref.getInt("itinStartLat", 0), sharedPref.getInt("itinStopLon", 0), sharedPref.getInt("itinStopLat", 0)};
            SetItineraryDialog itinDlg = SetItineraryDialog.newInstance("set itinerary test1 start/stop", coords);
            itinDlg.show(ItinFragment.this.getChildFragmentManager(), "SetItineraryDialog");
        });
        Button btnDel = (Button) rootView.findViewById(R.id.btn_del);
        btnDel.setOnClickListener(view -> {
            try {
                ApiItinerary.deleteItinerary("test1", SdkApplication.MAX);
            } catch (GeneralException e) {
                Toast.makeText(getActivity(), "No itinerary added!", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            refreshList("test1");
        });

        Button btnAddEntry = (Button) rootView.findViewById(R.id.btn_add_entry);
        btnAddEntry.setOnClickListener(view -> {
            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            int[] coords = {sharedPref.getInt("itinViaLon", 0), sharedPref.getInt("itinViaLat", 0)};
            SetViapointDialog earthDlg = SetViapointDialog.newInstance("set waypoint", coords);
            earthDlg.show(ItinFragment.this.getChildFragmentManager(), "SetItineraryDialog");
        });
    }


    /**
     * add itinerary test1
     *
     * @param startLon itinerary start longtitude
     * @param startLat itinerary start latitude
     * @param stopLon  itinerary stop longtitude
     * @param stopLat  itinerary stop latitude
     */
    public void addItin(int startLon, int startLat, int stopLon, int stopLat) {
        ArrayList<StopOffPoint> list = new ArrayList<>();
        list.add(new StopOffPoint(false, false, StopOffPoint.PointType.START, startLon, startLat, -1, 0, "", "", ""));
        list.add(new StopOffPoint(false, false, StopOffPoint.PointType.FINISH, stopLon, stopLat, -1, 0, "", "", ""));
        try {
            ApiItinerary.addItinerary(list, "test1", SdkApplication.MAX);
        } catch (GeneralException e) {
            e.printStackTrace();
        }
        refreshList("test1");
    }


    /**
     * add visible viapoint to itinerary test1
     *
     * @param viaLon itinerary waypoint longtitude
     * @param viaLat itinerary waypoint latitude
     */
    public void addVisibleViapoint(int viaLon, int viaLat) {
        try {
            StopOffPoint sop = new StopOffPoint(false, false, StopOffPoint.PointType.VIAPOINT, viaLon, viaLat, -1, 0, "", "", "");
            ApiItinerary.addEntryToItinerary("test1", sop, 1, SdkApplication.MAX);
        } catch (GeneralException e) {
            e.printStackTrace();
        }
        refreshList("test1");
    }


    /**
     * add invisible viapoint to itinerary test1
     *
     * @param viaLon itinerary waypoint longtitude
     * @param viaLat itinerary waypoint latitude
     */
    public void addInvisibleViapoint(int viaLon, int viaLat) {
        try {
            StopOffPoint sop = new StopOffPoint(false, false, StopOffPoint.PointType.INVISIBLE, viaLon, viaLat, -1, 0, "", "", "");
            ApiItinerary.addEntryToItinerary("test1", sop, 1, SdkApplication.MAX);
        } catch (GeneralException e) {
            e.printStackTrace();
        }
        refreshList("test1");
    }


    /**
     * shows point info on item clicked
     *
     * @param rootView
     */
    private void registerListView(View rootView) {
        ArrayList<String> arrStr = new ArrayList<>();
        mListAdapter = new ItinAdapter(getActivity(), arrStr);
        ListView mList = (ListView) rootView.findViewById(R.id.itin_list);
        mList.setAdapter(mListAdapter);
        mList.setOnItemClickListener((adapterView, view, i, l) -> {
            Fragment f = new PointInfoFragment();
            Bundle b = new Bundle();
            b.putInt("position", i);
            b.putString("name", (String) mSpin.getSelectedItem());
            f.setArguments(b);
            getFragmentManager().beginTransaction().add(ItinFragment.this.getId(), f)
                    .addToBackStack(null).hide(ItinFragment.this).commit();
        });
        refreshList("test1");

    }


    /**
     * fill with selected itinerary
     *
     * @param name
     */
    private void refreshList(String name) {
        mListAdapter.clear();
        try {
            ArrayList<StopOffPoint> mItinerary = ApiItinerary.getItineraryList(name, SdkApplication.MAX);
            if (mItinerary != null) {
                for (StopOffPoint p : mItinerary) {
                    String str = (p.getCaption() != null || p.getCaption() != "") ? p.getCaption() : p.getAddress();
                    mListAdapter.add(str);
                }
            }
        } catch (GeneralException e) {
            e.printStackTrace();
        }

        mListAdapter.notifyDataSetChanged();
    }

    private class ItinAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final List<String> items;
        ViewHolder holder = null;

        public ItinAdapter(Context context, List<String> items) {
            super(context, R.layout.itin_list_item, items);
            this.context = context;
            this.items = items;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                holder = new ViewHolder();
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.itin_list_item, null);
                holder.tvItem = (TextView) convertView.findViewById(R.id.tv_item);
                holder.btnDel = (Button) convertView.findViewById(R.id.btn_del);
                holder.btnDel.setOnClickListener(this::removeEntry);
                holder.btnDel.setTag(holder);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tvItem.setText(items.get(position));
            holder.pos = position;
            if (position == 0 || position == (items.size() - 1)) {
                holder.btnDel.setVisibility(View.INVISIBLE);
            } else {
                holder.btnDel.setVisibility(View.VISIBLE);
            }

            return convertView;
        }

        public void removeEntry(View v) {
            ViewHolder holder = (ViewHolder) v.getTag();
            try {
                ApiItinerary.deleteEntryInItinerary("test1", holder.pos, SdkApplication.MAX);
            } catch (GeneralException e) {
                e.printStackTrace();
            }
            mListAdapter.remove((String) holder.tvItem.getText());
            mListAdapter.notifyDataSetChanged();
        }
    }
}