package city.skeleton.firehawk;

import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class AlbumContainer implements Serializable {

    private List<MusicFileContainer> songs;
    private String name;
    private String artist;
    private boolean isComp;

    public AlbumContainer(String albumName, String albumArtist, boolean comp){
        songs = new ArrayList<>();
        name = albumName;
        artist = albumArtist;
        isComp = comp;
    }

    public void addSong(MusicFileContainer song) {
        songs.add(song);
    }

    public void sortAlbumByTrack(){
        songs.sort(getTrackComparator());
    }

    public void sortAlbumByTrackName(Locale locale){
        songs.sort(getTrackNameComparator(locale));
    }

    public List<MusicFileContainer> getSongs(){
        return songs;
    }

    public int getTrackCount(){
        return songs.size();
    }

    public String getName() { return name; }
    public String getArtist() { return artist; }

    private Comparator<MusicFileContainer> getTrackComparator(){
        return  new Comparator<MusicFileContainer>() {
            @Override
            public int compare(MusicFileContainer o1, MusicFileContainer o2) {
                Integer track1;
                Integer track2;
                try{
                     track1 = Integer.parseInt(o1.getTrack().substring(0, o1.getTrack().lastIndexOf("/")));
                     track2 = Integer.parseInt(o2.getTrack().substring(0, o2.getTrack().lastIndexOf("/")));
                }
                catch (Exception e){
                    return 0;
                }
                return Integer.compare(track1,track2);
            }
        };
    }

    private Comparator<MusicFileContainer> getTrackNameComparator(Locale locale){
        return  new Comparator<MusicFileContainer>() {
            private Collator localeCollator = Collator.getInstance(locale);
            @Override
            public int compare(MusicFileContainer o1, MusicFileContainer o2) {
                String s1 = o1.getTitle();
                String s2 = o1.getTitle();
                if(s1 == null || s2 == null)
                    return  s1 == null ? -1 : 1;
                return localeCollator.compare(s1,s2);
            }
        };
    }


}
