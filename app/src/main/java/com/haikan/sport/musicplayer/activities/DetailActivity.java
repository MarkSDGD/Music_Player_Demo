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

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andremion.music.MusicCoverView;
import com.haikan.sport.R;
import com.haikan.sport.musicplayer.application.MusicApplication;
import com.haikan.sport.musicplayer.music.AudioPlayer;
import com.haikan.sport.musicplayer.music.SongBean;
import com.haikan.sport.musicplayer.transition.PlayButtonTransition;
import com.haikan.sport.musicplayer.utils.UIUtils;
import com.haikan.sport.musicplayer.view.ProgressView;
import com.haikan.sport.musicplayer.view.TransitionAdapter;
import com.mark.socialhelper.callback.SocialShareCallback;
import com.mark.socialhelper.entities.ShareEntity;
import com.mark.socialhelper.entities.WXShareEntity;

import static com.haikan.sport.musicplayer.activities.MainActivity.localSongList;

public class DetailActivity extends BaseActivity implements View.OnClickListener, AudioPlayer.CompletionListener, SocialShareCallback {

    private MusicCoverView mCoverView;
    private TextView mSongTtitle;
    private boolean isSameSong;


    private ProgressView progress;
    private FloatingActionButton fab;
    private LinearLayout ordering;
    private ImageView repeat;
    private ImageView share;
    private LinearLayout controls;
    private ImageView previous;
    private ImageView rewind;
    private ImageView forward;
    private ImageView next;
    private SongBean currentSong;
    private static final int STEP = 10000;
    private String shareurl = "http://fm.sojson.com/";
    private ImageView sharefriends;
    private TextView lrcText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_detail);
        initView();


        getWindow().getSharedElementEnterTransition().addListener(new TransitionAdapter() {
            @Override
            public void onTransitionEnd(Transition transition) {
                Log.i("MARK", "onTransitionEnd: getCurrentSong==" + getCurrentSong());
                Log.i("MARK", "onTransitionEnd: getCurrentSongPosition()==" + getCurrentSongPosition());
                currentSong = getCurrentSong();
                play(isSameSong);
                mCoverView.start();
                lrcText.setVisibility(View.VISIBLE);
                setOnCompletionListener(DetailActivity.this);

            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.i("MARK", "detail onPause");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("MARK", "detail onResume");
    }


    private void initView() {

        progress = (ProgressView) findViewById(R.id.progress);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        repeat = (ImageView) findViewById(R.id.repeat);
        share = (ImageView) findViewById(R.id.share);
        sharefriends = (ImageView) findViewById(R.id.sharefriends);
        lrcText = (TextView) findViewById(R.id.lrc_text);


        previous = (ImageView) findViewById(R.id.previous);
        next = (ImageView) findViewById(R.id.next);
        rewind = (ImageView) findViewById(R.id.rewind);
        forward = (ImageView) findViewById(R.id.forward);


        repeat.setOnClickListener(this);
        share.setOnClickListener(this);
        sharefriends.setOnClickListener(this);
        previous.setOnClickListener(this);
        next.setOnClickListener(this);
        rewind.setOnClickListener(this);
        forward.setOnClickListener(this);

        isSameSong = getIntent().getBooleanExtra("isSameSong", false);
        mSongTtitle = (TextView) findViewById(R.id.song_title);
        if (getCurrentSong() != null) {
            mSongTtitle.setText(getCurrentSong().getTitle() + "  " + getCurrentSong().getSinger() + "-" + getCurrentSong().getAlbum());
            mSongTtitle.setSelected(true);
        }
        mCoverView = (MusicCoverView) findViewById(R.id.cover);
        mCoverView.setCallbacks(new MusicCoverView.Callbacks() {
            @Override
            public void onMorphEnd(MusicCoverView coverView) {
                // Nothing to do
            }

            @Override
            public void onRotateEnd(MusicCoverView coverView) {
                supportFinishAfterTransition();
            }
        });
    }


    @Override
    public void onBackPressed() {  //继续播放
        //  onFabClick(null);
        lrcText.setVisibility(View.GONE);

        setNeedResumePlay(true);
        setIsBackFromDetail(true);

        // pause();
        PlayButtonTransition.setmMode(PlayButtonTransition.MODE_PLAY);

        mCoverView.stop();
        setOnCompletionListener(null);
        getmUpdateProgressHandler().removeMessages(123);
    }

    public void onFabClick(View view) {  //回到暂停
        setNeedResumePlay(false);
        setIsBackFromDetail(true);
        pause();
        PlayButtonTransition.setmMode(PlayButtonTransition.MODE_PAUSE);
        mCoverView.stop();
        lrcText.setVisibility(View.GONE);
        setOnCompletionListener(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.repeat:
                UIUtils.showToast("更多模式敬请期待！");
                break;
            case R.id.share:
                UIUtils.showToast("分享");
                String title = "快来和我一起听音乐吧";
                String summary = "我正在使用海看音乐播放器听" + currentSong.getTitle() + "，快来和我一起听吧。";
                MusicApplication.getSocialHelper().shareWX(DetailActivity.this, createWXShareEntity(true, shareurl, R.mipmap.launcher_icon, title, summary), DetailActivity.this);

                break;
            case R.id.sharefriends:
                UIUtils.showToast("分享");
                String title1 = "快来和我一起听音乐吧";
                String summary1 = "我正在使用海看音乐播放器听" + currentSong.getTitle() + "，快来和我一起听吧。";
                MusicApplication.getSocialHelper().shareWX(DetailActivity.this, createWXShareEntity(false, shareurl, R.mipmap.launcher_icon, title1, summary1), DetailActivity.this);

                break;

            case R.id.previous:
                UIUtils.showToast("上一曲");
                previous();
                UpdateSongInfo();

                break;
            case R.id.next:
                UIUtils.showToast("下一曲");
                next();
                UpdateSongInfo();

                break;
            case R.id.forward:
                UIUtils.showToast("快进");
                int currentPos = getPosition();
                if (currentPos + STEP < currentSong.getDuration()) {
                    pause();
                    seekTo(currentPos + STEP);
                    playResume();
                } else {
                    next();
                    UpdateSongInfo();
                }

                break;
            case R.id.rewind:
                UIUtils.showToast("快退");
                int currentPosBack = getPosition();
                if (currentPosBack - STEP > 0) {
                    pause();
                    seekTo(currentPosBack - STEP);
                    playResume();
                } else {
                    previous();
                    UpdateSongInfo();
                }
                break;
        }
    }

    private void UpdateSongInfo() {
        currentSong = localSongList.get(getPlayIndex());
        setCurrentSong(currentSong);
        resetDuration(0, currentSong.getDuration());
        initSongPanel(0, currentSong);
        setCurrentSongPosition(getPlayIndex());
    }


    /**
     * @param flag true 分享到朋友圈 ，false 分享到好友
     * @return
     */
    private ShareEntity createWXShareEntity(boolean flag, String shareurl, int image, String title, String summary) { //
        ShareEntity shareEntity = null;

        shareEntity = WXShareEntity.createWebPageInfo(flag, shareurl, image, title, summary);

        return shareEntity;
    }


    @Override
    public void onCompleted(int postion) {
        Log.i("MARK", "detail onCompleted: postion=" + postion);
        next();
        UpdateSongInfo();
    }

    @Override
    public void shareSuccess(int type) {
        // UIUtils.showToast("分享成功！");
    }

    @Override
    public void socialError(String msg) {
        UIUtils.showToast(msg);
    }


}
