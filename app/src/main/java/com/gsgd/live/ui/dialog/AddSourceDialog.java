package com.gsgd.live.ui.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.EditText;

import com.gsgd.live.data.events.PlayEvent;
import com.gsgd.live.ui.base.BaseDialog;
import com.gsgd.live.utils.MeasureUtil;
import com.gsgd.live.utils.ToastUtil;
import com.gsgd.live.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;
import hdpfans.com.R;

/**
 * 添加源对话框
 */
public class AddSourceDialog extends BaseDialog {

    @BindView(R.id.et_name)
    EditText mEtName;
    @BindView(R.id.et_source)
    EditText mEtSource;

    public AddSourceDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_add_source;
    }

    @Override
    protected void screenAdapter() {
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = MeasureUtil.per2px(context.getResources(), 0.65);
        p.height = MeasureUtil.perh2px(context.getResources(), 0.55);
        p.gravity = Gravity.CENTER;
        getWindow().setAttributes(p);
    }

    @OnClick(R.id.btn_ok)
    void onOkClick() {
        String name = mEtName.getText().toString().trim();
        String source = mEtSource.getText().toString().trim();
//        source = "http://sjlivecdn.cbg.cn/201712200940/bfad0f2dcb825bb6b4eb412d7d04fbdf/app_2/_definst_/ls_3.stream/chunklist.m3u8#{\"Referer\":\"http://mapi.cbg.cn/live/detail?id=9&redirectUrl=http%3A%5C%2F%5C%2Fmapi.cbg.cn%2Flive%2Flist%2Fcqweb\"\u0010}";
        if (TextUtils.isEmpty(name)) {
            mEtName.requestFocus();
            ToastUtil.showToast("请输入名称！");
            return;
        }
        if (TextUtils.isEmpty(source)) {
            mEtSource.requestFocus();
            ToastUtil.showToast("请输入源地址！");
            return;
        }
        if (!Utils.isRightSource(source)) {
            ToastUtil.showToast("请输入正确格式的源地址！");
            return;
        }

        //保存用户信息
        EventBus.getDefault().post(new PlayEvent.SaveSourceEvent(name, source));
        dismiss();
    }

}
