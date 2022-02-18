package com.bit.kodari.controller;

import com.bit.kodari.config.BaseException;
import com.bit.kodari.config.BaseResponse;
import com.bit.kodari.config.BaseResponseStatus;
import com.bit.kodari.dto.EmailDto;
import com.bit.kodari.dto.UserDto;
import com.bit.kodari.repository.email.EmailRepository;
import com.bit.kodari.service.EmailService;
import com.bit.kodari.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/app/users")
public class EmailController {
    @Autowired
    private EmailService emailService;

    @GetMapping(value = "/user/email/send")
    public void sendmail(@RequestParam int userIdx) throws MessagingException, BaseException {
        EmailDto.UserEmail user = emailService.getEmailByUser(userIdx);
        StringBuffer emailcontent = new StringBuffer();
        emailcontent.append("<!DOCTYPE html>");
        emailcontent.append("<html>");
        emailcontent.append("<head>");
        emailcontent.append("</head>");
        emailcontent.append("<body>");
        emailcontent.append(
                " <div" 																																																	+
                        "	style=\"font-family: 'Apple SD Gothic Neo', 'sans-serif' !important; width: 400px; height: 600px; border-top: 4px solid #FDCC68; margin: 100px auto; padding: 30px 0; box-sizing: border-box;\">"		+
                        "	<h1 style=\"margin: 0; padding: 0 5px; font-size: 28px; font-weight: 400;\">"																															+
                        "		<span style=\"font-size: 15px; margin: 0 0 10px 3px;\">CODARI</span><br />"																													+
                        "		<span style=\"color: #FDCC68\">메일인증</span> 안내입니다."																																				+
                        "	</h1>\n"																																																+
                        "	<p style=\"font-size: 16px; line-height: 26px; margin-top: 50px; padding: 0 5px;\">"																													+
                        user.getNickName()																																																+
                        "		님 안녕하세요.<br />"																																													+
                        "		CODARI에 가입해 주셔서 진심으로 감사드립니다.<br />"																																						+
                        "		아래 <b style=\"color: #FDCC68\">'메일 인증'</b> 버튼을 클릭하여 회원가입을 완료해 주세요. 감사합니다.<br />"																													+

                        "	</p>"																																																	+
                        "	<a style=\"color: #FFF; text-decoration: none; text-align: center;\""																																	+
                        "	href=\"http://localhost:9000/app/users/user/email/certified?email=" + user.getEmail() + "&authKey=" + user.getAuthKey() + "\" target=\"_blank\">"														+
                        "		<p"																																																	+
                        "			style=\"display: inline-block; width: 210px; height: 45px; margin: 30px 5px 40px; background: #FDCC68; line-height: 45px; vertical-align: middle; font-size: 16px;\">"							+
                        "			메일 인증</p>"																																														+
                        "	</a>"																																																	+
                        "	<div style=\"border-top: 4px solid #FDCC68; padding: 5px;\"></div>"																																		+
                        " </div>"
        );
        emailcontent.append("</body>");
        emailcontent.append("</html>");
        emailService.sendMail(user.getEmail(), "[CODARI 이메일 인증]", emailcontent.toString());
    }


    @GetMapping(value = "/user/email/certified")
    @Transactional
    public BaseResponse<String> checkEmail(@RequestParam String email, String authKey){
        try{
            if(email == null || email.length() == 0) {
                return new BaseResponse<>(BaseResponseStatus.GET_USERS_NOT_EXISTS_EMAIL);
            }
            else if(authKey == null || authKey.length() == 0) {
                return new BaseResponse<>(BaseResponseStatus.GET_USERS_NOT_EXISTS_EMAIL);
            }
            String check = emailService.checkEmail(email, authKey);
            if(check.equals("false")) {
                return new BaseResponse<>(BaseResponseStatus.GET_USERS_NOT_EXISTS_EMAIL);
            }
            EmailDto.UpdateAuthKey update = new EmailDto.UpdateAuthKey(authKey);
            emailService.updateAuthKey(update);
            String result = "인증이 완료되었습니다.";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

    }

}