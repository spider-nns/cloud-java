package dev.spider.geo.entity.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@TableName(value = "school")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class School implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String schoolName;
    private String cityCode;
    private String cityName;
    private String location;
    private Date createTime;
}
