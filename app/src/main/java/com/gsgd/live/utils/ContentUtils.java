package com.gsgd.live.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.gsgd.live.data.model.MessageModel;
import com.jiongbull.jlog.JLog;

import java.util.ArrayList;
import java.util.List;


/**
 * 操作数据库
 * Created by Jackshao on 2017/11/29.
 */

public class ContentUtils {

	private Uri Message_ALL_URI = Uri.parse("content://" + "com.readyidu.messageprovider" + "/"+"live_message");

	public static ContentUtils getInstance(){
		return ContentUtils.ContentUtilsTool.instance;
	}

	private static class ContentUtilsTool {
		private static final ContentUtils instance = new ContentUtils();
	}

	//查询所有数据
	public List<MessageModel> query(Context context){
		List<MessageModel> modelList = new ArrayList<>();
		Cursor cursor = null;
		try {
			cursor = context.getContentResolver().query(Message_ALL_URI, null, null, null,null);
			if (cursor != null) {
				modelList.clear();
				while (cursor.moveToNext()) {
					String messageTitle = cursor.getString(cursor.getColumnIndex("messageTitle"));
					String messageContent = cursor.getString(cursor.getColumnIndex("messageContent"));
					int typeId = cursor.getInt(cursor.getColumnIndex("typeId"));
					int isRead = cursor.getInt(cursor.getColumnIndex("isRead"));
					int id = cursor.getInt(cursor.getColumnIndex("_id"));//主键
					String mTime = cursor.getString(cursor.getColumnIndex("mtime"));

//					System.out.println(id+"==========shshsjsjsjs===="+Utils.is24Hours(Long.parseLong(mTime)));
					System.out.println(messageContent+"====ddddd==="+"=========="+typeId);
					MessageModel model = new MessageModel();
					model.setId(id);
					model.setTypeId(typeId);
					model.setIsRead(isRead);
					model.setMtime(Long.parseLong(mTime));
					model.setMessageContent(messageContent);
					model.setMessageTitle(messageTitle);
					modelList.add(model);
				}
			}
			return modelList;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}


	/**
	 * 判断是否有未读消息
	 * @param context
	 * @return
	 */
	public boolean haveReadMsg(Context context){
		boolean isRead = false;
		Cursor cursor = null;
		try {
			cursor = context.getContentResolver().query(Message_ALL_URI, null, null, null,null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					int isReadMsg = cursor.getInt(cursor.getColumnIndex("isRead"));
					isRead = (isReadMsg == 0);
					if (isRead){
						return isRead;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return isRead;
	}


	//更新
	public void update(Context context,MessageModel messageModel) {
		ContentResolver mContentResolver = context.getContentResolver();
		ContentValues values = new ContentValues();
		values.put("typeId", messageModel.getTypeId());
		values.put("isRead", 1);
		values.put("mtime",messageModel.getMtime());
		values.put("messageTitle", messageModel.getMessageTitle());
		values.put("messageContent", messageModel.getMessageContent());

		String where = "_ID=" + messageModel.getId();
		int count = mContentResolver.update(Message_ALL_URI, values,
			where, null);
		System.out.println(count+"===========update");
	}


	//删除
	public void deleteId(Context context,int id){
		System.out.println(id+"=========delete========");
		ContentResolver mContentResolver = context.getContentResolver();
		String where = "_ID=" + id;
		mContentResolver.delete(Message_ALL_URI, where, null);
	}

	//删除
	public void deleteAll(Context context){
		ContentResolver mContentResolver = context.getContentResolver();
		mContentResolver.delete(Message_ALL_URI, null, null);
	}


	//插入
	public void insert(Context context,int typeId,String title,String content) {
		ContentResolver mContentResolver = context.getContentResolver();
		ContentValues values = new ContentValues();
		values.put("typeId", typeId);
		values.put("isRead", 0);
		values.put("mtime",System.currentTimeMillis()+"");
		values.put("messageTitle", title);
		values.put("messageContent", content);
		// 通过ContentResolver来向数据库插入数据
		Uri uri = mContentResolver.insert(Message_ALL_URI, values);
		JLog.d(uri.toString()+"==============insert");
	}

}
