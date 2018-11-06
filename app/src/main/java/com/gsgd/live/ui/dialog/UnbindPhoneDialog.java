package com.gsgd.live.ui.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.gsgd.live.ui.base.BaseDialog;
import com.gsgd.live.utils.MeasureUtil;

import butterknife.OnClick;
import hdpfans.com.R;

/**
 * 解绑手机对话框
 */
public class UnbindPhoneDialog extends BaseDialog {

    private View.OnClickListener mListener;

    public UnbindPhoneDialog(Context context, int theme, View.OnClickListener listener) {
        super(context, theme);
        this.mListener = listener;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_unbind_phone;
    }

    @Override
    protected void screenAdapter() {
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = MeasureUtil.getScreenSize(context).x;
        p.height = MeasureUtil.getScreenSize(context).y;
        p.gravity = Gravity.CENTER;
        getWindow().setAttributes(p);
    }

    @Override
    protected void initParams() {
        super.initParams();
    }

    @OnClick(R.id.btn_ok)
    void onOkClick(View v) {
        if (null != mListener) {
            mListener.onClick(v);
        }
        dismiss();
    }

    @OnClick(R.id.btn_cancel)
    void onCancelClick() {
        dismiss();
    }


}
