package com.bit.kodari.utils;

import com.bit.kodari.repository.account.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationRegex {

    @Autowired
    private AccountRepository accountRepository;

    // 이메일 형식 체크
    public static boolean isRegexEmail(String target) {
        String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

    // 날짜 형식, 전화 번호 형식 등 여러 Regex 인터넷에 검색하면 나옴.

    // 비밀번호 포맷 확인(영문, 특수문자, 숫자 포함 8자 이상)
    public static boolean isRegexPasswordKind(String pwd) {

        Pattern passPattern1 = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,20}$");
        Matcher passMatcher1 = passPattern1.matcher(pwd);

//        if (!passMatcher1.find()) {
//
//            //return "비밀번호는 영문과 특수문자 숫자를 포함하며 8자 이상이어야 합니다.";
//        }
        return passMatcher1.find();
    }



    // 비밀번호 특수문자 확인
    public static boolean isRegexPasswordSpecial(String pwd) {

        Pattern passPattern3 = Pattern.compile("\\W");
        Pattern passPattern4 = Pattern.compile("[!@#$%^*+=-]");

        boolean ok = true;
        for (int i = 0; i < pwd.length(); i++) {
            String s = String.valueOf(pwd.charAt(i));
            Matcher passMatcher3 = passPattern3.matcher(s);

//            if (passMatcher3.find()) {
//                Matcher passMatcher4 = passPattern4.matcher(s);
//                if (!passMatcher4.find()) {
//                    //
//                    //return "비밀번호에 특수문자는 !@#$%^*+=-만 사용 가능합니다.";
//                }
//
//            }

            if(passMatcher3.find()){
                Matcher passMatcher4 = passPattern4.matcher(s);
                if (!passMatcher4.find()) {
                    ok=false;
                }
            }
            //return passMatcher4.find();
        }
        return ok;
    }



    // 닉네임 특수문자 포함 예외
    public static boolean isRegexNickNameSpecial(String nickName) {

        //특수문자_공백_미포함_정규식_
        Pattern pattern = Pattern.compile("^[A-Z|a-z|0-9|가-힣\\w]+$");
        Matcher matcher = pattern.matcher(nickName);
        return matcher.find();
    }


}

