package mycom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import mycom.bean.Followers;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface FollowersMapper extends BaseMapper<Followers> {
    @Select("SELECT * FROM Followers WHERE UserId = #{userId} AND QueId = #{questionId}")
    Followers selectByUserIdAndQuestionId(@Param("userId") Long userId, @Param("questionId") Long questionId);
    @Insert("INSERT INTO Followers (QueId,UserId) VALUES (#{questionId},#{userId})")
    int insert(@Param("userId") Long userId, @Param("questionId") Long questionId);

    @Delete("DELETE FROM Followers WHERE UserId = #{userId} AND QueId = #{questionId}")
    int delete(@Param("userId") Long userId, @Param("questionId") Long questionId);
}
