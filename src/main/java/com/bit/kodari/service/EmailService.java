package com.bit.kodari.service;

import com.bit.kodari.repository.user.UserRepository;
import com.bit.kodari.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService {

    final Logger logger = LoggerFactory.getLogger(this.getClass()); // Log 처리부분: Log를 기록하기 위해 필요한 함수입니다.

    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************
    private final JavaMailSender javaMailSender;


    @Autowired //readme 참고
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    //이메일 인증
    @Transactional
    public void sendMail(String toEmail, String subject, String message) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        helper.setFrom("KODARI"); //보내는사람
        helper.setTo(toEmail); //받는사람
        helper.setSubject(subject); //메일제목
        helper.setText(message, true); //ture넣을경우 html


        javaMailSender.send(mimeMessage);
    }
}
