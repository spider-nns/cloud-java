package dev.spider.geo.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@ConfigurationProperties(prefix = "tongcheng")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class TongChConfig implements Serializable {

    //请求城市前缀分类
    private String letter;
}
