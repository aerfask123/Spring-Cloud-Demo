package com.java.sentinel.demo.feignclient;

import com.java.sentinel.demo.domain.po.MovieDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "imdb-service", url = "https://api.wmdb.tv")
public interface ImdbClient {

    @RequestMapping("/api/v1/top?type=Imdb&lang=Cn")
    List<MovieDTO> top250(@RequestParam("skip") int skip, @RequestParam("limit") int limit);
}
