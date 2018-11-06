package com.gsgd.live.ui.leftMenu;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gsgd.live.data.listener.AbstractOnMenuDialogImpl;
import com.gsgd.live.data.listener.OnItemListener;
import com.gsgd.live.data.model.MessageModel;
import com.gsgd.live.ui.adapter.MessageAdapter;
import com.gsgd.live.ui.widgets.DividerItemDecoration;
import com.gsgd.live.utils.ContentUtils;
import com.jiongbull.jlog.JLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hdpfans.com.R;

/**
 * @author zhangqy
 * @Description 消息中心
 * @date 2017/12/22
 */
public class MenuMContentView extends LinearLayout {

    @BindView(R.id.message_recycler)
    RecyclerView mRecyclerMsg;
    @BindView(R.id.bg_msg_cover)
    View mViewBgMsg;
    @BindView(R.id.no_message_lay)
    View noMessageLay;
    @BindView(R.id.message_right_arrow)
    ImageView mIvMsgRightArrow;

    private Context mContext;
    private LinearLayoutManager mLayoutManagerMessage;
    private MessageAdapter messageAdapter;
    private List<MessageModel> messageList = new ArrayList<>();
    private AbstractOnMenuDialogImpl menuDialogListener;
    private int selectMsgPosition = 0;

    public MenuMContentView(Context context) {
        this(context, null);
    }

    public MenuMContentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;

