package dev.spider.geo.entity.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@TableName("geo_letter")
public class Letter implements Serializable {
    @TableId
    private Integer id;
    private String cityId;
    private String cityName;
    private String letter;
    private String cityCode;
    private Date createTime;
    private Date updateTime;
    private Integer logicalDel;
}
