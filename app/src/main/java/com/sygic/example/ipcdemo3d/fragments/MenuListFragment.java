package com.sygic.example.ipcdemo3d.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.fragment.app.ListFragment;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sygic.example.ipcdemo3d.SdkActivity;
import com.sygic.example.ipcdemo3d.SdkApplication;
import com.sygic.example.ipcdemo3d.R;

/**
 * menu
 */
public class MenuListFragment extends ListFragment {

    private Callbacks mCallbacks = sDummyCallbacks;
    private boolean mEnabled = false;
    private ChangeStateMenuReceiver mChangeReceiver;
    protected static class ViewHolder {
		TextView tvItem;
	}

    /**
     * callback for the selected item
     */
    public interface Callbacks {
        public void onItemSelected(int id);
    }

    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(int id) {
        }
    };

    public MenuListFragment() {
    }

    /**
     * set adapter with our view and menu items from resources
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mChangeReceiver = new ChangeStateMenuReceiver();
        final String[] menu = getResources().getStringArray(R.array.menu);
        setListAdapter(new ArrayAdapter<String>(getActivity(),
                R.layout.sdk_list_item,
                R.id.list_item_text,
                menu) {
        	
        	@Override
        	public boolean isEnabled(int position) {
        		if (!mEnabled && (position > 0 && position < menu.length)) {
        			 return false;
        		}
        		return true;
        	}
        });
    }

    /**
     * throw an exception if the main activity is not implementing the callback
     *
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }
    
    
    @Override
    public void onResume() {
    	IntentFilter intentFilter = new IntentFilter(SdkApplication.INTENT_CHANGE_MENU_STATE);
    	getActivity().registerReceiver(mChangeReceiver, intentFilter);
    	super.onResume();
    }
    
    
    @Override
    public void onPause() {
    	getActivity().unregisterReceiver(mChangeReceiver);
    	super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        mCallbacks.onItemSelected(position);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        getListView().setItemChecked(SdkActivity.tabId, true);
    }
    
    
    public void setListState(boolean enabled) {
    	mEnabled = enabled;
    }
    
    
    private class ChangeStateMenuReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(SdkApplication.INTENT_CHANGE_MENU_STATE)) {
				boolean enabled = intent.getBooleanExtra("enabled", false);
				setListState(enabled);
			}
		}
    	
    }
    
}