        View root = LayoutInflater.from(context).inflate(R.layout.view_menu_message_content, this, true);
        ButterKnife.bind(this, root);
    }

    public void initData(AbstractOnMenuDialogImpl menuDialogListener) {
        this.menuDialogListener = menuDialogListener;

        initMessageListUI();
    }

    /**
     * 初始化消息
     */
    private void initMessageListUI() {
        messageList = ContentUtils.getInstance().query(mContext);
        JLog.d("===========initMessageListUI集合" + messageList.size());
        Collections.reverse(messageList);//倒序集合

        if (messageList.size() > 0) {
            boolean isReadMsg = ContentUtils.getInstance().haveReadMsg(mContext);
            //提示未读消息
            refreshUnreadMsgTip(isReadMsg);

        } else {
            refreshUnreadMsgTip(false);
        }

        noMessageLay.setVisibility(messageList.size() > 0 ? View.GONE : View.VISIBLE);
        mRecyclerMsg.setVisibility(messageList.size() > 0 ? View.VISIBLE : View.GONE);
        mIvMsgRightArrow.setVisibility(messageList.size() > 0 ? View.VISIBLE : View.GONE);

        mLayoutManagerMessage = new LinearLayoutManager(mContext);
        mRecyclerMsg.setLayoutManager(mLayoutManagerMessage);
        mRecyclerMsg.addItemDecoration(new DividerItemDecoration(mContext, 0));
        messageAdapter = new MessageAdapter(mContext, messageList, new OnItemListener() {
            @Override
            public void onItemClick(int position) {
                handleRightFocus();
            }

            @Override
            public void onItemFocus(int position, boolean hasFocus) {
                if (hasFocus) {
                    selectMsgPosition = position;
                    try {
                        MessageModel model = messageList.get(position);
                        if (model.getIsRead() == 0) {
                            ContentUtils.getInstance().update(mContext, model);//修改数据库

                            MessageModel model1 = new MessageModel();
                            model1.setTypeId(model.getTypeId());
                            model1.setIsRead(1);
                            model1.setMessageTitle(model.getMessageTitle());
                            model1.setMessageContent(model.getMessageContent());
                            model1.setMtime(model.getMtime());
                            messageList.set(position, model1);

                            boolean isReadMsg = ContentUtils.getInstance().haveReadMsg(mContext);
                            //提示未读消息
                            refreshUnreadMsgTip(isReadMsg);
                        }

                    } catch (Exception e) {
                        JLog.e(e);
                    }
                }
            }
        });

        mRecyclerMsg.setAdapter(messageAdapter);
    }

    private void refreshUnreadMsgTip(boolean isReadMsg) {
        if (null != menuDialogListener) {
            menuDialogListener.onToggleUnreadMsg(isReadMsg);
        }
    }

    /**
     * 是否有焦点
     */
    public boolean isHasFocus() {
        return mRecyclerMsg.hasFocus();
    }

    /**
     * 处理向上或向下事件
     */
    public void handleUpOrDownFocus(int keyCode, boolean result) {
        boolean isDown = KeyEvent.KEYCODE_DPAD_DOWN == keyCode;

        if (mRecyclerMsg.hasFocus()) {
            final RecyclerView.ViewHolder holder = mRecyclerMsg.findViewHolderForAdapterPosition(selectMsgPosition);
            if (null != holder) {
                if (((MessageAdapter.ViewHolder) holder).mTvViewMsgInfo.hasFocus()) {
                    handleLeftFocus();
                    return;
                }
            }

            if ((!isDown && selectMsgPosition <= 0) || (isDown && selectMsgPosition >= messageAdapter.getItemCount() - 1)) {
                return /*true*/;
            }

            View focusView = mRecyclerMsg.getFocusedChild();
            if (focusView == null) {
                return /*result*/;

            } else {
                boolean isNeedScroll = MenuUtil.isNeedScrollCenter(isDown, messageAdapter.getItemCount(), selectMsgPosition);

                MenuUtil.toggleFocusBg(true, mViewBgMsg);
                View nextView = FocusFinder.getInstance().findNextFocus(mRecyclerMsg, focusView, isDown ? View.FOCUS_DOWN : View.FOCUS_UP);
                if (nextView != null) {
                    if (isNeedScroll) {
                        mRecyclerMsg.scrollBy(0, isDown ? 80 : -80);
                    }
                    nextView.requestFocus();
                    return /*true*/;

                } else {
                    mRecyclerMsg.scrollBy(0, isDown ? 80 : -80);
                    View nextView2 = FocusFinder.getInstance().findNextFocus(mRecyclerMsg, focusView, isDown ? View.FOCUS_DOWN : View.FOCUS_UP);
                    if (nextView2 != null) {
                        nextView2.requestFocus();
                    }
                    return /*true*/;
                }
            }
        }
    }

    /**
     * 重新获取焦点
     */
    public boolean handleGetFocus() {
        if (null != messageList && messageList.size() > 0) {
            int position = selectMsgPosition;
            refreshListUI(position, true);
            MenuUtil.toggleFocusBg(true, mViewBgMsg);

            return true;
        }

        return false;
    }

    /**
     * 处理向左的焦点
     *
     * @return 返回true表示此组件需要loss焦点
     */
    public boolean handleLeftFocus() {
        if (mRecyclerMsg.hasFocus()) {
            final RecyclerView.ViewHolder holder = mRecyclerMsg.findViewHolderForAdapterPosition(selectMsgPosition);
            if (null != holder) {
                if (((MessageAdapter.ViewHolder) holder).mTvViewMsgInfo.hasFocus()) {
                    ((MessageAdapter.ViewHolder) holder).mViewSub.setBackgroundResource(R.drawable.bg_channel_sub_select);
                    ((MessageAdapter.ViewHolder) holder).mViewSub.requestFocus();

                    mIvMsgRightArrow.setVisibility(View.VISIBLE);
                    MenuUtil.toggleFocusBg(true, mViewBgMsg);

                } else {
                    MenuUtil.toggleFocusBg(false, mViewBgMsg);
                    return true;
                }

            } else {
                return true;
            }
        }

        return false;
    }

    /**
     * 处理向右的焦点
     */
    public void handleRightFocus() {
        try {
            RecyclerView.ViewHolder holder = mRecyclerMsg.findViewHolderForAdapterPosition(selectMsgPosition);
            if (null != holder) {
                if (((MessageAdapter.ViewHolder) holder).mViewSub.hasFocus()) {
                    ((MessageAdapter.ViewHolder) holder).mViewSub.setBackgroundResource(R.drawable.bg_item_cover2);
                    ((MessageAdapter.ViewHolder) holder).mTvViewMsgInfo.setVisibility(View.VISIBLE);
                    ((MessageAdapter.ViewHolder) holder).mTvViewMsgInfo.requestFocus();

                    mIvMsgRightArrow.setVisibility(View.GONE);
                    MenuUtil.toggleFocusBg(false, mViewBgMsg);
                }
            }

        } catch (Exception e) {
            JLog.e(e);
        }
    }

    private void refreshListUI(final int position, final boolean hasFocus) {
        JLog.d("******->refreshListUI:" + position);
        mRecyclerMsg.post(new Runnable() {
            @Override
            public void run() {
                RecyclerView.ViewHolder holder = mRecyclerMsg.findViewHolderForLayoutPosition(position);
                if (null != holder) {
                    if (hasFocus) {
                        ((MessageAdapter.ViewHolder) holder).mViewSub.setBackgroundResource(R.drawable.bg_channel_select);
                        holder.itemView.requestFocus();

                    } else {
                        ((MessageAdapter.ViewHolder) holder).mViewSub.setBackgroundResource(R.drawable.bg_item_cover2);
                    }
                }
            }
        });
    }

}
