package com.sygic.example.ipcdemo3d.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.sygic.example.ipcdemo3d.R;
import com.sygic.example.ipcdemo3d.SdkApplication;
import com.sygic.sdk.remoteapi.ApiTts;
import com.sygic.sdk.remoteapi.exception.GeneralException;

public class SoundFragment extends Fragment {
    private EditText m_etTts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final SharedPreferences prefs = getActivity().getSharedPreferences("SygicPrefs", Context.MODE_PRIVATE);
        boolean bMutex = prefs.getBoolean("mutex", false);

        View rootView = inflater.inflate(R.layout.fragment_sound, container, false);

        m_etTts = (EditText) rootView.findViewById(R.id.et_add_tts_text);

        Button btnPlayTTS = (Button) rootView.findViewById(R.id.b_play_tts);
        btnPlayTTS.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    if (!m_etTts.getText().toString().isEmpty())
                        ApiTts.playSound(m_etTts.getText().toString(), SdkApplication.MAX);
                } catch (GeneralException e) {
                    e.printStackTrace();
                }
            }
        });

        return rootView;
    }

}
