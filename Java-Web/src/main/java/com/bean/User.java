package com.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "User")
public class User implements Serializable{
    private static final long serialVersionUID = -40356785423868312L;

    @TableId(type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;
    @TableField(value = "name")
    private String userName;
    @TableField(value="password")
    private String password;
    @TableField(value="profile")
    private String profile;
    @TableField(value="trade")
    private String trade;

    @TableField(value = "avatar")
    private String avatar;

}
