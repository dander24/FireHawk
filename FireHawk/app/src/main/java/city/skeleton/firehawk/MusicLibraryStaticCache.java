package city.skeleton.firehawk;


import android.content.Intent;
import android.os.AsyncTask;
import java.util.ArrayList;
import java.util.List;


public class MusicLibraryStaticCache {

    private List<MusicFileContainer> allSongsList;
    private List<AlbumContainer> albumsList;
    private List<ArtistContainer> artistList;
    //artist list

    private static MusicLibraryStaticCacheSortService sortService;
    private static FireMediaScanner mediaScanner;

    public MusicLibraryStaticCache(){
        sortService = new MusicLibraryStaticCacheSortService();
        mediaScanner = new FireMediaScanner();
        allSongsList = new ArrayList<>();
        albumsList = new ArrayList<>();
    }

    public void prepareAllSongs(){
        mediaScanner.loadExistingLibrary("ALL_MUSIC", allSongsList);
        albumsList = sortService.createSortedListAlbums(allSongsList);
        artistList = sortService.createSortedListArtists(allSongsList);
        new LibraryLoader().execute();
        return;
    }

    public List<MusicFileContainer> getSongList() { return allSongsList; }
    public List<AlbumContainer> getAlbumsList() { return albumsList; }
    public List<ArtistContainer> getArtistList() { return artistList; }
    public MusicLibraryStaticCacheSortService getSortService() { return sortService; }

    public void resortAll(){
        sortService.createSortedListAllSongs(allSongsList);
        albumsList = sortService.createSortedListAlbums(allSongsList);
        artistList = sortService.createSortedListArtists(allSongsList);
    }

    private class LibraryLoader extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            if (allSongsList.size() == 0) {
                mediaScanner.cleanLoadAll(allSongsList,sortService);
            } else {
                mediaScanner.runFullScan(allSongsList,sortService);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            MainActivity.AppContext.sendBroadcast(new Intent("UpdateAllLists"));
        }
    }
}
