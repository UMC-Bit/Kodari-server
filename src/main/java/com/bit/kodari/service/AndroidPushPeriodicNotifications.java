package com.bit.kodari.service;

import com.bit.kodari.config.BaseException;
import com.bit.kodari.dto.FcmDto;
import com.bit.kodari.repository.firebase.FcmRepository;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.bit.kodari.dto.FcmDto.*;

import static com.bit.kodari.config.BaseResponseStatus.*;


public class AndroidPushPeriodicNotifications {

    private final FcmRepository fcmRepository;

    @Autowired
    public AndroidPushPeriodicNotifications(FcmRepository fcmRepository) {
        this.fcmRepository = fcmRepository;
    }

    public static String PeriodicNotificationJson(int userIdx) throws JSONException {

        //String token = FcmRepository.getTokenByUserIdx(userIdx);

        String sampleData[] = {"dyf4GQf_SQy1fBNe0sfwdE:APA91bEKkMVCJm2GRSVo3yidMVvEOTX7rczQudm8nTUg6lULbnt6bc8RJqi6DF1czVfT7IuD_P1uK0oMtb3WC6GUN8aGDoAGuCzWrZxBb5-HY9krUVc4QX4_NrFn4Fx-xXCzQPWKxG6r"};

        JSONObject body = new JSONObject();

        List<String> tokenlist = new ArrayList<String>();

        for(int i=0; i<sampleData.length; i++){
            tokenlist.add(sampleData[i]);
        }

        JSONArray array = new JSONArray();

        for(int i=0; i<tokenlist.size(); i++) {
            array.put(tokenlist.get(i));
        }

        body.put("registration_ids", array);

        JSONObject notification = new JSONObject();
        notification.put("title","Notice");
        notification.put("body","Your coin have reached the specified price.");

        body.put("notification", notification);

        System.out.println(body.toString());

        return body.toString();
    }
}
