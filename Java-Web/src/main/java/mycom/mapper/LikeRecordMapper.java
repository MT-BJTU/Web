package mycom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import mycom.bean.Followers;
import mycom.bean.LikeRecord;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LikeRecordMapper extends BaseMapper<LikeRecord> {
    @Select("SELECT * FROM LikeRecord WHERE UserID = #{userId} AND AnswerID = #{answerId}")
    LikeRecord selectByUserIdAndAnswerId(@Param("userId") Long userId, @Param("answerId") Long answerId);

    @Insert("INSERT INTO LikeRecord (UserId,AnswerID) VALUES (#{userId},#{answerId})")
    int insert(@Param("userId") Long userId, @Param("answerId") Long answerId);

    @Delete("DELETE FROM LikeRecord WHERE UserId = #{userId} AND AnswerID = #{answerId}")
    int delete(@Param("userId") Long userId, @Param("answerId") Long answerId);
}
