package com.gsgd.live.utils;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.text.TextUtils;

import com.gsgd.live.AppConfig;
import com.umeng.analytics.MobclickAgent;

import java.io.File;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

/**
 * 跳转
 *
 */
public class RouterUtils {

	public static RouterUtils getInstance(){
		return RouterUtils.RouterUtilsTool.instance;
	}
	private static class RouterUtilsTool {
		private static final RouterUtils instance = new RouterUtils();
	}

	/* 判断是否安装 */
	public boolean isAppInstalled(Context context, String packagename) {
		PackageInfo packageInfo;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(
				packagename, 0);
		} catch (PackageManager.NameNotFoundException e) {
			packageInfo = null;
			e.printStackTrace();
		}
		if (packageInfo == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 判断网络是否连接
	 * @return
	 */
	public boolean checknetwork(Context activity) {
		ConnectivityManager cm = (ConnectivityManager) activity
			.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null)
			return false;
		NetworkInfo netinfo = cm.getActiveNetworkInfo();
		if (netinfo == null) {
			return false;
		}
		if (netinfo.isConnected()) {
			return true;
		}
		return false;
	}


	/**
	 * 进入聊天界面
	 * @param activity
	 */
	public  void gotoChatApp(Context activity, int intent_type){
		MobclickAgent.onEvent(activity,"click_jk"); //点击聊天
		boolean chatAppInstalled = isAppInstalled(
			activity.getApplicationContext(), "com.iflytek.openvoice.sample");
		if (chatAppInstalled) {
			try {
				if (checknetwork(activity)) {
					String userId = userId();
					if (!TextUtils.isEmpty(userId)){
						RouterUtils.getInstance().onPoseLive(activity);//暂停视频
						Intent chatIntent = new Intent("com.readyidu.healthtvchat.main");
						chatIntent.putExtra("intent_type",intent_type);
						chatIntent.addFlags(FLAG_ACTIVITY_SINGLE_TOP);
						activity.startActivity(chatIntent);
					}else {
						ToastUtil.showToast("当前账号未登录，请登录再访问");
					}
				}else {
					ToastUtil.showToast("您的网络未连接,请稍后再重试");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 得到用户userid
	 * @return
	 */
	public String userId(){
		try {
			File file = new File(Environment.getExternalStoragePublicDirectory("") + "/.user_id_live");
			ACache mCache = ACache.get(file);
			String userId = mCache.getAsString("live_userId");
			return userId;
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}



	//设备管理
	public  void goToDeviceManager(Context activity){
		boolean appInstalled2 = isAppInstalled(
			activity.getApplicationContext(), "health.readyidu.com.oldhealth4tv");
		if (appInstalled2){
			if (checknetwork(activity)) {
				String userId = userId();
				if (!TextUtils.isEmpty(userId)){
					RouterUtils.getInstance().onPoseLive(activity);//暂停视频
					Intent healthIntent = new Intent("com.oldhealth4tv.mydevicemanager");
					healthIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					activity.startActivity(healthIntent);
				}else {
					ToastUtil.showToast("当前账号未登录，请登录再访问");
				}
			} else {
				ToastUtil.showToast("您的网络未连接,请稍后再重试");
			}
		}
	}


	//测量详情
	public  void goToHealthInfo(Context activity,int type){
		boolean appInstalled2 = isAppInstalled(
			activity.getApplicationContext(), "health.readyidu.com.oldhealth4tv");
		if (appInstalled2){
			if (checknetwork(activity)) {
				String userId = userId();
				if (!TextUtils.isEmpty(userId)){
					RouterUtils.getInstance().onPoseLive(activity);//暂停视频
					Intent healthIntent = new Intent("com.oldhealth4tv.healthinfo");
					healthIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					healthIntent.putExtra("child_type",type);
					healthIntent.putExtra("userId",Integer.parseInt(userId));
					activity.startActivity(healthIntent);
				}else {
					ToastUtil.showToast("当前账号未登录，请登录再访问");
				}
			} else {
				ToastUtil.showToast("您的网络未连接,请稍后再重试");
			}
		}
	}

	public void onPoseLive(Context context){
		Intent intent = new Intent(AppConfig.ACTION_CLOSE_PLAY_GSGD);
		context.sendBroadcast(intent);
	}

}
