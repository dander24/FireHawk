package city.skeleton.firehawk;

import android.net.Uri;

import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.DynamicConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import java.io.File;
import java.util.List;

public class PlaybackQueueBuilder {

    public static DynamicConcatenatingMediaSource currentQueue = new DynamicConcatenatingMediaSource();
    public static List<MusicFileContainer> currentSongs = null;

    public static void buildQueue(List<MusicFileContainer> songs){
        currentSongs = songs;
        currentQueue = new DynamicConcatenatingMediaSource();
        loadSongList(songs);
    }

    private static void loadSongList(List<MusicFileContainer> songs){
        for(MusicFileContainer song : songs){
            currentQueue.addMediaSource(buildMediaSource(Uri.fromFile(new File(song.get_id()))));
        }
    }

    private static MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource(uri,
                new DefaultDataSourceFactory(MainActivity.AppContext, "ua"),
                new DefaultExtractorsFactory(), null, null);
    }
}
