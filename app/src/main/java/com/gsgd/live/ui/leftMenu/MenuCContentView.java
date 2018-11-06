package com.gsgd.live.ui.leftMenu;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.gsgd.live.data.events.PlayEvent;
import com.gsgd.live.data.listener.AbstractOnMenuDialogImpl;
import com.gsgd.live.data.listener.OnItemListener;
import com.gsgd.live.data.model.CustomInfo;
import com.gsgd.live.data.model.CustomTypeInfo;
import com.gsgd.live.ui.adapter.CustomAdapter;
import com.gsgd.live.ui.adapter.CustomSubAdapter;
import com.gsgd.live.ui.widgets.DividerItemDecoration;
import com.gsgd.live.utils.PlayControlUtil;
import com.jiongbull.jlog.JLog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hdpfans.com.R;

/**
 * @author zhangqy
 * @Description 自建频道
 * @date 2017/12/22
 */
public class MenuCContentView extends LinearLayout {

    @BindView(R.id.rv_custom_list)
    RecyclerView mRvCustomList;
    @BindView(R.id.rv_custom_sub_list)
    RecyclerView mRvCustomSubList;
    @BindView(R.id.fl_custom_sub)
    View mViewCustomSub;
    @BindView(R.id.ll_custom_guide)
    View mViewCustomGuide;
    @BindView(R.id.view_custom_cover)
    View mViewCustomCover;
    @BindView(R.id.tv_empty_tip_custom)
    View mViewEmptyTip;

    private Context mContext;
    private CustomAdapter mCustomAdapter;
    private LinearLayoutManager mLayoutManagerType;
    private CustomSubAdapter mCustomSubAdapter;
    private LinearLayoutManager mLayoutManagerSub;

    private List<CustomTypeInfo> mCustomList = new ArrayList<>();
    private int selectTypePosition = 0;
    private int selectCustomSubPosition = 0;
    private int mCurrentSelectTypePosition = -1;
    private int mCurrentSelectCustomSubPosition = -1;
    private boolean isTypeChange = false;//类型是否发生变化
    private AbstractOnMenuDialogImpl menuDialogListener;
    private PlayControlUtil.PlayInfoListener mPlayInfoListener;

    public MenuCContentView(Context context) {
        this(context, null);
    }

    public MenuCContentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;

        View root = LayoutInflater.from(context).inflate(R.layout.view_menu_custom_content, this, true);
        ButterKnife.bind(this, root);

