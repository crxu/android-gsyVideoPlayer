package com.gsgd.live.ui.leftMenu;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gsgd.live.AppConfig;
import com.gsgd.live.MainApplication;
import com.gsgd.live.data.events.PlayEvent;
import com.gsgd.live.ui.dialog.ResetDialog;
import com.gsgd.live.utils.ACache;
import com.gsgd.live.utils.NoFastClickUtils;
import com.gsgd.live.utils.SPUtil;
import com.gsgd.live.utils.ScaleUtil;
import com.gsgd.live.utils.ToastUtil;
import com.jiongbull.jlog.JLog;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hdpfans.com.R;

/**
 * @author zhangqy
 * @Description 设置中心
 * @date 2017/12/22
 */
public class MenuSContentView extends LinearLayout {

    @BindView(R.id.fl_setting_content_bg)
    FrameLayout mFlSettingContentBg;

    @BindView(R.id.ll_main_setting)
    LinearLayout mLlMainSetting;
    @BindView(R.id.rl_reset)
    View mRlReset;
    @BindView(R.id.tv_reset_name)
    TextView mTvResetName;
    @BindView(R.id.tv_reset)
    TextView mTvReset;
    @BindView(R.id.rl_quiet)
    View mRlQuiet;
    @BindView(R.id.tv_quiet)
    TextView mTvQuiet;
    @BindView(R.id.tv_quiet_mode)
    TextView mTvQuietMode;
    @BindView(R.id.rl_scale)
    View mRlScale;
    @BindView(R.id.tv_scale)
    TextView mTvScale;
    @BindView(R.id.tv_scale_mode)
    TextView mTvScaleMode;
    @BindView(R.id.rl_decode)
    View mRlDecode;
    @BindView(R.id.tv_decode)
    TextView mTvDecode;
    @BindView(R.id.tv_decode_mode)
    TextView mTvDecodeMode;

    @BindView(R.id.ll_scale_setting)
    LinearLayout mLlScaleSetting;
    @BindView(R.id.tv_scale_smart)
    TextView mTvScaleSmart;
    @BindView(R.id.tv_scale_full)
    TextView mTvScaleFull;
    @BindView(R.id.tv_scale_original)
    TextView mTvScaleOriginal;

    @BindView(R.id.ll_decode_setting)
    LinearLayout mLlDecodeSetting;
    @BindView(R.id.tv_decode_smart)
    TextView mTvDecodeSmart;
    @BindView(R.id.tv_decode_hard)
    TextView mTvDecodeHard;
    @BindView(R.id.tv_decode_soft)
    TextView mTvDecodeSoft;

    private Context mContext;

    public MenuSContentView(Context context) {
        this(context, null);
    }

    public MenuSContentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.mContext = context;
        View root = LayoutInflater.from(context).inflate(R.layout.view_menu_setting_content, this, true);
        ButterKnife.bind(this, root);

