package com.example.runtracker.musicplayer;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.runtracker.R;
import com.example.runtracker.common.Constants;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.valdesekamdem.library.mdtoast.MDToast;
import java.io.File;
import java.util.ArrayList;

public class MusicMainActivity extends AppCompatActivity {

    String[] items;
    ListView lvSongs;
    Toolbar toolbar;
    ImageButton ibCloseMusic;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.songs_list);


        toolbar = (Toolbar) findViewById(R.id.toolbarMusic);
        setSupportActionBar(toolbar);
        ibCloseMusic = findViewById(R.id.ibCloseMusic);
        ibCloseMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        lvSongs = findViewById(R.id.lvSongsList);
//        Toast.makeText(this, "on create called", Toast.LENGTH_SHORT).show();
        askPermissions();
    }

    private void askPermissions()
    {
//        Toast.makeText(this, "asking permissions.......", Toast.LENGTH_SHORT).show();
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        display();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        MDToast.makeText(MusicMainActivity.this,"Please provide storage permission",MDToast.LENGTH_SHORT,MDToast.TYPE_WARNING).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    private void display()
    {
//        Toast.makeText(this, "displaying list............", Toast.LENGTH_SHORT).show();
        final ArrayList<File> songsList = findSongs(Environment.getExternalStorageDirectory());
        if(songsList.size()==0){
            MDToast.makeText(this,"No song file found",MDToast.LENGTH_SHORT,MDToast.TYPE_INFO).show();
            return;
        }
        items = new String[songsList.size()];
        for(int i=0;i<songsList.size();i++)
        {
            items[i]=songsList.get(i).getName()
                    .replace(".mp3","")
                    .replace(".wma","")
                    .replace(".wav","");
        }
        lvSongs.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,items));

        lvSongs.setOnItemClickListener((parent, view, position, id) -> {
            String name = songsList.get(position).getName();
            startActivity(new Intent(getApplicationContext(),MusicPlayer.class)
                    .putExtra(Constants.SONG_POSITION,position)
                    .putExtra(Constants.SONGS_LIST,songsList)
                    .putExtra(Constants.SONG_NAME,name));
        });
    }

    private ArrayList<File> findSongs(File file) {
        ArrayList<File> list = new ArrayList<>();
        File[] files = file.listFiles();
        if(files!=null) {
            for (File child : files) {
                if (child.isDirectory() && !child.isHidden()) {
                    list.addAll(findSongs(child));
                } else {
                    String s = child.getName();
                    if (s.endsWith(".mp3") || s.endsWith(".wma") || s.endsWith(".wav")) {
                        list.add(child);
                    }
                }
            }
        }
        return list;
    }

}
