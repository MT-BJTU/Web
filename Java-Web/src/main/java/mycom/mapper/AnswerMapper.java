package mycom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import mycom.bean.Answer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface AnswerMapper extends BaseMapper<Answer> {
    @Select("SELECT * FROM Answer WHERE QueID = #{queId}")
    List<Answer> selectByQueId(@Param("queId") Long queId);
    void updateLikes(@Param("answerId") Long answerId, @Param("likes") int likes);
    // 可以自定义查询方法...
}