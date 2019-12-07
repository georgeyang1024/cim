package com.crossoverjie.cim.server.controller;

import com.crossoverjie.cim.common.enums.StatusEnum;
import com.crossoverjie.cim.common.res.BaseResponse;
import com.crossoverjie.cim.common.res.NULLBody;
import com.crossoverjie.cim.server.util.SessionSocketHolder;
import com.crossoverjie.cim.server.vo.req.UserIdReqVO;
import com.crossoverjie.cim.server.vo.req.SendMsgReqVO;
import com.crossoverjie.cim.common.constant.Constants;
import com.crossoverjie.cim.server.server.CIMServer;
import com.crossoverjie.cim.server.vo.res.SendMsgResVO;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 22/05/2018 14:46
 * @since JDK 1.8
 */
@Controller
@RequestMapping("/")
public class IndexController {

    @Autowired
    private CIMServer cimServer;


    /**
     * 统计 service
     */
    @Autowired
    private CounterService counterService;

    /**
     * 向服务端发消息
     * @param sendMsgReqVO
     * @return
     */
    @ApiOperation("服务端发送消息")
    @RequestMapping(value = "sendMsg",method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse<SendMsgResVO> sendMsg(@RequestBody SendMsgReqVO sendMsgReqVO){
        BaseResponse<SendMsgResVO> res = new BaseResponse();
        cimServer.sendMsg(sendMsgReqVO) ;

        counterService.increment(Constants.COUNTER_SERVER_PUSH_COUNT);

        SendMsgResVO sendMsgResVO = new SendMsgResVO() ;
        sendMsgResVO.setMsg("OK") ;
        res.setCode(StatusEnum.SUCCESS.getCode()) ;
        res.setMessage(StatusEnum.SUCCESS.getMessage()) ;
        res.setDataBody(sendMsgResVO) ;
        return res ;
    }

    /**
     * 获取用户是否在线的接口
     * @param userIdReqVo
     * @return
     */
    @ApiOperation("获取用户是否在线")
    @RequestMapping(value = "isUserOnline",method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse<NULLBody> isUserOnline(@RequestBody UserIdReqVO userIdReqVo){
        NioSocketChannel socketChannel = SessionSocketHolder.get(userIdReqVo.getUserId());

        if (null == socketChannel) {
            return BaseResponse.create(null,StatusEnum.OFF_LINE);
        }

        return BaseResponse.create(null,StatusEnum.SUCCESS);
    }

}
