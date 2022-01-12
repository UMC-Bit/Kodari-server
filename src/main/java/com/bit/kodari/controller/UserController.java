package com.bit.kodari.controller;

import com.bit.kodari.config.BaseException;
import com.bit.kodari.config.BaseResponse;
import com.bit.kodari.config.BaseResponseStatus;
import com.bit.kodari.dto.UserDto;
import com.bit.kodari.service.UserService;
import com.bit.kodari.utils.JwtService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.bit.kodari.utils.ValidationRegex.isRegexEmail;

// 커밋 되는지 확인하기
@RestController
@RequestMapping("/app/users")
public class UserController {
    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************
    final Logger logger = LoggerFactory.getLogger(this.getClass()); // Log를 남기기:

    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService; // JWT부분은

    public UserController( UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    }

    // ******************************************************************************

    /**
     * 회원가입 API
     * [POST] /users/
     */
    // Body
    @ResponseBody
    @PostMapping("/sign-up")
    @ApiOperation(value = "유저등록", notes = "유저를 새로 등록함.")
    public BaseResponse<UserDto.PostUserRes> createUser(@RequestBody UserDto.PostUserReq postUserReq) {
        //  @RequestBody란, 클라이언트가 전송하는 HTTP Request Body(우리는 JSON으로 통신하니, 이 경우 body는 JSON)를 자바 객체로 매핑시켜주는 어노테이션
        // TODO: email 관련한 짧은 validation 예시입니다. 그 외 더 부가적으로 추가해주세요!

        // 회원가입 validation : email null값 예외
        if (postUserReq.getEmail() == null || postUserReq.getEmail().length()==0) {
            return new BaseResponse<>(BaseResponseStatus.POST_USERS_EMPTY_EMAIL);
        }
        // 회원가입 validation: 이메일 정규표현 = 입력받은 이메일이 email@domain.xxx와 같은 형식인지 검사합니다. 형식이 올바르지 않다면 에러 메시지를 보냅니다.
        if (!isRegexEmail(postUserReq.getEmail())) {
            return new BaseResponse<>(BaseResponseStatus.POST_USERS_INVALID_EMAIL);
        }
        // 회원가입 validation : nickName null값 예외
        if (postUserReq.getNickName() == null || postUserReq.getNickName().length()==0) {
            return new BaseResponse<>(BaseResponseStatus.POST_USERS_EMPTY_NICKNAME);
        }
        // 회원가입 validation :  password null값 예외
        if (postUserReq.getPassword() == null || postUserReq.getPassword().length()==0) {
            return new BaseResponse<>(BaseResponseStatus.POST_USERS_EMPTY_PASSWORD);
        }


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
    @ResponseBody
    @PostMapping("/log-in")
    @ApiOperation(value = "로그인", notes = "로그인하는 유저를 새로 등록함.")
    public BaseResponse<UserDto.PostLoginRes> logIn(@RequestBody UserDto.PostLoginReq postLoginReq) {
        try {
            // TODO: 로그인 값들에 대한 형식적인 validatin 처리해주셔야합니다!
            // 로그인 validation : email null값 예외
            if (postLoginReq.getEmail() == null || postLoginReq.getEmail().length()==0) {
                return new BaseResponse<>(BaseResponseStatus.POST_USERS_EMPTY_EMAIL);
            }
            // 로그인 validation: 이메일 정규표현 = 입력받은 이메일이 email@domain.xxx와 같은 형식인지 검사합니다. 형식이 올바르지 않다면 에러 메시지를 보냅니다.
            if (!isRegexEmail(postLoginReq.getEmail())) {
                return new BaseResponse<>(BaseResponseStatus.POST_USERS_INVALID_EMAIL);
            }

            // 로그인 validation : password null값 예외
            if (postLoginReq.getPassword() == null || postLoginReq.getPassword().length()==0) {
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
    @ResponseBody   // return되는 자바 객체를 JSON으로 바꿔서 HTTP body에 담는 어노테이션.
    @GetMapping("/get") // (GET) /app/users/get
    @ApiOperation(value = "유저 조회", notes = "닉네임으로 유저를 조회함, 닉네임을 안적으면 전체 유저 리스트를 반환한다.")
    public BaseResponse<List<UserDto.GetUserRes>> getUsers(@RequestParam(required = false) String nickName,@RequestParam(required = false) String email) {
        //  @RequestParam은, 1개의 HTTP Request 파라미터를 받을 수 있는 어노테이션(?뒤의 값). default로 RequestParam은 반드시 값이 존재해야 하도록 설정되어 있지만, (전송 안되면 400 Error 유발)
        try {
            //  nickname, 이메일이 없을 경우, 그냥 전체 유저정보를 불러온다.
            if (nickName == null && email == null) {
                List<UserDto.GetUserRes> getUsersRes = userService.getUsers();
                return new BaseResponse<>(getUsersRes);
            }
            else if (email == null) {
                //  nickname이 있을 경우, 조건을 만족하는 유저 조회
                List<UserDto.GetUserRes> getUsersRes = userService.getUserByNickname(nickName);
                return new BaseResponse<>(getUsersRes);
            }

            // email 있을 경우, 이메일로 유저 조회
            List<UserDto.GetUserRes> getUserRes = userService.getUserByEmail(email);
            return new BaseResponse<>(getUserRes);


        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }




    /**
     * 유저 삭제 : 유저활성상태변경 API
     * [PATCH] /users/delete/:userIdx?status=
     */
    @ResponseBody // return되는 자바 객체를 JSON으로 바꿔서 HTTP body에 담는 어노테이션.
    @PatchMapping("/delete/{userIdx}")
    public BaseResponse<String> deleteUser (@PathVariable("userIdx") int userIdx) {
        try {

 //**********해당 부분은 7 주차 - JWT 수업 후 주석해체 해주세요 ! ****************
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
    @ResponseBody // return되는 자바 객체를 JSON으로 바꿔서 HTTP body에 담는 어노테이션.
    @PatchMapping("/update/nickName/{userIdx}")
    public BaseResponse<String> updateNickName (@PathVariable("userIdx") int userIdx,@RequestParam String nickName) {
        try {

 //*********** 해당 부분은 7주차 - JWT 수업 후 주석해체 해주세요!  ****************
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }

 //**************************************************************************
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
     * [PATCH] /users/update/password/:userIdx?password
     */
    @ResponseBody // return되는 자바 객체를 JSON으로 바꿔서 HTTP body에 담는 어노테이션.
    @PatchMapping("/update/password/{userIdx}")
    public BaseResponse<String> updatePassword (@PathVariable("userIdx") int userIdx,@RequestParam String password) {
        try {

// *********** 해당 부분은 7주차 - JWT 수업 후 주석해체 해주세요!  ****************
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }

 //**************************************************************************
            //같다면 유저 패스워드 변경
            UserDto.UpdatePasswordReq updatePasswordReq = new UserDto.UpdatePasswordReq(userIdx,password);
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
    public BaseResponse<String> updateProfileImgUrl (@PathVariable("userIdx") int userIdx,@RequestParam String profileImgUrl) {
        try {
// *********** 해당 부분은 7주차 - JWT 수업 후 주석해체 해주세요!  ****************
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





}
