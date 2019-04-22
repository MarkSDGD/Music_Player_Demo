/*
 * Copyright (c) 2016. André Mion
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haikan.sport.musicplayer.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.haikan.sport.R;
import com.haikan.sport.musicplayer.listener.PermissionListener;
import com.haikan.sport.musicplayer.music.AudioPlayer;
import com.haikan.sport.musicplayer.music.PlayerService;
import com.haikan.sport.musicplayer.music.SongBean;
import com.haikan.sport.musicplayer.transition.PlayButtonTransition;
import com.haikan.sport.musicplayer.utils.DateUtils;
import com.haikan.sport.musicplayer.view.ProgressView;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseActivity extends AppCompatActivity {

    public PermissionListener mPermissionListener;

    public PlayerService mService;
    private boolean mBound = false;
    public TextView mTimeView;
    public TextView mDurationView;
    public ProgressView mProgressView;
    private TextView mSongTite;
    private static SongBean currentSong;
    private static int currentSongPosition = 0;
    private static boolean needResumePlay = false;
    private static boolean backFromDetail = false;

    public Handler getmUpdateProgressHandler() {
        return mUpdateProgressHandler;
    }

    private final Handler mUpdateProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int position = mService.getPosition();
            int duration = mService.getDuration();
            // Log.i("MARK", "position== " + position + "  duration==" + duration);

            onUpdateProgress(position, duration);
            if (position >= duration) {  //结束

            } else {
                mUpdateProgressHandler.sendEmptyMessageDelayed(123, 1000);
            }

        }
    };
    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private final ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to PlayerService, cast the IBinder and get PlayerService instance
            Log.i("MARK", "onServiceConnected: ");
            PlayerService.LocalBinder binder = (PlayerService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            onBind();
        }

        @Override
        public void onServiceDisconnected(ComponentName classname) {
            mBound = false;
            onUnbind();
        }
    };


    public void initSongPanel(int position, SongBean songBean) {
        if (songBean == null) {
            return;
        }
        if (mTimeView != null) {
            mTimeView.setText(DateUtils.formatTime(position));
        }
        if (mDurationView != null) {
            mDurationView.setText(DateUtils.formatTime(songBean.getDuration()));
        }
        if (mProgressView != null) {
            mProgressView.setMax(songBean.getDuration());
            mProgressView.setProgress(position);
        }
        if (mSongTite != null) {
            mSongTite.setText(songBean.getTitle() + "  " + songBean.getSinger() + "-" + songBean.getAlbum());
            mSongTite.setSelected(true);
        }

    }

    private void onUpdateProgress(int position, int duration) {
        if (mTimeView != null) {
            mTimeView.setText(DateUtils.formatTime(position));
        }
        if (mDurationView != null) {
            mDurationView.setText(DateUtils.formatTime(duration));
        }
        if (mProgressView != null) {
            mProgressView.setMax(duration);
            mProgressView.setProgress(position);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Bind to PlayerService
        Intent intent = new Intent(this, PlayerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        mTimeView = (TextView) findViewById(R.id.time);
        mDurationView = (TextView) findViewById(R.id.duration);
        mProgressView = (ProgressView) findViewById(R.id.progress);
        mSongTite = (TextView) findViewById(R.id.song_title);

    }


    @Override
    protected void onDestroy() {
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        super.onDestroy();
    }

    public void onBind() {
        // mUpdateProgressHandler.sendEmptyMessage(0);
        //  setOnCompletionListener(this);
    }

    public void onUnbind() {
        // mUpdateProgressHandler.removeMessages(0);
        //setOnCompletionListener(null);
    }


    public void play(int songPostion) {
        mService.play(songPostion);
        mUpdateProgressHandler.removeMessages(123);
        mUpdateProgressHandler.sendEmptyMessage(123);

    }

    public void play(boolean isSameSong) {
        if (isSameSong && !mService.isIdle()) {
            playResume();
            PlayButtonTransition.setmMode(PlayButtonTransition.MODE_PAUSE);

        } else {
            play(getCurrentSongPosition());
            PlayButtonTransition.setmMode(PlayButtonTransition.MODE_PAUSE);

        }
    }

    public void playOrPause() {
        Log.i("MARK", "playOrPause  isPlaying: " + mService.isPlaying() + " isPause== " + mService.isPause());
        Log.i("MARK", "playOrPause  isPreparing: " + mService.isPreparing() + " isIdle== " + mService.isIdle());

        if (mService.isPlaying()) {
            pause();
            PlayButtonTransition.setmMode(PlayButtonTransition.MODE_PLAY);
        } else if (mService.isPause()) {
            playResume();
            PlayButtonTransition.setmMode(PlayButtonTransition.MODE_PAUSE);

        } else if (mService.isPreparing()) {
            stopPlay();
            PlayButtonTransition.setmMode(PlayButtonTransition.MODE_PLAY);

        } else if (mService.isIdle()) {
            play(getCurrentSongPosition());
            PlayButtonTransition.setmMode(PlayButtonTransition.MODE_PAUSE);

        }

    }

    public boolean isPlaying() {
        return mService.isPlaying();
    }

    public boolean isPausing() {
        return mService.isPause();
    }

    public boolean isIdle() {
        return mService.isIdle();
    }

    public void stopPlay() {
        mService.stopPlayer();
        mUpdateProgressHandler.removeMessages(123);
        //todo
    }

    public void playResume() {
        mService.playResume();
        mUpdateProgressHandler.removeMessages(123);
        mUpdateProgressHandler.sendEmptyMessage(123);
    }

    public void pause() {
        mService.pause();
        mUpdateProgressHandler.removeMessages(123);
    }

    public void previous() {
        mService.previous();

    }

    public void next() {
        mService.next();

    }

    public void seekTo(int msec) {
        mService.seekTo(msec);
    }

    public void setOnCompletionListener(AudioPlayer.CompletionListener completionListener) {
        mService.setOnCompletionListener(completionListener);
    }

    public int getPosition() {  //获取进度
        return mService.getPosition();
    }

    public int getPlayIndex() {
        return mService.getPlayIndex();
    }

    public void setPlayIndex(int position) {
        mService.setPlayIndex(position);
    }

    public void resetDuration(int pos, int duration) {
        mService.setPosition(pos);
        mService.setDuration(duration);
    }

    public void setNeedResumePlay(boolean needPlay) {
        needResumePlay = needPlay;
    }

    public boolean getNeedResumePlay() {
        return needResumePlay;
    }

    public void setIsBackFromDetail(boolean fromDetail) {
        backFromDetail = fromDetail;
    }

    public boolean getIsBackFromDetail() {
        return backFromDetail;
    }

    /**
     * 申请运行时权限
     */
    public void requestRuntimePermission(String[] permissions, PermissionListener permissionListener) {
        mPermissionListener = permissionListener;
        List<String> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }

        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), 1);
        } else {
            permissionListener.onGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    List<String> deniedPermissions = new ArrayList<>();
                    for (int i = 0; i < grantResults.length; i++) {
                        int grantResult = grantResults[i];
                        String permission = permissions[i];
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            deniedPermissions.add(permission);
                        }
                    }
                    if (deniedPermissions.isEmpty()) {
                        mPermissionListener.onGranted();
                    } else {
                        mPermissionListener.onDenied(deniedPermissions);
                    }
                }
                break;
        }
    }

    public SongBean getCurrentSong() {
        return currentSong;
    }

    public void setCurrentSong(SongBean currentSong) {
        this.currentSong = currentSong;
    }

    public int getCurrentSongPosition() {
        return currentSongPosition;
    }

    public void setCurrentSongPosition(int currentSongPosition) {
        this.currentSongPosition = currentSongPosition;
    }

    /*public abstract void doOnComplete(int postion);
    @Override
    public void onCompleted(int postion) {
        doOnComplete(postion);
    }*/
}
