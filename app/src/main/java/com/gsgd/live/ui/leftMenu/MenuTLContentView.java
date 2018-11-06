package com.gsgd.live.ui.leftMenu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;
import com.gsgd.live.AppConfig;
import com.gsgd.live.data.events.PlayEvent;
import com.gsgd.live.data.listener.AbstractOnMenuDialogImpl;
import com.gsgd.live.data.listener.ChannelListener;
import com.gsgd.live.data.listener.OnItemListener;
import com.gsgd.live.data.listener.OnItemLongListener;
import com.gsgd.live.data.model.Channel;
import com.gsgd.live.data.model.ChannelType;
import com.gsgd.live.data.model.PlayOrder;
import com.gsgd.live.ui.adapter.ChannelAdapter;
import com.gsgd.live.ui.adapter.ChannelSubAdapter;
import com.gsgd.live.ui.adapter.ChannelTypeAdapter;
import com.gsgd.live.ui.widgets.DividerItemDecoration;
import com.gsgd.live.utils.PlayControlUtil;
import com.gsgd.live.utils.PlayOrdersMananger;
import com.gsgd.live.utils.ToastUtil;
import com.jiongbull.jlog.JLog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hdpfans.com.R;

/**
 * @author zhangqy
 * @Description 电视直播
 * @date 2017/12/22
 */
public class MenuTLContentView extends LinearLayout {

    private static final String TAG = MenuTLContentView.class.getName();
    private static final int ANIMATION_TIME = 100;//动画时间

    @BindView(R.id.lv_type)
    RecyclerView mLvChannelType;
    @BindView(R.id.lv_channel)
    RecyclerView mLvChannel;
    @BindView(R.id.lv_channel_sub)
    RecyclerView mLvSubChannel;
    @BindView(R.id.rl_channel_content)
    View mRlChannelContent;//二级菜单
    @BindView(R.id.bg_channel_cover)
    View mBgChannelCover;
    @BindView(R.id.rl_sub_channel_content)
    View mRlChannelSubContent;//三级菜单
    @BindView(R.id.iv_right_arrow)
    View mIvRightArrow;
    @BindView(R.id.iv_right_arrow2)
    View mIvRightArrow2;
    @BindView(R.id.tv_empty_tip)
    View mTvEmptyTip;
    @BindView(R.id.tv_empty_tip_sub)
    TextView mTvEmptySub;
    @BindView(R.id.ll_order_result)
    LinearLayout mLlOrderResultLayout;

    private Context mContext;
    private ArrayList<ChannelType> mChannelTypes;
    private LinearLayoutManager mLayoutManagerType;
    private LinearLayoutManager mLayoutManagerChannel;
    private LinearLayoutManager mLayoutManagerSubChannel;
    private ChannelTypeAdapter mChannelTypeAdapter;
    private ChannelAdapter mChannelAdapter;
    private ChannelSubAdapter mChannelSubAdapter;

    //当前播放视频选中的位置
    private int mCurrentSelectTypePosition = 0;
    private int mCurrentSelectPosition = 0;
    //当前光标选中的位置
    private int selectTypePosition = 0;//栏目
    private int selectChannelPosition = 0;//频道
    private int selectChannelSubPosition = 0;//子频道
    private boolean isTypeChange = false;//类型是否发生变化
    private ChannelListener mChannelListener;
    private AbstractOnMenuDialogImpl menuDialogListener;
    private MenuOrderContentView mMenuOrderContentView;
    private boolean isPlayCustom = false;

    public MenuTLContentView(Context context) {
        this(context, null);
    }

