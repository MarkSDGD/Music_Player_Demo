package com.mark.socialhelper;

import com.mark.socialhelper.callback.SocialLoginCallback;
import com.mark.socialhelper.callback.SocialShareCallback;
import com.mark.socialhelper.entities.ShareEntity;
import com.mark.socialhelper.entities.ThirdInfoEntity;

/**
 * Created by arvinljw on 17/11/24 16:06
 * Function：
 * Desc：
 */
public interface ISocial {
    void login(SocialLoginCallback callback);

    ThirdInfoEntity createThirdInfo();

    void share(SocialShareCallback callback, ShareEntity shareInfo);

    void onDestroy();
}
