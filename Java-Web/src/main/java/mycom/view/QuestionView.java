package mycom.view;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mycom.bean.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "question_view")
public class QuestionView {
    private static final long serialVersionUID = -40356785423868312L;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableId(value = "QuestionID", type = IdType.AUTO)
    private Long questionId;
    @TableField(value="UserID")
    private Long userID;
    @TableField(value="Title")
    private String title;
    @TableField(value = "Content")
    private String description;
    @TableField(value = "ReleaseTime")
    private String time;
    @TableField(value = "Answers_num")
    private int answers;
    @TableField(value = "name")
    private String userName;
    @TableField(value = "avatar")
    private String avatar;
    @TableField(value = "FollowCount")
    private int followCount;
    @TableField(exist = false)
    private boolean isFollower;
}
