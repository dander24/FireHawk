package city.skeleton.firehawk;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;

public class PlayerService extends Service {

    static final String START_SERVICE = "0";
    static final String LOAD_LIBRARY = "1";

    static SimpleExoPlayer player;
    static boolean playWhenReady;
    static int currentWindow;
    static long playbackPosition;

    public SimpleExoPlayer getPlayer(){
        return player;
    }

    public PlayerService(){
        player = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(MainActivity.AppContext),
                new DefaultTrackSelector(),
                new DefaultLoadControl());
        player.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {


            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity() {
                if(PlaybackQueueBuilder.currentQueue == null || PlaybackQueueBuilder.currentSongs == null)
                    return;
                int track = player.getCurrentWindowIndex();

                Intent notificationIntent = new Intent(MainActivity.AppContext, MainActivity.class);
                PendingIntent pendingIntent =
                        PendingIntent.getActivity(MainActivity.AppContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                Notification notification =
                        new NotificationCompat.Builder(MainActivity.AppContext)
                                .setContentTitle(PlaybackQueueBuilder.currentSongs.get(track).getTitle())
                                .setContentText(PlaybackQueueBuilder.currentSongs.get(track).getArtist() + " -- " + PlaybackQueueBuilder.currentSongs.get(track).getAlbum())
                                .setSmallIcon(R.drawable.exo_controls_play)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setChannelId("fireHawk")
                                .build();
                NotificationManager mNotificationManager = (NotificationManager) MainActivity.AppContext.getSystemService(MainActivity.AppContext.NOTIFICATION_SERVICE);
                mNotificationManager.notify(3, notification);
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }
        });
    }



    public void initializePlayer() {
        if(player != null)
        {
            player.setPlayWhenReady(playWhenReady);
            player.seekTo(currentWindow,playbackPosition);
        }
    }

    public void restartPlayer(){
        releasePlayer();
        initializePlayer();
    }



    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
        }
    }

    @Override //spin up on bind
    public IBinder onBind(Intent intent){
        return null;
    }

    public void onCreate() {
        Log.d("SERVICE", "onCreate: DONE");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        switch (intent.getAction()){
            case START_SERVICE:
                releasePlayer();
                int track = intent.getIntExtra("track", 0);
                Intent notificationIntent = new Intent(this, MainActivity.class);
                PendingIntent pendingIntent =
                        PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                Notification notification =
                        new NotificationCompat.Builder(MainActivity.AppContext)
                                .setContentTitle(PlaybackQueueBuilder.currentSongs.get(track).getTitle())
                                .setContentText(PlaybackQueueBuilder.currentSongs.get(track).getArtist() + " -- " + PlaybackQueueBuilder.currentSongs.get(track).getAlbum())
                                .setSmallIcon(R.drawable.exo_controls_play)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setChannelId("fireHawk")
                                .build();


                initializePlayer();
                player.setPlayWhenReady(true);
                player.prepare(PlaybackQueueBuilder.currentQueue,true,true);
                player.seekTo(track,0);
                MainActivity.AppContext.sendBroadcast(new Intent("UpdatePlayer"));
                startForeground(3, notification);
                break;
            case LOAD_LIBRARY:

                break;
        }
        return START_NOT_STICKY;
    }


    public void onDestroy() {
        releasePlayer();
    }



}
