package city.skeleton.firehawk;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//special case for album artists, pick up single songs out of compilation albums
public class ArtistContainer implements Serializable {

    private String name;
    private Map<String,AlbumContainer> personalAlbumList;

    public ArtistContainer(String Name)
    {
        name = Name;
        personalAlbumList = new HashMap<>();
    }

    public void addSong(MusicFileContainer song){
        if(song.getAlbum() == null)
        {
            AlbumContainer container = personalAlbumList.get("unknown");
            if(container == null){ //no existing album
                container = new AlbumContainer(song.getAlbum(), song.getArtist(),false);
                container.addSong(song);
                personalAlbumList.put("unknown",container);
            } else { //album in list
                container.addSong(song);
            }
        } else {
            String key = song.getAlbum();
            AlbumContainer container = personalAlbumList.get(key);
            if(container == null){ //no existing album
                container = new AlbumContainer(song.getAlbum(), song.getArtist(),false);
                container.addSong(song);
                personalAlbumList.put(key,container);
            } else { //album in list
                container.addSong(song);
            }
        }
    }

    public String getName(){ return name; }
    public List<AlbumContainer> getArtistAlbums(){
        return personalAlbumList.values().stream().collect(Collectors.toList());
    }
}
