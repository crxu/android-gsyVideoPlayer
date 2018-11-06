package com.gsgd.live.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gsgd.live.data.listener.OnItemListener;
import com.gsgd.live.data.model.MessageModel;
import com.gsgd.live.utils.RouterUtils;
import com.gsgd.live.utils.Utils;
import com.jiongbull.jlog.JLog;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import hdpfans.com.R;

/**
 * 消息列表
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private Context mContext;
    private List<MessageModel> mData;
    private OnItemListener mListener;

    public MessageAdapter(Context context, List<MessageModel> data, OnItemListener listener) {
        this.mContext = context;
        this.mData = data;
        this.mListener = listener;
    }

    @Override
    public int getItemCount() {
        return null == mData ? 0 : mData.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_tv_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final MessageModel model = mData.get(position);

        holder.titleTv.setText(model.getMessageTitle());
        holder.contentTv.setText(getEmoji(model.getMessageContent()));
        holder.timeTv.setText(Utils.timeAgo(model.getMtime()));
        holder.unread_message_count.setVisibility(model.getIsRead() == 0 ? View.VISIBLE : View.GONE);

        holder.mViewSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(position);
                }
            }
        });
        holder.mViewSub.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (null != mListener) {
                    mListener.onItemFocus(position, hasFocus);
                }

                holder.titleTv.setTextColor(hasFocus ? Color.parseColor("#555555") : Color.parseColor("#F0F0F0"));
                holder.contentTv.setTextColor(hasFocus ? Color.parseColor("#555555") : Color.parseColor("#F0F0F0"));
                holder.timeTv.setTextColor(hasFocus ? Color.parseColor("#555555") : Color.parseColor("#F0F0F0"));
                holder.contentTv.getPaint().setFakeBoldText(hasFocus);

                if (hasFocus) {
                    holder.unread_message_count.setVisibility(View.GONE);
                }
            }
        });

        holder.mTvViewMsgInfo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!holder.mViewSub.hasFocus()) {
                        holder.mViewSub.setBackgroundResource(R.drawable.bg_channel_sub_select);
                        holder.titleTv.setTextColor(Color.parseColor("#F0F0F0"));
                        holder.contentTv.setTextColor(Color.parseColor("#F0F0F0"));
                        holder.timeTv.setTextColor(Color.parseColor("#F0F0F0"));
                    }
                    holder.contentTv.getPaint().setFakeBoldText(false);
                    holder.mTvViewMsgInfo.setVisibility(View.INVISIBLE);

                } else {
                    holder.contentTv.getPaint().setFakeBoldText(true);
                }
            }
        });

        holder.mTvViewMsgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (model.getTypeId()) {
                    case 100://聊天
                        RouterUtils.getInstance().gotoChatApp(mContext, 0);
                        break;

                    case 101://设备管理页面
                        RouterUtils.getInstance().goToDeviceManager(mContext);
                        break;

                    case 102: //测量成功
                        if (model.getMessageContent().contains("血压")) {
                            RouterUtils.getInstance().goToHealthInfo(mContext, 1);

                        } else if (model.getMessageContent().contains("血糖")) {
                            RouterUtils.getInstance().goToHealthInfo(mContext, 3);

                        } else if (model.getMessageContent().contains("体温")) {
                            RouterUtils.getInstance().goToHealthInfo(mContext, 2);
                        }
                        break;
                }
            }
        });
    }

    /**
     * 文字转表情
     */
    public static String getEmoji(String str) {
        String string = str;
        String rep = "\\[(.*?)\\]";
        Pattern p = Pattern.compile(rep);
        Matcher m = p.matcher(string);
        while (m.find()) {
            String s1 = m.group().toString();
            String s2 = s1.substring(1, s1.length() - 1);
            String s3;
            try {
                s3 = String.valueOf((char) Integer.parseInt(s2, 16));
                string = string.replace(s1, s3);

            } catch (Exception e) {
                JLog.e(e);
            }
        }
        return string;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.data_lay)
        public LinearLayout mViewSub;
        @BindView(R.id.title_tv)
        public TextView titleTv;
        @BindView(R.id.content_tv)
        public TextView contentTv;
        @BindView(R.id.time_tv)
        public TextView timeTv;
        @BindView(R.id.unread_message_count)
        public ImageView unread_message_count;
        @BindView(R.id.message_info_btn)
        public TextView mTvViewMsgInfo;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
