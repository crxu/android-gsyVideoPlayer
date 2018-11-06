package com.gsgd.live.ui.dialog;

import android.content.Context;
import android.graphics.RectF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import com.gsgd.live.data.events.PlayEvent;
import com.gsgd.live.data.listener.OnItemListener;
import com.gsgd.live.data.model.Channel;
import com.gsgd.live.data.model.ChannelType;
import com.gsgd.live.tv.FlyFrameView;
import com.gsgd.live.tv.TvContentLayout;
import com.gsgd.live.ui.adapter.MatchTvAdapter;
import com.gsgd.live.ui.base.BaseDialog;
import com.gsgd.live.ui.widgets.DividerItemDecoration;
import com.gsgd.live.utils.MeasureUtil;
import com.gsgd.live.utils.Utils;
import com.jiongbull.jlog.JLog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import hdpfans.com.R;

/**
 * 选择匹配的源界面
 */
public class SelectMatchTvDialog extends BaseDialog {

    @BindView(R.id.rv_list)
    RecyclerView mRecyclerView;

    @BindView(R.id.tv_content_layout)
    TvContentLayout mRelativeLayout;
    @BindView(R.id.fly_view)
    FlyFrameView mMainUpView;

    private LinearLayoutManager mLayoutManager;
    private MatchTvAdapter mAdapter;

    private ArrayList<ChannelType> channelTypes;
    private ArrayList<Channel> channels;//源列表

    public SelectMatchTvDialog(Context context, int theme, ArrayList<ChannelType> channelTypes, ArrayList<Channel> channels) {
        super(context, theme);
        this.channelTypes = channelTypes;
        this.channels = channels;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_match_tv;
    }

    @Override
    protected void screenAdapter() {
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = MeasureUtil.per2px(context.getResources(), 0.55);
        p.height = MeasureUtil.perh2px(context.getResources(), 0.55);
        p.gravity = Gravity.CENTER;
        getWindow().setAttributes(p);
    }

    @Override
    protected void initParams() {
        super.initParams();

        mAdapter = new MatchTvAdapter(context, channels, new OnItemListener() {
            @Override
            public void onItemClick(int position) {
                EventBus.getDefault().post(new PlayEvent.SelectChannelEvent(Utils.getSelectType(channels.get(position), channelTypes), channels.get(position)));
                dismiss();
            }

            @Override
            public void onItemFocus(int position, boolean hasFocus) {

            }
        });
        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(context, 1));
        mRecyclerView.setAdapter(mAdapter);

        initFlyFrame();

        focusItem(mRecyclerView, mLayoutManager, 0);
    }

    private void initFlyFrame() {
        mMainUpView.setMinTop(80);
        mMainUpView.setMaxTop(MeasureUtil.perh2px(context.getResources(), 0.55) - 80);
        mMainUpView.setIsNeedInit(false);
        mMainUpView.setUpRectResource(R.drawable.ic_item_bg);
        mMainUpView.setUpPaddingRect(new RectF(20, 10, 20, 10));
        mRelativeLayout.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                if (null != mMainUpView) {
                    mMainUpView.setDrawUpRectEnabled(true);
                    mMainUpView.setFocusView(newFocus, oldFocus, 1);
                    mMainUpView.bringToFront();
                }
            }
        });
    }

    private void focusItem(final RecyclerView recyclerView, final LinearLayoutManager layoutManager, final int position) {
        try {
            JLog.d("******->focusItem:" + position);
            layoutManager.scrollToPosition(position);
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    RecyclerView.ViewHolder holder = recyclerView.findViewHolderForLayoutPosition(position);
                    if (null != holder) {
                        holder.itemView.requestFocus();
                    }
                }
            });

        } catch (Exception e) {
            JLog.e(e);
        }
    }
}
