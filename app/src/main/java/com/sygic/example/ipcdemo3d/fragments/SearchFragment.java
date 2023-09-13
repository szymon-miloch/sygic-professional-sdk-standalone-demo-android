package com.sygic.example.ipcdemo3d.fragments;

import java.util.ArrayList;

import com.sygic.example.ipcdemo3d.R;
import com.sygic.sdk.remoteapi.ApiLocation;
import com.sygic.sdk.remoteapi.callback.OnSearchListener;
import com.sygic.sdk.remoteapi.exception.GeneralException;
import com.sygic.sdk.remoteapi.model.Position;
import com.sygic.sdk.remoteapi.model.WayPoint;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class SearchFragment extends Fragment {
	
	private View mView;
	private TextView tvResults;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_search, container, false);
		
		tvResults = (TextView) mView.findViewById(R.id.tvSearchResults);
		
		mView.findViewById(R.id.btnSearch).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				tvResults.setText("");
				
				EditText edit = (EditText) mView.findViewById(R.id.editSearchText);
				String searchText = edit.getText().toString();
				boolean result = false;
				try {
					result = ApiLocation.searchLocation(searchText, mOnSearchListener, 0);
				} catch (GeneralException e) {
					e.printStackTrace();
				}
				if(!result) {
					tvResults.setText("Search failed");
				}
			}
		});
		
		return mView;
	}
	
	private OnSearchListener mOnSearchListener = new OnSearchListener() {
		
		@Override
		public void onResult(String input, ArrayList<WayPoint> waypoints, int resultCode) {
			if(resultCode != OnSearchListener.RC_OK) {
				tvResults.setText("Search failed");
			}
			else {
				StringBuilder sb = new StringBuilder();
				for(WayPoint wp : waypoints) {
					Position pos = wp.getLocation();
					
					sb.append(wp.getStrAddress()).append('\n')
						.append(pos.getX()).append(", ").append(pos.getY())
						.append("\n\n");
				}
				
				final String strRes = sb.toString();
				
				getActivity().runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						tvResults.setText(strRes);
					}
				});
			}
		}
	};

}
