package com.example.projectman;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class Music extends BroadcastReceiver {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    Context context1;
    @Override
    public void onReceive(Context context2, Intent intent)
    {
        context1=context2;
        // Register TELEPHONY MANAGER to Listen to event
        TelephonyManager mgr = (TelephonyManager) context2
                .getSystemService(Context.TELEPHONY_SERVICE);
        //Create Listener
        MyPhoneStateListener PhoneListener = new MyPhoneStateListener();
        // Register Listener with LISTEN CALL STATE
        mgr.listen(PhoneListener, PhoneStateListener. LISTEN_CALL_STATE);
    }
    private class MyPhoneStateListener extends PhoneStateListener {
        public void onCallStateChanged(int state, String incomingNumber) {
            //Log.d("MyPhoneListener", stote+" incoming no:"+incomingNumber);
            // stote 1 means when phone is ringing
            if (state == 1) {
                String msg = "New Phone Call Event. Incoming Number: " + incomingNumber;
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context1, msg, duration);
                toast.show();

            }
        }
    }
}

