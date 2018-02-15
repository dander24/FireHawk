package city.skeleton.firehawk;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.WorkerThread;
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.google.android.exoplayer2.ExoPlayerLibraryInfo.TAG;

public class FireMediaScanner {

    private static ArrayList<String> contentDirectories = new ArrayList<>();
    private static String[] validExtensions = new String[] { //needs more details
            "mp3"
    };

    public void loadExistingLibrary(String name, List<MusicFileContainer> loadList) {
        try{
            loadLibraryList(MainActivity.AppContext, MainActivity.PREFIX+name, loadList);
            Log.d("mslc", "old list load ok");
        } catch (Exception e) {
            Log.d("mslc", e.getMessage());
            Log.d("mslc", "unable to load old list need to build new");
        }
    }

    public void cleanLoadAll(List<MusicFileContainer> allSongs,  MusicLibraryStaticCacheSortService sortService){
        contentDirectories.clear();
        contentDirectories.add(Environment.getExternalStoragePublicDirectory("Music").getAbsolutePath());
        //contentDirectories.add(new File("storage/A251-2569/Music").getAbsolutePath());
        buildMFCList(contentDirectories,allSongs);
        try {
            storeLibraryList(MainActivity.AppContext, MainActivity.PREFIX+"ALL_MUSIC", allSongs);
            sortService.createSortedListAllSongs(allSongs);
            Log.d("mslc", "saved library list safely");
        } catch (Exception e) {
            Log.d("mslc", e.getMessage());
            Log.d("mslc", "unable to save new library list");
        }
    }

    public void runFullScan(List<MusicFileContainer> allSongs, MusicLibraryStaticCacheSortService sortService){
        //debug
        contentDirectories.clear();
        contentDirectories.add(Environment.getExternalStoragePublicDirectory("Music").getAbsolutePath());

        //try and load the existing databases
        try {
            ArrayList<MusicFileContainer> updatedSongList = new ArrayList<>();
            buildMFCList(contentDirectories,updatedSongList);
            allSongs.clear();
            allSongs.addAll(updatedSongList);
            sortService.createSortedListAllSongs(allSongs);
            Log.d("mslc", "safely updated music list with changes");

        } catch (Exception e) { // post new list
            Log.d("mslc", e.getMessage());
            Log.d("mslc", "unable to load old list, using new built list");
        }

        try {
            storeLibraryList(MainActivity.AppContext, MainActivity.PREFIX+"ALL_MUSIC", allSongs);
            Log.d("mslc", "saved library list safely");
        } catch (Exception e) {
            Log.d("mslc", e.getMessage());
            Log.d("mslc", "unable to save new library list");
        }
    }


    private void buildMFCList(List<String> directories, List<MusicFileContainer> outputList){
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        for(String directory: directories)
        {
            recursiveListBuild( new File(directory).listFiles(getAudioFilter()), outputList);
        }
    }


    private void recursiveListBuild(File[] directory, List<MusicFileContainer> outputList){
        for(File f: directory)
        {
            if(f.isDirectory()){
                recursiveListBuild(f.listFiles(getAudioFilter()),outputList);
            } else {
                String[] input = {f.getAbsolutePath()};
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                try {
                    mediaMetadataRetriever.setDataSource(input[0]);
                    outputList.add(new MusicFileContainer(mediaMetadataRetriever, input[0]));
                } catch (RuntimeException e){
                    try{
                        Log.d(TAG, "trying different metadata model");
                        mediaMetadataRetriever.setDataSource(MainActivity.AppContext, Uri.parse(input[0]));
                        outputList.add(new MusicFileContainer(mediaMetadataRetriever, input[0]));
                    } catch (RuntimeException ex) {
                        Log.d(TAG, "model change failed, skipping");
                    }
                }
            }
        }
    }



    @WorkerThread
    public  void storeLibraryList(Context context, String filename, List<MusicFileContainer> containerList) throws Exception
    {
        FileOutputStream fileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

        objectOutputStream.writeInt(containerList.size());
        for (MusicFileContainer m: containerList)
        {
            objectOutputStream.writeObject(m);
        }
        objectOutputStream.close();
        fileOutputStream.close();

    }

    @WorkerThread
    public  void loadLibraryList(Context context, String filename, List<MusicFileContainer> containerList) throws Exception
    {
        FileInputStream fileInputStream = context.openFileInput(filename);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

        int size = objectInputStream.readInt();
        for(int i = 1; i < size; i++)
        {
            containerList.add((MusicFileContainer) objectInputStream.readObject());
        }
        objectInputStream.close();
        fileInputStream.close();
    }

    public  FileFilter getAudioFilter() {
        return  file -> {
            if(!file.isHidden() && file.canRead()){
                if(file.isDirectory())
                    return true;
                else {
                    String extension = getFileExtension(file);
                    if(extension != "")
                    {
                        for (String ext: validExtensions)
                        {
                            if(extension.equalsIgnoreCase(ext))
                                return true;
                        }
                    }
                }
            }
            return false;
        };
    }

    public  String getFileExtension(File file){
        String filename = file.getName();
        if(filename.lastIndexOf(".") > 0){
            return filename.substring(filename.lastIndexOf(".")+1);
        }
        else return "";
    }
}
