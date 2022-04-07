package com.bit.kodari.service;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

// firebase_server_key = firebase project > cloud messaging > server key

@Service
public class AndroidPushNotificationsService {
    private static final String firebase_server_key="AAAAwecz6Rw:APA91bES84Ah592F3y8vVvhIzLdv7lYlzwXEAQiUgQFGpVxZbeLzQGEu8VyWpiEw4CYa-xjNsLlD_wgNgglZvaouVgKafJlEVGPTHdiZWmymuF8eTHVRMno3cfM7mlgyNws0PEBaHuPo";
    private static final String firebase_api_url="https://fcm.googleapis.com/fcm/send";

    @Async
    public CompletableFuture<String> send(HttpEntity<String> entity) {

        RestTemplate restTemplate = new RestTemplate();

        ArrayList<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();

        interceptors.add(new HeaderRequestInterceptor("Authorization",  "key=" + firebase_server_key));
        interceptors.add(new HeaderRequestInterceptor("Content-Type", "application/json; UTF-8mb4 "));
        restTemplate.setInterceptors(interceptors);

        String firebaseResponse = restTemplate.postForObject(firebase_api_url, entity, String.class);

        return CompletableFuture.completedFuture(firebaseResponse);
    }
}