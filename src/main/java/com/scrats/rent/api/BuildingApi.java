package com.scrats.rent.api;

import com.alibaba.fastjson.JSON;
import com.scrats.rent.common.APIRequest;
import com.scrats.rent.common.JsonResult;
import com.scrats.rent.common.PageInfo;
import com.scrats.rent.common.annotation.APIRequestControl;
import com.scrats.rent.common.annotation.IgnoreSecurity;
import com.scrats.rent.entity.Building;
import com.scrats.rent.entity.BuildingLandlord;
import com.scrats.rent.service.BuildingLandlordService;
import com.scrats.rent.service.BuildingService;
import com.scrats.rent.service.DictionaryItermService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Created with scrat.
 * @Description: ${DESCRIPTION}.
 * @Email: guosq@scrats.cn.
 * @Author: lol.
 * @Date: 2018/6/7 23:30.
 */
@RestController
@RequestMapping("/api/building")
public class BuildingApi {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BuildingService buildingService;
    @Autowired
    private BuildingLandlordService buildingLandlordService;
    @Autowired
    private DictionaryItermService dictionaryItermService;

    @PostMapping("/list")
    public JsonResult list(@APIRequestControl APIRequest apiRequest, Building building) {
        PageInfo<Building> pageInfo = buildingService.getBuildingListWithUserId(apiRequest, building, apiRequest.getUser().getUserId(), true);

        return new JsonResult<List>(pageInfo.getList(), (int) pageInfo.getTotal());
    }

    @PostMapping("/edit")
    public JsonResult edit(@APIRequestControl APIRequest apiRequest) {
        Building building = JSON.parseObject(JSON.toJSONString(apiRequest.getBody()),Building.class);

        if(null != building.getBuildingId()){
            building.setUpdateTs(System.currentTimeMillis());
            buildingService.updateByPrimaryKeySelective(building);
        }else{
            Building b = buildingService.findBy("name",building.getName());
            if(null != b){
                return new JsonResult<>("创建失败,该房源已存在");
            }
            building.setCreateTs(System.currentTimeMillis());
            buildingService.insertSelective(building);
            BuildingLandlord buildingLandlord = new BuildingLandlord(apiRequest.getUser().getUserId(), building.getBuildingId());
            buildingLandlord.setCreateTs(System.currentTimeMillis());
            buildingLandlordService.insertSelective(buildingLandlord);
        }

        return new JsonResult<>();
    }

    @PostMapping("/delete")
    public JsonResult delete(@APIRequestControl APIRequest apiRequest, Integer... ids){
        //校验是否是本人名下的删除
        List<BuildingLandlord> list = buildingLandlordService.findListBy("landlordId", apiRequest.getUser().getUserId());

        Set<Integer> idSet = new HashSet<>(Arrays.asList(ids));
        for(BuildingLandlord buildingLandlord : list){
            if(!idSet.contains(buildingLandlord.getBuildingId())){
                return new JsonResult<>("删除失败");
            }
        }

        //执行删除动作,将delete_ts副职
        buildingLandlordService.deleteBuildingByLandloordIds(ids);
        buildingService.deleteBuildingByIds(ids);

        return new JsonResult<>();
    }

    @GetMapping("/buildingAll")
    public JsonResult buildingAll(@APIRequestControl APIRequest apiRequest) {
        //获取所有房子select数据
        PageInfo<Building> pageInfo = buildingService.getBuildingListWithUserId(null, null, apiRequest.getUser().getUserId(), false);
        return new JsonResult<List>(pageInfo.getList());
    }

    @GetMapping("/detail/{buildingId}")
    @IgnoreSecurity
    public JsonResult detail(@PathVariable(name="buildingId") Integer buildingId) {
        //获取所有房子select数据
        Building building = buildingService.selectByPrimaryKey(buildingId);

        return new JsonResult<Building>(building);
    }
}
