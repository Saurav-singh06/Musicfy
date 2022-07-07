package com.saurav.musicfy;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.media.MediaParser;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


public class music_player extends Fragment {


    ImageView btn_play;
    SeekBar seekBar;
    MediaPlayer mediaPlayer;
    TextView txt_start,txt_end;
    String s1;

    private Handler handler = new Handler();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_music_player, container, false);
        btn_play = v.findViewById(R.id.btn_play);
        seekBar = v.findViewById(R.id.seek);
        txt_start=v.findViewById(R.id.txt_start);
        txt_end=v.findViewById(R.id.txt_end);
        SharedPreferences sh = getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);

        s1 = sh.getString("musicUrl", "");
        Toast.makeText(getActivity(), s1, Toast.LENGTH_SHORT).show();

        mediaPlayer = new MediaPlayer();

        seekBar.setMax(100);
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mediaPlayer.isPlaying()){
                    handler.removeCallbacks(update);
                    mediaPlayer.pause();
                    btn_play.setImageResource(R.drawable.ic_play);
                }else {
                    mediaPlayer.start();
                    btn_play.setImageResource(R.drawable.ic_pause);
                    updateSeekBar();
                }
            }
        });
        preparedPlayer();

        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                SeekBar seekBar = (SeekBar)v;
                int playPosition = (mediaPlayer.getDuration()/100)*seekBar.getProgress();
                mediaPlayer.seekTo(playPosition);
                txt_start.setText(milliSecondsToTimer(mediaPlayer.getCurrentPosition()));

                return false;
            }
        });

        return v;
    }
    private void preparedPlayer(){
        try {
            mediaPlayer.setDataSource(s1);
            mediaPlayer.prepare();
            txt_end.setText(milliSecondsToTimer(mediaPlayer.getDuration()));
        }catch (Exception exception){
            Toast.makeText(getActivity(),exception.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    private Runnable update = new Runnable() {
        @Override
        public void run() {
            updateSeekBar();
            long currentDuration = mediaPlayer.getCurrentPosition();
            txt_start.setText(milliSecondsToTimer(currentDuration));
        }
    };
    private void updateSeekBar(){
        if (mediaPlayer.isPlaying()) {
            seekBar.setProgress((int) (((float)mediaPlayer.getCurrentPosition()/mediaPlayer.getDuration()) *100));
            handler.postDelayed(update,1000);
        }
    }

    private String milliSecondsToTimer(long milliSeconds){
        String timerString ="";
        String secondsString;

        int hr =(int)(milliSeconds/(100*60*60));
        int min =(int)(milliSeconds%(1000*60*60))/(1000*60);
        int sec =(int)((milliSeconds%(1000*60*60))/(1000*60)/1000);

        if (hr>0){
            timerString=hr+":";
        }
        if (sec<10){
            secondsString ="0" + sec;
        }else {
            secondsString = "" +sec ;
        }
        timerString =timerString+min+":"+secondsString;
        return timerString;
    }
}