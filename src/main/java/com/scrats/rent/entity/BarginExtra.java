package com.scrats.rent.entity;

import com.scrats.rent.base.entity.BaseEntity;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;
import tk.mybatis.mapper.code.IdentityDialect;

import javax.persistence.Id;

/**
 * @Created with scrat.
 * @Description: ${DESCRIPTION}.
 * @Email: guosq@scrats.cn.
 * @Author: lol.
 * @Date: 2018/6/18 08:59.
 */
@Data
public class BarginExtra extends BaseEntity {

    private static final long serialVersionUID = -8826252737614873659L;

    @Id
    @KeySql(dialect = IdentityDialect.MYSQL)
    private Integer barginExtraId;//主键
    private Integer barginId;//合同Id
    private Integer roomId;//房间Id
    private Integer extraId;//额外收费项Id
    private String name;//名称
    private String unit;//单位
    private Integer price;//价格

}