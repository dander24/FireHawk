package city.skeleton.firehawk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class SongListAdapter extends ArrayAdapter{

    List<MusicFileContainer> songs;
    Context context;

    public SongListAdapter(Context context, List<MusicFileContainer> songs){

        super(context,R.layout.song_list_view, songs.toArray());
        this.songs = songs;
        this.context = context;
    }

    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(R.layout.song_list_view,null,true);
        TextView titleField = (TextView) rowView.findViewById(R.id.ALL_Title);
        TextView durationField = (TextView) rowView.findViewById(R.id.ALL_duration);
        TextView artistField = (TextView) rowView.findViewById(R.id.ALL_Artist);

        titleField.setText(songs.get(position).getTitle());
        durationField.setText(String.format("%d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(Integer.parseInt(songs.get(position).getDuration())),
                TimeUnit.MILLISECONDS.toSeconds(Integer.parseInt(songs.get(position).getDuration())) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(Integer.parseInt(songs.get(position).getDuration())))
        ));
        artistField.setText(songs.get(position).getArtist());

        return rowView;
    }
}
