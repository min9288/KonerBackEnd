package project.SangHyun.member.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.SangHyun.member.dto.request.*;
import project.SangHyun.member.dto.response.MemberChangePwResponseDto;
import project.SangHyun.member.dto.response.MemberLoginResponseDto;
import project.SangHyun.member.dto.response.MemberRegisterResponseDto;
import project.SangHyun.member.dto.response.TokenResponseDto;
import project.SangHyun.member.service.SignService;
import project.SangHyun.response.domain.SingleResult;
import project.SangHyun.response.service.ResponseServiceImpl;

import javax.validation.Valid;
import java.beans.PropertyEditorSupport;
import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/sign")
public class SignController {
    private final SignService signService;
    private final ResponseServiceImpl responseService;

    @ApiOperation(value = "회원가입", notes = "회원가입을 진행한다.")
    @PostMapping("/register")
    public SingleResult<MemberRegisterResponseDto> register(@Valid @ModelAttribute MemberRegisterRequestDto requestDto,
                                                            BindingResult bindingResult) throws IOException, BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
        return responseService.getSingleResult(signService.registerMember(requestDto));
    }

    @ApiOperation(value = "검증 메일 발송", notes = "검증을 위해 메일을 발송한다.")
    @PostMapping("/email")
    public SingleResult<String> sendEmail(@RequestBody MemberEmailAuthRequestDto requestDto) {
        return responseService.getSingleResult(signService.sendEmail(requestDto));
    }

    @ApiOperation(value = "이메일 인증", notes = "이메일 인증을 진행한다.")
    @PostMapping("/verify")
    public SingleResult<String> verify(@RequestBody VerifyEmailRequestDto requestDto) {
        return responseService.getSingleResult(signService.verify(requestDto));
    }

    @ApiOperation(value = "비밀번호 변경", notes = "비밀번호 변경을 진행한다.")
    @PostMapping("/password")
    public SingleResult<MemberChangePwResponseDto> changePassword(@Valid @RequestBody MemberChangePwRequestDto requestDto) {
        return responseService.getSingleResult(signService.changePassword(requestDto));
    }

    @ApiOperation(value = "로컬 로그인", notes = "로컬을 통해 로그인을 진행한다.")
    @PostMapping("/login")
    public SingleResult<MemberLoginResponseDto> login(@Valid @RequestBody MemberLoginRequestDto requestDto) {
        return responseService.getSingleResult(signService.loginMember(requestDto));
    }

    @ApiOperation(value = "토큰 재발급", notes = "Refresh Token을 통해 토큰을 재발급받는다.")
    @PostMapping("/reissue")
    public SingleResult<TokenResponseDto> reIssue(@RequestBody TokenRequestDto requestDto) {
        return responseService.getSingleResult(signService.tokenReIssue(requestDto));
    }
}
