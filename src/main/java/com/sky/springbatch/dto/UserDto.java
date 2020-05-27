package com.sky.springbatch.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {

    /*
     * 반드시 매핑될 파일의 컬럼 순서대로 변수를 생성
     */

    String userId;
    String passwd;
    String name;
    Integer intValue;

}
