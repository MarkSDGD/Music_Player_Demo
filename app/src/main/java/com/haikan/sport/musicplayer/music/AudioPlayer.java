package com.haikan.sport.musicplayer.music;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import static com.haikan.sport.musicplayer.activities.MainActivity.localSongList;

/**
 *
 */
public class AudioPlayer {
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PLAYING = 2;
    private static final int STATE_PAUSE = 3;


    private static int currentPos = 0;
    private Context context;

    private MediaPlayer mediaPlayer;

    private static AudioPlayer audioPlayer;

    private int state = STATE_IDLE;
    private CompletionListener completionListener;

    public interface CompletionListener {
        void onCompleted(int postion);
    }


    public static AudioPlayer getInstance(Context context) {
        if (audioPlayer == null) {
            audioPlayer = new AudioPlayer(context);
        }
        return audioPlayer;
    }

    public void setOnCompletionListener(CompletionListener completionListener) {
        this.completionListener = completionListener;
    }

    private AudioPlayer(Context context) {
        this.context = context;
        init();
    }

    public void init() {
        this.context = context.getApplicationContext();

        mediaPlayer = new MediaPlayer();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.i("MARK", "onCompletion: getPlayIndex()=" + getPlayIndex());
                if (completionListener != null) {
                    completionListener.onCompleted(getPlayIndex());
                }
            }
        });
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.i("MARK", "onPrepared: isPreparing()=" + isPreparing());
                if (isPreparing()) {
                    startPlayer();
                }
            }
        });

    }


    public void play(int position) {
        if (localSongList.isEmpty()) {
            return;
        }
        if (position < 0 || position >= localSongList.size()) {
            position = 0;
        }

        setPlayIndex(position);

        try {
            mediaPlayer.reset();
            Log.i("MARK", "play  position== " + position);

            Log.i("MARK", "play  filepath== " + localSongList.get(position).getFileUrl());
            mediaPlayer.setDataSource(localSongList.get(position).getFileUrl());
            mediaPlayer.prepareAsync();
            state = STATE_PREPARING;

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "当前歌曲无法播放！", Toast.LENGTH_SHORT).show();
        }
    }


    public void playorPause() {
        if (isPreparing()) {
            stopPlayer();
        } else if (isPlaying()) {
            pausePlayer();
        } else if (isPausing()) {
            startPlayer();
        } else {
            play(getPlayIndex());
        }
    }

    public void startPlayer() {
        Log.i("MARK", "startPlayer isPreparing()=" + isPreparing() + "  isPausing()==" + isPausing());
        if (!isPreparing() && !isPausing()) {
            return;
        }
        Log.i("MARK", "startPlayer ");

        mediaPlayer.start();
        state = STATE_PLAYING;

    }

    public void pausePlayer() {
        if (!isPlaying()) {
            return;
        }

        mediaPlayer.pause();
        state = STATE_PAUSE;
    }


    public void stopPlayer() {
        if (isIdle()) {
            return;
        }

        pausePlayer();
        mediaPlayer.reset();
        state = STATE_IDLE;
    }

    public void next() {
        if (localSongList.isEmpty()) {
            return;
        }
        play(getPlayIndex() + 1);

       /* PlayModeEnum mode = PlayModeEnum.valueOf(Preferences.getPlayMode());
        switch (mode) {
            case SHUFFLE:
                play(new Random().nextInt(musicList.size()));
                break;
            case SINGLE:
                play(getPlayIndex());
                break;
            case LOOP:
            default:
                play(getPlayIndex() + 1);
                break;
        }*/
    }

    public void prev() {
        if (localSongList.isEmpty()) {
            return;
        }
        play(getPlayIndex() - 1);
        /*PlayModeEnum mode = PlayModeEnum.valueOf(Preferences.getPlayMode());
        switch (mode) {
            case SHUFFLE:
                play(new Random().nextInt(musicList.size()));
                break;
            case SINGLE:
                play(getPlayIndex());
                break;
            case LOOP:
            default:
                play(getPlayIndex() - 1);
                break;
        }*/
    }

    /**
     * 跳转到指定的时间位置
     *
     * @param msec 时间
     */
    public void seekTo(int msec) {
        if (isPlaying() || isPausing()) {
            mediaPlayer.seekTo(msec);

        }
    }


    public int getAudioSessionId() {
        return mediaPlayer.getAudioSessionId();
    }

    public int getAudioPosition() {
        if (isPlaying() || isPausing()) {
            return mediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    public SongBean getPlayMusic() {
        if (localSongList.isEmpty()) {
            return null;
        }
        return localSongList.get(getPlayIndex());
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public List<SongBean> getMusicList() {
        return localSongList;
    }

    public boolean isPlaying() {
        return state == STATE_PLAYING;
    }

    public boolean isPausing() {
        return state == STATE_PAUSE;
    }

    public boolean isPreparing() {
        return state == STATE_PREPARING;
    }

    public boolean isIdle() {
        return state == STATE_IDLE;
    }

    public int getPlayIndex() {

        return currentPos;
    }

    public void setPlayIndex(int position) {
        currentPos = position;
    }
}
