package com.gsgd.live.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gsgd.live.data.listener.OnItemListener;
import com.gsgd.live.data.model.CustomTypeInfo;
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
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private Context mContext;
    private List<CustomTypeInfo> mData;
    private OnItemListener mListener;

    public CustomAdapter(Context context, OnItemListener listener) {
        this.mContext = context;
        this.mListener = listener;
    }

    public void setData(List<CustomTypeInfo> data) {
        this.mData = data;
        try {
            notifyDataSetChanged();

        } catch (Exception e) {
            JLog.e(e);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_custom, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final CustomTypeInfo info = mData.get(position);

        holder.mTvCustom.setCompoundDrawablesWithIntrinsicBounds(info.type == -1
                ? mContext.getResources().getDrawable(R.drawable.icon_custom_guide_select)
                : mContext.getResources().getDrawable(R.drawable.icon_custom_select), null, null, null);

        holder.mTvCustom.setText(TextUtils.isEmpty(info.phoneName) ? String.valueOf(info.userId) : info.phoneName);

        holder.mTvCustom.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (null != mListener) {
                    mListener.onItemFocus(position, hasFocus);
                }

                holder.mTvCustom.getPaint().setFakeBoldText(hasFocus);
            }
        });
    }

    @Override
    public int getItemCount() {
        return null != mData ? mData.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_custom_item)
        public TextView mTvCustom;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
