package com.crossoverjie.cim.route.vo.req;

import com.crossoverjie.cim.common.req.BaseRequest;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * Function:只需要用chatId进行请求的功能接口参数
 *
 * @author georgeyang
 *         Date: 2019/12/07 15:56
 * @since JDK 1.8
 */
public class UserIdReqVO extends BaseRequest {
    @NotNull(message = "userId 不能为空")
    @ApiModelProperty(required = true, value = "userId", example = "11")
    private Long userId ;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "ChatIdReqVO{" +
                "userId=" + userId +
                '}';
    }
}
