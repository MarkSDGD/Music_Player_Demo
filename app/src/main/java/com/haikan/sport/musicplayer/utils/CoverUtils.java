package com.haikan.sport.musicplayer.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.util.Log;

import com.haikan.sport.R;

import java.io.InputStream;

/**
 * Created by admin on 2019/4/17.
 * <p>Copyright 2019 SDGD.</p>
 */
public class CoverUtils {


    /**
     * 根据专辑ID获取专辑封面图   方式一
     * @param album_id 专辑ID
     * @return
     */
   public static Bitmap getAlbumCover(Context context,long album_id) {
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[]{"album_art"};
        Cursor cur = context.getContentResolver().query(Uri.parse(mUriAlbums + "/" + Long.toString(album_id)), projection, null, null, null);
        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        Bitmap bm = null;
       Log.i("MARK", "getAlbumCover:  album_art=="+album_art);
        if (album_art != null) {
            bm = BitmapFactory.decodeFile(album_art);
        } else {
            bm = BitmapFactory.decodeResource(context.getResources(),  R.drawable.default_cover);
        }
        return bm;
    }


    /**
     * 从媒体库加载封面<br>  方式二
     * 本地音乐
     */
    public static Bitmap loadCoverFromMediaStore(Context context, long albumId) {
        Log.i("MARK", "loadCoverFromMediaStore: albumId=="+albumId);
        ContentResolver resolver = context.getContentResolver();
        Uri uri = getMediaStoreAlbumCoverUri(albumId);
        InputStream is;
        try {
            is = resolver.openInputStream(uri);
        } catch (Exception ignored) {
            return  BitmapFactory.decodeResource(context.getResources(),  R.drawable.default_cover);
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bm = null;
        bm = BitmapFactory.decodeStream(is, null, options);
        Log.i("MARK", "loadCoverFromMediaStore: bm=="+bm);


        return bm;
    }

    public static Uri getMediaStoreAlbumCoverUri(long albumId) {
        Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
        return ContentUris.withAppendedId(artworkUri, albumId);
    }

    /**
     * 将图片剪裁为圆形
     */
    public static Bitmap createCircleImage(Bitmap source) {
        if (source == null) {
            return null;
        }

        int length = Math.min(source.getWidth(), source.getHeight());
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(length, length, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);
        canvas.drawCircle(source.getWidth() / 2, source.getHeight() / 2, length / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, 0, 0, paint);
        return target;
    }
}
