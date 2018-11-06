package com.gsgd.live.data.api;

import com.gsgd.live.data.response.RespAllChannel;
import com.gsgd.live.data.response.RespBase;
import com.gsgd.live.data.response.RespChannelType;
import com.gsgd.live.data.response.RespConfig;
import com.gsgd.live.data.response.RespCustom;
import com.gsgd.live.data.response.RespDevice;
import com.gsgd.live.data.response.RespParamsInfo;
import com.gsgd.live.data.response.RespPlayBill;
import com.gsgd.live.data.response.RespPlayOrder;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    /**
     * 获取初始参数
     * http://aged-server.readyidu.com  http://192.168.4.99:9988
     */
    @FormUrlEncoded
    @POST("http://aged-server.readyidu.com/appInit/getParams.do")
    Observable<RespParamsInfo> getParams(
            @Field("params") String params,
            @Field("paramsPlatform") String paramsPlatform,
            @Field("paramsVersion") String paramsVersion
    );

    /**
     * 获取频道配置地址
     */
    @GET("channel/getChannelInfo.do")
    Call<RespBase<RespConfig>> getChannelInfo(
    );

    /**
     * 获取频道类型列表
     */
    @GET("getChannelTypeList")
    Call<RespBase<List<RespChannelType>>> getChannelType(
    );

    /**
     * 获取全部频道列表
     */
    @GET("getChannelList")
    Call<RespBase<RespAllChannel>> getChannel();

    /**
     * 获取直播源播放地址
     */
    @GET("getSourceById")
    Observable<RespBase<String>> getSourceById(
            @Query("id") int id
    );

    /**
     * 获取点播源播放地址
     */
    @GET("getDemandById")
    Observable<RespBase<String>> getDemandById(
            @Query("id") int id
    );

    /**
     * 举报不能播放的源
     */
    @FormUrlEncoded
    @POST("insertReportLive")
    Observable<RespBase<String>> report(
            @Field("sourceId") int source,
            @Field("isLive") boolean isLive
    );

    /**
     * 获取播放信息
     */
    @GET("getChannelPlaybill")
    Observable<RespBase<RespPlayBill>> getPlayBill(
            @Query("channelId") long channelId
    );

    /**
     * 获取子栏目预约信息
     */
    @GET("getPlayBillOrder")
    Observable<RespBase<List<RespPlayOrder>>> getPlayBillOrder(
            @Query("channelId") int channelId
    );

    /**
     * 获取二维码
     *
     * @param tvAlias tv别名
     */
    @GET("getQRCode")
    Observable<RespBase<String>> getQrCode(
            @Query("tvAlias") String tvAlias
    );

    /**
     * 获取绑定的设备列表
     */
    @GET("getDevices")
    Observable<RespBase<List<RespDevice>>> getBindDevices();

    /**
     * 解绑设备
     */
    @GET("unBind")
    Observable<RespBase<String>> unBindDevice(
            @Query("AppUserId") int userId
    );

    /**
     * 获取自建频道数据
     */
    @GET("DevicesChannels")
    Observable<RespBase<List<RespCustom>>> getCustomList();

}
