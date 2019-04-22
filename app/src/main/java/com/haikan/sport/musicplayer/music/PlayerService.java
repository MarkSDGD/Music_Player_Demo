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

package com.haikan.sport.musicplayer.music;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.haikan.sport.musicplayer.application.MusicApplication;

public class PlayerService extends Service {

    private static final String TAG = PlayerService.class.getSimpleName();
    private static int DURATION = 240358;
    private static int position = 0;
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    private static AudioPlayer audioPlayer = AudioPlayer.getInstance(MusicApplication.getContext());
    ;

    public PlayerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        audioPlayer = AudioPlayer.getInstance(this.getApplicationContext());
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (audioPlayer != null) {
            audioPlayer.stopPlayer();
            audioPlayer = null;
        }

        return super.onUnbind(intent);
    }

    public void play(int songPosition) {
        if (audioPlayer != null) {
            audioPlayer.play(songPosition);
        }

    }

    public void playResume() {
        if (audioPlayer != null) {
            audioPlayer.startPlayer();
        }
    }

    public void stopPlayer() {
        if (audioPlayer != null) {
            audioPlayer.stopPlayer();
        }
    }

    public boolean isPlaying() {
        if (audioPlayer != null) {
            return audioPlayer.isPlaying();
        }
        return false;
    }

    public boolean isPause() {
        if (audioPlayer != null) {
            return audioPlayer.isPausing();
        }
        return false;
    }

    public boolean isPreparing() {
        if (audioPlayer != null) {
            return audioPlayer.isPreparing();
        }
        return false;
    }

    public boolean isIdle() {
        if (audioPlayer != null) {
            return audioPlayer.isIdle();
        }
        return false;
    }

    public void pause() {
        if (audioPlayer != null) {
            audioPlayer.pausePlayer();
        }

    }

    public void previous() {
        if (audioPlayer != null) {
            audioPlayer.prev();
        }

    }

    public void next() {
        if (audioPlayer != null) {
            audioPlayer.next();
        }

    }

    public void seekTo(int msec) {
        if (audioPlayer != null) {
            audioPlayer.seekTo(msec);
        }
    }

    public void setOnCompletionListener(AudioPlayer.CompletionListener completionListener) {
        if (audioPlayer != null) {
            audioPlayer.setOnCompletionListener(completionListener);
        }
    }

    public int getPlayIndex() {
        if (audioPlayer != null) {
            return audioPlayer.getPlayIndex();
        }
        return 0;
    }

    public void setPlayIndex(int position) {
        if (audioPlayer != null) {
            audioPlayer.setPlayIndex(position);
        }
    }

    public int getPosition() {  //获取进度
        if (audioPlayer != null) {
            position = audioPlayer.getAudioPosition();
        }
        return position;
    }

    public void setPosition(int pos) { //设置进度
        position = pos;
    }

    public int getDuration() {
        return DURATION;
    }

    public void setDuration(int duration) {
        DURATION = duration;
    }


    /**
     * Class used for the client Binder. Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {

        public PlayerService getService() {
            // Return this instance of PlayerService so clients can call public methods
            return PlayerService.this;
        }
    }
}
