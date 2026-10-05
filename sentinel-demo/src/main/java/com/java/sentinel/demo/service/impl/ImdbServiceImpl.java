package com.java.sentinel.demo.service.impl;

import com.java.sentinel.demo.domain.po.MovieDTO;
import com.java.sentinel.demo.feignclient.ImdbClient;
import com.java.sentinel.demo.service.ImdbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class ImdbServiceImpl implements ImdbService {

    private static final String BASE_DIR = "F:/FlutterProject/flutter/douban_app/assets/images/home";

    @Autowired
    private ImdbClient imdbClient;

    @Autowired
    private HttpClient httpClient;

    @Override
    public void download() {

        Random rn = new Random(100);
        int count = 0;
        do {
            List<MovieDTO> result = imdbClient.top250(0, 20);
            for (MovieDTO movieDTO : result) {
                String imageUrl = movieDTO.getData().get(0).getPoster();

                download(imageUrl);
                sleep(rn);
            }
            count = result.size();
        } while (count == 20);
    }

    @Override
    public void downloadImage() {
        String imageUrl = "https://www.mandarake.co.jp/dir/nkn/hon/up/2021/12/15/d4f926f0935210156f3a27c659cc4669414f6b29.JPG";
        download(imageUrl);
    }

    private void sleep(Random rn) {
        try {
            Thread.sleep(100 + rn.nextInt());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void download(String imageUrl) {
        System.out.println("downloading " + imageUrl);

        String fileName = imageUrl.substring(imageUrl.lastIndexOf("/"));
        String path = BASE_DIR + fileName;

        Map<String, String> headers = new HashMap<>();

//        headers.put("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
//        headers.put("accept-encoding", "gzip, deflate, br, zstd");
//        headers.put("accept-language", "zh-CN,zh;q=0.9");
//        headers.put("cache-control", "no-cache");
//        headers.put("pragma", "no-cache");
//        headers.put("priority", "u=0, i");
//        headers.put("sec-ch-ua", "\"Not;A=Brand\";v=\"99\", \"Google Chrome\";v=\"139\", \"Chromium\";v=\"139\"");
//        headers.put("sec-ch-ua-mobile", "?0");
//        headers.put("sec-ch-ua-platform", "\"Windows\"");
//        headers.put("sec-fetch-dest", "document");
//        headers.put("sec-fetch-mode", "navigate");
//        headers.put("sec-fetch-site", "none");
//        headers.put("sec-fetch-user", "?1");
//        headers.put("upgrade-insecure-requests", "1");
//        headers.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36");

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(imageUrl))
                .GET();

        headers.forEach(requestBuilder::header);

        HttpRequest request = requestBuilder.build();

        HttpResponse<Path> response = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofFile(Path.of(path)));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (response.statusCode() == 200) {
            System.out.println("Image downloaded successfully and saved to " + path);
        } else {
            System.err.println("Failed to download image. HTTP Status Code: " + response.statusCode());
        }
    }
}
