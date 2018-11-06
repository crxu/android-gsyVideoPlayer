package com.gsgd.live.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gsgd.live.data.listener.OnItemListener;
import com.gsgd.live.data.response.RespSource;
import com.gsgd.live.utils.ScaleUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hdpfans.com.R;

/**
 * 源列表
 */
public class SourceAdapter extends RecyclerView.Adapter<SourceAdapter.ViewHolder> {

    private Context mContext;
    private List<RespSource> sources;
    private int selectPosition;//选择的位置
    private OnItemListener mListener;

    public SourceAdapter(Context context, List<RespSource> sources, int selectPosition, OnItemListener listener) {
        this.mContext = context;
        this.sources = sources;
        this.selectPosition = selectPosition;
        this.mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_source, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        RespSource respSource = sources.get(position);

        if (position == 0) {
            holder.mTvSource.setText("主线/" + respSource.getSpeedDesc());

        } else {
            holder.mTvSource.setText("线路" + position + "/" + respSource.getSpeedDesc());
        }

        if ("极速".equals(respSource.getSpeedDesc())) {
            if (respSource.isHD()) {
                holder.itemView.setBackgroundResource(R.drawable.bg_speed_hdjs_select);

            } else {
                holder.itemView.setBackgroundResource(R.drawable.bg_speed_js_select);
            }

        } else if ("流畅".equals(respSource.getSpeedDesc())) {
            if (respSource.isHD()) {
                holder.itemView.setBackgroundResource(R.drawable.bg_speed_hdlc_select);

            } else {
                holder.itemView.setBackgroundResource(R.drawable.bg_speed_lc_select);
            }

        } else {
            if (respSource.isHD()) {
                holder.itemView.setBackgroundResource(R.drawable.bg_speed_hdyb_select);

            } else {
                holder.itemView.setBackgroundResource(R.drawable.bg_speed_yb_select);
            }
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
                if (hasFocus) {
                    holder.itemView.setPivotY(holder.itemView.getHeight());
                    ScaleUtil.scaleToBig(holder.itemView, 1.11f);

                } else {
                    ScaleUtil.scaleToNormal(holder.itemView);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return null == sources ? 0 : sources.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_source)
        TextView mTvSource;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
