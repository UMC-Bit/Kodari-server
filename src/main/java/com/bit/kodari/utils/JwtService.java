package com.bit.kodari.utils;


import com.bit.kodari.config.BaseException;
import com.bit.kodari.config.secret.Secret;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import springfox.documentation.service.ResponseMessage;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

import static com.bit.kodari.config.BaseResponseStatus.EMPTY_JWT;
import static com.bit.kodari.config.BaseResponseStatus.INVALID_JWT;

@Service
public class JwtService {

    /*
    JWT 생성
    @param userIdx
    @return String
     */
    public String createJwt(int userIdx){
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam("type","jwt")
                .claim("userIdx",userIdx)
                .setIssuedAt(now)
                .setExpiration(new Date(System.currentTimeMillis()+1*(1000*60*60*24*365)))
                .signWith(SignatureAlgorithm.HS256, Secret.JWT_SECRET_KEY)
                .compact();
    }

    /*
    Header에서 X-ACCESS-TOKEN 으로 JWT 추출
    @return String
     */
    public String getJwt(){
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getHeader("X-ACCESS-TOKEN");
    }

    /*
    JWT에서 userIdx 추출
    @return int
    @throws BaseException
     */
    public int getUserIdx() throws BaseException{
        //1. JWT 추출
        String accessToken = getJwt();
        // jwt토큰 validation: jwt토큰이 null이거나 빈 문장이면 예외처리
        if(accessToken == null || accessToken.length() == 0){
            throw new BaseException(EMPTY_JWT);
        }

        // 2. JWT parsing
        Jws<Claims> claims;
        try{
            claims = Jwts.parser()
                    .setSigningKey(Secret.JWT_SECRET_KEY)
                    .parseClaimsJws(accessToken);
        } catch (Exception ignored) {
            throw new BaseException(INVALID_JWT);
        }

        // 3. userIdx 추출
        return claims.getBody().get("userIdx",Integer.class);  // jwt 에서 userIdx를 추출합니다.
    }


    // 토큰에서 회원 정보 추출
    /*public String getJwtEmail(HttpServletRequest request) throws BaseException {
        String token = resolveToken(request);
        String email;
        // 토큰이 비었을 때 예외 처리
        if(token == null || token.length() == 0){
            throw new DefaultException(StatusCode.JWT_ERROR, ResponseMessage.EMPTY_JWT);
        }
        try{
            email = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
        }catch(Exception e){
            throw new DefaultException(StatusCode.JWT_ERROR, ResponseMessage.INVALID_JWT);
        }
        return email;
    }*/



    // Request의 Header에서 token 값을 가져옵니다. "X-AUTH-TOKEN" : "TOKEN값'
    /*public String resolveToken(HttpServletRequest request) {
        String token = null;
        String auth_token = request.getHeader("X-AUTH-TOKEN");
        if(auth_token != null){
            token = auth_token;
        }
        return token;
    }*/

}
