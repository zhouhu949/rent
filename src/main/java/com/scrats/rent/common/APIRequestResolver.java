package com.scrats.rent.common;

import com.scrats.rent.common.annotation.APIRequestControl;
import com.scrats.rent.common.exception.BusinessException;
import com.scrats.rent.entity.Renter;
import com.scrats.rent.entity.WxSns;
import com.scrats.rent.service.RenterService;
import com.scrats.rent.service.WxSnsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

/**
 * @Created with scrat.
 * @Description: 增加方法注入，将含有 @CurrentUser 注解的方法参数注入当前登录用户.
 * @Email: guosq@scrats.cn.
 * @Author: lol.
 * @Date: 2018/6/8 17:19.
 */
public class APIRequestResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private RenterService renterService;
    @Autowired
    private WxSnsService wxSnsService;

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterType().isAssignableFrom(APIRequest.class) && methodParameter.hasParameterAnnotation(APIRequestControl.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        APIRequest apiRequest = (APIRequest) nativeWebRequest.getAttribute("apiRequest", RequestAttributes.SCOPE_REQUEST);
        if (apiRequest != null) {
            switch (apiRequest.getUser().getType()){
                //租客
                case "0":
                    Renter renter = renterService.findBy("userId",apiRequest.getUser().getUserId());
                    WxSns wxSns = wxSnsService.findBy("userId",apiRequest.getUser().getUserId());
                    apiRequest.setRenterId(renter.getRenterId());
                    if(null != wxSns){
                        apiRequest.setOpenId(wxSns.getOpenid());
                    }
                    break;
                //房东
                case "1":
                    apiRequest.setLanlordId(apiRequest.getUser().getUserId());
                    break;
                //管理员
                case "2":
                    apiRequest.setAdminId(apiRequest.getUser().getUserId());
                    break;
                //巡管员
                case "3":
                    apiRequest.setGuardId(apiRequest.getUser().getUserId());
                    break;
                //超级管理员
                case "4":
                    apiRequest.setAdminId(apiRequest.getUser().getUserId());
                    apiRequest.setAdministratorFlag(true);
                    break;
                default:
                    throw new BusinessException("数据有误,请联系系统管理员");
            }
            return apiRequest;
        }
        throw new MissingServletRequestPartException("@APIRequestControl");
    }
}
