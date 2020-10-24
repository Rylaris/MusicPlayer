package com.example.musicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.musicplayer.R;
import com.example.musicplayer.MyMusic;

import java.text.SimpleDateFormat;
import java.util.List;

public class MusicAdapter extends BaseAdapter {
    private List<MyMusic> musics;
    private Context context;
    private LayoutInflater inflater;

    public MusicAdapter(List<MyMusic> musics, Context context) {
        this.musics = musics;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return musics == null ? 0 : musics.size();
    }

    @Override
    public MyMusic getItem(int position) {
        return musics == null ? null : musics.get(position);
    }

    @Override
    public long getItemId(int position) {
        return musics == null ? 0 : position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.list_item, null);
            viewHolder.name = view.findViewById(R.id.tv_name);
            viewHolder.author = view.findViewById(R.id.tv_author);
            viewHolder.duration = view.findViewById(R.id.tv_duration);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.name.setText(musics.get(position).getName());
        viewHolder.author.setText(musics.get(position).getAuthor());
        viewHolder.duration.setText(getDuration(musics.get(position).getDuration()));
        return view;
    }

    private String getDuration(long duration) {
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        return format.format(duration);
    }

    class ViewHolder {
        ImageView img;
        TextView name;
        TextView author;
        TextView duration;
    }
}
