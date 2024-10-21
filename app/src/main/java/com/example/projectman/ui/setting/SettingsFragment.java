package com.example.projectman.ui.setting;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.projectman.MyService;
import com.example.projectman.R;

public class SettingsFragment extends Fragment {
    private MyService myService;
    private boolean isBound = false;
    private Switch switchMusic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Initialize the switch
        switchMusic = view.findViewById(R.id.switchMusic);

        // Bind the service
        Intent intent = new Intent(getActivity(), MyService.class);
        getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        // Set listener for the switch
        switchMusic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isBound) {
                if (isChecked) {
                    myService.startMusic();  // Start music when switch is ON
                } else {
                    myService.stopMusic();   // Stop music when switch is OFF
                }
            } else {
                Toast.makeText(getActivity(), "Service is not bound", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            myService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };
}


