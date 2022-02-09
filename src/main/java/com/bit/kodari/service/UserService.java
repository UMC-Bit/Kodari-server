package com.bit.kodari.service;

import com.bit.kodari.config.BaseException;
import com.bit.kodari.config.BaseResponseStatus;
import com.bit.kodari.config.secret.Secret;
import com.bit.kodari.dto.UserDto;
import com.bit.kodari.repository.trade.TradeRepository;
import com.bit.kodari.repository.user.UserRepository;
import com.bit.kodari.repository.user.UserSql;
import com.bit.kodari.utils.AES128;
import com.bit.kodari.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springfox.documentation.service.ResponseMessage;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;


import java.util.List;

@Service
public class UserService {

    final Logger logger = LoggerFactory.getLogger(this.getClass()); // Log 처리부분: Log를 기록하기 위해 필요한 함수입니다.

    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************
    private final UserRepository userRepository;
    private final JwtService jwtService; // JWT부분
    private final TradeService tradeService;
    private final ProfitService profitService;
    private final AccountService accountService;
    private final PortfolioService portfolioService;
    private final UserCoinService userCoinService;
    private final RepresentService representService;





    @Autowired //readme 참고
    public UserService(UserRepository userRepository, JwtService jwtService, TradeService tradeService, ProfitService profitService
    ,AccountService accountService ,PortfolioService portfolioService, UserCoinService userCoinService, RepresentService representService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService; // JWT부분
        this.tradeService = tradeService;
        this.profitService = profitService;
        this.accountService = accountService;
        this.portfolioService = portfolioService;
        this.userCoinService = userCoinService;
        this.representService = representService;
    }
    // ******************************************************************************

