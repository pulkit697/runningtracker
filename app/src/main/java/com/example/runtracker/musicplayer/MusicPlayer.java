package com.example.runtracker.musicplayer;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.example.runtracker.R;
import com.example.runtracker.common.Constants;
import java.io.File;
import java.util.ArrayList;

public class MusicPlayer extends AppCompatActivity
{
    ImageView ivSongImage;
    TextView tvSongName;
    ImageButton ibPrev,ibNext,ibToggle;
    SeekBar sbSeekSong;

    static MediaPlayer mediaPlayer;
    int position;
    int totalCount;
    String name;
    ArrayList<File> songsList;
    Thread updateSeekBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_player);

        ivSongImage = findViewById(R.id.ivSongImage);
        tvSongName = findViewById(R.id.tvSongName);
        ibPrev = findViewById(R.id.btPrevSong);
        ibNext = findViewById(R.id.btNextSong);
        ibToggle = findViewById(R.id.btTogglePlay);
        sbSeekSong = findViewById(R.id.sbSeekSong);

        createThread();
        if(mediaPlayer!=null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        songsList = (ArrayList) bundle.getParcelableArrayList(Constants.SONGS_LIST);
        name = bundle.getString(Constants.SONG_NAME);
        position = bundle.getInt(Constants.SONG_POSITION,0);
        totalCount = songsList.size();
        tvSongName.setText(name);

        startPlayer();

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


    }

    private void setUpSeekBarUI()
    {
        sbSeekSong.setProgress(0);
        sbSeekSong.getThumb().setColorFilter(new PorterDuffColorFilter
                (getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN));
        sbSeekSong.getProgressDrawable().setColorFilter(new PorterDuffColorFilter
                (getResources().getColor(R.color.black), PorterDuff.Mode.MULTIPLY));
    }

    private void startPlayer() {
        Uri uri = Uri.parse(songsList.get(position).toString());
        mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            sbSeekSong.setProgress(0,true);
        else
            sbSeekSong.setProgress(0);
        sbSeekSong.setMax(mediaPlayer.getDuration());
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
                        sbSeekSong.setProgress(currentPosition);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    private void setUpClickListeners()
    {
        sbSeekSong.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {/*NO-OP*/}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {/*NO-OP*/}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        ibToggle.setOnClickListener((view)->{
            sbSeekSong.setMax(mediaPlayer.getDuration());
            if(mediaPlayer.isPlaying()){
                ibToggle.setImageResource(R.drawable.ic_play_song);
                mediaPlayer.pause();
            }
            else{
                ibToggle.setImageResource(R.drawable.ic_pause_song);
                mediaPlayer.start();
            }
        });

        ibPrev.setOnClickListener((view)->{
            previousSong();
        });

        ibNext.setOnClickListener((view)->{
            nextSong();
        });

    }

    private void previousSong()
    {
        mediaPlayer.stop();
        mediaPlayer.release();
        position--;
        if(position<0)
            position = totalCount-1;
//            position=((position-1)<0)?(songsList.size()-1):(position-1);
        name = songsList.get(position).getName();
        tvSongName.setText(name);

        startPlayer();
    }

    private void nextSong()
    {
        updateSeekBar.interrupt();
        mediaPlayer.stop();
        mediaPlayer.release();
        position++;
        if(position>=totalCount)
            position = 0;
//            position=((position+1)%songsList.size());
        name = songsList.get(position).getName();
        tvSongName.setText(name);
        startPlayer();
    }


}
