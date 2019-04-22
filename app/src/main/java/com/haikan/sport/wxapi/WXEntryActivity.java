package com.haikan.sport.wxapi;


import com.haikan.sport.musicplayer.application.MusicApplication;
import com.mark.socialhelper.SocialHelper;
import com.mark.socialhelper.WXHelperActivity;


public class WXEntryActivity extends WXHelperActivity {

    @Override
    protected SocialHelper getSocialHelper() {
        return MusicApplication.getSocialHelper();
    }
}