    public MenuTLContentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        setHorizontalGravity(HORIZONTAL);
        inflate(context, R.layout.view_menu_tv_live_content, this);
        ButterKnife.bind(this, this);
    }

    public interface OnFocusGetListener {
        void onIndex(int position, boolean isOrder);
    }

    public void setMenuOrderContentView(MenuOrderContentView view) {
        this.mMenuOrderContentView = view;
        if (view == null) return;
        view.setOnFocusGetListener(new OnFocusGetListener() {
            @Override
            public void onIndex(int position, boolean isOrder) {
                try {
                    RecyclerView.ViewHolder holder = mLvSubChannel.findViewHolderForLayoutPosition(position);
                    mChannelSubAdapter.getItem(position).setOrder(isOrder ? 1 : 0);
                    if (holder != null) {
                        holder.itemView.requestFocus();
                        ChannelSubAdapter.ViewHolder viewHolder = (ChannelSubAdapter.ViewHolder) holder;
                        if (mChannelSubAdapter.getItem(position).isOrder(mContext)) {
                            viewHolder.tvOrder.setText(R.string.already_order);
                        } else {
                            viewHolder.tvOrder.setText(R.string.order);
                        }
                    } else {
                        for (int i = 0; i < mChannelSubAdapter.getItemCount(); i++) {
                            holder = mLvSubChannel.findViewHolderForLayoutPosition(i);
                            if (holder != null) {
                                holder.itemView.requestFocus();
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    JLog.e(e);
                }
            }
        });
    }

    public void initData(PlayControlUtil.PlayInfoListener infoListener, ChannelListener listener, AbstractOnMenuDialogImpl menuDialogListener) {
        this.mChannelTypes = infoListener.getChannelTypeList();
        this.mChannelListener = listener;
        this.menuDialogListener = menuDialogListener;
        this.isPlayCustom = infoListener.isPlayCustom();

        mCurrentSelectTypePosition = getCurrentSelectTypePosition(infoListener.getCurrentChannelType());
        mCurrentSelectPosition = getCurrentSelectChannelPosition(infoListener.getCurrentChannel());

        selectTypePosition = mCurrentSelectTypePosition;
        selectChannelPosition = mCurrentSelectTypePosition;
        selectChannelSubPosition = -1;

        JLog.d(TAG, "******->mCurrentSelectTypePosition:" + mCurrentSelectTypePosition
                + "||mCurrentSelectPosition:" + mCurrentSelectPosition);
    }

    private int getCurrentSelectTypePosition(ChannelType type) {
        int position = 0;
        boolean hasFind = false;
        try {
            if (null != type) {
                position = mChannelTypes.indexOf(type);
                if (position != -1) {
                    hasFind = true;
                }
            }
        } catch (Exception e) {
            JLog.e(e);
        }
        if (!hasFind) {
            position = 0;
        }
        return position;
    }

    private int getCurrentSelectChannelPosition(Channel channel) {
        int position = 0;
        try {
            if (null != channel) {
                int indexOf = getChannels(mCurrentSelectTypePosition).indexOf(channel);
                if (indexOf != -1) {
                    position = indexOf;
                }
            }
        } catch (Exception e) {
            JLog.e(e);
        }

        return position;
    }

    private ArrayList<Channel> getChannels(int position) {
        return mChannelTypes.get(position).channels;
    }

    private Channel getChannel(int typePosition, int position) {
        return mChannelTypes.get(typePosition).channels.get(position);
    }

    private Channel getChannel(ChannelType type, int position) {
        return type.channels.get(position);
    }

    private ChannelType getChannelType(int typePosition) {
        return mChannelTypes.get(typePosition);
    }

    /**
     * 刷新UI
     */
    public void refreshUI() {
        //初始化栏目列表
        initTypeListUI();
        //初始化栏目子列表
        initChannelListUI();
        //初始化节目子列表
        initChannelSubListUI();

        mChannelTypeAdapter.setData(mChannelTypes);
        mChannelAdapter.setData(getChannels(mCurrentSelectTypePosition), false, 0);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AppConfig.isMenuDialogEnable = true;
            }
        }, 400);

        if (selectChannelSubPosition >= 0) {
            MenuUtil.toggleFocusBg(true, mLvSubChannel);

        } else {
            MenuUtil.toggleFocusBg(true, mBgChannelCover);
        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isPlayCustom) {
                    //不做选中
                    refreshListUI(mCurrentSelectTypePosition, mCurrentSelectPosition, false);

                } else {
                    initSelectUI();
                }
            }
        }, 200);

        mLlOrderResultLayout.setVisibility(INVISIBLE);
    }

    private Handler mHandler = new Handler();

    private void initTypeListUI() {
        mChannelTypeAdapter = new ChannelTypeAdapter(mContext, mCurrentSelectTypePosition, new OnItemListener() {
            @Override
            public void onItemClick(int position) {
            }

            @Override
            public void onItemFocus(int position, boolean hasFocus) {
                boolean isSame = false;
                if (hasFocus) {
                    isSame = selectTypePosition == position;
                    selectTypePosition = position;
                    isTypeChange = !isSame;
                }

                if (hasFocus) {
                    //动态刷新栏目子列表
                    mChannelAdapter.setData(getChannels(selectTypePosition), selectTypePosition == mCurrentSelectTypePosition, mCurrentSelectPosition);
                    if (0 == selectTypePosition && getChannels(selectTypePosition).isEmpty()) {
                        mTvEmptyTip.setVisibility(View.VISIBLE);
                        mIvRightArrow2.setVisibility(View.GONE);
                    } else {
                        mTvEmptyTip.setVisibility(View.GONE);
                        mIvRightArrow2.setVisibility(View.VISIBLE);
                    }
                    if (!isSame) {
                        refreshChannelListUI();
                    }
                }
            }
        });
        mLayoutManagerType = new LinearLayoutManager(mContext);
        mLvChannelType.setLayoutManager(mLayoutManagerType);
        mLvChannelType.addItemDecoration(new DividerItemDecoration(mContext, 0));
        mLvChannelType.setAdapter(mChannelTypeAdapter);
    }

    private void initChannelListUI() {
        mChannelAdapter = new ChannelAdapter(mContext, new OnItemListener() {
            @Override
            public void onItemClick(int position) {
                //选中播放
                ChannelType channelType = getChannelType(selectTypePosition);
                Channel channel = getChannel(channelType, position);
                if (channelType.id == AppConfig.CUSTOM_TYPE_ID
                        && channel.id == AppConfig.CUSTOM_TYPE_ADD_ID) {
                    EventBus.getDefault().post(new PlayEvent.ShowAddSourceDialog());

                } else {
                    PlayEvent.SelectChannelEvent selectChannelEvent = new PlayEvent.SelectChannelEvent(channelType, channel);
                    selectChannelEvent.setClickDialog(true);
                    EventBus.getDefault().post(selectChannelEvent);
                }

                dismiss();
            }

            @Override
            public void onItemFocus(int position, boolean hasFocus) {
                if (hasFocus) {
                    selectChannelPosition = position;
                    showSecondArrowAndHideSubContent();
//                    if (mRlChannelSubContent.getVisibility() == VISIBLE) {
//                        mRlChannelSubContent.setVisibility(GONE);
////                        Channel channel = getChannel(selectTypePosition, selectChannelPosition);
////                        getSubChannels(channel);
//                    }
                }
            }

        }, new OnItemLongListener() {
            @Override
            public void onItemLongPress(int position) {
                ChannelType channelType = getChannelType(selectTypePosition);
                if (channelType.id == AppConfig.CUSTOM_TYPE_ID) {
                    EventBus.getDefault().post(new PlayEvent.ShowDeleteSourceDialog(getChannel(channelType, position)));
                }
                dismiss();

            }
        }, new ChannelListener() {
            @Override
            public void optChannel(int type, Channel channel) {
                //1 删除自定义源；2 添加收藏；3 取消收藏
                if (null != mChannelListener) {
                    mChannelListener.optChannel(type, channel);
                    if (selectTypePosition == 0 && type == 3) {
                        dismiss();
                    }
                }
            }
        });
        mLayoutManagerChannel = new LinearLayoutManager(mContext);
        mLvChannel.setLayoutManager(mLayoutManagerChannel);
        mLvChannel.addItemDecoration(new

                DividerItemDecoration(mContext, 0));
        mLvChannel.setAdapter(mChannelAdapter);
    }

    private void dismiss() {
        // 对话框消失
        if (null != menuDialogListener) {
            menuDialogListener.onDismiss();
        }
    }

    private void initChannelSubListUI() {
        mChannelSubAdapter = new ChannelSubAdapter(mContext, new OnItemListener() {
            @Override
            public void onItemClick(int position) {
                PlayOrder item = mChannelSubAdapter.getItem(position);
                if (item.isFengmi() || (item.getTime() > System.currentTimeMillis() && !item.isOrder(mContext))) {
                    if (mMenuOrderContentView == null) return;
                    mMenuOrderContentView.show(mChannelSubAdapter.getItem(position), position, new MenuOrderContentView.OnPlayClickListener() {
                        @Override
                        public void onPlay(int position) {
                            PlayOrder item = mChannelSubAdapter.getItem(position);
                            if (item.isFengmi()) { //判断是否是蜜蜂视频
                                try {
                                    //跳转蜜蜂
                                    Intent intent = new Intent("com.mipt.videohj.intent.action.VOD_PLAY_ACTION");
                                    intent.putExtra("extra_video_id", String.valueOf(item.getFengmiId()));
                                    intent.putExtra("extra_drama_index", String.valueOf(item.getOrderIndex()));
                                    intent.putExtra("invokeFrom", "yidu");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    mContext.startActivity(intent);
                                    //关闭视频
                                    Intent gsgd_Intent = new Intent("action_close_play_gsgd");
                                    mContext.sendBroadcast(gsgd_Intent);
                                    AppConfig.isClickFengmi = true;
                                    EventBus.getDefault().post(new PlayEvent.SelectFenMiEvent());

                                } catch (Exception e) {
                                    JLog.e(e);
                                    ToastUtil.showToast("无法跳转到相应视频！");
                                    AppConfig.isClickFengmi = false;
                                }
                            }
                        }

                        @Override
                        public void onOrder(boolean isOrder) {
                            showOrderOrCancelOrderResult(isOrder);
                        }
                    });
                } else if (item.isOrder(mContext)) {
                    PlayOrdersMananger.cancelOrder(mContext, item);
                    mChannelSubAdapter.getItem(position).setOrder(0);
                    ChannelSubAdapter.ViewHolder holder = (ChannelSubAdapter.ViewHolder) mLvSubChannel.findViewHolderForLayoutPosition(position);
                    holder.tvOrder.setText(R.string.order);
                    showOrderOrCancelOrderResult(false);
                }
            }

            @Override
            public void onItemFocus(int position, boolean hasFocus) {
                if (hasFocus) {
                    selectChannelSubPosition = position;
                }
            }
        });
        mLayoutManagerSubChannel = new LinearLayoutManager(mContext);
        mLvSubChannel.setLayoutManager(mLayoutManagerSubChannel);
        mLvSubChannel.addItemDecoration(new DividerItemDecoration(mContext, 0));
        mLvSubChannel.setAdapter(mChannelSubAdapter);
    }

    private void showOrderOrCancelOrderResult(boolean isOrder) {
        mLlOrderResultLayout.setVisibility(VISIBLE);
        ImageView iv = (ImageView) mLlOrderResultLayout.getChildAt(0);
        TextView tv = (TextView) mLlOrderResultLayout.getChildAt(1);
        iv.setImageResource(isOrder ? R.drawable.pic_yuyuechenggong : R.drawable.pic_quxiaoyuyue);
        tv.setText(isOrder ? R.string.order_success : R.string.cancel_order);
        mLlOrderResultLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mLlOrderResultLayout.setVisibility(INVISIBLE);
            }
        }, 1000);
    }

    private void initSelectUI() {
        mLvChannelType.post(new Runnable() {
            @Override
            public void run() {
                focusItem(mLvChannelType, mLayoutManagerType, mCurrentSelectTypePosition, 1);
            }
        });
    }

    /**
     * 是否有焦点
     */
    public boolean isHasFocus() {
        return mLvChannelType.hasFocus()
                || mLvChannel.hasFocus()
                || mLvSubChannel.hasFocus();
    }

    /**
     * 处理向上或向下事件
     */
    public void handleUpOrDownFocus(int keyCode, boolean result) {
        boolean isDown = KeyEvent.KEYCODE_DPAD_DOWN == keyCode;

        if (mLvChannelType.hasFocus()) {
            focusNextView(mLvChannelType, isDown ? View.FOCUS_DOWN : View.FOCUS_UP, isDown, isDown ? 80 : -80, mChannelTypeAdapter.getItemCount(), selectTypePosition);

            return;
        }
        if (mLvChannel.hasFocus()) {
            //由于暂无节目显示时，需要使焦点在暂无节目视图中，这边做了一个假效果，就是焦点还在栏目，只是隐藏了特效
            if (mRlChannelSubContent.getVisibility() == VISIBLE) {
                return;
            }
            if ((!isDown && selectChannelPosition <= 0) || (isDown && selectChannelPosition >= mChannelAdapter.getItemCount() - 1)) {
                return;
            }

            MenuUtil.toggleFocusBg(true, mBgChannelCover);
            showSecondArrowAndHideSubContent();
            focusNextView(mLvChannel, isDown ? View.FOCUS_DOWN : View.FOCUS_UP, isDown, isDown ? 80 : -80, mChannelAdapter.getItemCount(), selectChannelPosition);

            return;
        }

        if (mLvSubChannel.hasFocus()) {
            focusNextView(mLvSubChannel, isDown ? View.FOCUS_DOWN : View.FOCUS_UP, isDown, isDown ? 80 : -80, mChannelSubAdapter.getItemCount(), selectChannelSubPosition);
        }
    }

    private void showSecondArrowAndHideSubContent() {
        if (mRlChannelSubContent.getVisibility() == GONE) {
            mIvRightArrow2.setVisibility(View.VISIBLE);
        }
        if (mRlChannelSubContent.getVisibility() == VISIBLE) {
            mRlChannelSubContent.setVisibility(GONE);
            mTvEmptySub.setVisibility(GONE);
        }
    }

    private void focusNextView(RecyclerView layout, int direction, boolean isDown, int dex, int size, int position) {
        try {
            View focusView = layout.getFocusedChild();
            if (focusView == null) {
                return /*result*/;
            } else {
                View nextView = FocusFinder.getInstance().findNextFocus(layout, focusView, direction);

                boolean isNeedScroll = MenuUtil.isNeedScrollCenter(isDown, size, position);
                if (nextView != null) {
                    if (isNeedScroll) {
                        layout.scrollBy(0, dex);
                    }
                    nextView.requestFocus();
                    return /*true*/;

                } else {
                    layout.scrollBy(0, dex);
                    View nextView2 = FocusFinder.getInstance().findNextFocus(layout, focusView, direction);
                    if (nextView2 != null) {
                        nextView2.requestFocus();
                    }
                    return /*true*/;
                }
            }

        } catch (Exception e) {
            JLog.e(e);
        }
    }

    /**
     * 重新获取焦点
     */
    public boolean handleGetFocus() {
        toggleChannelUI(1);
        int position = selectTypePosition;
        refreshTypeListUI(position, true);

        MenuUtil.toggleFocusBg(true, mLvChannelType);

        return true;
    }

    /**
     * 处理向右的焦点
     */
    public void handleRightFocus() {
        if (mLvChannelType.hasFocus()) {
            //判断二级列表是否有内容
            if (mTvEmptyTip.getVisibility() != View.VISIBLE) {
                int position = selectTypePosition;
                if (isTypeChange) {
                    mLvChannel.requestFocus();
                } else {
                    focusItem(mLvChannel, mLayoutManagerChannel, selectChannelPosition, 0);
                }
                refreshTypeListUI(position, false);

                MenuUtil.toggleFocusBg(true, mBgChannelCover);
                MenuUtil.toggleFocusBg(false, mLvChannelType);
            }
        } else if (mLvChannel.hasFocus()) {
            if (mRlChannelSubContent.getVisibility() == VISIBLE) return;
            RecyclerView.ViewHolder holder = mLvChannel.findViewHolderForAdapterPosition(selectChannelPosition);
            if (holder == null) return;
            final Channel channel = getChannel(selectTypePosition, selectChannelPosition);
            PlayOrdersMananger.getPlayOrder((int) channel.id, new PlayOrdersMananger.OnDownloadListener() {
                @Override
                public void onSuccess(int channelId, List<PlayOrder> list) {
                    if (list.isEmpty()) {
                        onFailed(channelId);
                    } else {
                        handleGetSubChannelsSuccess(channelId, list, channel);
                    }
                }

                @Override
                public void onFailed(int channelId) {
                    if (channelId == channel.id) {
                        mTvEmptySub.setText(R.string.no_program_message);
                    }
                }

                @Override
                public void onStart(long channelId) {
                    if (channelId == channel.id) {
                        mTvEmptySub.setText(R.string.loading);
                        mTvEmptySub.setVisibility(VISIBLE);
                        mLvSubChannel.setVisibility(INVISIBLE);
                        //没有节目时，栏目列表强制失去焦点特效，其实焦点仍在
                        refreshChannelListUI(selectChannelPosition, false, false);
                    }
                }

                @Override
                public void onPreStart(long channelId) {
                    if (channelId == channel.id) {
                        toggleChannelUI(2);
                    }
                }
            });
        }
    }

    private void handleGetSubChannelsSuccess(int channelId, List<PlayOrder> list, Channel channel) {
        if (channelId == channel.id) {
            ChannelType type = getChannelType(selectTypePosition);
            //当前播放的是不是存在这个栏目中
            boolean isExist = (mCurrentSelectTypePosition == selectTypePosition) && (mCurrentSelectPosition == selectChannelPosition);
            int selectIndex = -1;
            boolean isFind = false;
            long nowTime = System.currentTimeMillis();
            for (PlayOrder order : list) {
                order.setType(type.type);
                order.setChannelType(channel.channel);
                order.setChannelId(channel.id);
                order.setKey();
                order.setOrder(-1);
                if (!isFind) {//存在，计算出当前播放的节目index
                    if (order.getTime() <= nowTime) {
                        selectIndex++;
                    } else {
                        isFind = true;
                    }
                }
            }
            mChannelSubAdapter.setData(list, selectIndex, isExist);
            mTvEmptySub.setVisibility(GONE);
            mLvSubChannel.setVisibility(VISIBLE);
            int scrollY3 = getScrollY(mChannelSubAdapter.getItemCount(), selectIndex);
            if (scrollY3 > 0) {
                mLayoutManagerSubChannel.scrollToPositionWithOffset(selectIndex, scrollY3);
            } else {
                mLayoutManagerSubChannel.scrollToPosition(selectIndex);
            }
            mLvSubChannel.postDelayed(new Runnable() {
                @Override
                public void run() {
                    focusItem(mLvSubChannel, mLayoutManagerSubChannel, mChannelSubAdapter.getSelectIndex(), 0);
                    refreshChannelListUI(selectChannelPosition, false, false);
                    MenuUtil.toggleFocusBg(false, mBgChannelCover);
                    MenuUtil.toggleFocusBg(true, mLvSubChannel);
                }
            }, 10);
        }
    }

    /**
     * 切换显示
     *
     * @param type 0 显示一级菜单，加箭头；1 显示二级菜单 有箭头；2 显示三级菜单 无箭头
     */

    private void toggleChannelUI(int type) {
        if (0 == type) {
            mRlChannelContent.setVisibility(View.GONE);
            mRlChannelSubContent.setVisibility(View.GONE);
            mIvRightArrow.setVisibility(View.VISIBLE);
            mIvRightArrow2.setVisibility(View.GONE);

        } else if (1 == type) {
            if (mRlChannelContent.getVisibility() == View.GONE) {
                mRlChannelContent.setVisibility(View.VISIBLE);
                //执行动画
                ViewAnimator
                        .animate(mRlChannelContent)
                        .dp().translationX(-240, 0)
                        .alpha(0.5f, 1)
                        .duration(ANIMATION_TIME)
                        .start();
            }
            mIvRightArrow.setVisibility(View.GONE);
            mIvRightArrow2.setVisibility(View.VISIBLE);

            if (mRlChannelSubContent.getVisibility() == View.VISIBLE) {
                //执行动画
                ViewAnimator
                        .animate(mRlChannelSubContent)
                        .dp().width(240, 0)
                        .alpha(1, 0.5f)
                        .onStop(new AnimationListener.Stop() {
                            @Override
                            public void onStop() {
                                mRlChannelSubContent.setVisibility(View.GONE);
                            }
                        })
                        .duration(ANIMATION_TIME)
                        .start();
            }

        } else if (2 == type) {
            if (mRlChannelSubContent.getVisibility() == VISIBLE) return;
            mRlChannelContent.setVisibility(View.VISIBLE);
            mIvRightArrow.setVisibility(View.GONE);
            mIvRightArrow2.setVisibility(View.GONE);
            //执行动画
            mRlChannelSubContent.setVisibility(View.VISIBLE);
            ViewAnimator
                    .animate(mRlChannelSubContent)
                    .dp().width(0, 240)
                    .alpha(0.5f, 1)
                    .duration(ANIMATION_TIME)
                    .start();
        }
    }

    /**
     * 处理向左的焦点
     *
     * @return 返回true表示此组件需要loss焦点
     */
    public boolean handleLeftFocus() {
        if (mLvSubChannel.hasFocus()) {
            int position = selectChannelPosition;
            refreshChannelListUI(position, true, false);
            mIvRightArrow2.setVisibility(View.VISIBLE);
            toggleChannelUI(1);
            MenuUtil.toggleFocusBg(true, mBgChannelCover);

        } else if (mLvChannel.hasFocus()) {
            //由于暂无节目显示时，需要使焦点在暂无节目视图中，这边做了一个假效果，就是焦点还在栏目，只是隐藏了特效
            if (mRlChannelSubContent.getVisibility() == VISIBLE && mTvEmptySub.getVisibility() == VISIBLE) {
                int position = selectChannelPosition;
                refreshChannelListUI(position, true, true);
                mIvRightArrow2.setVisibility(View.VISIBLE);
                toggleChannelUI(1);
                MenuUtil.toggleFocusBg(true, mBgChannelCover);
            } else {
                final RecyclerView.ViewHolder holder = mLvChannel.findViewHolderForAdapterPosition(selectChannelPosition);
                if (null != holder) {
                    mIvRightArrow2.setVisibility(View.VISIBLE);
                    MenuUtil.toggleFocusBg(true, mBgChannelCover);
                    toggleChannelUI(1);
                    int position = selectTypePosition;
                    refreshTypeListUI(position, true);
                    MenuUtil.toggleFocusBg(false, mBgChannelCover);
                    MenuUtil.toggleFocusBg(true, mLvChannelType);
                }
            }
        } else if (mLvChannelType.hasFocus()) {
            MenuUtil.toggleFocusBg(false, mLvChannelType);
            toggleChannelUI(0);
            int position = selectTypePosition;
            refreshTypeListUI(position, false);
            return true;
        }
        return false;
    }

    private void focusItem(final RecyclerView recyclerView, final LinearLayoutManager layoutManager, final int position, final int isNeedSet) {
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
                    if (1 == isNeedSet) {
                        focusItem(mLvChannel, mLayoutManagerChannel, mCurrentSelectPosition, 2);
                        selectTypePosition = mCurrentSelectTypePosition;
                        selectChannelPosition = mCurrentSelectPosition;

                    } else if (2 == isNeedSet) {
                        refreshPosition(1);
                        refreshTypeListUI(mCurrentSelectTypePosition, false);
                    } else if (3 == isNeedSet) {
                        refreshChannelListUI(mCurrentSelectPosition, false, false);
                        refreshPosition(2);
                    }
                }
            });

        } catch (Exception e) {
            JLog.e(e);
        }
    }

    /**
     * 刷新位置
     *
     * @param type 1刷两列 2刷三列
     */
    private void refreshPosition(int type) {
        if (1 == type) {
            int scrollY = getScrollY(mChannelTypeAdapter.getItemCount(), selectTypePosition);
            if (scrollY > 0) {
                mLayoutManagerType.scrollToPositionWithOffset(selectTypePosition, scrollY);
            }

            int scrollY2 = getScrollY(mChannelAdapter.getItemCount(), selectChannelPosition);
            if (scrollY2 > 0) {
                mLayoutManagerChannel.scrollToPositionWithOffset(selectChannelPosition, scrollY2);
            }
            JLog.d("*********滑动距离->scrollY:" + scrollY + "||scrollY2:" + scrollY2);

        } else if (2 == type) {
            int scrollY = getScrollY(mChannelTypeAdapter.getItemCount(), selectTypePosition);
            if (scrollY > 0) {
                mLayoutManagerType.scrollToPositionWithOffset(selectTypePosition, scrollY);
            }

            int scrollY2 = getScrollY(mChannelAdapter.getItemCount(), selectChannelPosition);
            if (scrollY2 > 0) {
                mLayoutManagerChannel.scrollToPositionWithOffset(selectChannelPosition, scrollY2);
            }

            int scrollY3 = getScrollY(mChannelSubAdapter.getItemCount(), selectChannelSubPosition);
            if (scrollY3 > 0) {
                mLayoutManagerSubChannel.scrollToPositionWithOffset(selectChannelSubPosition, scrollY3);
            }

            JLog.d("*********滑动距离->scrollY:" + scrollY + "||scrollY2:" + scrollY2 + "||scrollY3:" + scrollY3);
        }
    }

    /**
     * 返回应该滑动的位置
     *
     * @param size
     * @param position
     */
    private int getScrollY(int size, int position) {
        if (size > 9) {
            if (position > 4) {
                return 4 * 80;
            } else {
                return position * 80;
            }
        }
        return 0;
    }

    private void refreshTypeListUI(final int position, final boolean hasFocus) {
        JLog.d("******->refreshTypeListUI:" + position);
        mLvChannelType.post(new Runnable() {
            @Override
            public void run() {
                RecyclerView.ViewHolder holder = mLvChannelType.findViewHolderForLayoutPosition(position);
                if (null != holder) {
                    ChannelTypeAdapter.ViewHolder viewHolder = (ChannelTypeAdapter.ViewHolder) holder;
                    if (hasFocus) {
                        viewHolder.itemView.setBackgroundResource(R.drawable.bg_channel_select);
                        viewHolder.tvChannelName.getPaint().setFakeBoldText(false);
                        viewHolder.itemView.requestFocus();
                    } else {
                        viewHolder.itemView.setBackgroundResource(R.drawable.bg_item_cover2);
                        viewHolder.tvChannelName.setTextColor(Color.parseColor("#FFFFFF"));
                        viewHolder.tvChannelName.getPaint().setFakeBoldText(true);
                        viewHolder.tvChannelName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
                    }
                }
            }
        });
    }

    private void refreshChannelListUI(final int position, final boolean hasFocus, final boolean isUpdate) {
        JLog.d("******->refreshChannelListUI:" + position);
        mLvChannel.post(new Runnable() {
            @Override
            public void run() {
                RecyclerView.ViewHolder holder = mLvChannel.findViewHolderForLayoutPosition(position);
                if (null != holder) {
                    ChannelAdapter.ViewHolder viewHolder = (ChannelAdapter.ViewHolder) holder;
                    if (hasFocus) {
                        viewHolder.itemView.setBackgroundResource(R.drawable.bg_channel_select);
                        viewHolder.itemView.requestFocus();
                        if (isUpdate) {
                            viewHolder.tvChannelName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
                            viewHolder.tvChannelName.setTextColor(Color.parseColor("#555555"));
                            viewHolder.tvChannelName.getPaint().setFakeBoldText(true);
                            viewHolder.tvChannelName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                            viewHolder.tvChannelName.setSelected(true);
                        }
                    } else {
                        viewHolder.itemView.setBackgroundResource(R.drawable.bg_item_cover2);
                        viewHolder.tvChannelName.setTextColor(Color.parseColor("#FFFFFF"));
                        viewHolder.tvChannelName.getPaint().setFakeBoldText(true);
                        viewHolder.tvChannelName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
                    }
                }
            }
        });
    }

    private void refreshChannelListUI() {
        try {
            mLayoutManagerChannel.scrollToPosition(0);
        } catch (Exception e) {
            JLog.e(e);
        }
    }

    private void refreshListUI(final int position, final int subPosition, boolean focus) {
        JLog.d("******->refreshListUI:" + position + "||subPosition:" + subPosition + "||focus:" + focus);

        toggleChannelUI(0);
        refreshPosition(1);
        selectChannelPosition = 0;
        refreshTypeListUI(position, false);
    }

}
