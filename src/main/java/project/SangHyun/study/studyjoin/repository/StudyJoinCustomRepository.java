package project.SangHyun.study.studyjoin.repository;

import project.SangHyun.study.studyjoin.domain.StudyJoin;
import project.SangHyun.study.studyjoin.repository.impl.StudyInfoDto;

import java.util.List;
import java.util.Optional;

public interface StudyJoinCustomRepository {
    Long findStudyJoinCount(Long studyId);
    List<StudyInfoDto> findStudyInfoByMemberId(Long memberId);
    Boolean exist(Long studyId, Long memberId);
    Optional<StudyJoin> findStudyRole(Long memberId, Long studyId);
}