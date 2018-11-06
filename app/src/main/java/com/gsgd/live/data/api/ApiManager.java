package com.gsgd.live.data.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gsgd.live.AppConfig;
import com.gsgd.live.MainApplication;
import com.gsgd.live.data.model.Channel;
import com.gsgd.live.data.model.ChannelType;
import com.gsgd.live.data.model.CollectionChannel;
import com.gsgd.live.data.model.ConfigInfo;
import com.gsgd.live.data.model.CustomChannel;
import com.gsgd.live.data.model.CustomInfo;
import com.gsgd.live.data.model.CustomTypeInfo;
import com.gsgd.live.data.model.PlayOrder;
import com.gsgd.live.data.model.PlayOrderList;
import com.gsgd.live.data.model.SourceModel;
import com.gsgd.live.data.response.RespBase;
import com.gsgd.live.data.response.RespChannel;
import com.gsgd.live.data.response.RespChannelType;
import com.gsgd.live.data.response.RespConfig;
import com.gsgd.live.data.response.RespCustom;
import com.gsgd.live.data.response.RespDevice;
import com.gsgd.live.data.response.RespParamsInfo;
import com.gsgd.live.data.response.RespPlayBill;
import com.gsgd.live.data.response.RespPlayOrder;
import com.gsgd.live.data.response.RespSource;
import com.gsgd.live.utils.ACache;
import com.gsgd.live.utils.AesUtils;
import com.gsgd.live.utils.DeviceUtils;
import com.gsgd.live.utils.MD5;
import com.gsgd.live.utils.SP2Util;
import com.gsgd.live.utils.SPUtil;
import com.gsgd.live.utils.download.CloseUtils;
import com.gsgd.live.utils.download.FileUtils;
import com.gsgd.live.utils.download.Utility;
import com.jiongbull.jlog.JLog;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

public class ApiManager {

    private static final String TAG = ApiManager.class.getSimpleName();

    private ApiService mApiService;

    public ApiManager(ApiService apiService) {
        this.mApiService = apiService;
    }

