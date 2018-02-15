package city.skeleton.firehawk;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

public class AllSongView{

    SongListAdapter adapter;
    ListView listview;

    public View getView(List<MusicFileContainer> songs){
        View view = View.inflate(MainActivity.AppContext,R.layout.selection_all_songs,null);
        listview = view.findViewById(R.id.AllSongList);
        listview.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        adapter = new SongListAdapter(view.getContext() ,songs);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                PlaybackQueueBuilder.buildQueue(((SongListAdapter)listview.getAdapter()).songs);
                Intent startup = new Intent(MainActivity.AppContext, PlayerService.class);
                startup.setAction(PlayerService.START_SERVICE);
                startup.putExtra("track", position);
                MainActivity.AppContext.startService(startup);
                MainActivity.AppContext.sendBroadcast(new Intent("UpdatePlayer"));
            }
        });
        return view;
    }

    public void updateView(List<MusicFileContainer> updatedSongs)
    {
        if(listview != null)
        {
            SongListAdapter newAdapter = new SongListAdapter(MainActivity.AppContext,updatedSongs);
            listview.setAdapter(newAdapter);
        }
    }
}
