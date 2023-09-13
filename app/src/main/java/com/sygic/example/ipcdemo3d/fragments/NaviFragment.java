package com.sygic.example.ipcdemo3d.fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.sygic.example.ipcdemo3d.ActivityResolver;
import com.sygic.example.ipcdemo3d.R;
import com.sygic.example.ipcdemo3d.SdkActivity;
import com.sygic.example.ipcdemo3d.SdkApplication;
import com.sygic.sdk.remoteapi.Api;
import com.sygic.sdk.remoteapi.ApiDialog;
import com.sygic.sdk.remoteapi.ApiOptions;
import com.sygic.sdk.remoteapi.exception.GeneralException;
import com.sygic.sdk.remoteapi.model.NaviVersion;
import com.sygic.sdk.remoteapi.model.Options;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * manages interaction with navigation
 */
public class NaviFragment extends Fragment {

    private static final int RC_OPTIONS = 1;

    private TextView mStatus;
	private View mView;
	private ActivityResolver activity;
	private ChangeStateReceiver mChangeReceiver;

	private ArrayAdapter<String> mSpinAdapterVoice = null;
	private ArrayAdapter<String> mSpinAdapterPerson = null;
	private ArrayAdapter<String> mSpinAdapterLang = null;

	private Spinner mSpinVoices;
	private Spinner mSpinPersons;
	private Spinner mSpinLangs;
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mChangeReceiver = new ChangeStateReceiver();
    }
    
    private void populateSpinnerAdapter(ArrayAdapter<String> spinnerAdapter, List<String> items) {
    	spinnerAdapter.clear();
        if (items != null && !items.isEmpty()) {
        	Collections.sort(items, new Comparator<String>() {

    			@Override
    			public int compare(String lhs, String rhs) {
    				return lhs.compareToIgnoreCase(rhs);
    			}
            	
            });
            for (String dir : items) {
            	spinnerAdapter.add(dir);
            }
        }
        spinnerAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	mView = inflater.inflate(R.layout.fragment_navigation, container, false);
        mStatus = (TextView) mView.findViewById(R.id.status);
        registerButtons(mView);
        refreshState(activity.isAppStarted(SdkApplication.MAX), activity.isServiceConnected());
        return mView;
    }
    
    @Override
    public void onAttach(Activity activity) {
    	this.activity = (ActivityResolver)activity;
    	super.onAttach(activity);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(SdkApplication.INTENT_CHANGE_STATE);
        getActivity().registerReceiver(mChangeReceiver, intentFilter);
        refreshState(activity.isAppStarted(SdkApplication.MAX), activity.isServiceConnected());
    }

    @Override
    public void onPause() {
    	getActivity().unregisterReceiver(mChangeReceiver);
        super.onPause();
    }
    
    
    /**
     * Refresh label state and button state based on Sygic service and Application state;
     * 
     * @param	appRunning	true if truck application is running
     * @param	connected	true if we are bound to SygicService
     */
    public void refreshState(boolean appRunning, boolean connected) {
    	setButtonState(appRunning, connected);
        mStatus.setText(setStateString(appRunning, connected));
        activity.setTabsState(appRunning && connected);
    }
    
    
    /**
     * Change state label based on Sygic service and Application state;
     * 
     * @param	appRunning	true if truck application is running
     * @param	connected	true if we are bound to SygicService
     * 
     * @return				status string
     */
    private String setStateString(boolean appRunning, boolean connected) {
    	String retStr;
    	
    	retStr = connected ? "Service connected" : "Service disconnected";
    	retStr = retStr + ", " + (!connected ? "App in unknown state" : (appRunning ? "App started" : "App stopped"));
    	
    	return retStr;
    }

    
    /**
     * Change buttons state based on Sygic service and Application state;
     * 
     * @param	appRunning	true if nav. application is running
     * @param	connected	true if we are bound to SygicService
     */
    private void setButtonState(boolean appRunning, boolean connected) {
    	((ToggleButton) mView.findViewById(R.id.btn_connect)).setChecked(connected);
    	((Button) mView.findViewById(R.id.btn_start_foreg)).setEnabled(connected);
    	((Button) mView.findViewById(R.id.btn_start_foreg)).setText(!appRunning && connected ? "Start navi in foreg" : "Bring navi to foreg");
    	((Button) mView.findViewById(R.id.btn_bring_foreg_5sec)).setEnabled(appRunning && connected);
    	((Button) mView.findViewById(R.id.btn_end_navi)).setEnabled(appRunning && connected);
    	mView.findViewById(R.id.btn_version_app).setEnabled(appRunning && connected);
    	mView.findViewById(R.id.btn_version_map).setEnabled(appRunning && connected);
    	mView.findViewById(R.id.btn_options).setEnabled(appRunning && connected);
    	mView.findViewById(R.id.btn_dev_id).setEnabled(appRunning && connected);
    	mView.findViewById(R.id.btn_flash).setEnabled(appRunning && connected);
    	mView.findViewById(R.id.btn_show).setEnabled(appRunning && connected);
    	mView.findViewById(R.id.btn_sdk_version).setEnabled(appRunning && connected);
    }
    

    /**
     * Implement callbacks for buttons.
     * 
     * @param	view	container for buttons
     */
    private void registerButtons(View view) {
        
        ToggleButton btnConnect = (ToggleButton) view.findViewById(R.id.btn_connect);
        //tBtnConnect.setChecked(false);
        btnConnect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (((ToggleButton) v).isChecked()) {
					Api.getInstance().connect();
				} else {
					SdkApplication.setService(false);
					Api.getInstance().disconnect();
					refreshState(activity.isAppStarted(SdkApplication.MAX), false);
				}
			}
		});
        
        Button btnStartForeg = (Button) view.findViewById(R.id.btn_start_foreg);
        btnStartForeg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					Api.getInstance().show(false);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				refreshState(activity.isAppStarted(SdkApplication.MAX), activity.isServiceConnected());
			}
		});
        
        Button btnBringForeg5sec = (Button) view.findViewById(R.id.btn_bring_foreg_5sec);
        btnBringForeg5sec.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					ApiDialog.flashMessage("Bring to background from demo after 5 sec...", SdkApplication.MAX);
					Api.getInstance().show(false);
				} catch (RemoteException e) {
					e.printStackTrace();
				} catch (GeneralException e) {
					e.printStackTrace();
				}
				activity.bringToBackg(5000);
			}	
		});
        
        Button btnEndNavi = (Button) view.findViewById(R.id.btn_end_navi);
        btnEndNavi.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					Api.endApplication(SdkApplication.MAX);
				} catch (GeneralException e) {
					e.printStackTrace();
				}
			}	
		});
        
        Button btnOptions = (Button) view.findViewById(R.id.btn_options);
        btnOptions.setOnClickListener(new View.OnClickListener() {
        	
            @TargetApi(Build.VERSION_CODES.FROYO) @Override
            public void onClick(View view) {
                final String PERMISSION_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                if(mSpinAdapterPerson == null) {
                    // we need WRITE_EXTERNAL_STORAGE permission for voices folders
                    if(ContextCompat.checkSelfPermission(getActivity(), PERMISSION_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        String[] permissions = new String[] { PERMISSION_STORAGE };
                        requestPermissions(permissions, RC_OPTIONS);
                        return;
                    }
                    else {
                        setupVoiceSpinAdapters();
                    }
                }
                showChangeOptionsDialog();
            }
        });

        final TextView tv = (TextView) view.findViewById(R.id.tv_info);
        Button btnVersion = (Button) view.findViewById(R.id.btn_version_app);
        btnVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    NaviVersion version = Api.getApplicationVersion(SdkApplication.MAX);
                    tv.setText(version.toString());
                } catch (GeneralException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });
        
        Button btnSdkVersion = (Button) view.findViewById(R.id.btn_sdk_version);
        btnSdkVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    tv.setText(Api.getLibVersion());
            }
        });


        Button btnVerMap = (Button) view.findViewById(R.id.btn_version_map);
        btnVerMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String version = Api.getMapVersion("SVK", SdkApplication.MAX);
                    tv.setText(version);
                } catch (GeneralException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });

        Button btnDevId = (Button) view.findViewById(R.id.btn_dev_id);
        btnDevId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String id = Api.getUniqueDeviceId(SdkApplication.MAX);
                    tv.setText(id);
                } catch (GeneralException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });

        Button btnFlash = (Button) view.findViewById(R.id.btn_flash);
        btnFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ApiDialog.flashMessage("This is sample message", SdkApplication.MAX);
                    Api.getInstance().show(false);
                } catch (GeneralException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (RemoteException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });

        Button btnShow = (Button) view.findViewById(R.id.btn_show);
        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                need to execute on a separate thread, waits for user feedback
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                int r = ApiDialog.showMessage("This is sample message", 1, true, 0);
                                Log.d(getClass().getCanonicalName(), "dialog has returned: " + (r == 201 ? "OK/Yes" : "No/Cancel"));
                            } catch (GeneralException e) {
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            }
                        }
                    }).start();
            }
        });
    }

    private void setupVoiceSpinAdapters() {
        File rootDirVoices = new File(SdkApplication.PATH_VOICES_2D);
        List<String> listStrVoices = new ArrayList<String>();
        if (rootDirVoices.exists()) {
            File[] dirsVoices = rootDirVoices.listFiles();
            List<File> listDirVoices = Arrays.asList(dirsVoices);
            for (File dir : listDirVoices) {
                if (dir.getName().startsWith("TTS"))
                    listStrVoices.add(dir.getName());
            }
        }
        mSpinAdapterVoice = new ArrayAdapter<String>((SdkActivity)activity, R.layout.spinner_item);
        mSpinAdapterVoice.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        populateSpinnerAdapter(mSpinAdapterVoice, listStrVoices);

        File rootDirPersons = new File(SdkApplication.PATH_VOICES_PERSON_2D);
        List<String> listStrPersons = new ArrayList<String>();
        if (rootDirPersons.exists()) {
            File[] dirsPersons = rootDirPersons.listFiles();
            List<File> listDirPersons = Arrays.asList(dirsPersons);
            for (File dir : listDirPersons) {
                listStrPersons.add(dir.getName());
            }
        }
        mSpinAdapterPerson = new ArrayAdapter<String>((SdkActivity)activity, R.layout.spinner_item);
        mSpinAdapterPerson.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        populateSpinnerAdapter(mSpinAdapterPerson, listStrPersons);

        File rootDirLangs = new File(SdkApplication.PATH_LANGS);
        List<String> listStrLangs = new ArrayList<String>();
        if (rootDirLangs.exists()) {
            File[] dirsLangs = rootDirLangs.listFiles();
            List<File> listDirLangs = Arrays.asList(dirsLangs);
            for (File dir : listDirLangs) {
                listStrLangs.add(dir.getName());
            }
        }
        mSpinAdapterLang = new ArrayAdapter<String>((SdkActivity)activity, R.layout.spinner_item);
        mSpinAdapterLang.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        populateSpinnerAdapter(mSpinAdapterLang, listStrLangs);

    }

    private void showChangeOptionsDialog() {
        Options opt = null;
        try {
            opt = ApiOptions.changeApplicationOptions(null, SdkApplication.MAX);
        } catch (GeneralException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        final Options current  = opt;
        /**
         * create a new dialog for adding the poi
         */
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_options, null);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("Set options")
                .setView(dialogView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                final CheckBox sound = (CheckBox) dialog.findViewById(R.id.check1);
                sound.setChecked(current != null && current.bSoundEnabled > 0);

                final CheckBox tts = (CheckBox) dialog.findViewById(R.id.check2);
                tts.setChecked(current != null && current.bTTSEnabled > 0);

                mSpinVoices = (Spinner) dialog.findViewById(R.id.spin1);
                mSpinVoices.setAdapter(mSpinAdapterVoice);
                String currentVoice = current.getVoice();
                int i;
                int itemCounts = mSpinVoices.getAdapter().getCount();
                for (i = 0; i < itemCounts; i++) {
                    if (mSpinVoices.getItemAtPosition(i).equals(currentVoice))
                        break;
                }
                mSpinVoices.setSelection(i >= itemCounts ? 0 : i);

                mSpinPersons = (Spinner) dialog.findViewById(R.id.spin2);
                mSpinPersons.setAdapter(mSpinAdapterPerson);
                String currentPerson = current.getVoicePerson();
                itemCounts = mSpinPersons.getAdapter().getCount();
                for (i = 0; i < mSpinPersons.getAdapter().getCount(); i++) {
                    if (mSpinPersons.getItemAtPosition(i).equals(currentPerson))
                        break;
                }
                mSpinPersons.setSelection(i >= itemCounts ? 0 : i);

                mSpinLangs = (Spinner) dialog.findViewById(R.id.spin3);
                mSpinLangs.setAdapter(mSpinAdapterLang);
                String currentLang = current.getLanguage();
                itemCounts = mSpinLangs.getAdapter().getCount();
                for (i = 0; i < mSpinLangs.getAdapter().getCount(); i++) {
                    if (mSpinLangs.getItemAtPosition(i).equals(currentLang))
                        break;
                }
                mSpinLangs.setSelection(i >= itemCounts ? 0 : i);

                Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Options opt = new Options();
                        opt.bSoundEnabled = sound.isChecked() ? 1 : 0;
                        opt.bTTSEnabled = tts.isChecked() ? 1: 0;
                        opt.setVoice(mSpinVoices.getSelectedItem() != null ? mSpinVoices.getSelectedItem().toString() : "");
                        opt.setVoicePerson(mSpinPersons.getSelectedItem() != null ? mSpinPersons.getSelectedItem().toString() : "");
                        opt.setLanguage(mSpinLangs.getSelectedItem() != null ? mSpinLangs.getSelectedItem().toString() : "");

                        try {
                            ApiOptions.changeApplicationOptions(opt, SdkApplication.MAX);
                        } catch (GeneralException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == RC_OPTIONS) {
            for(int res : grantResults)
                if(res != PackageManager.PERMISSION_GRANTED)
                    return;

            setupVoiceSpinAdapters();
            showChangeOptionsDialog();
        }
    }

    private class ChangeStateReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(SdkApplication.INTENT_CHANGE_STATE)) {
				refreshState(activity.isAppStarted(SdkApplication.MAX), activity.isServiceConnected());
			}
		}
    	
    }
    
}