    /**
     * 统一判断返回结果是否正确
     */
    private <T> Observable<T> flatResult(final RespBase<T> result) {
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> e) throws Exception {
                if (200 == result.code) {
                    if (null == result.data) {
                        e.onError(new ApiException(0, "没有数据"));

                    } else {
                        e.onNext(result.data);
                    }
                } else if (16000 == result.code) {
                    e.onNext(result.data);
                } else {
                    e.onError(new ApiException(result.code, result.message));
                }
                e.onComplete();
            }
        });
    }

    /**
     * 统一判断返回结果是否正确
     */
    private Observable<SourceModel> flatSourceResult(final RespBase<String> result) {
        return Observable.create(new ObservableOnSubscribe<SourceModel>() {
            @Override
            public void subscribe(ObservableEmitter<SourceModel> e) throws Exception {
                if (200 == result.code) {
                    if (null == result.data) {
                        e.onError(new ApiException(0, "没有数据"));

                    } else {
                        e.onNext(new SourceModel(true, result.data));
                    }
                } else if (16000 == result.code) {
                    e.onNext(new SourceModel(false, result.data));
                } else {
                    e.onError(new ApiException(result.code, result.message));
                }
                e.onComplete();
            }
        });
    }

    /**
     * 获取视频地址
     */
    public Observable<SourceModel> getSourceById(int sourceId, boolean isInDibbling) {

        if (isInDibbling) {
            //点播
            return mApiService
                    .getDemandById(sourceId)
                    .flatMap(new Function<RespBase<String>, ObservableSource<SourceModel>>() {
                        @Override
                        public ObservableSource<SourceModel> apply(RespBase<String> resp) throws Exception {
                            return flatSourceResult(resp);
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());

        } else {
            return mApiService
                    .getSourceById(sourceId)
                    .flatMap(new Function<RespBase<String>, ObservableSource<SourceModel>>() {
                        @Override
                        public ObservableSource<SourceModel> apply(RespBase<String> resp) throws Exception {
                            return flatSourceResult(resp);
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());

        }
    }

    /**
     * 上报出错的源
     */
    public Observable<String> reportSource(int sourceId, boolean isLive) {
        return mApiService
                .report(sourceId, isLive)
                .flatMap(new Function<RespBase<String>, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(RespBase<String> resp) throws Exception {
                        return flatResult(resp);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

    /**
     * 获取缓存的频道列表
     */
    private ArrayList<ChannelType> getCacheChannel() {
        try {
            JLog.d(TAG, "******->读取缓存");
            //获取最新的栏目配置
            RespConfig config = getCacheRespConfig();
            if (null != config) {
                //读取cache目录下的zip
                String path = AppConfig.BASE_CONFIG_PATH + File.separator + config.hash;
                String json = Utility.readConfigZipFile(path);
                ConfigInfo configInfo = new Gson().fromJson(json, ConfigInfo.class);
                return transformTypeList(configInfo);
            }

        } catch (Exception e) {
            JLog.e(TAG, e);
        }

        return new ArrayList<>();
    }

    /**
     * 读取内置的节目列表
     */
    private ArrayList<ChannelType> getDefaultChannelList() {
        try {
            JLog.d(TAG, "******->读取内置缓存");
            String path = AppConfig.BASE_CONFIG_PATH + File.separator + AppConfig.INNER_CONFIG_NAME;
            //拷贝Assets内置配置到cache目录下
            File file = new File(AppConfig.BASE_CONFIG_PATH, AppConfig.INNER_CONFIG_NAME);
            if (!file.exists()) {
                Utility.copyAssets(MainApplication.getContext(), AppConfig.INNER_CONFIG_NAME, path);
            }

            String json = Utility.readConfigZipFile(path);

            ConfigInfo configInfo = new Gson().fromJson(json, ConfigInfo.class);
            return transformTypeList(configInfo);

        } catch (Exception e) {
            JLog.e(TAG, e);
        }

        return new ArrayList<>();
    }

    private RespConfig getCacheRespConfig() {
        try {
            String json_config = SPUtil.getString(MainApplication.getContext(), AppConfig.SP_NAME, AppConfig.KEY_CHANNEL_CONFIG, "");

            if (!TextUtils.isEmpty(json_config)) {
                return new Gson().fromJson(json_config, RespConfig.class);
            }

        } catch (Exception e) {
            JLog.e(TAG, e);
        }

        return null;
    }

    private ArrayList<ChannelType> transformTypeList(ConfigInfo configInfo) {
        try {
            ArrayList<ChannelType> typeList = new ArrayList<>();

            //一级列表
            for (RespChannelType respChannelType : configInfo.data.typeList) {
                ChannelType channelType = new ChannelType();
                channelType.id = respChannelType.id;
                channelType.type = respChannelType.type;

                //加入频道
                typeList.add(channelType);
            }

            List<RespChannel> all_channel = configInfo.data.channels;
//            List<RespChannel> movie_channel = configInfo.data.movieList;

//            List<RespChannel> main_channel = new ArrayList<>();
            List<RespChannel> sub_channel = new ArrayList<>();//分级栏目

            //加入电影
//            if (null != movie_channel && movie_channel.size() > 0) {
//                for (RespChannel channel : movie_channel) {
//                    channel.isMovie = 1;//设置为电影类型
//                    if (channel.isSubChannel()) {
//                        sub_channel.add(channel);
//
//                    } else {
//                        main_channel.add(channel);
//                    }
//                }
//
//                all_channel.addAll(main_channel);
//            }

            //填入分级栏目
            for (RespChannel subChannel : sub_channel) {
                for (RespChannel respChannel : all_channel) {
                    if (subChannel.parentId == respChannel.id) {
                        respChannel.subChannels.add(subChannel);
                        break;
                    }
                }
            }

            for (RespChannel respChannel : all_channel) {

                //过滤源地址为空的
                if (null != respChannel.sources && respChannel.sources.size() > 0) {
                    Channel channel = new Channel();
                    channel.id = respChannel.id;
                    channel.channel = respChannel.channel;
                    channel.sources.addAll(respChannel.sources);
                    channel.isMovie = respChannel.isMovie;
                    channel.subParentId = respChannel.parentId;
                    channel.subChannels = formatChannels(respChannel.subChannels);
                    channel.isFengmi = respChannel.isFengmi;
                    channel.fengmiId = respChannel.fengmiId;
                    channel.orderIndex = respChannel.orderIndex;
                    channel.playType = respChannel.playType;

                    if (!TextUtils.isEmpty(respChannel.typeId)) {
                        String[] ids = respChannel.typeId.split("\\|");
                        channel.parentId = Arrays.asList(ids);

                        for (String id : ids) {
                            for (ChannelType channelType : typeList) {
                                if (String.valueOf(channelType.id).equals(id)) {
                                    //加入对应的分类频道
                                    channelType.channels.add(channel);
                                    break;
                                }
                            }
                        }
                    }

                } else {
                    JLog.e(TAG, "******->" + respChannel.channel + ":" + respChannel.id + ":没有源地址");
                }
            }

            //过滤节目数为0的栏目
            for (int i = 0; i < typeList.size(); i++) {
                if (null == typeList.get(i).channels || typeList.get(i).channels.size() == 0) {
                    typeList.remove(i);
                    i--;
                }
            }

            return typeList;

        } catch (Exception e) {
            JLog.e(TAG, e);
        }

        return new ArrayList<>();
    }

    /**
     * 从服务端拉取最新频道配置
     */
    private boolean getChannelConfigInfo() {
        try {
            //请求频道配置
            Call<RespBase<RespConfig>> call = mApiService.getChannelInfo();
            Response<RespBase<RespConfig>> response = call.execute();
            if (null != response
                    && response.isSuccessful()
                    && null != response.body()
                    && response.body().code == 200
                    && null != response.body().data) {

                RespConfig config = response.body().data;

                JLog.d(TAG, "开始下载最新配置！");
                boolean isSuccess = FileUtils.downloadFile(config.confUrl, AppConfig.BASE_CONFIG_TEMP_PATH, config.hash, config.hash);
                JLog.d(TAG, isSuccess ? "下载最新配置成功！" : "下载最新配置失败！");

                if (isSuccess) {
                    //缓存最新配置
                    SPUtil.putString(MainApplication.getContext(), AppConfig.SP_NAME, AppConfig.KEY_CHANNEL_CONFIG, config.toString());
                    return true;
                }
            }

        } catch (Exception exc) {
            JLog.e(TAG, exc);
        }

        return false;
    }

    private List<Channel> formatChannels(List<RespChannel> list) {
        List<Channel> channels = new ArrayList<>();

        for (RespChannel respChannel : list) {
            Channel channel = new Channel();
            channel.id = respChannel.id;
            channel.channel = respChannel.channel;
            channel.sources.addAll(respChannel.sources);
            channel.isMovie = respChannel.isMovie;
            channel.subParentId = respChannel.parentId;

            channel.isFengmi = respChannel.isFengmi;
            channel.fengmiId = respChannel.fengmiId;
            channel.orderIndex = respChannel.orderIndex;
            channel.playType = respChannel.playType;

            channels.add(channel);
        }

        return channels;
    }

    private ArrayList<ChannelType> getChannels() {
        ArrayList<ChannelType> typeList;

        typeList = getCacheChannel();//读取缓存
        if (null == typeList || typeList.size() == 0) {
            typeList = getDefaultChannelList();//读取内置缓存
        }

        if (null != typeList && typeList.size() > 0) {
            ArrayList<ChannelType> finalTypeList = new ArrayList<>();

//            ChannelType collectionChannelType = getCollectionChannel();//获取收藏源
//            finalTypeList.add(collectionChannelType);//添加收藏源栏目
//            initCollectStatus(collectionChannelType, typeList); //同步设置源收藏状态

            initAllChannels(typeList);//打平数据
            finalTypeList.addAll(typeList);

//            ChannelType customChannelType = getCustomChannel();//获取自定义源
//            finalTypeList.add(customChannelType);//添加自定义源栏目

            return finalTypeList;
        }

        return new ArrayList<>();
    }

    /**
     * 获取所有频道
     */
    public Observable<ArrayList<ChannelType>> getAllChannel() {
        return Observable
                .create(new ObservableOnSubscribe<ArrayList<ChannelType>>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<ArrayList<ChannelType>> e) throws Exception {
                        e.onNext(getChannels());
                        e.onComplete();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

    /**
     * 获取所有频道
     */
    public Observable<ArrayList<ChannelType>> getAllChannelDirect() {
        return Observable
                .create(new ObservableOnSubscribe<ArrayList<ChannelType>>() {
                    @Override
                    public void subscribe(ObservableEmitter<ArrayList<ChannelType>> e) throws Exception {
                        getChannelConfigInfo();
                        e.onNext(getChannels());
                        e.onComplete();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

    /**
     * 获取所有的源
     */
    private void initAllChannels(ArrayList<ChannelType> typeList) {
        List<Channel> list = new ArrayList<>();

        try {
            for (ChannelType channelType : typeList) {
                for (Channel channel : channelType.channels) {
                    if (!isInAllChannel(channel, list)) {
                        list.add(channel);
                    }
                }
            }

        } catch (Exception e) {
            JLog.e(TAG, e);
        }

        //设置全部频道
        AppConfig.ALL_CHANNEL = list;
    }

    private boolean isInAllChannel(Channel channel, List<Channel> list) {
        try {
            for (Channel c : list) {
                if (channel.id == c.id) {
                    return true;
                }
            }

        } catch (Exception e) {
            JLog.e(TAG, e);
        }

        return false;
    }

    /**
     * 初始化收藏状态
     *
     * @param collectType
     * @param typeList
     */
    private void initCollectStatus(ChannelType collectType, ArrayList<ChannelType> typeList) {
        try {
            for (ChannelType channelType : typeList) {
                for (Channel channel : channelType.channels) {
                    for (Channel collectChannel : collectType.channels) {
                        if (channel.id == collectChannel.id) {
                            channel.collectionStatus = 1;
                        }
                    }
                }
            }

        } catch (Exception e) {
            JLog.e(TAG, e);
        }
    }

    /**
     * 清空缓存
     */
    public void clearCache() {
        try {
            ACache mCache = ACache.get(MainApplication.getContext());
            mCache.clear();

        } catch (Exception e) {
            JLog.e(TAG, e);
        }
    }

    /**
     * 获取用户自定义源
     */
    private ChannelType getCustomChannel() {
        ChannelType customType = new ChannelType();
        customType.id = AppConfig.CUSTOM_TYPE_ID;
        customType.type = "自定义源";

        //添加用户自定义源数据
        Channel channel_top = new Channel();
        channel_top.id = AppConfig.CUSTOM_TYPE_ADD_ID;
        channel_top.channel = "添加自定义源";
        customType.channels.add(channel_top);

        List<CustomChannel> customChannels = DataSupport.findAll(CustomChannel.class);
        if (null != customChannels && customChannels.size() > 0) {
            for (CustomChannel customChannel : customChannels) {
                Channel channel = new Channel();
                channel.id = AppConfig.CUSTOM_START_ID + customChannel.getId();
                channel.channel = customChannel.getName();
                channel.isCustom = true;

                List<RespSource> listSource = new ArrayList<>();
                RespSource respSource = new RespSource();
                respSource.source = customChannel.getSource();
                listSource.add(respSource);
                channel.sources.addAll(listSource);

                ArrayList listId = new ArrayList<>();
                listId.add(String.valueOf(AppConfig.CUSTOM_TYPE_ID));
                channel.parentId = listId;

                //添加
                customType.channels.add(channel);
            }
        }

        return customType;
    }

    /**
     * 获取用户收藏的源
     */
    private ChannelType getCollectionChannel() {
        ChannelType collectionType = new ChannelType();
        collectionType.id = AppConfig.COLLECTION_TYPE_ID;
        collectionType.type = "我的收藏";

        //添加用户收藏的数据
        List<CollectionChannel> collectionChannels = DataSupport.findAll(CollectionChannel.class);
        if (null != collectionChannels && collectionChannels.size() > 0) {
            Gson gson = new Gson();
            for (CollectionChannel collectionChannel : collectionChannels) {
                try {
                    Channel channel = gson.fromJson(collectionChannel.getDesc(), Channel.class);
                    channel.collectionStatus = 1;
                    channel.isCollectionType = 1;
                    //添加
                    collectionType.channels.add(channel);

                } catch (Exception e) {
                    JLog.e(e);
                }
            }
        }

        return collectionType;
    }

    /**
     * 保存自定义源
     *
     * @param name   名称
     * @param source 地址
     */
    public Observable<Channel> saveCustomSource(final String name, final String source) {
        return Observable
                .create(new ObservableOnSubscribe<Channel>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<Channel> e) throws Exception {
                        try {
                            CustomChannel customChannel = new CustomChannel();
//                            customChannel.setId(System.currentTimeMillis());
                            customChannel.setName(name);
                            customChannel.setSource(source);
                            boolean isSuccess = customChannel.save();

                            if (isSuccess) {
                                Channel channel = new Channel();
                                channel.id = AppConfig.CUSTOM_START_ID + customChannel.getId();
                                channel.channel = customChannel.getName();
                                channel.isCustom = true;

                                JLog.d("************customChannel id:" + customChannel.getId());

                                List<RespSource> listSource = new ArrayList<>();
                                RespSource respSource = new RespSource();
                                respSource.source = customChannel.getSource();
                                listSource.add(respSource);
                                channel.sources.addAll(listSource);

                                ArrayList listId = new ArrayList<>();
                                listId.add(String.valueOf(AppConfig.CUSTOM_TYPE_ID));
                                channel.parentId = listId;

                                e.onNext(channel);

                            } else {
                                e.onNext(null);
                            }

                        } catch (Exception exc) {
                            JLog.e(exc);
                            e.onNext(null);
                        }

                        e.onComplete();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

    /**
     * 删除自定义源
     *
     * @param startId 源id
     */
    public Observable<Boolean> delCustomSource(final long startId) {
        return Observable
                .create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                        try {
                            long id = startId;
                            if (id >= AppConfig.CUSTOM_START_ID) {
                                id = startId - AppConfig.CUSTOM_START_ID;
                            }
                            JLog.d("******->delCustomSource:" + id);
                            boolean isSuccess = DataSupport.delete(CustomChannel.class, id) > 0;

                            try {
                                String json = SP2Util.getString(MainApplication.getContext(), AppConfig.SP_NAME, AppConfig.KEY_LAST_CHANNEL, "");
                                if (!TextUtils.isEmpty(json)) {
                                    Channel mCurrentChannel = new Gson().fromJson(json, Channel.class);
                                    if (id == mCurrentChannel.id) {
                                        JLog.d("******->匹配到当前自定义源:" + id);
                                        SP2Util.putString(MainApplication.getContext(), AppConfig.SP_NAME, AppConfig.KEY_LAST_CHANNEL_TYPE, "");
                                        SP2Util.putString(MainApplication.getContext(), AppConfig.SP_NAME, AppConfig.KEY_LAST_CHANNEL, "");
                                        SP2Util.putString(MainApplication.getContext(), AppConfig.SP_NAME, AppConfig.KEY_LAST_SOURCE, "");
                                    }
                                }

                            } catch (Exception exc) {
                                JLog.e(exc);
                            }

                            e.onNext(isSuccess);

                        } catch (Exception exc) {
                            JLog.e(exc);
                            e.onNext(false);
                        }

                        e.onComplete();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

    /**
     * 添加收藏
     *
     * @param channel 收藏的频道
     * @return
     */
    public Observable<Channel> addCollectionChannel(final Channel channel) {
        return Observable
                .create(new ObservableOnSubscribe<Channel>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<Channel> e) throws Exception {
                        try {
                            CollectionChannel customChannel = new CollectionChannel();
                            customChannel.setChannelId(channel.id);
                            customChannel.setName(channel.channel);
                            customChannel.setDesc(channel.toString());

                            if (customChannel.saveOrUpdate("channelId=?", "" + channel.id)) {
                                e.onNext(channel);

                            } else {
                                e.onNext(null);
                            }

                        } catch (Exception exc) {
                            JLog.e(exc);
                            e.onNext(null);
                        }

                        e.onComplete();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

    /**
     * 删除收藏
     *
     * @param id 频道id
     * @return
     */
    public Observable<Boolean> delCollectionChannel(final long id) {
        return Observable
                .create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                        try {
                            boolean isSuccess = DataSupport.deleteAll(CollectionChannel.class, "channelId=?", "" + id) > 0;

                            e.onNext(isSuccess);

                        } catch (Exception exc) {
                            JLog.e(exc);
                            e.onNext(false);
                        }

                        e.onComplete();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

    public Observable<ChannelType> refreshSourceList() {
        return Observable
                .create(new ObservableOnSubscribe<ChannelType>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<ChannelType> e) throws Exception {
                        e.onNext(getCustomChannel());
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

    public Observable<RespPlayBill> getPlayBill(long channelId) {
        return mApiService
                .getPlayBill(channelId)
                .flatMap(new Function<RespBase<RespPlayBill>, ObservableSource<RespPlayBill>>() {
                    @Override
                    public ObservableSource<RespPlayBill> apply(RespBase<RespPlayBill> resp) throws Exception {
                        return flatResult(resp);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

    public Observable<PlayOrderList> getPlayBillOrder(int channelId) {
        return mApiService.getPlayBillOrder(channelId)
                .flatMap(new Function<RespBase<List<RespPlayOrder>>, ObservableSource<PlayOrderList>>() {
                    @Override
                    public ObservableSource<PlayOrderList> apply(RespBase<List<RespPlayOrder>> resp) throws Exception {
                        PlayOrderList list = new PlayOrderList();
                        if (resp.code == 200) {
                            for (RespPlayOrder order : resp.data) {
                                list.addOrder(new PlayOrder(order));
                            }
                        }
                        return Observable.just(list);
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

    /**
     * 获取URL参数
     */
    public Observable<RespParamsInfo> getParams() {
        //先读取缓存的URL
        SharedPreferences sp = MainApplication.getContext().getSharedPreferences(AppConfig.SP_XY_APP, Context.MODE_PRIVATE);
        String url = sp.getString(AppConfig.KEY_API_URL, "");
        if (!TextUtils.isEmpty(url)) {
            AppConfig.BASE_API_URL = url;
        }

        String key = "561527c1095ed941e6282729738d7fd1";
        String param = DeviceUtils.getDeviceRegisterInfo();
        return mApiService
                .getParams(AesUtils.aesEncode(key, param), "0", "7")
                .flatMap(new Function<RespParamsInfo, ObservableSource<RespParamsInfo>>() {
                    @Override
                    public ObservableSource<RespParamsInfo> apply(@NonNull RespParamsInfo respParamsInfo) throws Exception {
                        if (null != respParamsInfo && respParamsInfo.isValid()) {
                            String apiUrl = respParamsInfo.data.brainUrl;
                            SharedPreferences sp = MainApplication.getContext().getSharedPreferences("sp_xy_id", Context.MODE_PRIVATE);

                            if (!apiUrl.endsWith("/")) {
                                apiUrl += "/";
                            }

                            sp.edit().putString(AppConfig.KEY_API_URL, apiUrl).apply();
                            AppConfig.BASE_API_URL = apiUrl;
                        }

                        return Observable.just(respParamsInfo);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

    /**
     * 删除数据库缓存
     */
    public void clearDb() {
        try {
            LitePal.deleteDatabase("custom_channel");
        } catch (Exception e) {
            JLog.e(e);
        }
    }

    /**
     * 获取tv二维码
     */
    public Observable<String> getTvQrCode() {
        return mApiService
                .getQrCode(DeviceUtils.getDeviceId())
                .flatMap(new Function<RespBase<String>, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(RespBase<String> resp) throws Exception {
                        return flatResult(resp);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

    /**
     * 获取绑定的设备
     */
    public Observable<List<RespDevice>> getBindDevices() {
        return mApiService
                .getBindDevices()
                .flatMap(new Function<RespBase<List<RespDevice>>, ObservableSource<List<RespDevice>>>() {
                    @Override
                    public ObservableSource<List<RespDevice>> apply(RespBase<List<RespDevice>> resp) throws Exception {
                        return flatResult(resp);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

    /**
     * 解绑设备
     */
    public Observable<String> unBindDevice(int userId) {
        return mApiService
                .unBindDevice(userId)
                .flatMap(new Function<RespBase<String>, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(RespBase<String> resp) throws Exception {
                        return flatResult(resp);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

    /**
     * 获取自建频道数据
     */
    public Observable<List<CustomTypeInfo>> getCustomList() {
        return mApiService
                .getCustomList()
                .flatMap(new Function<RespBase<List<RespCustom>>, ObservableSource<List<RespCustom>>>() {
                    @Override
                    public ObservableSource<List<RespCustom>> apply(RespBase<List<RespCustom>> resp) throws Exception {
                        return flatResult(resp);
                    }
                })
                .map(new Function<List<RespCustom>, List<CustomTypeInfo>>() {
                    @Override
                    public List<CustomTypeInfo> apply(List<RespCustom> respCustoms) throws Exception {
                        //下载自定义数据

                        List<CustomTypeInfo> list = new ArrayList<>();

                        List<String> fileNameList = new ArrayList<>();
                        for (RespCustom custom : respCustoms) {
                            String fileName = MD5.toMD5(custom.confUrl);
                            boolean isSuccess = FileUtils.downloadCustomFile(custom.confUrl, AppConfig.BASE_CUSTOM_TEMP_PATH, fileName, fileName);
                            if (isSuccess) {
                                CustomTypeInfo info = new CustomTypeInfo();
                                info.userId = custom.userId;
                                info.type = 0;
                                info.phoneName = custom.definedName;
                                info.sources = getCustomFromFile(new File(AppConfig.BASE_CUSTOM_PATH, fileName));

                                list.add(info);
                                fileNameList.add(fileName);
                            }
                        }

                        //删除临时文件夹
                        Utility.deleteAllFiles(AppConfig.BASE_CUSTOM_TEMP_PATH);

                        //删除冗余的文件
                        Utility.deleteAllFiles(AppConfig.BASE_CUSTOM_PATH, fileNameList);

                        return list;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

    private List<CustomInfo> getCustomFromFile(File file) {
        List<CustomInfo> list = new ArrayList<>();
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            isr = new InputStreamReader(new FileInputStream(file), "utf-8");
            br = new BufferedReader(isr);
            StringBuffer sb = new StringBuffer("");
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            String json = sb.toString();
            list = new Gson().fromJson(json, new TypeToken<List<CustomInfo>>() {
            }.getType());

        } catch (Exception e) {
            JLog.e(TAG, e);

        } finally {
            CloseUtils.closeIO(br, isr);
        }

        //过滤地址为空的
        List<CustomInfo> infoList = new ArrayList<>();
        for (CustomInfo info : list) {
            if (!TextUtils.isEmpty(info.sourceUrl)
                    && !TextUtils.isEmpty(info.sourceName)) {

                infoList.add(info);
            }
        }

        return infoList;
    }

    /**
     * 判断截图是否是纯色
     */
    public Observable<Boolean> judgmentIsBlack(final Bitmap bitmap) {
        return Observable
                .create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                        boolean isBlack = true;
                        int pixelColor;
                        int lastPixelColor = -1;
                        int height = bitmap.getHeight();
                        int width = bitmap.getWidth();

                        JLog.e("judgmentIsBlack:", "开始检查色值......");

                        OUT:
                        for (int y = 0; y < height; y++) {
                            for (int x = 0; x < width; x++) {
                                pixelColor = bitmap.getPixel(x, y);

                                if (lastPixelColor != -1 && pixelColor != lastPixelColor) {
                                    isBlack = false;
                                    break OUT;
                                }

                                lastPixelColor = pixelColor;
                            }
                        }

                        JLog.e("judgmentIsBlack:", "结束检查色值......" + isBlack);

                        e.onNext(isBlack);
                        e.onComplete();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

}
