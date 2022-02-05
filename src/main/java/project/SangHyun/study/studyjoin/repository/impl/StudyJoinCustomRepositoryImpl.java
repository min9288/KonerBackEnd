package project.SangHyun.study.studyjoin.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import project.SangHyun.study.study.domain.Study;
import project.SangHyun.study.study.domain.enums.StudyRole;
import project.SangHyun.study.studyjoin.domain.StudyJoin;
import project.SangHyun.study.studyjoin.repository.StudyJoinCustomRepository;

import java.util.List;
import java.util.Optional;

import static project.SangHyun.member.domain.QMember.member;
import static project.SangHyun.study.study.domain.QStudy.study;
import static project.SangHyun.study.studyjoin.domain.QStudyJoin.studyJoin;

@RequiredArgsConstructor
public class StudyJoinCustomRepositoryImpl implements StudyJoinCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Long findStudyJoinCount(Long studyId) {
        return jpaQueryFactory
                .select(study)
                .from(studyJoin)
                .where(studyJoin.study.id.eq(studyId),
                        studyJoin.studyRole.ne(StudyRole.APPLY))
                .fetchCount();
    }

    @Override
    public Boolean exist(Long studyId, Long memberId) {
        StudyJoin findStudyJoin = jpaQueryFactory
                .selectFrom(studyJoin)
                .where(studyJoin.study.id.eq(studyId),
                        studyJoin.member.id.eq(memberId),
                        studyJoin.studyRole.ne(StudyRole.APPLY))
                .fetchFirst();

        return findStudyJoin != null;
    }

    @Override
    public Optional<StudyJoin> findStudyRole(Long memberId, Long studyId) {
        return Optional.ofNullable(jpaQueryFactory
                .selectFrom(studyJoin)
                .where(studyJoin.study.id.eq(studyId),
                        studyJoin.member.id.eq(memberId))
                .fetchOne());
    }

    @Override
    public List<StudyMembersInfoDto> findStudyMembers(Long studyId) {
        return jpaQueryFactory
                .select(Projections.constructor(StudyMembersInfoDto.class,
                        member.id, member.nickname.nickname, studyJoin.studyRole, studyJoin.applyContent))
                .from(studyJoin)
                .innerJoin(studyJoin.member, member)
                .where(studyJoin.study.id.eq(studyId))
                .fetch();
    }

    @Override
    public List<Study> findStudiesByMemberId(Long memberId) {
        return jpaQueryFactory
                .select(study)
                .from(studyJoin)
                .innerJoin(studyJoin.study, study)
                .where(studyJoin.member.id.eq(memberId),
                        studyJoin.studyRole.ne(StudyRole.APPLY))
                .fetch();
    }

    @Override
    public Optional<StudyJoin> findApplyStudy(Long studyId, Long memberId) {
        return Optional.ofNullable(jpaQueryFactory
                .selectFrom(studyJoin)
                .innerJoin(studyJoin.member, member).fetchJoin()
                .innerJoin(studyJoin.study, study).fetchJoin()
                .where(studyJoin.study.id.eq(studyId),
                        studyJoin.member.id.eq(memberId))
                .fetchFirst());
    }

    @Override
    public List<StudyJoin> findAdminAndCreator(Long studyId) {
        return jpaQueryFactory.select(studyJoin)
                .from(studyJoin)
                .innerJoin(studyJoin.member, member).fetchJoin()
                .innerJoin(studyJoin.study, study).fetchJoin()
                .where(studyJoin.id.eq(studyId).and(studyJoin.studyRole.eq(StudyRole.CREATOR))
                        .or(studyJoin.studyRole.eq(StudyRole.ADMIN)))
                .fetch();
    }
}
