package mycom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import mycom.bean.Followers;
import mycom.bean.Stars;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface StarsMapper extends BaseMapper<Stars> {
    @Select("SELECT * FROM Stars WHERE UserID = #{userId} AND EssayID = #{essayId}")
    Stars selectByUserIdAndEssayId(@Param("userId") Long userId, @Param("essayId") Long essayId);
    @Insert("INSERT INTO Stars (UserID,EssayID) VALUES (#{userId},#{essayId})")
    int insert(@Param("userId") Long userId, @Param("essayId") Long essayId);

    @Delete("DELETE FROM Stars WHERE UserID = #{userId} AND EssayID = #{essayId}")
    int delete(@Param("userId") Long userId, @Param("essayId") Long essayId);
}