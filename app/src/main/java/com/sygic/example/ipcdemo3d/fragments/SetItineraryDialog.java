package com.sygic.example.ipcdemo3d.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import com.sygic.example.ipcdemo3d.ActivityResolver;
import com.sygic.example.ipcdemo3d.R;

public class SetItineraryDialog extends DialogFragment implements OnDialogSet {
	private ActivityResolver callback;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			callback = (ActivityResolver) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement ActivityResolver");
		}
	}
	
	
	public static SetItineraryDialog newInstance(String title, int coord[]) {
		SetItineraryDialog frag = new SetItineraryDialog();
		Bundle args = new Bundle();
		args.putString("title", title);
		args.putString("startlon", coord[0] == 0 ? "" : Integer.toString(coord[0]));
		args.putString("startlat", coord[1] == 0 ? "" : Integer.toString(coord[1]));
		args.putString("stoplon", coord[2] == 0 ? "" : Integer.toString(coord[2]));
		args.putString("stoplat", coord[3] == 0 ? "" : Integer.toString(coord[3]));
		frag.setArguments(args);
		
		
		return frag;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle args = getArguments();
		SetAlertDialogEarth sed = new SetAlertDialogEarth(getActivity(), this, args);
		return sed;
	}

	@Override
	public void onDialogSet(Bundle bundle) {
		int outCoord[] = new int[4];
		outCoord[0] = bundle.getInt("startlon");
		outCoord[1] = bundle.getInt("startlat");
		outCoord[2] = bundle.getInt("stoplon");
		outCoord[3] = bundle.getInt("stoplat");
		
		callback.addItin(outCoord[0], outCoord[1], outCoord[2], outCoord[3]);
		dismiss();
	}
	
	@Override
	public void onDialogCancel() {
		dismiss();
	}
	
	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		dismiss();
	}
	
	private class SetAlertDialogEarth extends AlertDialog {
	    private final EditText et[] = new EditText[4];
	    private String init[] = new String[4];
	    private String title;
	    private final OnDialogSet callback;


	    public SetAlertDialogEarth(Context context,
	    		OnDialogSet callBack,
	    		Bundle args) {
	        this(context, 0, callBack, args);
	    }


	    public SetAlertDialogEarth(Context context, int theme, OnDialogSet callBack, Bundle args) {
	        super(context, theme);
	        callback = callBack;
	        title = args.getString("title");
	        init[0] = args.getString("startlon");
	        init[1] = args.getString("startlat");
	        init[2] = args.getString("stoplon");
	        init[3] = args.getString("stoplat");

	        setCanceledOnTouchOutside(false);
	        setIcon(0);
	        setTitle(title);


	        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        View view = inflater.inflate(R.layout.dialog_itin, null);
	        setView(view);
	        et[0] = (EditText) view.findViewById(R.id.etLonStart);
	        et[1] = (EditText) view.findViewById(R.id.etLatStart);
	        et[2] = (EditText) view.findViewById(R.id.etLonStop);
	        et[3] = (EditText) view.findViewById(R.id.etLatStop);

	        // initialize state
	        et[0].setText(init[0]);
	        et[1].setText(init[1]);
	        et[2].setText(init[2]);
	        et[3].setText(init[3]);
	        
	        Button btn_ok = (Button) view.findViewById(R.id.btn_add_itin_ok);
	        btn_ok.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Bundle bundle = new Bundle();
			    	final Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
			    	if (et[0].getText().length() < 1) {
			    		et[0].startAnimation(shake);
			    		return;
			    	}
			    	if (et[1].getText().length() < 1) {
			    		et[1].startAnimation(shake);
			    		return;
			    	}
			    	if (et[2].getText().length() < 1) {
			    		et[2].startAnimation(shake);
			    		return;
			    	}
			    	if (et[3].getText().length() < 1) {
			    		et[3].startAnimation(shake);
			    		return;
			    	}
			        if (callback != null) {
			        	et[0].clearFocus();
			        	et[1].clearFocus();
			        	et[2].clearFocus();
			        	et[3].clearFocus();
			            bundle.putInt("startlon", Integer.parseInt(et[0].getText().toString()));
			            bundle.putInt("startlat", Integer.parseInt(et[1].getText().toString()));
			            bundle.putInt("stoplon", Integer.parseInt(et[2].getText().toString()));
			            bundle.putInt("stoplat", Integer.parseInt(et[3].getText().toString()));
			            callback.onDialogSet(bundle);
			        }
				}
			});
	        
	        Button btn_cancel = (Button) view.findViewById(R.id.btn_add_itin_cancel);
	        btn_cancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					callback.onDialogCancel();
				}
			});
	    }

	    @Override
	    public Bundle onSaveInstanceState() {
	        Bundle state = super.onSaveInstanceState();
	        state.putString("startlon", et[0].getText().toString());
	        state.putString("startlat", et[1].getText().toString());
	        state.putString("stoplon", et[2].getText().toString());
	        state.putString("stoplat", et[3].getText().toString());
	        return state;
	    }

	    @Override
	    public void onRestoreInstanceState(Bundle savedInstanceState) {
	        super.onRestoreInstanceState(savedInstanceState);
	        et[0].setText(savedInstanceState.getString("startlon"));
	        et[1].setText(savedInstanceState.getString("startlat"));
	        et[2].setText(savedInstanceState.getString("stoplon"));
	        et[3].setText(savedInstanceState.getString("stoplat"));
	    }


	}

}