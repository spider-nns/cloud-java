package dev.spider.geo.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import dev.spider.geo.config.TongChConfig;
import dev.spider.geo.entity.dto.TcLetterDTO;
import dev.spider.geo.entity.pojo.GeoCity;
import dev.spider.geo.entity.pojo.School;
import dev.spider.geo.mapper.GeoCityMapper;
import dev.spider.geo.mapper.LetterMapper;
import dev.spider.geo.mapper.SchoolMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Optional;

/**
 * HOTEL数据
 */
@Slf4j
@Controller
public class TongCGeo {

    @Resource
    RestTemplate restTemplate;

    @Resource
    TongChConfig tongChConfig;

    @Resource
    LetterMapper letterMapper;

    @Resource
    GeoCityMapper geoCityMapper;

    @Resource
    SchoolMapper schoolMapper;


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

    @GetMapping("gDe")
    public void loopCityCallGdGetLat() {
        String url = "https://restapi.amap.com/v3/place/text?key=0c2c18f48042a59b58f60c71506d0ab6&types=141201&city=";
        Integer integer = geoCityMapper.count();
        int group = integer / 20 + 1;
        for (int i = 1; i <= group; i++) {
            log.info("第{}组城市", i);
            Page<GeoCity> geoCityPage = new Page<>(i, 20);
            List<GeoCity> geoCityIPage = geoCityMapper.selectGeoCity(geoCityPage);
            for (GeoCity record : geoCityIPage) {
                //api limit  qps 20/s pageSize 20
                String adCode = record.getAdCode();
                String cityName = record.getCityName();
//                if ((adCode.contains("0000") && cityName.contains("省"))) {
                String target = url.concat(adCode);
                loopCallForThisCity(target, adCode, cityName);
            }
//            }
        }
    }

    private void loopCallForThisCity(String target, String cityCode, String cityName) {
        String originUrl = target;
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(target, String.class);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String body = responseEntity.getBody();
            JSONObject jsonObject = JSONObject.parseObject(body);
            String status = (String) jsonObject.get("status");
            String info = (String) jsonObject.get("info");
            if ("1".equals(status) && "OK".equals(info)) {
                String count = (String) jsonObject.get("count");
                Object pois = jsonObject.get("pois");
                JSONArray objects = JSONArray.parseArray(JSON.toJSONString(pois));
                for (Object object : objects) {
                    JSONObject dataObj = JSONObject.parseObject(JSON.toJSONString(object));
                    String schoolName = (String) dataObj.get("name");
                    String location = (String) dataObj.get("location");
                    Optional.ofNullable(schoolName).ifPresent(name -> {
                        //todo write to kafka or lsm-db
                        School school = new School();
                        school.setCityCode(cityCode);
                        school.setCityName(cityName);
                        school.setSchoolName(name);
                        school.setLocation(location);
                        int insert = schoolMapper.insert(school);
                        log.info("{}  {}  {}", schoolName, insert == 1, location);
                    });
                }
                int sed = Integer.parseInt(count);
                //call next page
                for (int i = 2; i < sed / 20 + 2; i++) {
                    loopCall(originUrl.concat("&page=" + i), cityCode, cityName);
                }
            }
        }
    }

    private void loopCall(String target, String cityCode, String cityName) {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(target, String.class);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String body = responseEntity.getBody();
            JSONObject jsonObject = JSONObject.parseObject(body);
            String status = (String) jsonObject.get("status");
            String info = (String) jsonObject.get("info");
            if ("1".equals(status) && "OK".equals(info)) {
                Object pois = jsonObject.get("pois");
                JSONArray objects = JSONArray.parseArray(JSON.toJSONString(pois));
                for (Object object : objects) {
                    JSONObject dataObj = JSONObject.parseObject(JSON.toJSONString(object));
                    String schoolName = (String) dataObj.get("name");
                    String location = (String) dataObj.get("location");
                    Optional.ofNullable(schoolName).ifPresent(name -> {
                        //fixme [use kafka or lsm-db to optimize write]
                        School school = new School();
                        school.setCityCode(cityCode);
                        school.setCityName(cityName);
                        school.setSchoolName(name);
                        school.setLocation(location);
                        int insert = schoolMapper.insert(school);
                        log.info("{} {} {}", schoolName, insert == 1, location);
                    });
                }
            }
        }
    }

    @GetMapping("redirect")
    public void get() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("h1", "v1");
        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);
        String redirectUrl = "http://127.0.0.1:9704/change?p=changep";

        ResponseEntity<String> exchange = restTemplate.
                exchange("http://127.0.0.1:8083/back?name=spider&redirectUrl=" + redirectUrl, HttpMethod.GET, httpEntity, String.class);
        log.info("statusCode:{}", exchange.getStatusCode());
        log.info("body:{}", exchange.getBody());

    }

    @GetMapping("change")
    @ResponseBody
    public String back(@RequestParam(value = "p") String p, HttpServletRequest request) {
        log.info("p is {}", p);
        String h2 = request.getHeader("h2");
        log.info("h2 is :{}", h2);
        return "hello redirect";
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        System.out.println(URLEncoder.encode("http://127.0.0.1:9704/change?p=spider", "UTF-8"));
    }
}