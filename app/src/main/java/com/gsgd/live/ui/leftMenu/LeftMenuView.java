package com.gsgd.live.ui.leftMenu;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;
import com.gsgd.live.data.listener.OnMenuChangeListener;
import com.jiongbull.jlog.JLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import hdpfans.com.R;

/**
 * @author zhangqy
 * @Description 左侧菜单
 * @date 2017/12/22
 */
public class LeftMenuView extends LinearLayout {

    private static final String TAG = LeftMenuView.class.getName();
    private static final int ANIMATION_TIME = 100;//动画时间

    @BindView(R.id.ll_left_menu_bg)
    LinearLayout mLlLeftMenuBg;

    @BindView(R.id.ll_tvLive)
    View mLlTvLive;
    @BindView(R.id.ll_phone_live)
    View mLlPhoneLive;
    @BindView(R.id.ll_message)
    View mLlMessage;
    @BindView(R.id.ll_custom)
    View mLlTvCustom;
    @BindView(R.id.ll_setting)
    View mLlSetting;

    @BindView(R.id.tv_live)
    TextView mTvLive;
    @BindView(R.id.tv_phone_live)
    TextView mTvPhoneLive;
    @BindView(R.id.tv_message)
    TextView mTvMessage;
    @BindView(R.id.tv_custom)
    TextView mTvCustom;
    @BindView(R.id.tv_setting)
    TextView mTvSetting;

    @BindView(R.id.iv_live)
    ImageView mIvLive;
    @BindView(R.id.iv_phone_live)
    ImageView mIvPhoneLive;
    @BindView(R.id.iv_message)
    ImageView mIvMessage;
    @BindView(R.id.iv_custom)
    ImageView mIvCustom;
    @BindView(R.id.iv_setting)
    ImageView mIvSetting;
    @BindView(R.id.unread_message)
    ImageView mIvUnread;

    private Context mContext;
    private MenuType mMenuType = MenuType.TV_LIVE;//当前的位置
    private OnMenuChangeListener mOnMenuChangeListener;

    public void setOnMenuChangeListener(OnMenuChangeListener listener) {
        this.mOnMenuChangeListener = listener;
    }

    public LeftMenuView(Context context) {
        this(context, null);
    }

    public LeftMenuView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.mContext = context;
        View root = LayoutInflater.from(context).inflate(R.layout.view_left_menu, this, true);
        ButterKnife.bind(this, root);

