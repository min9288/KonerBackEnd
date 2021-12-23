package project.SangHyun.member.service.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import project.SangHyun.advice.exception.*;
import project.SangHyun.config.jwt.JwtTokenHelper;
import project.SangHyun.member.domain.Member;
import project.SangHyun.member.repository.MemberRepository;
import project.SangHyun.member.tools.sign.SignRequestFactory;
import project.SangHyun.member.tools.sign.SignResponseFactory;
import project.SangHyun.study.studyjoin.repository.StudyJoinRepository;
import project.SangHyun.utils.service.EmailService;
import project.SangHyun.member.service.impl.SignServiceImpl;
import project.SangHyun.utils.service.RedisService;
import project.SangHyun.member.dto.response.MemberChangePwResponseDto;
import project.SangHyun.member.dto.response.MemberLoginResponseDto;
import project.SangHyun.member.dto.response.MemberRegisterResponseDto;
import project.SangHyun.member.dto.response.TokenResponseDto;
import project.SangHyun.member.dto.request.*;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@ExtendWith(MockitoExtension.class)
class SignServiceUnitTest {

    SignServiceImpl signService;
    @Mock
    JwtTokenHelper accessTokenHelper;
    @Mock
    JwtTokenHelper refreshTokenHelper;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    MemberRepository memberRepository;
    @Mock
    RedisService redisService;
    @Mock
    EmailService emailService;
    @Mock
    StudyJoinRepository studyJoinRepository;

    @BeforeEach
    public void init() {
        signService = new SignServiceImpl(accessTokenHelper, refreshTokenHelper, passwordEncoder, memberRepository, studyJoinRepository, redisService, emailService);
    }

    @Test
    @DisplayName("회원 가입을 진행한다.")
    public void register() throws Exception {
        //given
        MemberRegisterRequestDto requestDto = SignRequestFactory.makeRegisterRequestDto();
        Member member = SignRequestFactory.makeAuthTestMember();
        MemberRegisterResponseDto ExpectResult = SignResponseFactory.makeRegisterResponseDto(member);

        //mocking
        given(memberRepository.findByEmail(any())).willReturn(Optional.empty());
        given(memberRepository.findByNickname(any())).willReturn(Optional.empty());
        given(passwordEncoder.encode(any())).willReturn("encodedPW");
        given(memberRepository.save(any())).willReturn(member);
        
        //when
        MemberRegisterResponseDto ActualResult = signService.registerMember(requestDto);

        //then
        Assertions.assertEquals(ExpectResult.getId(), ActualResult.getId());
    }

    @Test
    @DisplayName("이메일중복으로 인해 회원 가입에 실패한다.")
    public void register_fail1() throws Exception {
        //given
        MemberRegisterRequestDto requestDto = SignRequestFactory.makeRegisterRequestDto();
        Member member = SignRequestFactory.makeAuthTestMember();

        //mocking
        given(memberRepository.findByEmail(any())).willReturn(Optional.ofNullable(member));

        //when, then
        Assertions.assertThrows(MemberEmailAlreadyExistsException.class, ()->signService.registerMember(requestDto));
    }

    @Test
    @DisplayName("닉네임중복으로 인해 회원 가입에 실패한다.")
    public void register_fail2() throws Exception {
        //given
        MemberRegisterRequestDto requestDto = SignRequestFactory.makeRegisterRequestDto();
        Member member = SignRequestFactory.makeAuthTestMember();

        //mocking
        given(memberRepository.findByEmail(any())).willReturn(Optional.empty());
        given(memberRepository.findByNickname(any())).willReturn(Optional.ofNullable(member));

        //when, then
        Assertions.assertThrows(MemberNicknameAlreadyExistsException.class, ()->signService.registerMember(requestDto));
    }
    
