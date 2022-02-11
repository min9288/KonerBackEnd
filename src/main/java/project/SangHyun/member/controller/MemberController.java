package project.SangHyun.member.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import project.SangHyun.common.response.domain.Result;
import project.SangHyun.common.response.domain.SingleResult;
import project.SangHyun.common.response.service.ResponseServiceImpl;
import project.SangHyun.config.security.member.MemberDetails;
import project.SangHyun.member.controller.dto.request.ChangePwRequestDto;
import project.SangHyun.member.controller.dto.request.MemberUpdateRequestDto;
import project.SangHyun.member.controller.dto.response.*;
import project.SangHyun.member.service.MemberService;
import project.SangHyun.member.service.dto.MemberDto;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class MemberController {
    private final ResponseServiceImpl responseService;
    private final MemberService memberService;

    @ApiOperation(value = "회원 정보 로드", notes = "Access Token으로 유저에 대한 정보를 얻어온다.")
    @PostMapping("/info")
    public SingleResult<MemberResponseDto> getMemberInfo(@ApiIgnore @AuthenticationPrincipal MemberDetails memberDetails) {
        MemberDto memberDto = memberService.getMemberInfo(memberDetails);
        return responseService.getSingleResult(MemberResponseDto.create(memberDto));
    }

    @ApiOperation(value = "회원 프로필 정보 로드", notes = "회원 ID(PK)로 프로필에 대한 정보를 얻어온다.")
    @GetMapping("/{memberId}")
    public SingleResult<MemberResponseDto> getProfile(@PathVariable Long memberId) {
        MemberDto memberDto = memberService.getProfile(memberId);
        return responseService.getSingleResult(MemberResponseDto.create(memberDto));
    }

    @ApiOperation(value = "회원 정보 수정", notes = "회원에 대한 정보를 수정한다.")
    @PutMapping("/{memberId}")
    public SingleResult<MemberResponseDto> updateMember(@PathVariable Long memberId, @Valid @ModelAttribute MemberUpdateRequestDto requestDto,
                                                              BindingResult bindingResult) throws IOException, BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
        MemberDto memberDto = memberService.updateMember(memberId, requestDto.toServiceDto());
        return responseService.getSingleResult(MemberResponseDto.create(memberDto));
    }

    @ApiOperation(value = "회원 삭제", notes = "회원을 삭제한다.")
    @DeleteMapping("/{memberId}")
    public Result deleteMember(@PathVariable Long memberId) {
        memberService.deleteMember(memberId);
        return responseService.getDefaultSuccessResult();
    }

    @ApiOperation(value = "회원 비밀번호 변경", notes = "회원 비밀번호 변경을 진행한다.")
    @PostMapping("/password")
    public Result changePassword(@Valid @RequestBody ChangePwRequestDto requestDto) {
        memberService.changePassword(requestDto);
        return responseService.getDefaultSuccessResult();
    }
}
