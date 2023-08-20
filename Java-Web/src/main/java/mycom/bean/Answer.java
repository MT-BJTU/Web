package mycom.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "Answer")
public class Answer  {
    @TableId(value = "AnswerID", type = IdType.AUTO)
    private Long AnswerID;

    @TableField(value="QueID")
    private Long QueID;

    @TableField(value="UserID")
    private Long userID;

    @TableField(value = "Content")
    private String content;

    @TableField(value = "ReleaseTime")
    private String ReleaseTime;

    @TableField(value = "Likes")
    private int likes;
    @TableField(exist = false)
    private boolean liked;
    @TableField(exist = false)
    private User user;
}