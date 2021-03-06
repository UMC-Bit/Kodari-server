package com.bit.kodari.controller;

import com.bit.kodari.config.BaseException;
import com.bit.kodari.config.BaseResponse;
import com.bit.kodari.config.BaseResponseStatus;
import com.bit.kodari.dto.UserDto;
import com.bit.kodari.repository.user.UserRepository;
import com.bit.kodari.service.UserService;
import com.bit.kodari.utils.JwtService;
import com.fasterxml.jackson.databind.ser.Serializers;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bit.kodari.utils.ValidationRegex.*;

// 커밋 되는지 확인하기
@RestController
@RequestMapping("/app/users")
public class UserController {
    // git ignore후 커밋 테스트용 주석
    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService; // JWT부분
    @Autowired
    private final UserRepository userRepository;

    public UserController( UserService userService, JwtService jwtService, UserRepository userRepository) {
        this.userService = userService;
        this.jwtService = jwtService; // JWT부분
        this.userRepository = userRepository;
    }
    // ******************************************************************************

    /**
     * 회원가입 API
     * [POST] /users/
     */
    // Body
    @PostMapping("/sign-up")
    @ApiOperation(value = "유저등록", notes = "유저를 새로 등록함.")
    public BaseResponse<UserDto.PostUserRes> createUser(@RequestBody UserDto.PostUserReq postUserReq) {
        // TODO: email 관련한 짧은 validation 예시입니다. 그 외 더 부가적으로 추가해주세요!

        // 회원가입 validation : email null값 예외
        String email = postUserReq.getEmail().replaceAll(" ","");
        if (email == null || email.length()==0) {
            return new BaseResponse<>(BaseResponseStatus.POST_USERS_EMPTY_EMAIL);
        }
        // 회원가입 validation: 이메일 정규표현 = 입력받은 이메일이 email@domain.xxx와 같은 형식인지 검사합니다. 형식이 올바르지 않다면 에러 메시지를 보냅니다.
        if (!isRegexEmail(postUserReq.getEmail())) {
            return new BaseResponse<>(BaseResponseStatus.POST_USERS_INVALID_EMAIL);
        }

        String nickName = postUserReq.getNickName().replaceAll(" ","");
        // 회원가입 validation : nickName null값 예외
        if (nickName == null || nickName.length()==0) {
            return new BaseResponse<>(BaseResponseStatus.POST_USERS_EMPTY_NICKNAME);
        }
        // 닉네임 길이 15글자 초과 예외
        if (postUserReq.getNickName().length()<=0 || postUserReq.getNickName().length()>15) {// 닉네임 길이 Validation
            return new BaseResponse<>(BaseResponseStatus.POST_USERS_LENGTH_NICKNAME);
        }
        // 닉네임 특수문자 포함 예외
        if (!isRegexNickNameSpecial(postUserReq.getNickName())) {
            return new BaseResponse<>(BaseResponseStatus.POST_USERS_INVALID_NICKNAME);
        }
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        int num = 0;
        do {
            num = random.nextInt(75) + 48;
            if ((num >= 48 && num <= 57) || (num >= 65 && num <= 90) || (num >= 97 && num <= 122)) {
                sb.append((char) num);
            } else {
                continue;
            }

        } while (sb.length() < 10);
        postUserReq.setAuthKey(sb.toString());




        String password = postUserReq.getPassword().replaceAll(" ","");
        // 회원가입 validation :  password null값 예외
        if (password == null || password.length()==0) {
            return new BaseResponse<>(BaseResponseStatus.POST_USERS_EMPTY_PASSWORD);
        }
        // 비밀번호 포맷 확인(영문, 특수문자, 숫자 포함 8자 이상)
        if(!isRegexPasswordKind(postUserReq.getPassword())){
            return new BaseResponse<>(BaseResponseStatus.POST_USERS_INVALID_PASSWORD);
        }
        // 비밀번호 특수문자 확인 예외
//        if(!isRegexPasswordSpecial(postUserReq.getPassword())){
//            return new BaseResponse<>(BaseResponseStatus.POST_USERS_INVALID_PASSWORD);
//        }



        try {
            // 유저 생성 요청
            UserDto.PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 로그인 API
     * [POST] /users/logIn
     */
    @PostMapping("/log-in")
    @ApiOperation(value = "로그인", notes = "로그인하는 유저를 새로 등록함.")
    public BaseResponse<UserDto.PostLoginRes> logIn(@RequestBody UserDto.PostLoginReq postLoginReq) {
        try {
            // TODO: 로그인 값들에 대한 형식적인 validatin 처리해주셔야합니다!
            // 로그인 validation : email null값 예외
            String email = postLoginReq.getEmail().replaceAll(" ","");
            if (email == null || email.length()==0) {
                return new BaseResponse<>(BaseResponseStatus.POST_USERS_EMPTY_EMAIL);
            }
            // 로그인 validation: 이메일 정규표현 = 입력받은 이메일이 email@domain.xxx와 같은 형식인지 검사합니다. 형식이 올바르지 않다면 에러 메시지를 보냅니다.
            if (!isRegexEmail(postLoginReq.getEmail())) {
                return new BaseResponse<>(BaseResponseStatus.POST_USERS_INVALID_EMAIL);
            }

            // 로그인 validation : password null값 예외
            String password = postLoginReq.getPassword().replaceAll(" ","");
            if (password == null || password.length()==0) {
                return new BaseResponse<>(BaseResponseStatus.POST_USERS_EMPTY_PASSWORD);
            }
            // TODO: 유저의 status ex) 비활성화된 유저, 탈퇴한 유저 등을 관리해주고 있다면 해당 부분에 대한 validation 처리도 해주셔야합니다.

            UserDto.PostLoginRes postLoginRes = userService.logIn(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }






    /**
     * 모든 회원들의  조회 API
     * [GET] /users/get
     * 또는
     * 해당 닉네임을 같는 유저들의 정보 조회 API
     * [GET] /users/get/:nickName
     */
    @GetMapping("/get") // (GET) /app/users/get
    @ApiOperation(value = "유저 조회", notes = "닉네임 또는 이메일로 유저를 조회함, 닉네임을 안적으면 전체 유저 리스트를 반환한다.")
    public BaseResponse<List<UserDto.GetUserRes>> getUsers(@RequestParam(required = false) String nickName,@RequestParam(required = false) String email,@RequestParam(required = false) String userIdx) {
        //  @RequestParam은, 1개의 HTTP Request 파라미터를 받을 수 있는 어노테이션(?뒤의 값). default로 RequestParam은 반드시 값이 존재해야 하도록 설정되어 있지만, (전송 안되면 400 Error 유발)
        try {
            //  nickname, 이메일이 없을 경우, 그냥 전체 유저정보를 불러온다.
            //nickName = nickName.replaceAll(" ",""); // 공백 제거
            //email = email.replaceAll(" ","");// 공백 제거
            /*if ((nickName == null || nickName.length()==0) && (email == null || email.length() == 0)) {
                List<UserDto.GetUserRes> getUsersRes = userService.getUsers();
                return new BaseResponse<>(getUsersRes);
            }
            else if ((email == null || email.length() == 0)) {
                //  nickname이 있을 경우, 조건을 만족하는 유저 조회
                List<UserDto.GetUserRes> getUsersRes = userService.getUserByNickname(nickName);
                return new BaseResponse<>(getUsersRes);
            }*/
            if ((nickName != null && nickName.length()!=0)) {
                //  nickname이 있을 경우, 조건을 만족하는 유저 조회
                List<UserDto.GetUserRes> getUsersRes = userService.getUserByNickname(nickName);
                return new BaseResponse<>(getUsersRes);
            }
            else if ((email != null && email.length()!=0)) {
                // email 있을 경우, 이메일로 유저 조회
                List<UserDto.GetUserRes> getUserRes = userService.getUserByEmail(email);
                return new BaseResponse<>(getUserRes);
            }
            else if (userIdx != null && userIdx.length()!=0) {
                // userIdx 있을 경우, 유저인덱스로 유저 조회
                List<UserDto.GetUserRes> getUserRes = userService.getUserByUserIdx(userIdx);
                return new BaseResponse<>(getUserRes);
            }

            // 주어진 정보 없는 경우, 전체 유저 조회
            List<UserDto.GetUserRes> getUsersRes = userService.getUsers();
            return new BaseResponse<>(getUsersRes);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }




    /**
     * 유저 삭제 : 유저활성상태변경 API
     * [PATCH] /users/delete/:userIdx?status=
     */
    @PatchMapping("/delete/{userIdx}")
    @ApiOperation(value = "유저삭제", notes = "유저삭제, status를 inactiv로 변경")
    public BaseResponse<String> deleteUser (@PathVariable("userIdx") int userIdx) {
        try {

            // jwt 부분
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }

 //**************************************************************************
            //같다면 유저 삭제 = 유저 status 를 "inactive" 로 변경
            UserDto.DeleteUserReq deleteUserReq = new UserDto.DeleteUserReq(userIdx);
            userService.deleteUser(deleteUserReq);

            String result = "회원이 삭제되었습니다.";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }




    /**
     * 유저 정보 업데이트: 유저 닉네임변경 API
     * [PATCH] /users/update/nickName/:userIdx?nickName=
     */
    @PatchMapping("/update/nickName/{userIdx}")
    @ApiOperation(value = "유저 닉네임", notes = "유저 닉네임 변경")
    public BaseResponse<String> updateNickName (@PathVariable("userIdx") int userIdx,@RequestParam String nickName) {
        try {

            // jwt 부분
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }

 //**************************************************************************
            // 닉네임 Validation
            String nickNameVal = nickName.replaceAll(" ","");
            // 회원가입 validation : nickName null값 예외
            if (nickNameVal == null || nickNameVal.length()==0) {
                return new BaseResponse<>(BaseResponseStatus.POST_USERS_EMPTY_NICKNAME);
            }
            // 닉네임 길이 15글자 초과 예외
            if (nickName.length()<=0 || nickName.length()>15) {// 닉네임 길이 Validation
                return new BaseResponse<>(BaseResponseStatus.POST_USERS_LENGTH_NICKNAME);
            }
            // 닉네임 특수문자 포함 예외
            if (!isRegexNickNameSpecial(nickName)) {
                return new BaseResponse<>(BaseResponseStatus.POST_USERS_INVALID_NICKNAME);
            }

            //같다면 유저 닉네임 변경
            UserDto.UpdateNickNameReq updateNickNameReq = new UserDto.UpdateNickNameReq(userIdx,nickName);
            userService.updateNickName(updateNickNameReq);

            String result = "회원 닉네임이 변경되었습니다.";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }





    /**
     * 유저 정보 업데이트: 유저 비밀번호 변경 API
     * [PATCH] /users/update/password/:userIdx
     */
    @PatchMapping("/update/password/{userIdx}")
    @ApiOperation(value = "유저 패스워드", notes = "유저 패스워드 변경")
    public BaseResponse<String> updatePassword (@PathVariable("userIdx") int userIdx, @RequestBody UserDto.UpdatePasswordReq updatePasswordReq) {
        try {

            // jwt 부분
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }

 //**************************************************************************
            // 비밀번호 Validation
            String password = updatePasswordReq.getPassword();
            String passwordVal = password.replaceAll(" ","");
            // 회원가입 validation :  password null값 예외
            if (passwordVal == null || passwordVal.length()==0) {
                return new BaseResponse<>(BaseResponseStatus.POST_USERS_EMPTY_PASSWORD);
            }
            // 비밀번호 포맷 확인(영문, 특수문자, 숫자 포함 8자 이상)
            if(!isRegexPasswordKind(password)){
                return new BaseResponse<>(BaseResponseStatus.POST_USERS_INVALID_PASSWORD);
            }
            // 비밀번호 특수문자 확인 예외
//            if(!isRegexPasswordSpecial(password)){
//                return new BaseResponse<>(BaseResponseStatus.POST_USERS_INVALID_PASSWORD);
//            }

            //같다면 유저 패스워드 변경
            updatePasswordReq.setUserIdx(userIdx);
            //UserDto.UpdatePasswordReq updatePasswordReq = new UserDto.UpdatePasswordReq(userIdx,password);
            userService.updatePassword(updatePasswordReq);

            String result = "회원 비밀번호가 변경되었습니다.";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }







    /**
     * 유저 정보 업데이트: 유저 프로필 사진 변경 API
     * [PATCH] /users/update/profileImgUrl/:userIdx?profileImgUrl
     */
    @ResponseBody // return되는 자바 객체를 JSON으로 바꿔서 HTTP body에 담는 어노테이션.
    @PatchMapping("/update/profileImgUrl/{userIdx}")
    @ApiOperation(value = "유저 프로필 사진 URL", notes = "유저 프로필사진 변경")
    public BaseResponse<String> updateProfileImgUrl (@PathVariable("userIdx") int userIdx,@RequestParam String profileImgUrl) {
        try {
            // jwt 부분
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }

//**************************************************************************
            //같다면 유저 패스워드 변경
            UserDto.UpdateProfileImgUrlReq updateProfileImgUrlReq = new UserDto.UpdateProfileImgUrlReq(userIdx,profileImgUrl);
            userService.updateProfileImgUrl(updateProfileImgUrlReq);

            String result = "회원 프로필사진이 변경되었습니다.";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    // 유저 패스워드 변경 시, 현재 비밀번호 일치 확인
    @ResponseBody
    @PostMapping("/get/checkPassword/{userIdx}")
    @ApiOperation(value = "유저 현재 비밀번호", notes = "유저 비밀번호 변경 시 현재 패스워드 확인")
    public BaseResponse<String> getCheckPassword(@PathVariable("userIdx") int userIdx, @RequestBody UserDto.GetCheckPasswordReq getCheckPasswordReq){
        try{
            // jwt 부분
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }

            // 비밀번호 Validation
            String password = getCheckPasswordReq.getPassword();
            String passwordVal = password.replaceAll(" ","");
            // 회원가입 validation :  password null값 예외
            if (passwordVal == null || passwordVal.length()==0) {
                return new BaseResponse<>(BaseResponseStatus.POST_USERS_EMPTY_PASSWORD);
            }
            //**************************************************************************
            //같다면 유저 현재 비밀번호 맞는지 확인
            getCheckPasswordReq.setUserIdx(userIdx);
            userService.getCheckPassword(getCheckPasswordReq);

            String result = "현재 비밀번호가 맞습니다.";
            return new BaseResponse<>(result);

        }catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    // 유저 회원가입 시 이메일 validatio api
    @ResponseBody
    @PostMapping("/get/getCheckEmail")
    @ApiOperation(value = "유저 이메일", notes = "회원가입 시 이메일 검증 확인")
    public BaseResponse<String> getCheckEmail(@RequestBody UserDto.GetCheckEmailReq getCheckEmailReq){
        try {

            //**************************************************************************
            // 회원가입 validation : email null값 예외
            String email = getCheckEmailReq.getEmail().replaceAll(" ","");
            if (email == null || email.length()==0) {
                return new BaseResponse<>(BaseResponseStatus.POST_USERS_EMPTY_EMAIL);
            }
            // 회원가입 validation: 이메일 정규표현 = 입력받은 이메일이 email@domain.xxx와 같은 형식인지 검사합니다. 형식이 올바르지 않다면 에러 메시지를 보냅니다.
            if (!isRegexEmail(getCheckEmailReq.getEmail())) {
                return new BaseResponse<>(BaseResponseStatus.POST_USERS_INVALID_EMAIL);
            }

            // 이메일 중복 확인 validation: 해당 이메일을 가진 유저가 있는지 확인
            email = getCheckEmailReq.getEmail();
            List<UserDto.GetUserRes> emailUser = userRepository.getUserByEmail(email); // 이메일로 유저 조회
            if(emailUser.size() != 0){ //  이미 존재하면 이메일 중복 예외
                throw new BaseException(BaseResponseStatus.POST_USERS_EXISTS_EMAIL);
            }

            String result = "사용 가능한 이메일 입니다.";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    // 유저 회원가입 시 패스워드 validatio api
    @ResponseBody
    @PostMapping("/get/getCheckPassword")
    @ApiOperation(value = "유저 패스워드", notes = "회원가입 시 패스워드 검증 확인")
    public BaseResponse<String> getCheckPassword(@RequestBody UserDto.GetCheckPasswordReq getCheckPasswordReq){

//        try
        {


            //**************************************************************************
            String password = getCheckPasswordReq.getPassword().replaceAll(" ","");
            // 회원가입 validation :  password null값 예외
            if (password == null || password.length()==0) {
                return new BaseResponse<>(BaseResponseStatus.POST_USERS_EMPTY_PASSWORD);
            }
            // 비밀번호 포맷 확인(영문, 특수문자, 숫자 포함 8자 이상)
            if(!isRegexPasswordKind(getCheckPasswordReq.getPassword())){
                return new BaseResponse<>(BaseResponseStatus.POST_USERS_INVALID_PASSWORD);
            }


            String result = "사용 가능한 패스워드입니다.";
            return new BaseResponse<>(result);

        }
//        catch (BaseException exception){
//            return new BaseResponse<>(exception.getStatus());
//        }
    }



    // 유저 회원가입 시 닉네임 validatio api
    @ResponseBody
    @PostMapping("/get/getCheckNickName")
    @ApiOperation(value = "유저 닉네임", notes = "회원가입 시 닉네임 검증 확인")
    public BaseResponse<String> getCheckNickName(@RequestBody UserDto.GetCheckNickNameReq getCheckNickNameReq){
        try {


            //**************************************************************************
            String nickName = getCheckNickNameReq.getNickName().replaceAll(" ","");
            // 회원가입 validation : nickName null값 예외
            if (nickName == null || nickName.length()==0) {
                return new BaseResponse<>(BaseResponseStatus.POST_USERS_EMPTY_NICKNAME);
            }
            // 닉네임 길이 15글자 초과 예외
            if (getCheckNickNameReq.getNickName().length()<=0 || getCheckNickNameReq.getNickName().length()>15) {// 닉네임 길이 Validation
                return new BaseResponse<>(BaseResponseStatus.POST_USERS_LENGTH_NICKNAME);
            }
            // 닉네임 특수문자 포함 예외
            if (!isRegexNickNameSpecial(getCheckNickNameReq.getNickName())) {
                return new BaseResponse<>(BaseResponseStatus.POST_USERS_INVALID_NICKNAME);
            }

            // 닉네임 중복 확인 validation: 해당 닉네임을 가진 유저가 있는지 확인
            nickName = getCheckNickNameReq.getNickName();
            List<UserDto.GetUserRes> nickNameUser = userRepository.getUserByNickname(nickName); // 닉네임으로 유저 조회
            if(nickNameUser.size() != 0){ //  이미 존재하면 이메일 중복 예외
                throw new BaseException(BaseResponseStatus.POST_USERS_EXISTS_NICKNAME);
            }


            String result = "사용 가능한 닉네임 입니다.";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }




}
