package com.java.sentinel.demo.domain.po;

import lombok.Data;

import java.util.List;

@Data
public class MovieDTO {
    private String id;
    private String originalName;
    private String alias;
    private String year;
    private String doubanId;
    private String doubanRating;
    private Integer doubanVotes;
    private String type;
    private Integer episodes;
    private String dateReleased;

    private List<MovieData> data;
}