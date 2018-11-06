package com.gsgd.live.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.util.TimeUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gsgd.live.AppConfig;
import com.gsgd.live.data.listener.OnItemListener;
import com.gsgd.live.data.model.Channel;
import com.gsgd.live.data.model.PlayOrder;
import com.gsgd.live.data.response.RespPlayOrder;
import com.gsgd.live.utils.DateUtils;
import com.gsgd.live.utils.ImageLoadUtil;
import com.gsgd.live.utils.PlayOrdersMananger;
import com.gsgd.live.utils.Utils;
import com.jiongbull.jlog.JLog;

import java.util.List;
import java.util.Observable;

import butterknife.BindView;
import butterknife.ButterKnife;
import hdpfans.com.R;

/**
 * 子频道频道列表
 */
public class ChannelSubAdapter extends RecyclerView.Adapter<ChannelSubAdapter.ViewHolder> {

    private Context mContext;
    private List<PlayOrder> data;
    private OnItemListener mListener;
    private int mCurrentPosition = -1;//-1 不存在选中的
    private int mSelectIndex;
    int[] colos = new int[4];
    int[] bgs = new int[4];

    public ChannelSubAdapter(Context context, OnItemListener listener) {
        mContext = context;
        this.mListener = listener;
        Resources resources = mContext.getResources();
        colos[0] = resources.getColor(R.color.color_cccccc);
        bgs[0] = R.drawable.bg_sub_channel_normal_bg;
        colos[1] = resources.getColor(R.color.color_555555);
        bgs[1] = R.drawable.bg_sub_channel_focus_bg;
        colos[2] = resources.getColor(R.color.color_ffffff);
        bgs[2] = R.drawable.bg_sub_channel_select_bg;
        colos[3] = resources.getColor(R.color.color_a7a7a7);
        bgs[3] = R.drawable.bg_sub_channel_disenable_bg;
    }

    public void setData(List<PlayOrder> data, int selectIndex, boolean isExit) {
        this.data = data;
        this.mCurrentPosition = isExit ? selectIndex : -1;
        this.mSelectIndex = selectIndex == -1 ? 0 : selectIndex;
        try {
            notifyDataSetChanged();
        } catch (Exception e) {
            JLog.e(e);
        }
    }

    public PlayOrder getItem(int position) {
        return this.data.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_channel_sub, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final PlayOrder channel = getItem(position);
        if (position == mCurrentPosition) {
            holder.mIvPlayStatus.setVisibility(View.VISIBLE);
            ImageLoadUtil.loadGif(mContext, holder.mIvPlayStatus, R.drawable.icon_play_status);
        } else {
            holder.mIvPlayStatus.setVisibility(View.GONE);
            ImageLoadUtil.clearImage(mContext, holder.mIvPlayStatus);
        }
        setItemBg(holder, position, false);
        holder.tvTime.setText(channel.getShowTime());
        holder.tvChannelName.setText(Utils.simplifyName(channel.getChannelName()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onItemClick(position);
                }
            }
        });
        setOrderStatue(holder, channel);
        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (null != mListener) {
                    mListener.onItemFocus(position, hasFocus);
                }
                setItemBg(holder, position, hasFocus);
                if (hasFocus) {
                    setItemStatue(holder, 1);
                } else {
                    if (position == mCurrentPosition) {
                        setItemStatue(holder, 2);
                    } else {
                        if (channel.getTime() <= System.currentTimeMillis()) {
                            setItemStatue(holder, 3);
                        } else {
                            setItemStatue(holder, 0);
                        }
                    }

                }
            }
        });
    }

    private void setItemBg(ViewHolder holder, int position, boolean hasFocus) {
        if ((mCurrentPosition == position) && !hasFocus) {
            holder.itemView.setBackgroundResource(R.drawable.bg_item_cover2);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.bg_channel_sub_select);
        }
    }

    @Override
    public int getItemCount() {
        return null == data ? 0 : data.size();
    }

    private void setOrderStatue(ViewHolder holder, PlayOrder order) {
        if (order.isFengmi()) {//蜂蜜视频可以点播
            holder.tvPlay.setVisibility(View.VISIBLE);
        } else {
            holder.tvPlay.setVisibility(View.GONE);
        }
        //节目时间小于当前时间不可以预约
        if (order.getTime() <= System.currentTimeMillis()) {
            holder.tvOrder.setVisibility(View.GONE);
            setItemStatue(holder, 3);
        } else {
            setItemStatue(holder, 0);
            holder.tvOrder.setVisibility(View.VISIBLE);
            if (order.isOrder(mContext)) {
                holder.tvOrder.setText(R.string.already_order);
            } else {
                holder.tvOrder.setText(R.string.order);
            }
        }
    }

    /**
     * @param holder
     * @param type   0 正常 1 焦点 2 选中 3 不可预约
     */
    private void setItemStatue(ViewHolder holder, int type) {
        int textColor = colos[0];
        int bg = bgs[0];
        switch (type) {
            case 1:
                textColor = colos[1];
                bg = bgs[1];
                break;
            case 2:
                textColor = colos[2];
                bg = bgs[2];
                break;
            case 3:
                textColor = colos[3];
                bg = bgs[3];
                break;
            default:
                textColor = colos[0];
                bg = bgs[0];
                break;
        }
        holder.tvTime.setTextColor(textColor);
        holder.tvOrder.setTextColor(textColor);
        holder.tvOrder.setBackgroundResource(bg);
        holder.tvPlay.setTextColor(textColor);
        holder.tvPlay.setBackgroundResource(bg);
        holder.tvChannelName.setTextColor(textColor);
        if (type == 2 || type == 1) {//获取焦点或者选中后字体加粗
            holder.tvPlay.getPaint().setFakeBoldText(true);
            holder.tvTime.getPaint().setFakeBoldText(true);
            holder.tvChannelName.getPaint().setFakeBoldText(true);
            holder.tvOrder.getPaint().setFakeBoldText(true);
            holder.tvChannelName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            holder.tvChannelName.setSelected(true);
        } else {
            holder.tvPlay.getPaint().setFakeBoldText(false);
            holder.tvTime.getPaint().setFakeBoldText(false);
            holder.tvChannelName.getPaint().setFakeBoldText(false);
            holder.tvOrder.getPaint().setFakeBoldText(false);
            holder.tvChannelName.setEllipsize(TextUtils.TruncateAt.END);
            holder.tvChannelName.setSelected(true);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_channel_name)
        public TextView tvChannelName;
        @BindView(R.id.iv_play_status)
        public ImageView mIvPlayStatus;
        @BindView(R.id.tv_time)
        public TextView tvTime;
        @BindView(R.id.tv_order)
        public TextView tvOrder;
        @BindView(R.id.tv_play)
        public TextView tvPlay;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public int getSelectIndex() {
        return mSelectIndex;
    }

}
