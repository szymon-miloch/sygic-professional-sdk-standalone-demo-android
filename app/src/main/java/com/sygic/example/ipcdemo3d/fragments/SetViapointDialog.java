package com.sygic.example.ipcdemo3d.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import com.sygic.example.ipcdemo3d.ActivityResolver;
import com.sygic.example.ipcdemo3d.Constants;
import com.sygic.example.ipcdemo3d.R;

public class SetViapointDialog extends DialogFragment implements OnDialogSet {
    private ActivityResolver callback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            callback = (ActivityResolver) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity + " must implement ActivityResolver");
        }
    }


    public static SetViapointDialog newInstance(String title, int[] coord) {
        SetViapointDialog frag = new SetViapointDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString(Constants.START_LON, coord[0] == 0 ? "" : Integer.toString(coord[0]));
        args.putString(Constants.START_LAT, coord[1] == 0 ? "" : Integer.toString(coord[1]));
        frag.setArguments(args);

        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        return new SetAlertDialogEarth(getActivity(), this, args);
    }

    @Override
    public void onDialogSet(Bundle bundle) {
        int[] outCoord = new int[4];
        outCoord[0] = bundle.getInt(Constants.START_LON);
        outCoord[1] = bundle.getInt(Constants.START_LAT);
        boolean visible = bundle.getBoolean("visible");

        if (visible) {
            callback.addVisibleViapoint(outCoord[0], outCoord[1]);
        } else {
            callback.addInvisibleViapoint(outCoord[0], outCoord[1]);
        }

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
        private final EditText[] et = new EditText[2];
        private final CheckBox cb;
        private final OnDialogSet callback;


        public SetAlertDialogEarth(Context context, OnDialogSet callBack, Bundle args) {
            this(context, 0, callBack, args);
        }


        public SetAlertDialogEarth(Context context, int theme, OnDialogSet callBack, Bundle args) {
            super(context, theme);
            callback = callBack;
            String title = args.getString("title");
            String[] init = new String[2];
            init[0] = args.getString(Constants.START_LON);
            init[1] = args.getString(Constants.START_LAT);

            setCanceledOnTouchOutside(false);
            setIcon(0);
            setTitle(title);

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.dialog_viapoint, null);
            setView(view);
            et[0] = (EditText) view.findViewById(R.id.etLonStart);
            et[1] = (EditText) view.findViewById(R.id.etLatStart);
            cb = (CheckBox) view.findViewById(R.id.cbVisible);
            cb.setChecked(true);

            // initialize state
            et[0].setText(init[0]);
            et[1].setText(init[1]);

            Button btn_ok = (Button) view.findViewById(R.id.btn_add_via_ok);
            btn_ok.setOnClickListener(v -> {
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
                if (callback != null) {
                    et[0].clearFocus();
                    et[1].clearFocus();
                    bundle.putInt(Constants.START_LON, Integer.parseInt(et[0].getText().toString()));
                    bundle.putInt(Constants.START_LAT, Integer.parseInt(et[1].getText().toString()));
                    bundle.putBoolean("visible", cb.isChecked());
                    callback.onDialogSet(bundle);
                }
            });

            Button btn_cancel = (Button) view.findViewById(R.id.btn_add_via_cancel);
            btn_cancel.setOnClickListener(v -> callback.onDialogCancel());
        }

        @Override
        public Bundle onSaveInstanceState() {
            Bundle state = super.onSaveInstanceState();
            state.putString(Constants.START_LON, et[0].getText().toString());
            state.putString(Constants.START_LAT, et[1].getText().toString());
            return state;
        }

        @Override
        public void onRestoreInstanceState(Bundle savedInstanceState) {
            super.onRestoreInstanceState(savedInstanceState);
            et[0].setText(savedInstanceState.getString(Constants.START_LON));
            et[1].setText(savedInstanceState.getString(Constants.START_LAT));
        }


    }

}
