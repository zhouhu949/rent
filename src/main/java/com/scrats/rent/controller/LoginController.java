package com.scrats.rent.controller;

import com.scrats.rent.common.JsonResult;
import com.scrats.rent.common.annotation.IgnoreSecurity;
import com.scrats.rent.entity.Account;
import com.scrats.rent.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created with scrat.
 * Description: ${DESCRIPTION}.
 * Email:    guosq@scrats.cn.
 * Author:   lol.
 * Date:     2018/5/27 22:37.
 */
@Controller
public class LoginController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AccountService accountService;

    @IgnoreSecurity
    @PostMapping("/login")
    @ResponseBody
    public JsonResult login(@RequestBody Account account) {

        return accountService.login(account.getUsername(), account.getPwd());
    }

}
