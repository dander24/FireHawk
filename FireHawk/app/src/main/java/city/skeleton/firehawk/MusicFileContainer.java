package city.skeleton.firehawk;

import android.media.MediaMetadataRetriever;
import android.support.annotation.WorkerThread;
import android.util.Log;

import java.io.Serializable;

public class MusicFileContainer implements Serializable {
    private String title;
    private String album;
    private String albumArtist;
    private String artist;
    private String composer;
    private String track;
    private String year;
    private String duration;
    private String disk;
    private String _id;


    @WorkerThread
    public MusicFileContainer(MediaMetadataRetriever retriever, String uri) {
        title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        albumArtist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST);
        composer = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER);
        track = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER);
        year = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR);
        duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        disk = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DISC_NUMBER);
        _id =  uri;
        Log.d(MainActivity.PREFIX, "MusicFileContainer: " + title);
    }

    private String getOrUnknown(String string) { return string != null ? string : "unknown";}

    public String getTitle() {
        return getOrUnknown(title);
    }

    public String getAlbum() {
        return getOrUnknown(album);
    }

    public String getArtist() {
        return getOrUnknown(artist);
    }

    public String getComposer() {
        return getOrUnknown(composer);
    }

    public String getTrack() {
        return getOrUnknown(track);
    }

    public String getYear() {
        return getOrUnknown(year);
    }

    public String getDuration() {
        return getOrUnknown(duration);
    }

    public String getAlbumArtist() {
        return getOrUnknown(albumArtist);
    }

    public String get_id() {
        return _id;
    }

    public String compString(){
        return title +
        album +
        albumArtist +
        artist +
        composer +
        track +
        year +
        duration +
        disk +
        _id;
    }

    @Override
    public boolean equals(Object obj){
        if(obj == this)
            return true;
        if(obj.getClass() != MusicFileContainer.class)
            return false;

        MusicFileContainer other = (MusicFileContainer) obj;
        return (this.compString() == other.compString());
    }
}
