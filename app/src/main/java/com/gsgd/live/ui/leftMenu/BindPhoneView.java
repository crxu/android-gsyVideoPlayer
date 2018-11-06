package com.gsgd.live.ui.leftMenu;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import hdpfans.com.R;

/**
 * @author zhangqy
 * @Description 绑定手机view
 * @date 2017/12/25
 */
public class BindPhoneView extends RelativeLayout {

    @BindView(R.id.tv_phone_name)
    TextView mTvPhoneName;
    @BindView(R.id.tv_phone_unbind)
    TextView mTvPhoneUnbind;

    public BindPhoneView(Context context) {
        this(context, null);
    }

    public BindPhoneView(Context context, AttributeSet attrs) {
        super(context, attrs);

        View root = LayoutInflater.from(context).inflate(R.layout.view_bind_phone, this, true);
        ButterKnife.bind(this, root);
    }

    public void onViewFocusChange(boolean hasFocus) {
        mTvPhoneName.setTextColor(Color.parseColor(hasFocus ? "#555555" : "#F0F0F0"));
        mTvPhoneName.setCompoundDrawablesWithIntrinsicBounds(hasFocus
                ? getContext().getResources().getDrawable(R.drawable.pic_shoujihover)
                : getContext().getResources().getDrawable(R.drawable.pic_shouji), null, null, null);
        mTvPhoneUnbind.setTextColor(Color.parseColor(hasFocus ? "#555555" : "#F0F0F0"));
        mTvPhoneUnbind.setCompoundDrawablesWithIntrinsicBounds(null, null, hasFocus
                ? getContext().getResources().getDrawable(R.drawable.icon_setting_arrow_s)
                : getContext().getResources().getDrawable(R.drawable.icon_setting_arrow_n), null);
    }

    public void setPhoneName(String phoneName) {
        mTvPhoneName.setText(phoneName);
    }

}
