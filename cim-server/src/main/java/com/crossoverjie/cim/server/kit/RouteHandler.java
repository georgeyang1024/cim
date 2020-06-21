package com.crossoverjie.cim.server.kit;

import com.alibaba.fastjson.JSONObject;
import com.crossoverjie.cim.common.pojo.CIMUserInfo;
import com.crossoverjie.cim.server.config.AppConfiguration;
import com.crossoverjie.cim.server.util.SeesionWebSocketHolder;
import com.crossoverjie.cim.server.util.SessionSocketHolder;
import com.crossoverjie.cim.server.util.SpringBeanFactory;
import io.netty.channel.socket.nio.NioSocketChannel;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2019-01-20 17:20
 * @since JDK 1.8
 */
@Component
public class RouteHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(RouteHandler.class);


    private final MediaType mediaType = MediaType.parse("application/json");

    /**
     * 用户下线
     *
     * @param userInfo
     * @param channel
     * @throws IOException
     */
    public void userOffLine(CIMUserInfo userInfo, NioSocketChannel channel) {
        if (channel == null || channel.isActive()) {
            LOGGER.info("过滤新上线连接断开");
            //channel是用户唯一的最新的连接通道
            //前一个断线，没被检查出来之前，又马上被重新上，过滤
            return;
        }
        SessionSocketHolder.remove(channel);
        SeesionWebSocketHolder.remove(channel);
        if (userInfo != null) {
            LOGGER.info("用户[{}]下线", userInfo.getUserName());
            SessionSocketHolder.removeSession(userInfo.getUserId());
            SeesionWebSocketHolder.removeSession(userInfo.getUserId());
            //清除路由关系
            try {
                clearRouteInfo(userInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 清除路由关系
     *
     * @param userInfo
     * @throws IOException
     */
    public void clearRouteInfo(CIMUserInfo userInfo) throws IOException {
        OkHttpClient okHttpClient = SpringBeanFactory.getBean(OkHttpClient.class);
        AppConfiguration configuration = SpringBeanFactory.getBean(AppConfiguration.class);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", userInfo.getUserId());
        jsonObject.put("msg", "offLine");
        RequestBody requestBody = RequestBody.create(mediaType, jsonObject.toString());

        Request request = new Request.Builder()
                .url(configuration.getClearRouteUrl())
                .post(requestBody)
                .build();

        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
        } finally {
            try {
                response.body().close();
            } catch (Exception e) {
            }
            try {
                response.close();
            } catch (Exception e) {
            }
        }
    }

}
