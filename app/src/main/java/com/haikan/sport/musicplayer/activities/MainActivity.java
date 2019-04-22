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

import android.Manifest;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.haikan.sport.R;
import com.haikan.sport.musicplayer.listener.PermissionListener;
import com.haikan.sport.musicplayer.music.AudioPlayer;
import com.haikan.sport.musicplayer.music.SongBean;
import com.haikan.sport.musicplayer.transition.PlayButtonTransition;
import com.haikan.sport.musicplayer.utils.ScanUtils;
import com.haikan.sport.musicplayer.view.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements RecyclerViewAdapter.OnItemLisenter, AudioPlayer.CompletionListener, AppBarLayout.OnOffsetChangedListener {

    private static final int EXPANDED = 0;
    private static final int COLLAPSED = 1;

    private View mCoverView;
    private View mTitleView;
    public View mTimeView;
    public View mDurationView;
    public View mProgressView;
    private View mFabView;
    public static List<SongBean> localSongList = new ArrayList<>();
    private RecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private int currentSongPosition = 0;
    private SongBean currentSong;
    private TextView mCounter;
    private AppBarLayout mAppBar;
    private int mCurrentState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 避免从桌面启动程序后，会重新实例化入口类的activity
        Log.i("MARK", "MainActivity  onCreate ");
        if (!this.isTaskRoot()) {
            Intent intent = getIntent();
            if (intent != null) {
                String action = intent.getAction();
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                    finish();
                    return;
                }
            }
        }

        setContentView(R.layout.activity_main_layout);
        Log.i("MARK", "MainActivity  onCreate1 ");
        //
        mAppBar = findViewById(R.id.appbar);
        mAppBar.addOnOffsetChangedListener(this);
        mCoverView = findViewById(R.id.cover);
        mTitleView = findViewById(R.id.title);
        mTimeView = findViewById(R.id.time);
        mDurationView = findViewById(R.id.duration);
        mProgressView = findViewById(R.id.progress);
        mFabView = findViewById(R.id.fab);

        mCounter = (TextView) findViewById(R.id.counter);
        // Set the recycler adapter
        recyclerView = (RecyclerView) findViewById(R.id.tracks);
        mLinearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        adapter = new RecyclerViewAdapter(this, localSongList);
        adapter.setOnItemClickLisenter(this);
        recyclerView.setAdapter(adapter);
        requestPermission();
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.i("MARK", "MainActivity onStart: mService=" + mService);
        if (getIsBackFromDetail()) {
            setIsBackFromDetail(false);
            mAppBar.setExpanded(true, false);
        }
        if (mService != null) {
            setOnCompletionListener(this);
            currentSong = getCurrentSong();
            currentSongPosition = getCurrentSongPosition();
            initSongPanel(mService.getPosition(), currentSong);
            if (getNeedResumePlay() || isPlaying()) {
                playResume();
            } else {
                pause();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        setOnCompletionListener(null);
        getmUpdateProgressHandler().removeMessages(123);
    }

   /* public void onBind() { //todo  注册时机

        setOnCompletionListener(this);
    }*/

    private void requestPermission() {
        requestRuntimePermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionListener() {
            @Override
            public void onGranted() {
                localSongList = ScanUtils.getAllSongs(MainActivity.this);
                adapter.setDataList(localSongList);
                adapter.notifyDataSetChanged();
                mCounter.setText(localSongList.size() + " 首");
                currentSong = localSongList.get(getCurrentSongPosition());
                setCurrentSong(currentSong);
                initSongPanel(0, currentSong);
            }

            @Override
            public void onDenied(List<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "没有权限访问本地音乐！", Toast.LENGTH_SHORT).show();
            }
        });


    }


    public void onFabClick(View view) {  //  控制当前播放状态
        playControl();
        // JumpToDetail(true);

    }

    public void onPanelCick(View view) {  //跳转到详情 进入详情是 同一首歌
        JumpToDetail(true);
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.i("MARK", "onItemClick: position=" + position);
        if (position != currentSongPosition) {

            currentSong = localSongList.get(position);
            setCurrentSong(currentSong);
            resetDuration(0, currentSong.getDuration());
            initSongPanel(0, currentSong);
            currentSongPosition = position;
            setCurrentSongPosition(currentSongPosition);
            //JumpToDetail(false);
            play(getCurrentSongPosition());
            PlayButtonTransition.setmMode(PlayButtonTransition.MODE_PLAY);
            ChangeFabview(R.drawable.ic_play_animatable); //开始->暂停
        } else {
            playControl();

        }
    }

    private void playControl() {
        if (isPlaying()) {
            pause();
            PlayButtonTransition.setmMode(PlayButtonTransition.MODE_PAUSE);
            ChangeFabview(R.drawable.ic_pause_animatable); //暂停->开始

        } else if (isPausing()) {
            playResume();
            PlayButtonTransition.setmMode(PlayButtonTransition.MODE_PLAY);
            ChangeFabview(R.drawable.ic_play_animatable);  //开始->暂停

        } else if (isIdle()) {
            play(getCurrentSongPosition());
            PlayButtonTransition.setmMode(PlayButtonTransition.MODE_PLAY);
            ChangeFabview(R.drawable.ic_play_animatable); //开始->暂停
        }
    }

    private void ChangeFabview(int drawableResource) {
        ((FloatingActionButton) mFabView).setImageResource(drawableResource);
        Drawable drawable = ((FloatingActionButton) mFabView).getDrawable();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }
    }

    private void JumpToDetail(boolean isSameSong) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("isSameSong", isSameSong);
       /* if(mCurrentState== COLLAPSED){ //去除 mCoverView
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, new Pair<>(mTitleView, ViewCompat.getTransitionName(mTitleView)), new Pair<>(mTimeView, ViewCompat.getTransitionName(mTimeView)), new Pair<>(mDurationView, ViewCompat.getTransitionName(mDurationView)), new Pair<>(mProgressView, ViewCompat.getTransitionName(mProgressView)), new Pair<>(mFabView, ViewCompat.getTransitionName(mFabView)));
            ActivityCompat.startActivity(this, intent, options.toBundle());
        }else{*/
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, new Pair<>(mCoverView, ViewCompat.getTransitionName(mCoverView)), new Pair<>(mTitleView, ViewCompat.getTransitionName(mTitleView)), new Pair<>(mTimeView, ViewCompat.getTransitionName(mTimeView)), new Pair<>(mDurationView, ViewCompat.getTransitionName(mDurationView)), new Pair<>(mProgressView, ViewCompat.getTransitionName(mProgressView)), new Pair<>(mFabView, ViewCompat.getTransitionName(mFabView)));
        ActivityCompat.startActivity(this, intent, options.toBundle());
        // }

    }


    private void UpdateSongInfo() {
        Log.i("MARK", "onCompleted: getPlayIndex()=" + getPlayIndex());

        currentSongPosition = getPlayIndex();
        currentSong = localSongList.get(currentSongPosition);
        setCurrentSong(currentSong);
        resetDuration(0, currentSong.getDuration());
        initSongPanel(0, currentSong);
        setCurrentSongPosition(getPlayIndex());
    }

    @Override
    public void onCompleted(int postion) {
        Log.i("MARK", "main onCompleted: postion=" + postion);
        next();
        UpdateSongInfo();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (verticalOffset == 0) {

            mCurrentState = EXPANDED;
        } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {

            mCurrentState = COLLAPSED;
        }

    }
}
