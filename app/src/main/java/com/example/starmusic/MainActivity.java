package com.example.starmusic;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
private ListView listView;
private String[] arr={"ram","syam","jay","ramendra","vivek"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Toast.makeText(MainActivity.this,"action is granted",Toast.LENGTH_SHORT).show();
                        ArrayList<File> songs=fetchSong(Environment.getExternalStorageDirectory());
                        String[] items=new String[songs.size()];
                        for(int i=0;i< songs.size();i++){
                            items[i]=songs.get(i).getName().replace(".m4a","");
                        }
                        //ArrayAdapter<String> ad=new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1,items);
                        MyAdapter ad1=new MyAdapter(MainActivity.this,R.layout.songadapter,songs);
                        listView=findViewById(R.id.listView);
                        listView.setAdapter(ad1);
//                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                            @Override
//                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                String currentsong=listView.getItemAtPosition(position).toString();
//                                Intent intent=new Intent(MainActivity.this,musicplay.class);
//                                intent.putExtra("songs",songs);
//                                intent.putExtra("currents",currentsong);
//                                intent.putExtra("position",position);
//                                startActivity(intent);
//
//                            }
//                        });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(MainActivity.this,"action is denied",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }
    public ArrayList<File> fetchSong(File file){
        ArrayList<File> arrayList=new ArrayList<>();
        File[] songs=file.listFiles();
        if(songs!=null){
            for(File myfile:songs){
                if(!myfile.isHidden() && myfile.isDirectory()){
                    arrayList.addAll(fetchSong(myfile));
                }
                else{
                    if((myfile.getName().endsWith(".m4a") || myfile.getName().endsWith(".mp3")) && !myfile.getName().startsWith(".")){
                        arrayList.add(myfile);
                    }
                }
            }
        }
        return arrayList;
    }
}