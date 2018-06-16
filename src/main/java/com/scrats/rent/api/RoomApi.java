package com.scrats.rent.api;

import com.alibaba.fastjson.JSON;
import com.scrats.rent.common.APIRequest;
import com.scrats.rent.common.JsonResult;
import com.scrats.rent.common.PageInfo;
import com.scrats.rent.common.annotation.APIRequestControl;
import com.scrats.rent.entity.BuildingLandlord;
import com.scrats.rent.entity.Room;
import com.scrats.rent.service.BuildingLandlordService;
import com.scrats.rent.service.BuildingService;
import com.scrats.rent.service.RoomService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Created with scrat.
 * @Description: ${DESCRIPTION}.
 * @Email: guosq@scrats.cn.
 * @Author: lol.
 * @Date: 2018/6/15 12:37.
 */
@RestController
@RequestMapping("/api/room")
public class RoomApi {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RoomService roomService;
    @Autowired
    private BuildingService buildingService;
    @Autowired
    private BuildingLandlordService buildingLandlordService;

    @PostMapping("/list/{buildingId}")
    public String list(@APIRequestControl APIRequest apiRequest, @PathVariable(name="buildingId") Integer buildingId) {

        PageInfo<Room> pageInfo = roomService.getRoomListByBuildingId(apiRequest.getPage(), apiRequest.getRows(), buildingId);

        return JSON.toJSONString(new JsonResult<List>(pageInfo.getList(), (int) pageInfo.getTotal()));
    }

    @PostMapping("/edit")
    public String edit(@APIRequestControl APIRequest apiRequest, Room room, String[] dicItermIds, String[] extraIds) {

        String dicItermId = StringUtils.join(dicItermIds, ",");
        String extraId = StringUtils.join(extraIds, ",");
        room.setFacilities(dicItermId);
        room.setExtraFee(extraId);

        if(null != room.getRoomId()){
            room.setUpdateTs(System.currentTimeMillis());
            roomService.updateByPrimaryKeySelective(room);
        }else{
            room.setCreateTs(System.currentTimeMillis());
            roomService.insertSelective(room);
        }

        return JSON.toJSONString(new JsonResult());
    }

    @PostMapping("/delete")
    public String delete(@APIRequestControl APIRequest apiRequest, Integer... ids){
        //校验是否是本人名下的删除
        List<BuildingLandlord> list = buildingLandlordService.findListBy("landlord_id", apiRequest.getUser().getUserId());

        String roomIds = StringUtils.join(ids,",");
        List<Room> roomList = roomService.selectByIds(roomIds);

        for(Room room : roomList){
            boolean flag = true;
            for(BuildingLandlord buildingLandlord : list){
                if((buildingLandlord.getBuilding_id() - room.getBuildingId()) == 0){
                    flag = false;
                    break;
                }
            }
            if(flag){
                return JSON.toJSONString(new JsonResult("删除失败"));
            }
        }

        roomService.deleteRoomByIds(ids);

        return JSON.toJSONString(new JsonResult());
    }
}