package com.scrats.rent.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.scrats.rent.common.APIRequest;
import com.scrats.rent.common.JsonResult;
import com.scrats.rent.common.PageInfo;
import com.scrats.rent.common.annotation.APIRequestControl;
import com.scrats.rent.entity.*;
import com.scrats.rent.service.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @Autowired
    private DictionaryItermService dictionaryItermService;
    @Autowired
    private RenterService renterService;
    @Autowired
    private UserService userService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private RoomRenterService roomRenterService;
    @Autowired
    private RentService rentService;

    @PostMapping("/list/{buildingId}")
    public JsonResult list(@APIRequestControl APIRequest apiRequest, @PathVariable(name="buildingId") Integer buildingId, Room room) {
        room.setBuildingId(buildingId);
        PageInfo<Room> pageInfo = roomService.getRoomList(apiRequest, room);
        return new JsonResult<List>(pageInfo.getList(), (int) pageInfo.getTotal());
    }

    @PostMapping("/edit")
    public JsonResult edit(@APIRequestControl APIRequest apiRequest) {

        Room room = JSON.parseObject(JSON.toJSONString(apiRequest.getBody()),Room.class);

        if(null != room.getFacilityIds() && room.getFacilityIds().size()>0){
            room.setFacilities(String.join(",", room.getFacilityIds()));
        }
        if(null != room.getExtraIds() && room.getExtraIds().size()>0){
            room.setExtraFee(String.join(",", room.getExtraIds()));
        }
        if(null != room.getDepositIds() && room.getDepositIds().size()>0){
            room.setDeposits(String.join(",", room.getDepositIds()));
        }

        if(null != room.getRoomId()){
            room.setUpdateTs(System.currentTimeMillis());
            roomService.updateByPrimaryKeySelective(room);
        }else{
            List<Room> rlist = roomService.getRoomByRoomNoAndBuildingId(room.getRoomNo(),room.getBuildingId());
            if(null != rlist && rlist.size() > 1){
                return new JsonResult<>("创建失败,该房间号已存在");
            }
            room.setCreateTs(System.currentTimeMillis());
            roomService.insertSelective(room);

            Building building = buildingService.selectByPrimaryKey(room.getBuildingId());
            building.setRooms(building.getRooms() + 1);
            building.setRoomAble(building.getRoomAble() + 1);
            buildingService.updateByPrimaryKeySelective(building);
        }

        return new JsonResult();
    }

    @PostMapping("/delete")
    public JsonResult delete(@APIRequestControl APIRequest apiRequest, Integer... ids){
        //校验是否是本人名下的删除
        List<BuildingLandlord> list = buildingLandlordService.findListBy("landlordId", apiRequest.getUser().getUserId());

        String roomIds = StringUtils.join(ids,",");
        List<Room> roomList = roomService.selectByIds(roomIds);

        for(Room room : roomList){
            boolean flag = true;
            for(BuildingLandlord buildingLandlord : list){
                if((buildingLandlord.getBuildingId() - room.getBuildingId()) == 0){
                    flag = false;
                    break;
                }
            }
            if(flag){
                return new JsonResult("删除失败");
            }
        }

        roomService.deleteRoomByIds(ids);

        return new JsonResult();
    }

    @GetMapping("/detail/{roomId}")
    public JsonResult detail(@PathVariable(name="roomId") Integer roomId) {
        //获取所有房子select数据
        Room room = roomService.detail(roomId);
        if(null == room){
            return new JsonResult("获取房间详情失败");
        }
        return new JsonResult<Room>(room);
    }

    @GetMapping("/renterAll/{roomId}")
    public JsonResult renterAll(@PathVariable(name="roomId") Integer roomId){

        List<RoomRenter> list = roomRenterService.findListBy("roomId", roomId);
        JSONArray jsonArray = new JSONArray();
        for(RoomRenter roomRenter : list){
            User user = userService.selectByPrimaryKey(roomRenter.getUserId());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("idCard",user.getIdCard());
            jsonObject.put("user",user);
            jsonObject.put("phone",user.getPhone());
            jsonObject.put("roomRenterId",roomRenter.getRoomRenterId());
            jsonArray.add(jsonObject);
        }
        return new JsonResult<JSONArray>(jsonArray);
    }

    @GetMapping(value={"/buildingInfo/{roomId}"})
    public JsonResult buildingInfo(@PathVariable(name="roomId") Integer roomId){
        Building building = buildingService.getBuildingByRoomId(roomId);
        return new JsonResult<Building>(building);

    }

    @GetMapping(value={"/extras/{roomId}"})
    public JsonResult extras(@PathVariable(name="roomId") Integer roomId){
        Room room = roomService.selectByPrimaryKey(roomId);
        List<DictionaryIterm> extras = null;
        if(StringUtils.isNotEmpty(room.getExtraFee())){
            extras = dictionaryItermService.selectByIds(room.getExtraFee());
        }
        return new JsonResult<List>(extras);

    }

    @GetMapping(value={"/facilities/{roomId}"})
    public JsonResult facilities(@PathVariable(name="roomId") Integer roomId){
        Room room = roomService.selectByPrimaryKey(roomId);
        List<DictionaryIterm> facilities = null;
        if(StringUtils.isNotEmpty(room.getFacilities())){
            facilities = dictionaryItermService.selectByIds(room.getFacilities());
        }
        return new JsonResult<List>(facilities);

    }


    @PostMapping("/rent")
    @ResponseBody
    public JsonResult rent(@APIRequestControl APIRequest apiRequest) {
        Bargin bargin = JSON.parseObject(JSON.toJSONString(apiRequest.getBody()),Bargin.class);
        Integer roomId = APIRequest.getParameterValue(apiRequest, "roomId", Integer.class);
        //补齐landlordId字段
        bargin.setLandlordId(apiRequest.getUser().getUserId());
        bargin.setRoomId(roomId);
        Room room = roomService.selectByPrimaryKey(bargin.getRoomId());
        if(room.getRentTs() != null && room.getRentTs() > 0){
            return new JsonResult("办理入住失败, 请先办理退房");
        }


        return roomService.rent(bargin);
    }

    @PostMapping("/charge")
    @ResponseBody
    public JsonResult charge(@APIRequestControl APIRequest apiRequest){
        Integer barginId = APIRequest.getParameterValue(apiRequest,"barginId",Integer.class);
        String month = APIRequest.getParameterValue(apiRequest,"month",String.class);
        String remark = APIRequest.getParameterValue(apiRequest,"remark",String.class);
        Integer roomId = APIRequest.getParameterValue(apiRequest, "roomId", Integer.class);
        List<ExtraHistory> list = JSON.parseArray(JSON.toJSONString(apiRequest.getBody().get("barginExtraList")),ExtraHistory.class);

        Rent rent = new Rent();
        rent.setRentMonth(month);
        rent.setRoomId(roomId);
        List<Rent> rentList  = rentService.getListByRent(rent);
        if(null != list && list.size() > 0){
            return new JsonResult("已经计算房租, 请勿重复生成");
        }

        return roomService.charge(list, month, barginId, roomId, remark);
    }
}
