package dev.spider.geo.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class TcLetterDTO implements Serializable {
    private String cityId;
    private String cityName;
    private String letter;
}
