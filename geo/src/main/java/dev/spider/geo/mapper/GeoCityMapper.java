package dev.spider.geo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import dev.spider.geo.entity.pojo.GeoCity;

import java.util.List;

public interface GeoCityMapper extends BaseMapper<GeoCity> {

    int count();

    List<GeoCity> selectGeoCity(Page page);
}
