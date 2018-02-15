package city.skeleton.firehawk;

import android.Manifest;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.ViewAnimator;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    public static final int PLAYER_ID = 0, ALL_SONGS_ID = 1, ARTIST_ID = 2, ALBUMS_ID = 3, SUBLIST_ID = 4, SUB_SUBLIST_ID = 5, SETTINGS_ID = 6;

    private boolean inSubView = false;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    public static Context AppContext;
    public static String PREFIX = "CITY.SKELETON.FIREHAWK";
    public Activity AppActivity;

    public static  PlayerService playerService;
    public static MusicLibraryStaticCache cache;
    public ViewAnimator viewAnimator;
    public static int currentView = 0;
    View playerView, songSelectorView, artistSelectorView, albumSelectorView, subView, subSubView, settingsView;
    ImageButton playerButton, allSongButton, allAlbumButton, allArtistButton, settingsButton;
    public AllSongView songView, subSongView;
    public AllAlbumView albumView, subAlbumView;
    public AllArtistView artistView;
    public SimpleExoPlayerView exoPayerView;
    public BroadcastReceiver receiver;

    public HashMap<String,Locale> localeHashMap;

    private float downX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()){
                    case("UpdateAllLists"):
                        songView.updateView(cache.getSongList());
                        albumView.updateView(cache.getAlbumsList());
                        artistView.updateView(cache.getArtistList());
                        break;
                    case("UpdatePlayer"):
                        exoPayerView = AppActivity.findViewById(R.id.player);
                        exoPayerView.setPlayer(playerService.getPlayer());
                        changeView(PLAYER_ID);
                        inSubView = false;
                        playerView.invalidate();
                        break;
                    case("AlbumClicked"):
                        changeView(SUB_SUBLIST_ID);
                        if(inSubView)
                            subSongView.updateView(subAlbumView.getAlbumToLoad().getSongs());
                        else
                            subSongView.updateView(albumView.getAlbumToLoad().getSongs());
                        inSubView = true;
                        break;
                    case("ArtistClicked"):
                        changeView(SUBLIST_ID);
                        subAlbumView.updateView(artistView.getArtistToLoad().getArtistAlbums());
                        inSubView = true;
                        break;
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("UpdateAllLists");
        filter.addAction("UpdatePlayer");
        filter.addAction("AlbumClicked");
        filter.addAction("ArtistClicked");

        this.registerReceiver(receiver,filter);
        AppContext = this.getApplicationContext();
        if(AppActivity == null)
            AppActivity = this;
        //make sure we're not going to crash
        checkPermissions();

        setContentView(R.layout.activity_main);

        if(cache == null)
        {
            cache = new MusicLibraryStaticCache();
            cache.prepareAllSongs();
        }

        songView = new AllSongView();
        albumView = new AllAlbumView();
        artistView = new AllArtistView();
        subAlbumView = new AllAlbumView();
        subSongView = new AllSongView();

        playerService = new PlayerService();//configure player
        exoPayerView = AppActivity.findViewById(R.id.player);

        startViewAnimator();
        setupControls();
        setupOptionsSpinner();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume(){
        super.onResume();
        playerService.restartPlayer();
    }

    public void checkPermissions()
    {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode){
            case REQUEST_EXTERNAL_STORAGE:
                if(!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED))
                {
                    System.exit(0);
                }
        }

    }

    public void startViewAnimator(){
        viewAnimator = findViewById(R.id.Views);
        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);

        viewAnimator.setInAnimation(in);
        viewAnimator.setOutAnimation(out);
        viewAnimator.setAnimateFirstView(true);

        LayoutInflater inflater = LayoutInflater.from(this);
        playerView = inflater.inflate(R.layout.player_view, null, false);
        viewAnimator.addView(playerView);
        songSelectorView = songView.getView(cache.getSongList());
        viewAnimator.addView(songSelectorView);

        //add artist view
        artistSelectorView = artistView.getView(cache.getArtistList());
        viewAnimator.addView(artistSelectorView);

        //add album view
        albumSelectorView = albumView.getView(cache.getAlbumsList());
        viewAnimator.addView(albumSelectorView);

        //add subview 1
        subView = subAlbumView.getView(new ArrayList<>());
        viewAnimator.addView(subView);

        //add subview 2
        subSubView = subSongView.getView(new ArrayList<>());
        viewAnimator.addView(subSubView);

        settingsView = inflater.inflate(R.layout.settings_view,null,false);
        viewAnimator.addView(settingsView);
    }

    public  void changeView(int view)
    {
        currentView = view;
        viewAnimator.setDisplayedChild(view);
    }

    public void setupControls(){
        playerButton = findViewById(R.id.playerButton);
        allSongButton = findViewById(R.id.allSongButton);
        allAlbumButton = findViewById(R.id.allAlbumButton);
        allArtistButton = findViewById(R.id.allArtistButton);
        settingsButton = findViewById(R.id.settingsButton);
        playerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentView != MainActivity.PLAYER_ID)
                    viewAnimator.setDisplayedChild(MainActivity.PLAYER_ID);
                currentView = MainActivity.PLAYER_ID;
                inSubView = false;
            }
        });
        allSongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentView != MainActivity.ALL_SONGS_ID)
                    viewAnimator.setDisplayedChild(MainActivity.ALL_SONGS_ID);
                currentView = MainActivity.ALL_SONGS_ID;
                inSubView = false;
            }
        });
        allAlbumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentView != MainActivity.ALBUMS_ID)
                    viewAnimator.setDisplayedChild(MainActivity.ALBUMS_ID);
                currentView = MainActivity.ALBUMS_ID;
                inSubView = false;
            }
        });
        allArtistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentView != MainActivity.ARTIST_ID)
                    viewAnimator.setDisplayedChild(MainActivity.ARTIST_ID);
                currentView = MainActivity.ARTIST_ID;
                inSubView = false;
            }
        });
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentView != MainActivity.SETTINGS_ID)
                    viewAnimator.setDisplayedChild(MainActivity.SETTINGS_ID);
                currentView = MainActivity.SETTINGS_ID;
                inSubView = false;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){

        int action = event.getAction();
        switch (action){
            case (MotionEvent.ACTION_DOWN):
                downX = event.getX();
                return true;
            case(MotionEvent.ACTION_UP):
                float upX = event.getX();
                float delta = downX - upX;
                if(Math.abs(delta) > 50)
                {
                    if(upX < downX)
                    {
                        if(currentView < 1) {
                            viewAnimator.showNext();
                            currentView++;
                        }

                    }

                    else
                    {
                        if(currentView > 0) {
                            viewAnimator.showPrevious();
                            currentView--;
                        }
                    }
                }
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    public void setupOptionsSpinner()
    {
        Spinner spinner = findViewById(R.id.localeSpinner);
        Field[] fields = Locale.class.getFields();
        localeHashMap = new HashMap<>();
        for(Field field : fields)
        {
            if(field.getType() == Locale.class)
            {
                try{
                    localeHashMap.put(field.getName(), (Locale) field.get(null));
                }
                catch (Exception e)
                {

                }
            }
        }
        String[] temp = new String[localeHashMap.size()];
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item,
                        localeHashMap.keySet().toArray(temp));
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String key = parent.getItemAtPosition(position).toString();
                cache.getSortService().updateLocale(localeHashMap.get(key));
                cache.resortAll();
                MainActivity.AppContext.sendBroadcast(new Intent("UpdateAllLists"));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

}
