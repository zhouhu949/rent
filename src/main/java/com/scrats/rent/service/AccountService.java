package com.scrats.rent.service;

import com.scrats.rent.base.service.BaseService;
import com.scrats.rent.common.JsonResult;
import com.scrats.rent.entity.Account;
import com.scrats.rent.mapper.AccountMapper;
import com.scrats.rent.view.LandlordView;

import java.util.List;

/**
 * Created with scrat.
 * Description: ${DESCRIPTION}.
 * Email:    guosq@scrats.cn.
 * Author:   lol.
 * Date:     2018/5/24 00:16.
 */
public interface AccountService extends BaseService<Account, AccountMapper> {

    JsonResult login(String username, String pwd);

    List<LandlordView> getPhoneByBuildingId(Integer building_id);
}
