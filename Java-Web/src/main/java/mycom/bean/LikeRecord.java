package mycom.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "LikeRecord")
public class LikeRecord {
    @TableId(value = "AnswerID")
    private Long AnswerID;
    @TableField(value="UserID")
    private Long userID;
}