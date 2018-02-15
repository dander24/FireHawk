package city.skeleton.firehawk;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

public class AllAlbumView {

    AlbumContainer albumToLoad;
    AlbumListAdapter adapter;
    ListView listView;

    public AlbumContainer getAlbumToLoad() {return albumToLoad;}

    public View getView(List<AlbumContainer> albums)
    {

        View view = View.inflate(MainActivity.AppContext,R.layout.all_album_view,null);
        listView = view.findViewById(R.id.AllAlbumView);
        listView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        adapter = new AlbumListAdapter(view.getContext(), albums);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                albumToLoad = ((AlbumListAdapter)listView.getAdapter()).albums.get(position);
                MainActivity.AppContext.sendBroadcast(new Intent("AlbumClicked"));
        }});
        return view;
    }

    public void updateView(List<AlbumContainer> updatedAlbums)
    {
        if(listView != null)
        {
           listView.setAdapter(new AlbumListAdapter(MainActivity.AppContext, updatedAlbums));
        }
    }
}
