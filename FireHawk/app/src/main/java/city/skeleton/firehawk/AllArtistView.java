package city.skeleton.firehawk;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

public class AllArtistView {
    ArtistContainer artistToLoad;
    ArtistListAdapter adapter;
    ListView listView;

    public ArtistContainer getArtistToLoad() { return  artistToLoad; }

    public View getView(List<ArtistContainer> artists)
    {
        View view = View.inflate(MainActivity.AppContext,R.layout.all_artist_view, null);
        listView = view.findViewById(R.id.AllArtistView);
        listView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        adapter = new ArtistListAdapter(view.getContext(),artists);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                artistToLoad = ((ArtistListAdapter)listView.getAdapter()).artists.get(position);
                MainActivity.AppContext.sendBroadcast(new Intent("ArtistClicked"));
            }
        });
        return  view;
    }

    public void updateView(List<ArtistContainer> updatedArtists)
    {
        if(listView != null)
        {
            listView.setAdapter(new ArtistListAdapter(MainActivity.AppContext,updatedArtists));
        }
    }
}
