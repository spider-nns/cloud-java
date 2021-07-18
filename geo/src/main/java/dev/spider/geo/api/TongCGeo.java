package dev.spider.geo.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import dev.spider.geo.config.TongChConfig;
import dev.spider.geo.entity.dto.TcLetterDTO;
import dev.spider.geo.mapper.LetterMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;

/**
 * HOTEL数据
 */
@Slf4j
@RestController
public class TongCGeo {

    @Resource
    RestTemplate restTemplate;

    @Resource
    TongChConfig tongChConfig;


    @Resource
    LetterMapper letterMapper;


    @GetMapping("letter")
    public void stealLetter() {
        ResponseEntity<String> letterEntity = restTemplate.getForEntity(tongChConfig.getLetter(), String.class);
        log.info("请求酒店LETTER结果:\n{}", JSON.toJSONString(letterEntity));
        if (letterEntity.getStatusCode().is2xxSuccessful()) {
            String body = letterEntity.getBody();
            JSONObject jsonObject = JSONObject.parseObject(body);
            List<TcLetterDTO> data = JSONArray.parseArray(JSON.toJSONString(jsonObject.get("data")), TcLetterDTO.class);
            List<List<TcLetterDTO>> partition = Lists.partition(data, 20);
            for (List<TcLetterDTO> tcLetterDTOS : partition) {
                letterMapper.batchInsert(tcLetterDTOS);
            }
        }
    }
}
