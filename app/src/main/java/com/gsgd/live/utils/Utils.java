package com.gsgd.live.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

import com.google.gson.Gson;
import com.gsgd.live.AppConfig;
import com.gsgd.live.data.model.Channel;
import com.gsgd.live.data.model.ChannelList;
import com.gsgd.live.data.model.ChannelType;
import com.gsgd.live.data.response.RespSource;
import com.jiongbull.jlog.JLog;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * @author zhangqy
 * @Description
 * @date 2017/7/4
 */
public final class Utils {

    /**
     * 读取asset目录下文件。
     *
     * @return content
     */
    public static String readFile(Context mContext, String file, String code) {
        int len = 0;
        byte[] buf = null;
        String result = "";
        try {
            InputStream in = mContext.getAssets().open(file);
            len = in.available();
            buf = new byte[len];
            in.read(buf, 0, len);

            result = new String(buf, code);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getChannelId(long id) {
        String str = String.valueOf(id);
        if (str.length() == 1) {
            str = "00" + str;

        } else if (str.length() == 2) {
            str = "0" + str;
        }

        return str;
    }

    public static ArrayList<Channel> getMatchChannel(String mTvListStr, String matchId) {
        JLog.d("*********->matchId:" + matchId + "||mTvListStr:" + mTvListStr);
        ArrayList<Channel> list = new ArrayList<>();

        try {
            if (!TextUtils.isEmpty(mTvListStr)) {
                if (mTvListStr.contains("[{")) {
                    ChannelList channelList = new Gson().fromJson(mTvListStr, ChannelList.class);
                    if (null != channelList.channelInfoList && channelList.channelInfoList.size() > 0) {

                        for (ChannelList.ChannelInfo info : channelList.channelInfoList) {
                            for (Channel channel : AppConfig.ALL_CHANNEL) {
                                if (channel.channel.equals(info.channel.channel)) {
                                    list.add(channel);
                                    break;
                                }
                            }
                        }
                    }

                } else {
                    String[] strArr = mTvListStr.split("\\|");
                    if (strArr.length > 0) {
                        for (String tvName : strArr) {
                            for (Channel channel : AppConfig.ALL_CHANNEL) {
                                if (channel.channel.equals(tvName)) {
                                    list.add(channel);
                                    break;
                                }
                            }
                        }
                    }
                }

            } else {
                if (!TextUtils.isEmpty(matchId)) {
                    for (Channel channel : AppConfig.ALL_CHANNEL) {
                        if (matchId.equals(String.valueOf(channel.id))) {
                            list.add(channel);
                            break;
                        }
                    }
                }
            }

            //按id大小排序
            Collections.sort(list, new Comparator<Channel>() {
                @Override
                public int compare(Channel o1, Channel o2) {
                    return (int) (o1.id - o2.id);
                }
            });

        } catch (Exception e) {
            JLog.e(e);
        }

        return list;
    }

    //http、https、rtmp
    private static final String regex = "^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://|[rR][tT][mM][pP]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+(\\?{0,1}(([A-Za-z0-9-~]+\\={0,1})([A-Za-z0-9-~]*)\\&{0,1})*)$";

    /**
     * 验证视频源是否正确
     */
    public static boolean isRightSource(String source) {
        try {
            Pattern pattern = Pattern.compile(regex);
            return pattern.matcher(source).matches();

        } catch (Exception e) {
            JLog.e(e);
        }

        return true;
    }

    /**
     * 获取选择的type
     *
     * @param channel
     * @param channelTypes
     * @return
     */
    public static ChannelType getSelectType(Channel channel, ArrayList<ChannelType> channelTypes) {
        int size = channelTypes.size();

        //跳过我的收藏
        for (int i = 0; i < size; i++) {
            for (Channel c : channelTypes.get(i).channels) {
                if (c.id == channel.id) {
                    return channelTypes.get(i);
                }
            }
        }

        return null;
    }

    /**
     * 加密内部url
     *
     * @param url
     * @return 加密后的url
     */
    public static String encryptionInnerUrl(String url) {
        //TODO 确定密钥
        try {
//                ?t=1509698888&s=v6UqfbFeOI9rq602ZmYiyQ
            long time = new Date().getTime() + 1000 * 60;
            String s = MD5.toMD5(url + time + "secret");
            s = Base64.encodeToString(s.getBytes(), Base64.DEFAULT);

            return url + "?t=" + time + "&s=" + s;

        } catch (Exception e) {
            JLog.e(e);
        }

        return url;
    }

    /**
     * 简化名称，去掉频道
     *
     * @param name
     * @return
     */
    public static String simplifyName(String name) {
        if (!TextUtils.isEmpty(name) && name.length() >= 6) {
            if (name.endsWith("频道")) {
                name = name.substring(0, name.length() - 2);
            }
        }
        return name;
    }

    /**
     * 当前数据是否有效
     *
     * @return -1 无效 ; 0有效 ; 1源无效，取Channel第一个 ;2频道有效，需要从新获取channelType
     */
    public static int isValidCurrentData(List<ChannelType> typeList, ChannelType channelType, Channel channel, RespSource source) {
        int responseType = -1;//无效

        try {
            //1、检验channelType
            ChannelType findType = null;
            for (ChannelType type : typeList) {
                if (type.id == channelType.id) {
                    findType = type;
                    break;
                }
            }
            if (null == findType) {
                return responseType;
            }

            //2. 检验channel
            Channel findChannel = null;
            for (Channel c : findType.channels) {
                if (c.id == channel.id) {
                    findChannel = c;
                    break;
                }
            }
            if (null == findChannel) {
                //检查channel是否在全部频道中
                for (Channel c : AppConfig.ALL_CHANNEL) {
                    if (c.id == channel.id) {
                        findChannel = c;
                        break;
                    }
                }

                if (null == findChannel) {
                    return responseType;
                }

                responseType = 2;
                return responseType;
            }

            //3. 检验source
            RespSource findSource = null;
            for (RespSource respSource : findChannel.sources) {
                if (respSource.id == source.id) {
                    findSource = respSource;
                    break;
                }
            }
            if (null == findSource) {
                responseType = 1;

            } else {
                responseType = 0;
            }

        } catch (Exception e) {
            JLog.e(e);
        }

        return responseType;
    }


    /**
     * 返回发布时间距离当前的时间
     *
     * @param millisecond the create time
     * @return the result string
     */
    public static String timeAgo(long millisecond) {
        Date createdTime = new Date(millisecond);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);

        if (createdTime != null) {
            long agoTimeInMin = (new Date(System.currentTimeMillis()).getTime() - createdTime.getTime()) / 1000 / 60;
            //如果在当前时间以前一分钟内
            if (agoTimeInMin <= 1) {
                return "刚刚";
            } else if (agoTimeInMin <= 60) {
                //如果传入的参数时间在当前时间以前10分钟之内
                return agoTimeInMin + "分钟前";
            } else if (agoTimeInMin <= 60 * 24) {
                return agoTimeInMin / 60 + "小时前";
            } else if (agoTimeInMin <= 60 * 24 * 2) {
                return agoTimeInMin / (60 * 24) + "天前";
            } else {
                return format.format(createdTime);
            }
        } else {
            return format.format(new Date(0));
        }
    }


    /**
     * 判断当前时间小于24小时
     *
     * @param millisecond
     * @return
     */
    public static boolean is24Hours(long millisecond) {
        Date createdTime = new Date(millisecond);
        if (createdTime != null) {
            long agoTimeInMin = (new Date(System.currentTimeMillis()).getTime() - createdTime.getTime()) / 1000 / 60;
            if (agoTimeInMin <= 6) {
                return true;
            }
        }
        return false;
    }

}
