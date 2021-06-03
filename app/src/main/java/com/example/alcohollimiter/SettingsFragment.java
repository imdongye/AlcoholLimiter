package com.example.alcohollimiter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {
    public static final String PREFS_NAME = "lim_dongye";
    public static final String P_NAME = "name";
    public static final String P_HEIGHT = "height";
    public static final String P_WEIGHT = "weight";
    public static final String P_SOJU_CAP = "soju_cap";
    public static final String P_PHONE = "phone";
    public static final String P_ISMAN = "isman";
    String[] gender_items = {"남자", "여자"};
    View rootView;
    Context myContext;


    public SettingsFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        myContext = container.getContext();
        Spinner spinner = (Spinner) rootView.findViewById(R.id.settings_gender_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(myContext, android.R.layout.simple_spinner_item, gender_items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        {
            SharedPreferences sharedPref = myContext.getSharedPreferences(SettingsFragment.PREFS_NAME, 0);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(P_NAME, "임동예");
            editor.putFloat(P_HEIGHT, 176.5f);
            editor.putFloat(P_WEIGHT, 71.0f);
            editor.putFloat(P_SOJU_CAP, 2.0f);
            editor.putString(P_PHONE, "010-9368-5763");
            editor.putBoolean(P_ISMAN, true);
            editor.commit();
            Log.i("songjo","set pref first");
        }
        return rootView;
    }
}