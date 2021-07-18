package dev.spider.geo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import dev.spider.geo.entity.dto.TcLetterDTO;
import dev.spider.geo.entity.pojo.Letter;

import java.util.List;

public interface LetterMapper extends BaseMapper<Letter> {

    void batchInsert(List<TcLetterDTO> insert);
}
