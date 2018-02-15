package city.skeleton.firehawk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class AlbumListAdapter extends ArrayAdapter{

    List<AlbumContainer> albums;
    Context context;

    public AlbumListAdapter(Context context, List<AlbumContainer> albums){
        super(context,R.layout.album_list_view,albums.toArray());
        this.albums = albums;
        this.context = context;
    }

    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(R.layout.album_list_view,null,true);
        TextView albumName = rowView.findViewById(R.id.ALBUM_Name);
        TextView artistName = rowView.findViewById(R.id.ALBUM_Artist);
        albumName.setText(albums.get(position).getName());
        artistName.setText(albums.get(position).getArtist());
        return rowView;
    }

    public void swapAlbums(List<AlbumContainer> albums){
        this.albums = albums;
        notifyDataSetChanged();
    }
}
