package dev.spider.geo.entity.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@TableName(value = "city")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class GeoCity implements Serializable {
    @TableId
    private Integer id;
    private String cityName;
    private String adCode;
    private String cityCode;
}
