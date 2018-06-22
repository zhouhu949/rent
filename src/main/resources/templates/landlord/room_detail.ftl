<!DOCTYPE html>
<html lang="zh-cn">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>房屋出租 - by scrats</title>
    <#include "landlord/menu.ftl" />

    <div class="layui-body childrenBody">
        <div class="layui-tab layui-tab-brief" lay-filter="docDemoTabBrief">
            <input type="hidden" id="roomId" autocomplete="off" class="layui-input" value="${room.roomId}">
            <ul class="layui-tab-title">
                <li class="layui-this">房子详情</li>
                <li>租赁历史</li>
            </ul>
            <div class="layui-tab-content">
                <div class="layui-tab-item layui-show">
                    <table class="layui-table" lay-even lay-skin="line">
                        <tbody>
                            <tr>
                                <th>楼盘：</th>
                                <td>${room.building.name}</td>
                                <th>房间号：</th>
                                <td>${room.roomNo}</td>
                            </tr>
                            <tr>
                                <th>房间朝向：</th>
                                <td>${room.orientationName}</td>
                                <th>装修情况：</th>
                                <td>${room.decoration}</td>
                            </tr>
                            <tr>
                                <th>使用面积：</th>
                                <td>${room.area/10000}</td>
                                <th>房间情况：</th>
                                <td>${room.bedroom}房${room.living}厅${room.toilet}卫</td>
                            </tr>
                            </tr>
                            <tr>
                                <th>押付方式：</th>
                                <td>押${room.guaranty}付${room.pay}</td>
                                <th>租金</th>
                                <td>${room.rentFee/100}元/月</td>
                            </tr>
                            <tr>
                                <th>配套设施：</th>
                                <td><#list room.facilitiesIterm as iterm>${iterm.value},</#list></td>
                                <th>额外收费项：</th>
                                <td><#list room.extraFeeIterm as iterm>${iterm.value},</#list></td>
                            </tr>
                            <tr>
                                <th>描述：</th>
                                <td colspan="3">${room.description}</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <div class="layui-tab-item">
                    租赁历史内容
                </div>
            </div>
        </div>
        <hr class="layui-bg-green">
        <blockquote class="layui-elem-quote">
            租客列表&nbsp;&nbsp;&nbsp;&nbsp;
            <div class="layui-inline" id="layerRenter">
                <button data-method="addBargin" data-type="add" class="layui-btn layui-btn-normal">办理入住</button>
                <button data-method="addRenter" data-type="add" class="layui-btn layui-btn-normal">添加租客</button>
                <button data-method="qrcodeRenter" data-type="qrcode" class="layui-btn layui-btn-normal">弹出房间二维码</button>
            </div>
        </blockquote>
        <table class="layui-hide" id="lay_table_room_renter" lay-filter="renterRoomTableFilter"></table>
    </div>

<#include "landlord/footer.ftl"/>
<script src="${base}/static/js/extends/qrcode.min.js" charset="utf-8"></script>
<script src="${base}/static/js/landlord/room_detail.js" charset="utf-8"></script>

<#include "landlord/room_renter.ftl"/>
    <div id="qrcodeShow" style="display: none" >
        <div id="qrcode"></div>
    </div>
</html>