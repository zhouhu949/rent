package com.scrats.rent.api;

import com.scrats.rent.base.service.RedisService;
import com.scrats.rent.base.service.SmsService;
import com.scrats.rent.common.APIRequest;
import com.scrats.rent.common.JsonResult;
import com.scrats.rent.common.annotation.IgnoreSecurity;
import com.scrats.rent.common.exception.BusinessException;
import com.scrats.rent.util.AccountValidatorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

/**
 * @Created with scrat.
 * @Description: ${DESCRIPTION}.
 * @Email: guosq@scrats.cn.
 * @Author: lol.
 * @Date: 2018/8/13 22:46.
 */
@RestController
@RequestMapping("/api/sms")
public class SmsApi {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RedisService redisService;

    @Autowired
    private SmsService smsService;

    @IgnoreSecurity
    @PostMapping("/send")
    public JsonResult send(@RequestBody APIRequest apiRequest) throws UnsupportedEncodingException, NoSuchAlgorithmException {

        String phone = APIRequest.getParameterValue(apiRequest,"phone",String.class);

        if(!AccountValidatorUtil.isMobile(phone)){
            throw new BusinessException("请求的手机号不正确");
        }

        if(smsService.send(phone)){
            return new JsonResult();
        }
        return new JsonResult("发送短信失败, 请重试!");
    }

}
