package city.skeleton.firehawk;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.WorkerThread;
import android.util.Log;

import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class MusicLibraryStaticCacheSortService {

    private static List<Locale> LocaleList;
    private static Locale currentLocale;
    private SharedPreferences sharedPreferences;


    public MusicLibraryStaticCacheSortService(){
        LocaleList = Arrays.asList(Locale.getAvailableLocales());

        //try to load selected locale (or default to current)
       sharedPreferences = MainActivity.AppContext.getSharedPreferences(
                MainActivity.PREFIX+"OPTIONS", Context.MODE_PRIVATE);
       String localeName = sharedPreferences.getString("CURRENT_LOCALE", Locale.getDefault().toLanguageTag());
       currentLocale = Locale.forLanguageTag(localeName);
    }

    public MusicLibraryStaticCacheSortService(Locale locale){
        LocaleList = Arrays.asList(Locale.getAvailableLocales());

        //try to load selected locale (or default to current)
        sharedPreferences = MainActivity.AppContext.getSharedPreferences(
                MainActivity.PREFIX+"OPTIONS", Context.MODE_PRIVATE);
        String localeName = sharedPreferences.getString("CURRENT_LOCALE", Locale.getDefault().toLanguageTag());
    }

    public Locale getCurrentLocale(){
        return currentLocale;
    }

    public void updateLocale(Locale locale){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("CURRENT_LOCALE", locale.toLanguageTag());
        editor.commit();
    }

    private Comparator<MusicFileContainer> getSongNameComparator(Locale locale){
        return new Comparator<MusicFileContainer>() {
            private Collator localeCollator = Collator.getInstance(locale);
            @Override
            public int compare(MusicFileContainer o1, MusicFileContainer o2) {
                int comp = safeStringCompare(o1.getTitle(),o2.getTitle(),localeCollator);
                if(comp == 0)
                {
                    comp = safeStringCompare(o1.getArtist(), o2.getArtist(),localeCollator);
                    if(comp == 0)
                    {
                        comp = safeStringCompare(o1.getAlbum(), o2.getAlbum(),localeCollator);
                    }
                }
                return comp;
            }
        };
    }

    @WorkerThread
    private int safeStringCompare(String s1, String s2, Collator collator){
        if(s1 == null || s2 == null)
            return  s1 == null ? -1 : 1;
        return collator.compare(s1,s2);
    }

    private Comparator<AlbumContainer> getAlbumNameComparator(Locale locale){
        return new Comparator<AlbumContainer>() {
            private Collator localeCollator = Collator.getInstance(locale);
            @Override
            public int compare(AlbumContainer o1, AlbumContainer o2) {
                int comp = safeStringCompare(o1.getName(), o2.getName(),localeCollator);
                if (comp == 0) {
                    comp = safeStringCompare(o1.getArtist(), o2.getArtist(), localeCollator);
                }
                return comp;
            }
        };
    }

    private Comparator<ArtistContainer> getArtistNameComparator(Locale locale){
        return new Comparator<ArtistContainer>() {
            private Collator localeCollator = Collator.getInstance(locale);
            @Override
            public int compare(ArtistContainer o1, ArtistContainer o2) {
                return safeStringCompare(o1.getName(), o2.getName(),localeCollator);
            }
        };
    }



    public void createSortedListAllSongs(List<MusicFileContainer> songs){
        songs.sort(getSongNameComparator(currentLocale));
    }

    public List<ArtistContainer> createSortedListArtists(List<MusicFileContainer> songs) {
        HashMap<String, ArtistContainer> artistMap = new HashMap<>();

        ArtistContainer unknown = new ArtistContainer("unknown");
        for (MusicFileContainer song : songs) {
            if (song.getArtist() == null) {
                unknown.addSong(song);
            } else {
                ArtistContainer container = artistMap.get(song.getArtist());
                if (container == null) {
                    container = new ArtistContainer(song.getArtist());
                    container.addSong(song);
                    artistMap.put(song.getArtist(), container);
                } else {
                    container.addSong(song);
                }
            }
        }
        List<ArtistContainer> artistList = artistMap.values().stream().collect(Collectors.toList());
        for (ArtistContainer artist : artistList) {
            for (AlbumContainer album : artist.getArtistAlbums()) {
                album.sortAlbumByTrackName(currentLocale);
            }
        }
        artistList.sort(getArtistNameComparator(currentLocale));
        return artistList;
    }

    public List<AlbumContainer> createSortedListAlbums(List<MusicFileContainer> songs){
        HashMap<String,AlbumContainer> albumMap = new HashMap<>();
        //special container for things with missing tags
        AlbumContainer unknown = new AlbumContainer("unknown", "unknown", true);
        for(MusicFileContainer song: songs){
            if(song.getAlbum() == null)
            {
                unknown.addSong(song);
            } else {
                String key = song.getAlbum() +
                        ( (song.getAlbumArtist() == null) ? song.getArtist() : song.getAlbumArtist() );
                AlbumContainer container = albumMap.get(key);
                if(container == null){
                    container = new AlbumContainer(song.getAlbum(),
                            (song.getAlbumArtist() == null) ? song.getArtist() : song.getAlbumArtist(),
                            (song.getAlbumArtist() != null));
                    container.addSong(song);
                    albumMap.put(key,container);
                } else { //album entry already exists
                    container.addSong(song);
                }
            }
        }

        List<AlbumContainer> albumList = albumMap.values().stream().collect(Collectors.toList());
        for(AlbumContainer album: albumList)
        {
            album.sortAlbumByTrack();
        }
        if(unknown.getTrackCount() != 0)
            albumList.add(unknown);

        albumList.sort(getAlbumNameComparator(currentLocale));
        return albumList;
    }

    public void testAllSongSort(List<MusicFileContainer> songs, Locale locale){
        Log.d("sort+", "UNSORTED");
        for(MusicFileContainer song: songs){
            Log.d("sort+", song.getTitle());
        }
        updateLocale(locale);
        createSortedListAllSongs(songs);
        Log.d("sort+", "SORTED");
        for(MusicFileContainer song: songs){
            Log.d("sort+", song.getTitle());
        }
    }


    public void testAlbumSort(List<MusicFileContainer> songs, Locale locale){
        Log.d("sort+", "UNSORTED");
        for(MusicFileContainer song: songs){
            Log.d("sort+", song.get_id());
        }
        updateLocale(locale);

        List<AlbumContainer> sorted = createSortedListAlbums(songs);
        Log.d("sort+", "SORTED");
        for(AlbumContainer album: sorted){
            Log.d("sort+", album.getName() + " -- " + album.getArtist());
            for(MusicFileContainer song: album.getSongs())
            {
                Log.d("sort+", "\t " + song.getTitle());
            }
            Log.d("sort+", "\\\\\\\\");
        }
    }

    public void testArtistSort(List<MusicFileContainer> songs, Locale locale){
        updateLocale(locale);
        List<ArtistContainer> sorted = createSortedListArtists(songs);
        Log.d("sort+", "SORTED");
        for(ArtistContainer artist: sorted){
            Log.d("sort+", artist.getName() );
            for(AlbumContainer album: artist.getArtistAlbums()){
                Log.d("sort+", "\t" +album.getName() + " -- " + album.getArtist());
                for(MusicFileContainer song: album.getSongs())
                {
                    Log.d("sort+", "\t\t " + song.getTitle());
                }
                Log.d("sort+", "\\\\\\\\");
            }
        }
    }
}