        initLeftMenuEvents();
    }

    private void initLeftMenuEvents() {
        mLlTvLive.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    focusMenu(mTvLive, mIvLive, R.drawable.icon_dianshihover);
                    mMenuType = MenuType.TV_LIVE;
                    if (null != mOnMenuChangeListener) {
                        mOnMenuChangeListener.onMenuChange(MenuType.TV_LIVE);
                    }

                } else {
                    unFocusMenu(mTvLive, mIvLive, R.drawable.icon_dianshi);
                }
            }
        });
        mLlPhoneLive.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    focusMenu(mTvPhoneLive, mIvPhoneLive, R.drawable.icon_shoujihover);
                    mMenuType = MenuType.PHONE_LIVE;
                    if (null != mOnMenuChangeListener) {
                        mOnMenuChangeListener.onMenuChange(MenuType.PHONE_LIVE);
                    }

                } else {
                    unFocusMenu(mTvPhoneLive, mIvPhoneLive, R.drawable.icon_shoujiactive);
                }
            }
        });
        mLlMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    focusMenu(mTvMessage, mIvMessage, R.drawable.icon_xiaoxihover);
                    mMenuType = MenuType.TV_MESSAGE;
                    if (null != mOnMenuChangeListener) {
                        mOnMenuChangeListener.onMenuChange(MenuType.TV_MESSAGE);
                    }

                } else {
                    unFocusMenu(mTvMessage, mIvMessage, R.drawable.icon_xiaoxi);
                }
            }
        });
        mLlTvCustom.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    focusMenu(mTvCustom, mIvCustom, R.drawable.icon_zijianhover);
                    mMenuType = MenuType.TV_CUSTOM;
                    if (null != mOnMenuChangeListener) {
                        mOnMenuChangeListener.onMenuChange(MenuType.TV_CUSTOM);
                    }

                } else {
                    unFocusMenu(mTvCustom, mIvCustom, R.drawable.icon_zijianactive);
                }
            }
        });
        mLlSetting.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    focusMenu(mTvSetting, mIvSetting, R.drawable.icon_shezhihover);
                    mMenuType = MenuType.TV_SETTING;
                    if (null != mOnMenuChangeListener) {
                        mOnMenuChangeListener.onMenuChange(MenuType.TV_SETTING);
                    }

                } else {
                    unFocusMenu(mTvSetting, mIvSetting, R.drawable.icon_shezhi);
                }
            }
        });
    }

    private void focusMenu(TextView textView, ImageView imageView, int resId) {
        try {
            textView.setTextColor(Color.parseColor("#555555"));
            textView.getPaint().setFakeBoldText(true);
            imageView.setImageResource(resId);

        } catch (Exception e) {
            JLog.e(TAG, e);
        }
    }

    private void unFocusMenu(TextView textView, ImageView imageView, int resId) {
        try {
            textView.setTextColor(Color.parseColor("#F0F0F0"));
            textView.getPaint().setFakeBoldText(false);
            imageView.setImageResource(resId);

        } catch (Exception e) {
            JLog.e(TAG, e);
        }
    }

    /**
     * 获取当前的menu位置
     */
    public MenuType getCurrentMenuType() {
        return mMenuType;
    }

    /**
     * 设置菜单选中
     */
    public void setLeftMenuSelected(MenuType menuType) {
        mLlTvLive.setBackgroundResource(MenuType.TV_LIVE == menuType ? R.drawable.bg_item_cover2 : R.drawable.bg_item_cover3);
        mLlPhoneLive.setBackgroundResource(MenuType.PHONE_LIVE == menuType ? R.drawable.bg_item_cover2 : R.drawable.bg_item_cover3);
        mLlMessage.setBackgroundResource(MenuType.TV_MESSAGE == menuType ? R.drawable.bg_item_cover2 : R.drawable.bg_item_cover3);
        mLlTvCustom.setBackgroundResource(MenuType.TV_CUSTOM == menuType ? R.drawable.bg_item_cover2 : R.drawable.bg_item_cover3);
        mLlSetting.setBackgroundResource(MenuType.TV_SETTING == menuType ? R.drawable.bg_item_cover2 : R.drawable.bg_item_cover3);
        mMenuType = menuType;
    }

    /**
     * 菜单恢复焦点
     */
    public void setLeftMenuFocus() {
        mLlTvLive.setBackgroundResource(R.drawable.bg_left_menu_select);
        mLlPhoneLive.setBackgroundResource(R.drawable.bg_left_menu_select);
        mLlMessage.setBackgroundResource(R.drawable.bg_left_menu_select);
        mLlTvCustom.setBackgroundResource(R.drawable.bg_left_menu_select);
        mLlSetting.setBackgroundResource(R.drawable.bg_left_menu_select);
    }

    /**
     * 是否有焦点
     */
    public boolean isHasFocus() {
        return mLlLeftMenuBg.hasFocus();
    }

    /**
     * 处理向上或向下事件
     */
    public void handleUpOrDownFocus(int keyCode) {
        try {
            View focusView = mLlLeftMenuBg.getFocusedChild();
            if (null != focusView) {
                View nextView = FocusFinder.getInstance().findNextFocus(mLlLeftMenuBg, focusView, keyCode == KeyEvent.KEYCODE_DPAD_UP ? View.FOCUS_UP : View.FOCUS_DOWN);
                if (nextView != null) {
                    nextView.requestFocus();
                }
            }

        } catch (Exception e) {
            JLog.e(TAG, e);
        }
    }


    /**
     * 失去焦点
     */
    public void handleLossFocus() {
        toggleLeftMenu(false);
    }

    /**
     * 重新获取焦点
     */
    public void handleGetFocus() {
        toggleLeftMenu(true);
    }

    /**
     * 菜单展开/关闭
     *
     * @param isNeedExpand true展开
     */
    private void toggleLeftMenu(boolean isNeedExpand) {
        if (isNeedExpand) {
            mTvLive.setVisibility(View.VISIBLE);
            mTvPhoneLive.setVisibility(View.VISIBLE);
            mTvMessage.setVisibility(View.VISIBLE);
            mTvCustom.setVisibility(View.VISIBLE);
            mTvSetting.setVisibility(View.VISIBLE);

            switch (mMenuType) {
                case TV_LIVE:
                    mLlTvLive.requestFocus();

                    ViewAnimator
                            .animate(mLlLeftMenuBg)
                            .dp().width(100, 240)
                            .duration(ANIMATION_TIME)
                            .start();
                    break;

                case PHONE_LIVE:
                    mLlPhoneLive.requestFocus();
                    break;

                case TV_MESSAGE:
                    mLlMessage.requestFocus();
                    break;

                case TV_CUSTOM:
                    mLlTvCustom.requestFocus();
                    break;

                case TV_SETTING:
                    mLlSetting.requestFocus();
                    break;
            }

            setLeftMenuFocus();
            MenuUtil.toggleFocusBg(true, mLlLeftMenuBg);

        } else {
            switch (mMenuType) {
                case TV_LIVE:
                    ViewAnimator
                            .animate(mLlLeftMenuBg)
                            .dp().width(240, 100)
                            .duration(ANIMATION_TIME)
                            .onStop(new AnimationListener.Stop() {
                                @Override
                                public void onStop() {
                                    mTvLive.setVisibility(View.GONE);
                                    mTvPhoneLive.setVisibility(View.GONE);
                                    mTvMessage.setVisibility(View.GONE);
                                    mTvCustom.setVisibility(View.GONE);
                                    mTvSetting.setVisibility(View.GONE);
                                }
                            })
                            .start();
                    break;
            }

            MenuUtil.toggleFocusBg(false, mLlLeftMenuBg);
            setLeftMenuSelected(mMenuType);
        }
    }

    /**
     * 是否显示未读消息提示
     *
     * @param hasUnread 是否有未读消息
     */
    public void toggleUnreadMsg(boolean hasUnread) {
        mIvUnread.setVisibility(hasUnread ? VISIBLE : GONE);
    }

}
