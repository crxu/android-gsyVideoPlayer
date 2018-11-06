package com.gsgd.live.ui.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.gsgd.live.ui.base.BaseDialog;
import com.gsgd.live.utils.MeasureUtil;

import hdpfans.com.R;

/**
 * @author andy on 2018/1/22
 */

public class HintDialog extends BaseDialog {

    public HintDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_hint;
    }

    @Override
    protected void screenAdapter() {
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = MeasureUtil.dip2px(getContext(), 374);
        p.height = MeasureUtil.dip2px(getContext(), 267);
        p.gravity = Gravity.CENTER;
        getWindow().setAttributes(p);
    }

    @Override
    protected void initParams() {
        super.initParams();
        setCanceled(false);
        setCanceledOnTouchOutside(false);
        setCanceledOnTouchOutside(false);
        findViewById(R.id.btn_continue_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

}
