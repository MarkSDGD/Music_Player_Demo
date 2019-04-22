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

package com.haikan.sport.musicplayer.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.haikan.sport.R;
import com.haikan.sport.musicplayer.music.SongBean;
import com.haikan.sport.musicplayer.utils.CoverUtils;
import com.haikan.sport.musicplayer.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private final Context mContext;
    private List<SongBean> mValues = new ArrayList<SongBean>();

    private OnItemLisenter itemLisenter;

    public void setOnItemClickLisenter(OnItemLisenter lisenter) {
        this.itemLisenter = lisenter;
    }

    public interface OnItemLisenter {
        void onItemClick(View view, int position);
    }

    public RecyclerViewAdapter(Context context, List<SongBean> items) {
        mContext = context;
        mValues = items;
    }

    public void setDataList(List<SongBean> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        // holder.mCoverView.setImageResource(holder.mItem.getCover());

        Bitmap bitmap = CoverUtils.getAlbumCover(mContext, holder.mItem.getAlbumId());

        holder.mCoverView.setImageBitmap(CoverUtils.createCircleImage(bitmap));

        holder.mTitleView.setText(holder.mItem.getTitle());
        holder.mArtistView.setText(holder.mItem.getSinger() + "-" + holder.mItem.getAlbum());
        holder.mDurationView.setText(DateUtils.formatTime(holder.mItem.getDuration()));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Nothing to do
                if(itemLisenter!=null){
                    itemLisenter.onItemClick(v,position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mCoverView;
        public final TextView mTitleView;
        public final TextView mArtistView;
        public final TextView mDurationView;
        public SongBean mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mCoverView = (ImageView) view.findViewById(R.id.cover);
            mTitleView = (TextView) view.findViewById(R.id.title);
            mArtistView = (TextView) view.findViewById(R.id.artist);
            mDurationView = (TextView) view.findViewById(R.id.duration);
        }
    }


}
