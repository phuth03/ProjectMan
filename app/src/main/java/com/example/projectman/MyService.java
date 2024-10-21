package com.example.projectman;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

public class MyService extends Service {
    private final IBinder binder = new LocalBinder();
    private MediaPlayer player;
    private boolean isRunning = false;

    public class LocalBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        player = MediaPlayer.create(this, R.raw.mono);
        player.setLooping(true);
    }

    public void startMusic() {
        if (!isRunning) {
            player.start();
            isRunning = true;
            Toast.makeText(this, "Music Started", Toast.LENGTH_SHORT).show();
        }
    }

    public void stopMusic() {
        if (isRunning) {
            player.pause();
            isRunning = false;
            Toast.makeText(this, "Music Stopped", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.stop();
            player.release();
        }
    }
}
