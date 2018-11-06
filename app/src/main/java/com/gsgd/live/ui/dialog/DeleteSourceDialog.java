package com.gsgd.live.ui.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import com.gsgd.live.data.events.PlayEvent;
import com.gsgd.live.data.model.Channel;
import com.gsgd.live.ui.base.BaseDialog;
import com.gsgd.live.utils.MeasureUtil;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;
import hdpfans.com.R;

/**
 * 删除源对话框
 */
public class DeleteSourceDialog extends BaseDialog {

    @BindView(R.id.tv_desc)
    TextView mTvDesc;
    Channel channel;

    public DeleteSourceDialog(Context context, int theme, Channel channel) {
        super(context, theme);
        this.channel = channel;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_delete_source;
    }

    @Override
    protected void screenAdapter() {
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = MeasureUtil.per2px(context.getResources(), 0.45);
        p.height = MeasureUtil.perh2px(context.getResources(), 0.45);
        p.gravity = Gravity.CENTER;
        getWindow().setAttributes(p);
    }

    @Override
    protected void initParams() {
        super.initParams();
        mTvDesc.setText("确定要删除" + channel.channel + "吗？");
    }

    @OnClick(R.id.btn_ok)
    void onOkClick() {
        EventBus.getDefault().post(new PlayEvent.DeleteSourceEvent(channel));
        dismiss();
    }

}
