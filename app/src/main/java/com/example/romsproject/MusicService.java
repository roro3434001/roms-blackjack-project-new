package com.example.romsproject;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import android.os.Binder;

public class MusicService extends Service {

    private MediaPlayer mediaPlayer;

    // Binder for the MusicService to allow binding with the service
    private final IBinder binder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder; // Return the binder to allow binding
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("MusicService", "MusicService is starting...");

        mediaPlayer = MediaPlayer.create(this, R.raw.cozycoffeehouse);

        if (mediaPlayer == null) {
            Log.e("MusicService", "Error: MediaPlayer initialization failed");
        } else {
            // Set the audio to loop continuously until explicitly stopped
            mediaPlayer.setLooping(true);

            // Start playing the audio
            mediaPlayer.start();
            Log.d("MusicService", "Music started playing.");
        }

        // Set up the foreground service with a notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "music_channel", "Music Service", NotificationManager.IMPORTANCE_LOW);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, "music_channel")
                    .setContentTitle("Music Service")
                    .setContentText("Music is playing...")
                    .setSmallIcon(android.R.drawable.ic_media_play)
                    .build();

            // Start the service in the foreground
            startForeground(1, notification);
            Log.d("MusicService", "Started foreground service");
        }
    }

    // Method to set the volume of the music
    public void setVolume(float volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume, volume);
            Log.d("MusicService", "Volume set to: " + volume);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            Log.d("MusicService", "Music stopped.");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Ensures the service stays running even when the app goes into the background
        return START_STICKY;
    }

    // LocalBinder class to provide access to MusicService for binding
    public class LocalBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }
}
