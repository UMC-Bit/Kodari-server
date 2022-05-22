package com.bit.kodari.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class EmailDto {

    @Data // @Getter @Setter 포함
    @AllArgsConstructor // 인자 포함한 생성자 생성
    @NoArgsConstructor // 인자 없는 생성자 생성
    public static class UserEmail {
        private int userIdx;
        private String nickName;
        private String email;
        private String authKey;
    }

    @Data // @Getter @Setter 포함
    @AllArgsConstructor // 인자 포함한 생성자 생성
    @NoArgsConstructor // 인자 없는 생성자 생성
    public static class CheckEmail {
        private String email;
        private String authKey;
        private String check;
    }

    @Data // @Getter @Setter 포함
    @AllArgsConstructor // 인자 포함한 생성자 생성
    @NoArgsConstructor // 인자 없는 생성자 생성
    public static class UpdateAuthKey {
        private String authKey;
    }
}
