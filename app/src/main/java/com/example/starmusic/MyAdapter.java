package com.example.starmusic;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends ArrayAdapter<File> {
  Context context;
  ArrayList<File> songs;
  //String[] songname;
    public MyAdapter(@NonNull Context context, int resource, @NonNull ArrayList<File> objects) {
        super(context, resource, objects);
        this.context=context;
        this.songs=objects;
//        songname=new String[songs.size()];
//        for(int i=0;i< songs.size();i++){
//            songname[i]=songs.get(i).getName().replace(".m4a","");
//        }
    }

    @Nullable
    @Override
    public File getItem(int position) {
        return songs.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView=LayoutInflater.from(getContext()).inflate(R.layout.songadapter,parent,false);
        TextView sno=convertView.findViewById(R.id.sno);
        sno.setText(String.valueOf(position+1));
        TextView sname=convertView.findViewById(R.id.songname);
        sname.setText(getItem(position).getName());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentsong=getItem(position).getName();
                                Intent intent=new Intent(context,musicplay.class);
                                intent.putExtra("songs",songs);
                                intent.putExtra("currents",currentsong);
                                intent.putExtra("position",position);
                               context.startActivity(intent);
            }
        });
        return convertView;
    }

}
