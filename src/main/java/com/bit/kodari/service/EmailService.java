package com.bit.kodari.service;

import com.bit.kodari.config.BaseException;
import com.bit.kodari.config.BaseResponseStatus;
import com.bit.kodari.dto.EmailDto;
import com.bit.kodari.dto.PostDto;
import com.bit.kodari.repository.email.EmailRepository;
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
import java.util.List;

import static com.bit.kodari.config.BaseResponseStatus.ALREADY_CERTIFICATION_USER;
import static com.bit.kodari.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class EmailService {

    final Logger logger = LoggerFactory.getLogger(this.getClass()); // Log 처리부분: Log를 기록하기 위해 필요한 함수입니다.

    @Autowired
    private EmailRepository emailRepository;
    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************
    private final JavaMailSender javaMailSender;


    @Autowired //readme 참고
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    // 이메일 인증을 위한 해당 유저 정보
    @Transactional
    public EmailDto.UserEmail getEmailByUser(int userIdx) throws BaseException {
        try {
            EmailDto.UserEmail emailUser = emailRepository.getToUser(userIdx);
            return emailUser;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    //이메일 인증
    @Transactional
    public void sendMail(String toEmail, String subject, String message) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        helper.setFrom("CODARI"); //보내는사람
        helper.setTo(toEmail); //받는사람
        helper.setSubject(subject); //메일제목
        helper.setText(message, true); //ture넣을경우 html
        javaMailSender.send(mimeMessage);
    }

    // 이메일 인증을 위한 해당 유저 체크
    @Transactional
    public String checkEmail(String email, String authKey) throws BaseException {
        try {
            String check = emailRepository.checkUser(email, authKey);
            return check;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional
    public void updateAuthKey (EmailDto.UpdateAuthKey update) throws BaseException {
        int result = emailRepository.updateUser(update);
        if (result == 0) {// result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }
    }


}