    @Test
    @DisplayName("로그인을 진행한다.")
    public void login() throws Exception {
        //given
        MemberLoginRequestDto requestDto = SignRequestFactory.makeAuthMemberLoginRequestDto();
        Member member = SignRequestFactory.makeAuthTestMember();
        MemberLoginResponseDto ExpectResult = SignResponseFactory.makeLoginResponseDto(member);

        //mocking
        given(memberRepository.findByEmail(any())).willReturn(Optional.ofNullable(member));
        given(studyJoinRepository.findStudyInfoByMemberId(any())).willReturn(new ArrayList<>());
        given(passwordEncoder.matches(any(), any())).willReturn(true);
        given(accessTokenHelper.createToken(any())).willReturn("accessToken");
        given(refreshTokenHelper.createToken(any())).willReturn("refreshToken");
        willDoNothing().given(redisService).setDataWithExpiration(any(), any(), any());

        //when
        MemberLoginResponseDto ActualResult = signService.loginMember(requestDto);

        //then
        Assertions.assertEquals(ExpectResult.getId(), ActualResult.getId());
    }

    @Test
    @DisplayName("인증이 미완료된 회원은 로그인에 실패한다.")
    public void login_fail1() throws Exception {
        //given
        MemberLoginRequestDto requestDto = SignRequestFactory.makeAuthMemberLoginRequestDto();
        Member member = SignRequestFactory.makeNotAuthTestMember();

        //mocking
        given(memberRepository.findByEmail(any())).willReturn(Optional.ofNullable(member));
        given(passwordEncoder.matches(any(), any())).willReturn(true);

        //when, then
        Assertions.assertThrows(EmailNotAuthenticatedException.class, ()->signService.loginMember(requestDto));
    }

    @Test
    @DisplayName("비밀번호가 틀린 회원은 로그인에 실패한다.")
    public void login_fail2() throws Exception {
        //given
        MemberLoginRequestDto requestDto = SignRequestFactory.makeAuthMemberLoginRequestDto();
        Member member = SignRequestFactory.makeNotAuthTestMember();

        //mocking
        given(memberRepository.findByEmail(any())).willReturn(Optional.ofNullable(member));
        given(passwordEncoder.matches(any(), any())).willReturn(false);

        //when, then
        Assertions.assertThrows(LoginFailureException.class, ()->signService.loginMember(requestDto));
    }

    @Test
    @DisplayName("회원가입 후 인증을 위한 이메일을 전송한다.")
    public void sendMail_register() throws Exception {
        //given
        MemberEmailAuthRequestDto requestDto = SignRequestFactory.makeEmailAuthRequestDto("VERIFY");
        Member member = SignRequestFactory.makeNotAuthTestMember();

        //mocking
        given(memberRepository.findByEmail(any())).willReturn(Optional.ofNullable(member));
        willDoNothing().given(redisService).setDataWithExpiration(any(), any(), any());
        willDoNothing().given(emailService).send(any(), any(), any());

        //when
        String ActualResult = signService.sendEmail(requestDto);

        //then
        Assertions.assertEquals("이메일 전송에 성공하였습니다.", ActualResult);
    }

    @Test
    @DisplayName("비밀번호 변경을 위한 이메일을 전송한다.")
    public void sendMail_pw() throws Exception {
        //given
        MemberEmailAuthRequestDto requestDto = SignRequestFactory.makeEmailAuthRequestDto("PASSWORD");
        Member member = SignRequestFactory.makeNotAuthTestMember();

        //mocking
        given(memberRepository.findByEmail(any())).willReturn(Optional.ofNullable(member));
        willDoNothing().given(redisService).setDataWithExpiration(any(), any(), any());
        willDoNothing().given(emailService).send(any(), any(), any());

        //when
        String ActualResult = signService.sendEmail(requestDto);

        //then
        Assertions.assertEquals("이메일 전송에 성공하였습니다.", ActualResult);
    }

