package mycom.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "Essay")
public class Article  {
    @TableId(value = "EssayID", type = IdType.AUTO)
    private Long essayID;
    @TableField(value="UserID")
    private Long userID;
    @TableField(value = "Title")
    private String title;
    @TableField(value = "Content")
    private String content;
    @TableField(value = "ReleaseTime")
    private String ReleaseTime;
    @TableField(value = "Star_num")
    private int stars;
    @TableField(exist = false)
    private boolean stared;
    @TableField(exist = false)
    private User user;
}