        initParams();
    }

    private void initParams() {
        //初始化一级列表
        mCustomAdapter = new CustomAdapter(mContext, new OnItemListener() {
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

                    toggleGuideUI(selectTypePosition == 0);
                    mCustomSubAdapter.setData(mCustomList.get(position).sources, selectTypePosition == mCurrentSelectTypePosition, mCurrentSelectCustomSubPosition);

                    if (selectTypePosition != 0) {
                        mViewEmptyTip.setVisibility(mCustomSubAdapter.getItemCount() > 0 ? GONE : VISIBLE);
                    }

                    if (!isSame) {
                        try {
                            mLayoutManagerSub.scrollToPosition(0);
                        } catch (Exception e) {
                            JLog.e(e);
                        }
                    }
                }
            }
        });
        mLayoutManagerType = new LinearLayoutManager(mContext);
        mRvCustomList.setLayoutManager(mLayoutManagerType);
        mRvCustomList.addItemDecoration(new DividerItemDecoration(mContext, 0));
        mRvCustomList.setAdapter(mCustomAdapter);

        //初始化二级列表
        mCustomSubAdapter = new CustomSubAdapter(mContext, new OnItemListener() {
            @Override
            public void onItemClick(int position) {
                try {
                    //播放选中的频道
                    CustomTypeInfo typeInfo = mCustomList.get(selectTypePosition);
                    CustomInfo info = typeInfo.sources.get(position);

                    PlayEvent.SelectCustomChannelEvent selectChannelEvent = new PlayEvent.SelectCustomChannelEvent(typeInfo, info);
                    EventBus.getDefault().post(selectChannelEvent);

                } catch (Exception e) {
                    JLog.e(e);
                }

                if (null != menuDialogListener) {
                    menuDialogListener.onDismiss();
                }
            }

            @Override
            public void onItemFocus(int position, boolean hasFocus) {
                if (hasFocus) {
                    selectCustomSubPosition = position;
                }
            }
        });
        mLayoutManagerSub = new LinearLayoutManager(mContext);
        mRvCustomSubList.setLayoutManager(mLayoutManagerSub);
        mRvCustomSubList.addItemDecoration(new DividerItemDecoration(mContext, 0));
        mRvCustomSubList.setAdapter(mCustomSubAdapter);

        setCustomList(null);
    }

    private void toggleGuideUI(boolean isNeedShow) {
        mViewCustomGuide.setVisibility(isNeedShow ? VISIBLE : GONE);
        mViewCustomSub.setVisibility(isNeedShow ? GONE : VISIBLE);
    }

    public void initData(PlayControlUtil.PlayInfoListener infoListener, AbstractOnMenuDialogImpl menuDialogListener) {
        this.mPlayInfoListener = infoListener;
        this.menuDialogListener = menuDialogListener;
        if (null != menuDialogListener) {
            this.menuDialogListener.onGetCustomList();
        }
    }

    /**
     * 设置自建数据
     */
    public void setCustomList(List<CustomTypeInfo> list) {
        if (isHasFocus() && null != menuDialogListener) {
            menuDialogListener.onGetLeftMenuFocus();
        }

        mCustomList.clear();
        CustomTypeInfo info_guide = new CustomTypeInfo();
        info_guide.type = -1;
        info_guide.phoneName = "同步自建频道";
        mCustomList.add(info_guide);

        if (null != list) {
            mCustomList.addAll(list);
        }

        boolean needPosition = false;

        try {
            //判断是否播放当前的
            if (null != mPlayInfoListener) {
                CustomTypeInfo typeInfo = mPlayInfoListener.getCurrentCustomType();
                CustomInfo info = mPlayInfoListener.getCurrentCustomInfo();

                if (null != typeInfo && null != info && !TextUtils.isEmpty(info.sourceUrl)) {
                    int typeSize = mCustomList.size();
                    for (int i = 0; i < typeSize; i++) {
                        if (typeInfo.userId == mCustomList.get(i).userId) {

                            int sourceSize = mCustomList.get(i).sources.size();

                            for (int j = 0; j < sourceSize; j++) {
                                if (!TextUtils.isEmpty(mCustomList.get(i).sources.get(j).sourceUrl)
                                        && mCustomList.get(i).sources.get(j).sourceUrl.equals(info.sourceUrl)) {

                                    mCurrentSelectTypePosition = i;
                                    mCurrentSelectCustomSubPosition = j;
                                    needPosition = true;
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }
            }

        } catch (Exception e) {
            JLog.e(e);
        }

        mCustomAdapter.setData(mCustomList);

        if (needPosition) {
            selectTypePosition = 0;
            selectCustomSubPosition = 0;
            //需要定位
            boolean isFocus = MenuType.TV_CUSTOM == menuDialogListener.getCurrentMenuType();
            if (isFocus) {
                menuDialogListener.onLossLeftMenuFocus();
            }
            refreshListUI(mCurrentSelectTypePosition, mCurrentSelectCustomSubPosition, isFocus);

        } else {
            mCurrentSelectTypePosition = -1;
            mCurrentSelectCustomSubPosition = -1;
            selectTypePosition = 0;
            selectCustomSubPosition = 0;
        }

        mViewCustomGuide.setVisibility(GONE);
        mViewCustomSub.setVisibility(GONE);
    }

    /**
     * 是否有焦点
     */
    public boolean isHasFocus() {
        return mRvCustomList.hasFocus()
                || mRvCustomSubList.hasFocus();
    }

    /**
     * 处理向上或向下事件
     */
    public void handleUpOrDownFocus(int keyCode) {
        boolean isDown = KeyEvent.KEYCODE_DPAD_DOWN == keyCode;

        if (mRvCustomList.hasFocus()) {
            focusNextView(mRvCustomList, isDown ? View.FOCUS_DOWN : View.FOCUS_UP, isDown, isDown ? 80 : -80, mCustomAdapter.getItemCount(), selectTypePosition);

            return;
        }

        if (mRvCustomSubList.hasFocus()) {
            focusNextView(mRvCustomSubList, isDown ? View.FOCUS_DOWN : View.FOCUS_UP, isDown, isDown ? 80 : -80, mCustomSubAdapter.getItemCount(), selectCustomSubPosition);
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
        MenuUtil.toggleFocusBg(true, mViewCustomCover);
        refreshTypeListUI(selectTypePosition, true);
        return true;
    }

    /**
     * 处理向左的焦点
     */
    public boolean handleLeftFocus() {
        if (mRvCustomSubList.hasFocus()) {
            MenuUtil.toggleFocusBg(true, mViewCustomCover);
            refreshTypeListUI(selectTypePosition, true);

            MenuUtil.toggleFocusBg(false, mRvCustomSubList);

            return false;
        }

        MenuUtil.toggleFocusBg(false, mViewCustomCover);
        refreshTypeListUI(selectTypePosition, false);

        mViewCustomGuide.setVisibility(GONE);
        mViewCustomSub.setVisibility(GONE);
        return true;
    }

    /**
     * 处理向右的焦点
     */
    public void handleRightFocus() {
        if (mRvCustomList.hasFocus() && mCustomSubAdapter.getItemCount() > 0) {
            //有数据
            MenuUtil.toggleFocusBg(true, mRvCustomSubList);
            MenuUtil.toggleFocusBg(false, mViewCustomCover);

            if (isTypeChange) {
                refreshSubListUI(0, true);

            } else {
                refreshSubListUI(selectCustomSubPosition, true);
            }
            refreshTypeListUI(selectTypePosition, false);
        }
    }

    private void refreshListUI(final int position, final int subPosition, boolean focus) {
        JLog.d("******->refreshListUI:" + position + "||subPosition:" + subPosition + "||focus:" + focus);

        mRvCustomList.post(new Runnable() {
            @Override
            public void run() {
                RecyclerView.ViewHolder holder = mRvCustomList.findViewHolderForLayoutPosition(position);
                if (null != holder) {
                    ((CustomAdapter.ViewHolder) holder).itemView.setBackgroundResource(R.drawable.bg_channel_select);
                    holder.itemView.requestFocus();

                    refreshSubListUI(subPosition, true);
                    refreshTypeListUI(position, false);

                    int scrollY = getScrollY(mCustomSubAdapter.getItemCount(), subPosition);
                    if (scrollY > 0) {
                        mLayoutManagerSub.scrollToPositionWithOffset(subPosition, scrollY);
                    }

                    MenuUtil.toggleFocusBg(true, mRvCustomSubList);
                }
            }
        });
    }

    private void refreshTypeListUI(final int position, final boolean hasFocus) {
        JLog.d("******->refreshTypeListUI:" + position);
        mRvCustomList.post(new Runnable() {
            @Override
            public void run() {
                RecyclerView.ViewHolder holder = mRvCustomList.findViewHolderForLayoutPosition(position);
                if (null != holder) {
                    if (hasFocus) {
                        ((CustomAdapter.ViewHolder) holder).itemView.setBackgroundResource(R.drawable.bg_channel_select);
                        holder.itemView.requestFocus();

                    } else {
                        ((CustomAdapter.ViewHolder) holder).itemView.setBackgroundResource(R.drawable.bg_item_cover2);
                        ((CustomAdapter.ViewHolder) holder).mTvCustom.getPaint().setFakeBoldText(hasFocus);
                    }
                }
            }
        });
    }

    private void refreshSubListUI(final int position, final boolean hasFocus) {
        JLog.d("******->refreshTypeListUI:" + position);
        mRvCustomSubList.post(new Runnable() {
            @Override
            public void run() {
                RecyclerView.ViewHolder holder = mRvCustomSubList.findViewHolderForLayoutPosition(position);
                if (null != holder) {
                    if (hasFocus) {
                        ((CustomSubAdapter.ViewHolder) holder).itemView.setBackgroundResource(R.drawable.bg_channel_select);
                        holder.itemView.requestFocus();

                    } else {
                        ((CustomSubAdapter.ViewHolder) holder).itemView.setBackgroundResource(R.drawable.bg_item_cover2);
                        ((CustomSubAdapter.ViewHolder) holder).mTvCustom.getPaint().setFakeBoldText(hasFocus);
                    }
                }
            }
        });
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


}
