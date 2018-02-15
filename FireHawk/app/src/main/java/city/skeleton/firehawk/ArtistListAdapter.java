package city.skeleton.firehawk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ArtistListAdapter extends ArrayAdapter {

    List<ArtistContainer> artists;
    Context context;

    public ArtistListAdapter(Context context, List<ArtistContainer> artists){
        super(context,R.layout.artist_list_view,artists.toArray());
        this.artists = artists;
        this.context = context;
    }

    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(R.layout.artist_list_view,null);
        TextView artist = rowView.findViewById(R.id.ArtistName);
        artist.setText(artists.get(position).getName());
        return rowView;
    }

    public void swapArtists(List<ArtistContainer> artists){
        this.artists = artists;
        notifyDataSetChanged();
    }
}
