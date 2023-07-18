package com.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.view.QuestionView;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface QuestionViewMapper extends BaseMapper<QuestionView> {
}
