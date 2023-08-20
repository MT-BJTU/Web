package mycom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import mycom.bean.Article;
import mycom.bean.Question;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
@Mapper
@Repository
public interface ArticleMapper extends BaseMapper<Article> {

}
