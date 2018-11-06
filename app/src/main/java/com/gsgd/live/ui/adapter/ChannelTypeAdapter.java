package com.gsgd.live.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gsgd.live.data.listener.OnItemListener;
import com.gsgd.live.data.model.ChannelType;
import com.gsgd.live.utils.ImageLoadUtil;
import com.jiongbull.jlog.JLog;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import hdpfans.com.R;

/**
 * 栏目列表
 */
public class ChannelTypeAdapter extends RecyclerView.Adapter<ChannelTypeAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<ChannelType> data;
    private OnItemListener mListener;
    private int mCurrentPosition = 1;

    public ChannelTypeAdapter(Context context, int position, OnItemListener listener) {
        this.mContext = context;
        this.mCurrentPosition = position;
        this.mListener = listener;
    }

    public void setData(ArrayList<ChannelType> data) {
        this.data = data;
        try {
            notifyDataSetChanged();
        } catch (Exception e) {
            JLog.e(e);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_channel_type, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (mCurrentPosition == position) {
            holder.mIvPlayStatus.setVisibility(View.VISIBLE);
            ImageLoadUtil.clearImage(mContext, holder.mIvPlayStatus);
            ImageLoadUtil.loadGif(mContext, holder.mIvPlayStatus, R.drawable.icon_play_status);

        } else {
            holder.mIvPlayStatus.setVisibility(View.GONE);
            ImageLoadUtil.clearImage(mContext, holder.mIvPlayStatus);
        }

        holder.tvChannelName.setText(data.get(position).type);

        holder.itemView.setBackgroundResource(R.drawable.bg_channel_select);
        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (null != mListener) {
                    mListener.onItemFocus(position, hasFocus);
                }
                if (hasFocus) {
                    holder.tvChannelName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
                    holder.tvChannelName.setTextColor(Color.parseColor("#555555"));
                    holder.tvChannelName.getPaint().setFakeBoldText(true);
                } else {
                    holder.tvChannelName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
                    holder.tvChannelName.setTextColor(Color.parseColor("#F0F0F0"));
                    holder.tvChannelName.getPaint().setFakeBoldText(false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return null == data ? 0 : data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_play_status)
        public ImageView mIvPlayStatus;
        @BindView(R.id.tv_channel_name)
        public TextView tvChannelName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
