package com.sky.springbatch.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieDto {

    private String title;
    private int year;
    private String[] cast;
    private String[] genres;
}
