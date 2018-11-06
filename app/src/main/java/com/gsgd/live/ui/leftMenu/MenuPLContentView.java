package com.gsgd.live.ui.leftMenu;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.FocusFinder;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gsgd.live.data.listener.AbstractOnMenuDialogImpl;
import com.gsgd.live.data.response.RespDevice;
import com.gsgd.live.ui.dialog.UnbindPhoneDialog;
import com.gsgd.live.utils.ImageLoadUtil;
import com.gsgd.live.utils.ScaleUtil;
import com.gsgd.live.utils.ToastUtil;
import com.jiongbull.jlog.JLog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hdpfans.com.R;

/**
 * @author zhangqy
 * @Description 手机直播
 * @date 2017/12/22
 */
public class MenuPLContentView extends LinearLayout {

    @BindView(R.id.iv_qr_bind)
    ImageView mIvQrBind;
    @BindView(R.id.ll_bind_list)
    View mViewBind;
    @BindView(R.id.ll_bind_list_content)
    LinearLayout mLlBindListContent;

    private Context mContext;
    private AbstractOnMenuDialogImpl menuDialogListener;

    public MenuPLContentView(Context context) {
        this(context, null);
    }

    public MenuPLContentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;

        View root = LayoutInflater.from(context).inflate(R.layout.view_menu_phone_live_content, this, true);
        ButterKnife.bind(this, root);
    }

    public void initData(AbstractOnMenuDialogImpl menuDialogListener) {
        this.menuDialogListener = menuDialogListener;

        if (null != this.menuDialogListener) {
            this.menuDialogListener.onGetBindQrCode();
            this.menuDialogListener.onGetBindPhoneList();
        }
    }

    /**
     * 是否有焦点
     */
    public boolean isHasFocus() {
        return mLlBindListContent.getChildCount() > 0;
    }

    /**
     * 处理向上或向下事件
     */
    public void handleUpOrDownFocus(int keyCode) {
        try {
            View focusView = mLlBindListContent.getFocusedChild();
            View nextView = FocusFinder.getInstance().findNextFocus(mLlBindListContent, focusView, keyCode == KeyEvent.KEYCODE_DPAD_UP ? View.FOCUS_UP : View.FOCUS_DOWN);
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
        if (mLlBindListContent.getChildCount() > 0) {
            mLlBindListContent.requestFocus();
            return true;
        }

        return false;
    }

    /**
     * 处理向左的焦点
     */
    public boolean handleLeftFocus() {
        return true;
    }

    /**
     * 处理向右的焦点
     */
    public void handleRightFocus() {
        //不做处理
    }

    /**
     * 加载二维码
     */
    public void loadQrCode(String qrCodeUrl) {
        ImageLoadUtil.loadImage(mContext, mIvQrBind, qrCodeUrl);
    }

    /**
     * 设置绑定列表
     */
    public void setDeviceList(List<RespDevice> value) {
        if (isHasFocus() && null != menuDialogListener) {
            menuDialogListener.onGetLeftMenuFocus();
        }

        if (null == value || value.size() <= 0) {
            mViewBind.setVisibility(GONE);
            mLlBindListContent.removeAllViews();
            return;
        }

        LinearLayout.LayoutParams params = new LayoutParams(776, LayoutParams.WRAP_CONTENT);
        params.topMargin = 5;
        params.gravity = Gravity.CENTER_HORIZONTAL;

        mViewBind.setVisibility(VISIBLE);
        mLlBindListContent.removeAllViews();

        if (value.size() > 0) {
            for (final RespDevice device : value) {
                final BindPhoneView phoneView = new BindPhoneView(mContext);
                phoneView.setPhoneName(TextUtils.isEmpty(device.nickname)
                        ? String.valueOf(device.userId)
                        : device.nickname);
                phoneView.setFocusable(true);

                phoneView.setBackgroundResource(R.drawable.bg_item_setting_select);
                phoneView.setOnFocusChangeListener(new OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            ScaleUtil.scaleToBig2(phoneView, 1.05f);

                        } else {
                            ScaleUtil.scaleToNormal2(phoneView);
                        }
                        phoneView.onViewFocusChange(hasFocus);
                    }
                });
                phoneView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showUnbindPhoneDialog(device.userId);
                    }
                });

                phoneView.setTag(device.userId);
                mLlBindListContent.addView(phoneView, params);
            }
        }
    }

    UnbindPhoneDialog unbindPhoneDialog;

    private void showUnbindPhoneDialog(final int userId) {
        try {
            if (null != unbindPhoneDialog && unbindPhoneDialog.isShowing()) {
                unbindPhoneDialog.dismiss();
                unbindPhoneDialog = null;
            }

        } catch (Exception e) {
            JLog.e(e);
        }

        unbindPhoneDialog = new UnbindPhoneDialog(mContext, R.style.TransparentDialog, new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != menuDialogListener) {
                    menuDialogListener.onUnBindPhone(userId);
                }
            }
        });
        unbindPhoneDialog.show();
    }

    /**
     * 解绑手机
     *
     * @param userId
     * @param isSuccess
     */
    public void unBindPhone(int userId, boolean isSuccess) {
        //解绑提示
        TextView view = new TextView(mContext);
        view.setBackgroundResource(R.drawable.bg_dialog_reset);
        view.setText(isSuccess ? "解绑成功" : "解绑失败");
        view.setTextSize(18);
        view.setGravity(Gravity.CENTER_VERTICAL);
        view.setPadding(27, 9, 27, 9);
        view.setTextColor(Color.parseColor("#F0F0F0"));
        view.setCompoundDrawablePadding(6);
        view.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.drawable.icon_chenggong), null, null, null);
        ToastUtil.showToastView(view);

        if (isSuccess) {
            int size = mLlBindListContent.getChildCount();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    if (null != mLlBindListContent.getChildAt(i).getTag()
                            && userId == (Integer) mLlBindListContent.getChildAt(i).getTag()) {

                        if (size == 1) {
                            //回到
                            if (null != menuDialogListener) {
                                menuDialogListener.onGetLeftMenuFocus();
                            }

                        } else {
                            if (i == size - 1) {
                                //是最后一个
                                handleUpOrDownFocus(KeyEvent.KEYCODE_DPAD_UP);

                            } else {
                                handleUpOrDownFocus(KeyEvent.KEYCODE_DPAD_DOWN);
                            }
                        }

                        mLlBindListContent.removeViewAt(i);
                        break;
                    }
                }

                mViewBind.setVisibility(mLlBindListContent.getChildCount() > 0 ? VISIBLE : GONE);
            }
        }
    }

    public void dismiss() {
        try {
            if (null != unbindPhoneDialog && unbindPhoneDialog.isShowing()) {
                unbindPhoneDialog.dismiss();
                unbindPhoneDialog = null;
            }

        } catch (Exception e) {
            JLog.e(e);
        }
    }

}