        initSettingEvents();
    }

    private void initSettingEvents() {
        mRlReset.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ScaleUtil.scaleToBig2(mRlReset, 1.05f);
                    mTvResetName.setTextColor(Color.parseColor("#555555"));
                    mTvResetName.getPaint().setFakeBoldText(true);
                    mTvReset.setTextColor(Color.parseColor("#555555"));
                    mTvReset.getPaint().setFakeBoldText(true);
                    mTvReset.setCompoundDrawablesWithIntrinsicBounds(null, null, getContext().getResources().getDrawable(R.drawable.icon_setting_arrow_s), null);

                } else {
                    ScaleUtil.scaleToNormal2(mRlReset);
                    mTvResetName.setTextColor(Color.parseColor("#F0F0F0"));
                    mTvResetName.getPaint().setFakeBoldText(false);
                    mTvReset.setTextColor(Color.parseColor("#F0F0F0"));
                    mTvReset.getPaint().setFakeBoldText(false);
                    mTvReset.setCompoundDrawablesWithIntrinsicBounds(null, null, getContext().getResources().getDrawable(R.drawable.icon_setting_arrow_n), null);
                }
            }
        });

        mRlQuiet.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ScaleUtil.scaleToBig2(mRlQuiet, 1.05f);
                    mTvQuiet.setTextColor(Color.parseColor("#555555"));
                    mTvQuiet.getPaint().setFakeBoldText(true);
                    mTvQuietMode.setTextColor(Color.parseColor("#555555"));
                    mTvQuietMode.getPaint().setFakeBoldText(true);
                    mTvQuietMode.setCompoundDrawablesWithIntrinsicBounds(null, null, getContext().getResources().getDrawable(R.drawable.icon_setting_arrow_s), null);

                } else {
                    ScaleUtil.scaleToNormal2(mRlQuiet);
                    mTvQuiet.setTextColor(Color.parseColor("#F0F0F0"));
                    mTvQuiet.getPaint().setFakeBoldText(false);
                    mTvQuietMode.setTextColor(Color.parseColor("#F0F0F0"));
                    mTvQuietMode.getPaint().setFakeBoldText(false);
                    mTvQuietMode.setCompoundDrawablesWithIntrinsicBounds(null, null, getContext().getResources().getDrawable(R.drawable.icon_setting_arrow_n), null);
                }
            }
        });
        mRlScale.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ScaleUtil.scaleToBig2(mRlScale, 1.05f);
                    mTvScale.setTextColor(Color.parseColor("#555555"));
                    mTvScale.getPaint().setFakeBoldText(true);
                    mTvScaleMode.setTextColor(Color.parseColor("#555555"));
                    mTvScaleMode.getPaint().setFakeBoldText(true);
                    mTvScaleMode.setCompoundDrawablesWithIntrinsicBounds(null, null, getContext().getResources().getDrawable(R.drawable.icon_setting_arrow_s), null);

                } else {
                    ScaleUtil.scaleToNormal2(mRlScale);
                    mTvScale.setTextColor(Color.parseColor("#F0F0F0"));
                    mTvScale.getPaint().setFakeBoldText(false);
                    mTvScaleMode.setTextColor(Color.parseColor("#F0F0F0"));
                    mTvScaleMode.getPaint().setFakeBoldText(false);
                    mTvScaleMode.setCompoundDrawablesWithIntrinsicBounds(null, null, getContext().getResources().getDrawable(R.drawable.icon_setting_arrow_n), null);
                }
            }
        });
        mRlDecode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ScaleUtil.scaleToBig2(mRlDecode, 1.05f);
                    mTvDecode.setTextColor(Color.parseColor("#555555"));
                    mTvDecode.getPaint().setFakeBoldText(true);
                    mTvDecodeMode.setTextColor(Color.parseColor("#555555"));
                    mTvDecodeMode.getPaint().setFakeBoldText(true);
                    mTvDecodeMode.setCompoundDrawablesWithIntrinsicBounds(null, null, getContext().getResources().getDrawable(R.drawable.icon_setting_arrow_s), null);

                } else {
                    ScaleUtil.scaleToNormal2(mRlDecode);
                    mTvDecode.setTextColor(Color.parseColor("#F0F0F0"));
                    mTvDecode.getPaint().setFakeBoldText(false);
                    mTvDecodeMode.setTextColor(Color.parseColor("#F0F0F0"));
                    mTvDecodeMode.getPaint().setFakeBoldText(false);
                    mTvDecodeMode.setCompoundDrawablesWithIntrinsicBounds(null, null, getContext().getResources().getDrawable(R.drawable.icon_setting_arrow_n), null);
                }
            }
        });

        mTvScaleSmart.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ScaleUtil.scaleToBig2(mTvScaleSmart, 1.05f);
                    mTvScaleSmart.setTextColor(Color.parseColor("#555555"));
                    mTvScaleSmart.getPaint().setFakeBoldText(true);

                } else {
                    ScaleUtil.scaleToNormal2(mTvScaleSmart);
                    mTvScaleSmart.setTextColor(Color.parseColor("#F0F0F0"));
                    mTvScaleSmart.getPaint().setFakeBoldText(false);
                }
            }
        });
        mTvScaleFull.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ScaleUtil.scaleToBig2(mTvScaleFull, 1.05f);
                    mTvScaleFull.setTextColor(Color.parseColor("#555555"));
                    mTvScaleFull.getPaint().setFakeBoldText(true);

                } else {
                    ScaleUtil.scaleToNormal2(mTvScaleFull);
                    mTvScaleFull.setTextColor(Color.parseColor("#F0F0F0"));
                    mTvScaleFull.getPaint().setFakeBoldText(false);
                }
            }
        });
        mTvScaleOriginal.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ScaleUtil.scaleToBig2(mTvScaleOriginal, 1.05f);
                    mTvScaleOriginal.setTextColor(Color.parseColor("#555555"));
                    mTvScaleOriginal.getPaint().setFakeBoldText(true);

                } else {
                    ScaleUtil.scaleToNormal2(mTvScaleOriginal);
                    mTvScaleOriginal.setTextColor(Color.parseColor("#F0F0F0"));
                    mTvScaleOriginal.getPaint().setFakeBoldText(false);
                }
            }
        });

        mTvDecodeSmart.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ScaleUtil.scaleToBig2(mTvDecodeSmart, 1.05f);
                    mTvDecodeSmart.setTextColor(Color.parseColor("#555555"));
                    mTvDecodeSmart.getPaint().setFakeBoldText(true);

                } else {
                    ScaleUtil.scaleToNormal2(mTvDecodeSmart);
                    mTvDecodeSmart.setTextColor(Color.parseColor("#F0F0F0"));
                    mTvDecodeSmart.getPaint().setFakeBoldText(false);
                }
            }
        });
        mTvDecodeHard.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ScaleUtil.scaleToBig2(mTvDecodeHard, 1.05f);
                    mTvDecodeHard.setTextColor(Color.parseColor("#555555"));
                    mTvDecodeHard.getPaint().setFakeBoldText(true);

                } else {
                    ScaleUtil.scaleToNormal2(mTvDecodeHard);
                    mTvDecodeHard.setTextColor(Color.parseColor("#F0F0F0"));
                    mTvDecodeHard.getPaint().setFakeBoldText(false);
                }
            }
        });
        mTvDecodeSoft.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ScaleUtil.scaleToBig2(mTvDecodeSoft, 1.05f);
                    mTvDecodeSoft.setTextColor(Color.parseColor("#555555"));
                    mTvDecodeSoft.getPaint().setFakeBoldText(true);

                } else {
                    ScaleUtil.scaleToNormal2(mTvDecodeSoft);
                    mTvDecodeSoft.setTextColor(Color.parseColor("#F0F0F0"));
                    mTvDecodeSoft.getPaint().setFakeBoldText(false);
                }
            }
        });
    }

    ResetDialog dialog;

    /**
     * 恢复默认设置
     */
    @OnClick(R.id.rl_reset)
    void onSettingResetClick() {
        try {
            if (null != dialog && dialog.isShowing()) {
                dialog.dismiss();
                dialog = null;
            }

        } catch (Exception e) {
            JLog.e(e);
        }

        dialog = new ResetDialog(mContext, R.style.TransparentDialog);
        dialog.show();
    }

    /**
     * 看电视免打扰
     */
    @OnClick(R.id.rl_quiet)
    void onQuietClick() {
        if (!NoFastClickUtils.isFastClick()) {
            String quietMode = getQuietMode();
            if ("0".equals(quietMode)) {
                saveQuietMode("1");

            } else {
                saveQuietMode("0");
            }

            refreshQuietMode();

        } else {
            ToastUtil.showToast("您的速度已经突破天际,请您手速慢一点");
        }
    }

    /**
     * 屏幕拉伸
     */
    @OnClick(R.id.rl_scale)
    void onSettingScaleClick() {
        mLlScaleSetting.setVisibility(View.VISIBLE);
        mTvScaleSmart.requestFocus();

        mLlMainSetting.setVisibility(View.GONE);
        mLlDecodeSetting.setVisibility(View.GONE);

        refreshScaleMode();
    }

    /**
     * 解码方式
     */
    @OnClick(R.id.rl_decode)
    void onSettingDecodeClick() {
        mLlDecodeSetting.setVisibility(View.VISIBLE);
        mTvDecodeSmart.requestFocus();

        mLlMainSetting.setVisibility(View.GONE);
        mLlScaleSetting.setVisibility(View.GONE);

        refreshDecodeMode();
    }

    @OnClick({R.id.tv_scale_smart, R.id.tv_scale_full, R.id.tv_scale_original})
    void onScaleModeClick(View view) {
        switch (view.getId()) {
            case R.id.tv_scale_smart:
                saveScaleMode(0);
                break;

            case R.id.tv_scale_full:
                saveScaleMode(1);
                break;

            case R.id.tv_scale_original:
                saveScaleMode(2);
                break;
        }
    }

    @OnClick({R.id.tv_decode_smart, R.id.tv_decode_hard, R.id.tv_decode_soft})
    void onDecodeModeClick(View view) {
        switch (view.getId()) {
            case R.id.tv_decode_smart:
                saveDecodeMode(0);
                break;

            case R.id.tv_decode_hard:
                saveDecodeMode(1);
                break;

            case R.id.tv_decode_soft:
                saveDecodeMode(2);
                break;
        }
    }

    private String getQuietMode() {
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory("") + "/.live_message_open");
            ACache mCache = ACache.get(file);
            String quietMode = mCache.getAsString("message_open");
            return quietMode;

        } catch (Exception e) {
            JLog.e(e);
        }

        return "0";
    }

    /**
     * 保存看电视免打扰设置
     */
    private void saveQuietMode(String quietMode) {
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory("") + "/.live_message_open");
            if (!file.exists()) {
                file.mkdirs();
            }
            ACache mCache = ACache.get(file);
            mCache.put("message_open", quietMode);

        } catch (Exception e) {
            JLog.e(e);
        }
    }

    /**
     * 刷新看电视免打扰设置
     */
    private void refreshQuietMode() {
        String quietMode = getQuietMode();
        if ("0".equals(quietMode)) {
            mTvQuietMode.setText("打开");

        } else {
            mTvQuietMode.setText("关闭");
        }
    }

    /**
     * 保存拉伸模式
     */
    private void saveScaleMode(int mode) {
        SPUtil.putInt(MainApplication.getContext(), AppConfig.SP_SETTING_NAME, AppConfig.KEY_SETTING_SCALE_MODE, mode);
        refreshScaleMode();
        EventBus.getDefault().post(new PlayEvent.SelectScaleEvent(mode));
    }

    /**
     * 刷新拉伸模式
     */
    private void refreshScaleMode() {
        int mode = SPUtil.getInt(MainApplication.getContext(), AppConfig.SP_SETTING_NAME, AppConfig.KEY_SETTING_SCALE_MODE, 0);
        switch (mode) {
            case 1:
                mTvScaleMode.setText("全屏拉伸");
                mTvScaleSmart.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.icon_gou_un_select), null);
                mTvScaleFull.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.icon_gou_select), null);
                mTvScaleOriginal.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.icon_gou_un_select), null);
                break;

            case 2:
                mTvScaleMode.setText("原始比例");
                mTvScaleSmart.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.icon_gou_un_select), null);
                mTvScaleFull.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.icon_gou_un_select), null);
                mTvScaleOriginal.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.icon_gou_select), null);
                break;

            default:
                mTvScaleMode.setText("智能全屏");
                mTvScaleSmart.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.icon_gou_select), null);
                mTvScaleFull.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.icon_gou_un_select), null);
                mTvScaleOriginal.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.icon_gou_un_select), null);
                break;
        }
    }

    /**
     * 保存解码模式
     */
    private void saveDecodeMode(int mode) {
        SPUtil.putInt(MainApplication.getContext(), AppConfig.SP_SETTING_NAME, AppConfig.KEY_SETTING_DECODE_MODE, mode);
        refreshDecodeMode();
    }

    /**
     * 刷新解码模式
     */
    private void refreshDecodeMode() {
        int mode = SPUtil.getInt(MainApplication.getContext(), AppConfig.SP_SETTING_NAME, AppConfig.KEY_SETTING_DECODE_MODE, 0);
        switch (mode) {
            case 1:
                mTvDecodeMode.setText("硬解码");
                mTvDecodeSmart.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.icon_gou_un_select), null);
                mTvDecodeHard.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.icon_gou_select), null);
                mTvDecodeSoft.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.icon_gou_un_select), null);
                break;

            case 2:
                mTvDecodeMode.setText("软解码");
                mTvDecodeSmart.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.icon_gou_un_select), null);
                mTvDecodeHard.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.icon_gou_un_select), null);
                mTvDecodeSoft.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.icon_gou_select), null);
                break;

            default:
                mTvDecodeMode.setText("智能解码");
                mTvDecodeSmart.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.icon_gou_select), null);
                mTvDecodeHard.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.icon_gou_un_select), null);
                mTvDecodeSoft.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.icon_gou_un_select), null);
                break;
        }
    }

    /**
     * 回到主设置
     *
     * @param index
     */
    private void backToMainSetting(int index) {
        mLlMainSetting.setVisibility(View.VISIBLE);
        if (2 == index) {
            mRlScale.requestFocus();//屏幕拉伸

        } else if (3 == index) {
            mRlDecode.requestFocus();//解码方式
        }

        mLlScaleSetting.setVisibility(View.GONE);
        mLlDecodeSetting.setVisibility(View.GONE);

        refreshScaleMode();
        refreshDecodeMode();
    }

    /**
     * 刷新UI
     */
    public void refreshUI() {
        refreshQuietMode();
        refreshScaleMode();
        refreshDecodeMode();
    }

    /**
     * 是否可以返回
     */
    public boolean isCanBack() {
        if (mLlScaleSetting.getVisibility() == View.VISIBLE) {
            backToMainSetting(2);
            return false;

        } else if (mLlDecodeSetting.getVisibility() == View.VISIBLE) {
            backToMainSetting(3);
            return false;
        }

        return true;
    }

    /**
     * 是否有焦点
     */
    public boolean isHasFocus() {
        return mLlMainSetting.hasFocus()
                || mLlScaleSetting.hasFocus()
                || mLlDecodeSetting.hasFocus();
    }

    /**
     * 处理向上或向下事件
     */
    public void handleUpOrDownFocus(int keyCode) {
        if (mLlMainSetting.hasFocus()) {
            focusNextView(mLlMainSetting, keyCode == KeyEvent.KEYCODE_DPAD_UP ? View.FOCUS_UP : View.FOCUS_DOWN);
        }

        if (mLlScaleSetting.hasFocus()) {
            focusNextView(mLlScaleSetting, keyCode == KeyEvent.KEYCODE_DPAD_UP ? View.FOCUS_UP : View.FOCUS_DOWN);
        }

        if (mLlDecodeSetting.hasFocus()) {
            focusNextView(mLlDecodeSetting, keyCode == KeyEvent.KEYCODE_DPAD_UP ? View.FOCUS_UP : View.FOCUS_DOWN);
        }
    }

    private void focusNextView(ViewGroup layout, int direction) {
        try {
            View focusView = layout.getFocusedChild();
            View nextView = FocusFinder.getInstance().findNextFocus(layout, focusView, direction);
            if (nextView != null) {
                nextView.requestFocus();
            }

        } catch (Exception e) {
            JLog.e(e);
        }
    }

    /**
     * 重新获取焦点
     */
    public boolean handleGetFocus() {
        MenuUtil.toggleFocusBg(true, mFlSettingContentBg);
        mLlMainSetting.setVisibility(View.VISIBLE);
        mLlMainSetting.requestFocus();
        mLlScaleSetting.setVisibility(View.GONE);
        mLlDecodeSetting.setVisibility(View.GONE);

        return true;
    }

    /**
     * 处理向左的焦点
     *
     * @return 返回true表示此组件需要loss焦点
     */
    public boolean handleLeftFocus() {
        if (mLlMainSetting.hasFocus()) {
            MenuUtil.toggleFocusBg(false, mFlSettingContentBg);
            return true;

        } else if (mLlScaleSetting.hasFocus()) {
            backToMainSetting(2);

        } else if (mLlDecodeSetting.hasFocus()) {
            backToMainSetting(3);
        }

        return false;
    }

    /**
     * 处理向右的焦点
     */
    public void handleRightFocus() {
        //不做处理
    }

    public void dismiss() {
        try {
            if (null != dialog && dialog.isShowing()) {
                dialog.dismiss();
                dialog = null;
            }

        } catch (Exception e) {
            JLog.e(e);
        }
    }

}
