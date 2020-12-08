package com.example.runtracker.musicplayer;

import android.Manifest;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
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
import java.util.Random;

public class MusicMainActivity extends AppCompatActivity {

    String[] items;
    ListView lvSongs;
    Toolbar toolbar;
    ImageButton ibCloseMusic;

    int[] imageList = new int[]{R.drawable.ic_music_color,
            R.drawable.ic_music_black,
            R.drawable.ic_music_1,
            R.drawable.ic_music_2,
            R.drawable.ic_music_3,
            R.drawable.ic_music_4,
            R.drawable.ic_music_5};
    int icon_number = 0;
    int total_icons = 7;

    ImageView ivSongImageMini;
    TextView tvSongNameMini;
    ImageButton ibPrevMini,ibNextMini,ibToggleMini;
    SeekBar sbSeekSongMini;
    static MediaPlayer mediaPlayer;
    int position;
    int totalCount;
    String name;
    ArrayList<File> songsList;
    Thread updateSeekBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.songs_list);

        lvSongs = findViewById(R.id.lvSongsList);
        ivSongImageMini = findViewById(R.id.ivSongImageMini);
        tvSongNameMini = findViewById(R.id.tvSongNameMini);
        ibPrevMini = findViewById(R.id.ibPrevSongMini);
        ibNextMini = findViewById(R.id.ibNextSongMini);
        ibToggleMini = findViewById(R.id.ibToggleSongMini);
        sbSeekSongMini = findViewById(R.id.sbSeeksongMini);
        tvSongNameMini.setSelected(true);
        askPermissions();

        toolbar = (Toolbar) findViewById(R.id.toolbarMusic);
        setSupportActionBar(toolbar);
        ibCloseMusic = findViewById(R.id.ibCloseMusic);
        ibCloseMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        createThread();
        if(mediaPlayer!=null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        position=0;
        totalCount=songsList.size();

        if(items.length>0) {
            name = items[position];
            tvSongNameMini.setText(name);
        }

        LoadPlayer();
        setUpSeekBarUI();
        setUpClickListeners();

        updateSeekBar.start();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                try {
                    nextSong();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


//        Toast.makeText(this, "on create called", Toast.LENGTH_SHORT).show();
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
        songsList = findSongs(Environment.getExternalStorageDirectory());
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
            if(mediaPlayer!=null)
            {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
            this.name = items[position];
            this.position = position;
            startPlayer();
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


    private void setUpSeekBarUI()
    {
        sbSeekSongMini.setProgress(0);
        sbSeekSongMini.getThumb().setColorFilter(new PorterDuffColorFilter
                (getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN));
        sbSeekSongMini.getProgressDrawable().setColorFilter(new PorterDuffColorFilter
                (getResources().getColor(R.color.black), PorterDuff.Mode.MULTIPLY));
    }

    private void LoadPlayer() {
        getRandomImage();
        Uri uri = Uri.parse(songsList.get(position).toString());
        mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            sbSeekSongMini.setProgress(0,true);
        else
            sbSeekSongMini.setProgress(0);
        sbSeekSongMini.setMax(mediaPlayer.getDuration());
//        mediaPlayer.start();
    }

    private void startPlayer() {
        name = items[position];
        tvSongNameMini.setText(name);
        getRandomImage();
        ibToggleMini.setImageResource(R.drawable.ic_pause_song);
        Uri uri = Uri.parse(songsList.get(position).toString());
        mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            sbSeekSongMini.setProgress(0,true);
        else
            sbSeekSongMini.setProgress(0);
        sbSeekSongMini.setMax(mediaPlayer.getDuration());
        mediaPlayer.start();
    }

    private void createThread()
    {
        updateSeekBar = new Thread(){
            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = 0;
                while(currentPosition < totalDuration)
                {
                    try {
                        sleep(500);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        sbSeekSongMini.setProgress(currentPosition);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    private void setUpClickListeners()
    {
        sbSeekSongMini.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {/*NO-OP*/}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {/*NO-OP*/}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        ibToggleMini.setOnClickListener((view)->{
            sbSeekSongMini.setMax(mediaPlayer.getDuration());
            if(mediaPlayer.isPlaying()){
                ibToggleMini.setImageResource(R.drawable.ic_play_song);
                mediaPlayer.pause();
            }
            else{
                ibToggleMini.setImageResource(R.drawable.ic_pause_song);
                mediaPlayer.start();
            }
        });

        ibPrevMini.setOnClickListener((view)->{
            previousSong();
        });

        ibNextMini.setOnClickListener((view)->{
            nextSong();
        });

    }

    private void previousSong()
    {
//        updateSeekBar.interrupt();
        mediaPlayer.stop();
        mediaPlayer.release();
        position--;
        if(position<0)
            position = totalCount-1;
//            position=((position-1)<0)?(songsList.size()-1):(position-1);
        startPlayer();
    }

    private void nextSong()
    {
//        updateSeekBar.interrupt();
        mediaPlayer.stop();
        mediaPlayer.release();
        position++;
        if(position>=totalCount)
            position = 0;
//            position=((position+1)%songsList.size());
        startPlayer();
    }

    private void getRandomImage()
    {
        Random r = new Random();
        icon_number = r.nextInt(total_icons);
        ivSongImageMini.setImageResource(imageList[icon_number]);
    }

}