    // 회원가입(POST)
    @Transactional
    // Trancaction 기능 : 데이터 생성,수정,삭제와같은 데이터를 작업하는 일이 여러 과정을 한번에 수행 항 때 수행을 끝마쳐야 저장, 오류나면 Rollback 해서 안전성을 부여.
    public UserDto.PostUserRes createUser(UserDto.PostUserReq postUserReq) throws BaseException {
        // 이메일 중복 확인 validation: 해당 이메일을 가진 유저가 있는지 확인
        String email = postUserReq.getEmail();
        List<UserDto.GetUserRes> emailUser = userRepository.getUserByEmail(email); // 이메일로 유저 조회
        if(emailUser.size() != 0){ //  이미 존재하면 이메일 중복 예외
            throw new BaseException(BaseResponseStatus.POST_USERS_EXISTS_EMAIL);
        }

        // 닉네임 중복 확인 validation: 해당 닉네임을 가진 유저가 있는지 확인
        String nickName = postUserReq.getNickName();
        List<UserDto.GetUserRes> nickNameUser = userRepository.getUserByNickname(nickName); // 닉네임으로 유저 조회
        if(nickNameUser.size() != 0){ //  이미 존재하면 이메일 중복 예외
            throw new BaseException(BaseResponseStatus.POST_USERS_EXISTS_NICKNAME);
        }

        String pwd;
        try {
            // 암호화: postUserReq에서 제공받은 비밀번호를 보안을 위해 암호화시켜 DB에 저장합니다.
            // ex) password123 -> dfhsjfkjdsnj4@!$!@chdsnjfwkenjfnsjfnjsd.fdsfaifsadjfjaf
            pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(postUserReq.getPassword()); // 암호화코드
            postUserReq.setPassword(pwd);
        } catch (Exception ignored) { // 암호화가 실패하였을 경우 에러 발생
            throw new BaseException(BaseResponseStatus.PASSWORD_ENCRYPTION_ERROR);
        }
        try {
            // 유저 생성 요청
            UserDto.PostUserRes postUserRes = userRepository.createUser(postUserReq); //
            //return postUserRes;


//  *********** 해당 부분은 7주차 수업 후 주석해제하서 대체해서 사용해주세요! ***********
            //jwt 발급.
            int userIdx = postUserRes.getUserIdx();
            //int userIdx = jwtService.getUserIdx();
            String jwt = jwtService.createJwt(userIdx); // jwt 발급
            return new UserDto.PostUserRes(userIdx,nickName,jwt); // jwt 담아서 서비스로 반환
//  *********************************************************************
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }





    // 로그인(password 검사)

    @Transactional
    public UserDto.PostLoginRes logIn(UserDto.PostLoginReq postLoginReq) throws BaseException {
        /*
		로그인 - 예외처리 : 1. 아이디 존재 X, 2. 비밀번호가 틀릴 때 3. 복호화 하는도중 에러날 때
		JwtToken을 받아서 로그인을 진행함
	    */
        UserDto.User user;
        // 로그인 아이디 존재 X validation: 존재하지 않는 이메일로 로그인 시 예외처리
        try {
            user = userRepository.getPwd(postLoginReq);
        } catch (Exception ignored) {
            throw new BaseException(BaseResponseStatus.GET_USERS_NOT_EXISTS_EMAIL);
        }

//        UserDto.PostLoginRes postLoginRes = userRepository.getPwd(postLoginReq);/////////////////////
//        String password = postLoginRes.getPassword();///////////////////////
        // 복호화 validation: 복호롸 하는도중 에러
        String password;
        // 리파지토리의 암호화된 password 복호화  안되면 에러
        try {
            password = new AES128(Secret.USER_INFO_PASSWORD_KEY).decrypt(user.getPassword()); // 복호화
            // 회원가입할 때 비밀번호가 암호화되어 저장되었기 떄문에 로그인을 할때도 복호화된 값끼리 비교를 해야합니다.
        } catch (Exception ignored) {
            throw new BaseException(BaseResponseStatus.PASSWORD_DECRYPTION_ERROR);
        }

        // password 틀림 validation  : password 비교
        if (postLoginReq.getPassword().equals(password)) { //비말번호가 일치한다면 userIdx를 가져온다.
            int userIdx = userRepository.getPwd(postLoginReq).getUserIdx();
            //return new UserDto.PostLoginRes(userIdx);
//  *********** 해당 부분은 7주차 - JWT 수업 후 주석해제 및 대체해주세요!  **************** //
            String jwt = jwtService.createJwt(userIdx); // jwt 토큰 생성
            return new UserDto.PostLoginRes(userIdx,jwt); // 생성된 access토큰과 함께 반환
//  **************************************************************************
        } else { // 비밀번호가 다르다면 에러메세지를 출력한다.
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
        }
    }



    // User 정보조회: 전체목록 조회
    public List<UserDto.GetUserRes> getUsers() throws BaseException {
        List<UserDto.GetUserRes> getUsersRes = userRepository.getUsers();
        // 예외처리: 유저 없으면 에러
        if (getUsersRes.size() == 0) {
            throw new BaseException(BaseResponseStatus.GET_USERS_NOT_EXISTS);
        }
        try {
            return getUsersRes;
        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // User 정보조회: 해당 nickName을 갖는 User 조회
    public List<UserDto.GetUserRes> getUserByNickname(String nickName) throws BaseException {
        List<UserDto.GetUserRes> getUserRes = userRepository.getUserByNickname(nickName);
        // 예외처리: 없는 닉네임일 경우
        if(getUserRes.size() == 0) {
            throw new BaseException(BaseResponseStatus.GET_USERS_NOT_EXISTS_NICKNAME);
        }
        try {
            return getUserRes;
        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // User 정보조회: 해당 email 갖는 User 조회
    public List<UserDto.GetUserRes> getUserByEmail(String email) throws BaseException {
        List<UserDto.GetUserRes> getUserRes = userRepository.getUserByEmail(email);
        // 예외처리: 없는 이메일일 경우
        if(getUserRes.size() == 0) {
            throw new BaseException(BaseResponseStatus.GET_USERS_NOT_EXISTS_EMAIL);
        }
        try {
            return getUserRes;
        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }


    // User 정보조회: 해당 userIdx 갖는 User 조회
    public List<UserDto.GetUserRes> getUserByUserIdx(String userIdx) throws BaseException {
        List<UserDto.GetUserRes> getUserRes = userRepository.getUserByUserIdx(userIdx);
        // 예외처리: 없는 userIdx일 경우
        if(getUserRes.size() == 0) {
            throw new BaseException(BaseResponseStatus.GET_USERS_NOT_EXISTS_USERIDX);
        }
        try {
            return getUserRes;
        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }


    // 회원삭제: 회원 활성상태 정보 수정(Patch)
    @Transactional // Trancaction 기능 : 데이터 생성,수정,삭제와같은 데이터를 작업하는 일이 여러 과정을 한번에 수행 항 때 수행을 끝마쳐야 저장, 오류나면 Rollback 해서 안전성을 부여.
    public void deleteUser(UserDto.DeleteUserReq deleteUserReq) throws BaseException{
        // 이미 삭제된 회원 validation
        String status = userRepository.getStatusByUserIdx(deleteUserReq.getUserIdx());
        if(status.equals("inactive")){
            throw new BaseException(BaseResponseStatus.ALREADY_DELETED_USER); //
        }



        int result = userRepository.deleteUser(deleteUserReq);
        if (result == 0) {// result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }

        // 유저 삭제가 완성되면
        // 해당 유저의 거래내역 전체 삭제
        // 주의: 참조관계 따져서 순서도 맞아야 삭제가능
        tradeService.deleteAllTradeByUserIdx(deleteUserReq.getUserIdx());
        System.out.println("해당 유저의 전체 거래내역 삭제 완료");
        // 해당 유저의 수익내역 전체 삭제
        profitService.deleteAllProfitByUserIdx(deleteUserReq.getUserIdx());
        System.out.println("해당 유저의 전체 수익내역 삭제 완료");
        // 해당 유저의 데표코인 전체 삭제
        representService.deleteAllReprsentByUserIdx(deleteUserReq.getUserIdx());
        System.out.println("해당 유저의 전체 대표코인 삭제 완료");
        // 해당 유저의 소유코인 전체 삭제
        userCoinService.deleteAllUserCoinByUserIdx(deleteUserReq.getUserIdx());
        System.out.println("해당 유저의 전체 소유코인 삭제 완료");
        // 해당 유저의 포트폴리오 전체 삭제
        portfolioService.deleteAllPortfolioByUserIdx(deleteUserReq.getUserIdx());
        System.out.println("해당 유저의 전체 포트폴리오 삭제 완료");
        // 해당 유저의 계좌 전체 삭제
        accountService.deleteAllAccountByUserIdx(deleteUserReq.getUserIdx());
        System.out.println("해당 유저의 전체 계좌 삭제 완료");





    }

    // 회원 닉네임 정보 수정(Patch)
    @Transactional // Trancaction 기능 : 데이터 생성,수정,삭제와같은 데이터를 작업하는 일이 여러 과정을 한번에 수행 항 때 수행을 끝마쳐야 저장, 오류나면 Rollback 해서 안전성을 부여.
    public void updateNickName(UserDto.UpdateNickNameReq updateNickNameReq) throws BaseException{

        // 닉네임 중복 확인 validation: 해당 닉네임을 가진 유저가 있는지 확인
        String nickName = updateNickNameReq.getNickName();
        List<UserDto.GetUserRes> nickNameUser = userRepository.getUserByNickname(nickName); // 닉네임으로 유저 조회
        if(nickNameUser.size() != 0){ //  이미 존재하면 이메일 중복 예외
            throw new BaseException(BaseResponseStatus.POST_USERS_EXISTS_NICKNAME);
        }

        int result = userRepository.updateNickName(updateNickNameReq);
        if (result == 0) {// result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }
    }

    // 회원 프로필사진 정보 수정(Patch)
    @Transactional // Trancaction 기능 : 데이터 생성,수정,삭제와같은 데이터를 작업하는 일이 여러 과정을 한번에 수행 항 때 수행을 끝마쳐야 저장, 오류나면 Rollback 해서 안전성을 부여.
    public void updateProfileImgUrl(UserDto.UpdateProfileImgUrlReq updateProfileImgUrlReq) throws BaseException{

        int result = userRepository.updateProfileImgUrl(updateProfileImgUrlReq);
        if (result == 0) {// result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }
    }

    // 회원 비밀번호 정보 수정(Patch)
    @Transactional // Trancaction 기능 : 데이터 생성,수정,삭제와같은 데이터를 작업하는 일이 여러 과정을 한번에 수행 항 때 수행을 끝마쳐야 저장, 오류나면 Rollback 해서 안전성을 부여.
    public void updatePassword(UserDto.UpdatePasswordReq updatePasswordReq) throws BaseException{

        // 패스워드 복호화 해서 저장
        String pwd;
        try {
            // 암호화: postUserReq에서 제공받은 비밀번호를 보안을 위해 암호화시켜 DB에 저장합니다.
            // ex) password123 -> dfhsjfkjdsnj4@!$!@chdsnjfwkenjfnsjfnjsd.fdsfaifsadjfjaf
            pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(updatePasswordReq.getPassword()); // 암호화코드
            updatePasswordReq.setPassword(pwd);
        } catch (Exception ignored) { // 암호화가 실패하였을 경우 에러 발생
            throw new BaseException(BaseResponseStatus.PASSWORD_ENCRYPTION_ERROR);
        }

        // 복호화 잘 됬다면 패스워드 변경 요청
        int result = userRepository.updatePassword(updatePasswordReq);
        if (result == 0) {// result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }
    }




}
