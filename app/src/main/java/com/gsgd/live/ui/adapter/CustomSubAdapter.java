package com.gsgd.live.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gsgd.live.data.listener.OnItemListener;
import com.gsgd.live.data.model.CustomInfo;
import com.gsgd.live.utils.ImageLoadUtil;
import com.jiongbull.jlog.JLog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hdpfans.com.R;

/**
 * @author zhangqy
 * @Description 自建频道
 * @date 2017/12/26
 */
public class CustomSubAdapter extends RecyclerView.Adapter<CustomSubAdapter.ViewHolder> {

    private Context mContext;
    private List<CustomInfo> mData;
    private OnItemListener mListener;
    private boolean isCurrentType;
    private int mCurrentPosition;

    public CustomSubAdapter(Context context, OnItemListener listener) {
        this.mContext = context;
        this.mListener = listener;
    }

    public void setData(List<CustomInfo> data, boolean isCurrentType, int position) {
        this.mData = data;
        this.isCurrentType = isCurrentType;
        this.mCurrentPosition = position;
        try {
            notifyDataSetChanged();

        } catch (Exception e) {
            JLog.e(e);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_custom_sub, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        CustomInfo info = mData.get(position);
        holder.mTvCustom.setText(info.sourceName);

        if (isCurrentType && position == mCurrentPosition) {
            holder.mIvPlayStatus.setVisibility(View.VISIBLE);
            ImageLoadUtil.clearImage(mContext, holder.mIvPlayStatus);
            ImageLoadUtil.loadGif(mContext, holder.mIvPlayStatus, R.drawable.icon_play_status);

        } else {
            holder.mIvPlayStatus.setVisibility(View.GONE);
            ImageLoadUtil.clearImage(mContext, holder.mIvPlayStatus);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onItemClick(position);
                }
            }
        });

        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (null != mListener) {
                    mListener.onItemFocus(position, hasFocus);
                }

                holder.mTvCustom.setTextColor(Color.parseColor(hasFocus ? "#555555" : "#F0F0F0"));
                holder.mTvCustom.getPaint().setFakeBoldText(hasFocus);
            }
        });
    }

    @Override
    public int getItemCount() {
        return null != mData ? mData.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_custom_sub_item)
        public TextView mTvCustom;
        @BindView(R.id.iv_play_status)
        public ImageView mIvPlayStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
