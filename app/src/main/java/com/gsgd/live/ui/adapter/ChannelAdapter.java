package com.gsgd.live.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gsgd.live.AppConfig;
import com.gsgd.live.data.listener.ChannelListener;
import com.gsgd.live.data.listener.OnItemListener;
import com.gsgd.live.data.listener.OnItemLongListener;
import com.gsgd.live.data.model.Channel;
import com.gsgd.live.utils.ImageLoadUtil;
import com.gsgd.live.utils.Utils;
import com.jiongbull.jlog.JLog;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import hdpfans.com.R;

/**
 * 某个栏目下的频道列表
 */
public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Channel> data;
    private OnItemListener mListener;
    private OnItemLongListener onLongListener;
    private ChannelListener mChannelListener;
    private boolean isCurrentType;
    private int mCurrentPosition;

    public ChannelAdapter(Context context, OnItemListener listener, OnItemLongListener longListener, ChannelListener channelListener) {
        mContext = context;
        this.mListener = listener;
        this.onLongListener = longListener;
        this.mChannelListener = channelListener;
    }

    public void setData(ArrayList<Channel> data, boolean isCurrentType, int position) {
        this.data = data;
        this.isCurrentType = isCurrentType;
        this.mCurrentPosition = position;
        try {
            notifyDataSetChanged();

        } catch (Exception e) {
            JLog.e(e);
        }
    }

    public Channel getItem(int position) {
        return this.data.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_channel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Channel channel = data.get(position);

        if (channel.id == AppConfig.CUSTOM_TYPE_ADD_ID) {
            holder.mIvStatus.setImageResource(R.drawable.icon_tianjia);
            holder.mIvStatus.setVisibility(View.VISIBLE);

        } else if (channel.id >= AppConfig.CUSTOM_START_ID) {
            holder.mIvStatus.setImageResource(R.drawable.icon_zidingyi);
            holder.mIvStatus.setVisibility(View.GONE);
//            holder.mIvCollection.setImageResource(R.drawable.pic_shanchu);

        } else {
            if (channel.isCollectionType == 1) {
                //是"我的收藏"类型
                holder.mIvStatus.setImageResource(R.drawable.icon_shoucang);
                holder.mIvStatus.setVisibility(View.GONE);
//                holder.mIvCollection.setImageResource(R.drawable.icon_choucang_cancel);

            } else {
                holder.mIvStatus.setImageResource(R.drawable.icon_shoucang);
//                holder.mIvStatus.setVisibility(channel.collectionStatus == 1 ? View.VISIBLE : View.INVISIBLE);
                holder.mIvStatus.setVisibility(View.GONE);
//                holder.mIvCollection.setImageResource(channel.collectionStatus == 1 ? R.drawable.icon_choucang_cancel : R.drawable.icon_shoucang_n);
            }
        }

        if (isCurrentType && position == mCurrentPosition) {
            holder.mIvPlayStatus.setVisibility(View.VISIBLE);
            ImageLoadUtil.clearImage(mContext, holder.mIvPlayStatus);
            ImageLoadUtil.loadGif(mContext, holder.mIvPlayStatus, R.drawable.icon_play_status);

        } else {
            holder.mIvPlayStatus.setVisibility(View.GONE);
            ImageLoadUtil.clearImage(mContext, holder.mIvPlayStatus);
        }

        holder.tvChannelName.setText(Utils.simplifyName(channel.channel));

        holder.initData(channel);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onItemClick(position);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (null != onLongListener) {
                    if (channel.id != AppConfig.CUSTOM_TYPE_ADD_ID
                            && null != channel.parentId
                            && channel.parentId.size() == 1
                            && String.valueOf(AppConfig.CUSTOM_TYPE_ID).equals(channel.parentId.get(0))) {
                        onLongListener.onItemLongPress(position);
                    }
                }
                return true;
            }
        });


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
                    holder.tvChannelName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                    holder.tvChannelName.setSelected(true);
                    if (channel.id == AppConfig.CUSTOM_TYPE_ADD_ID) {
                        holder.mIvStatus.setImageResource(R.drawable.icon_tianjiahover);
                    }
                } else {
                    holder.tvChannelName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
                    holder.tvChannelName.setTextColor(Color.parseColor("#F0F0F0"));
                    holder.tvChannelName.getPaint().setFakeBoldText(false);
                    holder.tvChannelName.setEllipsize(TextUtils.TruncateAt.END);
                    holder.tvChannelName.setSelected(false);

                    if (channel.id == AppConfig.CUSTOM_TYPE_ADD_ID) {
                        holder.mIvStatus.setImageResource(R.drawable.icon_tianjia);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return null == data ? 0 : data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_status)
        ImageView mIvStatus;
        @BindView(R.id.tv_channel_name)
        public TextView tvChannelName;
        @BindView(R.id.iv_play_status)
        public ImageView mIvPlayStatus;

        private Channel channel;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public boolean isCanShow() {
            return channel.id != AppConfig.CUSTOM_TYPE_ADD_ID;
        }

        public void initData(Channel channel) {
            this.channel = channel;
        }

        public boolean hadSubChannel() {
            return channel.hadSubChannel();
        }
    }

}
