package com.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bean.Question;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface QuestionMapper extends BaseMapper<Question> {

}