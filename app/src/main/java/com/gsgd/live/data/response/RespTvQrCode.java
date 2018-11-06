package com.gsgd.live.data.response;

/**
 * @author zhangqy
 * @Description tv二维码
 * @date 2017/12/26
 */
public class RespTvQrCode {

    public String QRDecodeUrl;//	解析二维码的URL地址	string	@mock=app/bundling
    public String token;//	验证二维码令牌	string	@mock=afde5ced-58e1-41ab-b6a9-c88350b741f5
    public String tvAlias;//	机顶盒别名	string	@mock=bbb
    public String tvDeviceId;//	机顶盒id	string	@mock=1012628141
}
