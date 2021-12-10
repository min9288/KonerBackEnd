package project.SangHyun.domain.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.SangHyun.advice.exception.MemberNotFoundException;
import project.SangHyun.config.security.member.MemberDetails;
import project.SangHyun.domain.repository.StudyJoinRepository;
import project.SangHyun.domain.repository.impl.dto.StudyInfoDto;
import project.SangHyun.dto.response.member.MemberDeleteResponseDto;
import project.SangHyun.dto.response.member.MemberInfoResponseDto;
import project.SangHyun.dto.response.member.MemberProfileResponseDto;
import project.SangHyun.dto.response.member.MemberUpdateInfoResponseDto;
import project.SangHyun.domain.entity.Member;
import project.SangHyun.domain.repository.MemberRepository;
import project.SangHyun.domain.service.MemberService;
import project.SangHyun.dto.request.member.MemberUpdateInfoRequestDto;

import java.util.List;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final StudyJoinRepository studyJoinRepository;

    /**
     * AccessToken으로 유저 정보 조회
     * @param memberDetails
     * @return
     */
    @Override
    public MemberInfoResponseDto getMemberInfo(MemberDetails memberDetails) {
        Member member = memberRepository.findByEmail(memberDetails.getUsername()).orElseThrow(MemberNotFoundException::new);
        List<StudyInfoDto> studyInfoDtos = studyJoinRepository.findStudyInfoByMemberId(member.getId());
        return MemberInfoResponseDto.createDto(member, studyInfoDtos);
    }

    /**
     * ID(PK)로 유저 정보 조회
     * @param memberId
     * @return
     */
    @Override
    public MemberProfileResponseDto getProfile(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        return MemberProfileResponseDto.createDto(member);
    }

    /**
     * 유저 정보 수정
     * @param memberId
     * @param requestDto
     * @return
     */
    @Override
    @Transactional
    public MemberUpdateInfoResponseDto updateMemberInfo(Long memberId, MemberUpdateInfoRequestDto requestDto) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        return MemberUpdateInfoResponseDto.createDto(member.updateMemberInfo(requestDto));
    }

    @Override
    @Transactional
    public MemberDeleteResponseDto deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        memberRepository.delete(member);
        return  MemberDeleteResponseDto.createDto(member);
    }
}