    @Test
    @DisplayName("회원가입 후 인증에 대한 메일을 검증한다.")
    public void verifyMail_register() throws Exception {
        //given
        Member member = SignRequestFactory.makeNotAuthTestMember();
        VerifyEmailRequestDto requestDto = SignRequestFactory.makeVerifyEmailRequestDto(member.getEmail(), "authCode", "VERIFY");

        //mocking
        given(memberRepository.findByEmail(any())).willReturn(Optional.ofNullable(member));
        given(redisService.getData(any())).willReturn("authCode");
        willDoNothing().given(redisService).deleteData(any());

        //when
        String ActualResult = signService.verify(requestDto);

        //then
        Assertions.assertEquals("이메일 인증이 완료되었습니다.", ActualResult);
    }

    @Test
    @DisplayName("비밀번호 변경에 대한 메일을 검증한다.")
    public void verifyMail_pw() throws Exception {
        //given
        VerifyEmailRequestDto requestDto = SignRequestFactory.makeVerifyEmailRequestDto("xptmxm1!", "authCode", "PASSWORD");

        //mocking
        given(redisService.getData(any())).willReturn("authCode");
        willDoNothing().given(redisService).deleteData(any());

        //when
        String ActualResult = signService.verify(requestDto);

        //then
        Assertions.assertEquals("이메일 인증이 완료되었습니다.", ActualResult);
    }

    @Test
    @DisplayName("Redis에 저장된 값과 검증 값과 달라 이메일 인증에 실패한다.")
    public void verify_fail() throws Exception {
        //given
        VerifyEmailRequestDto requestDto = SignRequestFactory.makeVerifyEmailRequestDto("xptmxm1!", "authCode","PASSWORD");

        //mocking
        given(redisService.getData(any())).willReturn("different");

        //when, then
        Assertions.assertThrows(RedisValueDifferentException.class, ()->signService.verify(requestDto));
    }

    @Test
    @DisplayName("비밀번호 변경을 진행한다.")
    public void changePW() throws Exception {
        //given
        Member member = SignRequestFactory.makeAuthTestMember();
        MemberChangePwRequestDto requestDto = SignRequestFactory.makeChangePwRequestDto(member.getEmail(), "change");

        //mocking
        given(memberRepository.findByEmail(any())).willReturn(Optional.ofNullable(member));
        given(passwordEncoder.encode(any())).willReturn("encodedChangedPW");
        willDoNothing().given(redisService).deleteData(any());

        //when
        MemberChangePwResponseDto ActualResult = signService.changePassword(requestDto);

        //then
        Assertions.assertEquals(1L, ActualResult.getId());
    }

    @Test
    @DisplayName("RefreshToken을 이용해 토큰을 재발행한다.")
    public void reIssue() throws Exception {
        //given
        TokenRequestDto requestDto = SignRequestFactory.makeTokenRequestDto("refreshToken");
        Member member = SignRequestFactory.makeAuthTestMember();
        TokenResponseDto ExpectResult = SignResponseFactory.makeTokenResponseDto(member);

        //mocking
        given(redisService.getData(any())).willReturn("test");
        given(refreshTokenHelper.extractSubject(any())).willReturn("test");
        given(memberRepository.findByEmail(any())).willReturn(Optional.ofNullable(member));
        given(accessTokenHelper.createToken(any())).willReturn("newAccessToken");
        given(refreshTokenHelper.createToken(any())).willReturn("newRefreshToken");
        given(studyJoinRepository.findStudyInfoByMemberId(any())).willReturn(new ArrayList<>());

        //when
        TokenResponseDto ActualResult = signService.tokenReIssue(requestDto);

        //then
        Assertions.assertEquals(ExpectResult.getId(), ActualResult.getId());
    }

    @Test
    @DisplayName("잘못된 RefreshToken에 의해 토큰 재발행에 실패한다.")
    public void reIssue_fail() throws Exception {
        //given
        TokenRequestDto requestDto = SignRequestFactory.makeTokenRequestDto("refreshToken");
        Member member = SignRequestFactory.makeAuthTestMember();

        //mocking
        given(redisService.getData(any())).willReturn("wrongToken!!!!");
        given(refreshTokenHelper.extractSubject(any())).willReturn("!!!wrongToken");

        //when, then
        Assertions.assertThrows(RedisValueDifferentException.class, ()->signService.tokenReIssue(requestDto));
    }
}