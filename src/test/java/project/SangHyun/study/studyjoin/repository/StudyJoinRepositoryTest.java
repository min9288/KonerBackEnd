package project.SangHyun.study.studyjoin.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import project.SangHyun.TestDB;
import project.SangHyun.config.jwt.JwtTokenHelper;
import project.SangHyun.member.domain.Member;
import project.SangHyun.member.enums.MemberRole;
import project.SangHyun.member.repository.MemberRepository;
import project.SangHyun.study.study.domain.Study;
import project.SangHyun.study.study.enums.RecruitState;
import project.SangHyun.study.study.enums.StudyRole;
import project.SangHyun.study.study.enums.StudyState;
import project.SangHyun.study.study.repository.StudyRepository;
import project.SangHyun.study.studyarticle.domain.StudyArticle;
import project.SangHyun.study.studyarticle.repository.StudyArticleRepository;
import project.SangHyun.study.studyboard.domain.StudyBoard;
import project.SangHyun.study.studyjoin.domain.StudyJoin;
import project.SangHyun.study.studyjoin.repository.StudyJoinRepository;
import project.SangHyun.study.studyjoin.repository.impl.StudyInfoDto;
import project.SangHyun.utils.service.RedisService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class StudyJoinRepositoryTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    StudyRepository studyRepository;
    @Autowired
    StudyArticleRepository studyArticleRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    StudyJoinRepository studyJoinRepository;

    @BeforeEach
    void beforeEach() {
        Member memberA = new Member("xptmxm3!", passwordEncoder.encode("xptmxm3!"), "상현", "컴공", MemberRole.ROLE_MEMBER);
        memberRepository.save(memberA);

        Member memberB = new Member("xptmxm5!", passwordEncoder.encode("xptmxm5!"), "진영", "컴공", MemberRole.ROLE_MEMBER);
        memberRepository.save(memberB);

        Study study = new Study("백엔드 모집", "백엔드", "백엔드 모집합니다.", StudyState.STUDYING, RecruitState.PROCEED, 3L, memberA, new ArrayList<>(), new ArrayList<>());

        StudyJoin studyJoin = new StudyJoin(memberA, study, StudyRole.CREATOR);
        study.join(studyJoin);

        StudyJoin studyJoin2 = new StudyJoin(memberB, study, StudyRole.ADMIN);
        study.join(studyJoin2);

        StudyBoard studyBoard1 = new StudyBoard("공지사항", study);
        StudyBoard studyBoard2 = new StudyBoard("자유게시판", study);
        StudyBoard studyBoard3 = new StudyBoard("알고리즘", study);
        study.addBoard(studyBoard1);
        study.addBoard(studyBoard2);
        study.addBoard(studyBoard3);

        studyRepository.save(study);

        StudyArticle studyArticle1 = new StudyArticle("공지사항 테스트 글", "공지사항 테스트 글입니다.", 0L, memberA, studyBoard1);
        StudyArticle studyArticle2 = new StudyArticle("자유게시판 테스트 글", "자유게시판 테스트 글입니다.", 0L, memberA, studyBoard1);
        StudyArticle studyArticle3 = new StudyArticle("알고리즘 테스트 글", "알고리즘 테스트 글입니다.", 0L, memberA, studyBoard1);

        studyArticleRepository.save(studyArticle1);
        studyArticleRepository.save(studyArticle2);
        studyArticleRepository.save(studyArticle3);
    }

    @Test
    @DisplayName("모든 스터디에 참여한 사람의 수를 조회한다.")
    public void findStudyJoinCount() throws Exception {
        //given
        Study study = studyRepository.findStudyByTitle("백엔드").get(0);

        //when
        Long studyJoinCount = studyJoinRepository.findStudyJoinCount(study.getId());

        //then
        Assertions.assertEquals(2, studyJoinCount);
    }

    @Test
    @DisplayName("회원 ID로 스터디에 대한 정보를 조회한다.")
    public void findStudyInfoByMemberId() throws Exception {
        //given
        Member member = memberRepository.findByEmail("xptmxm3!").get();

        //when
        List<StudyInfoDto> studyInfoByMemberId = studyJoinRepository.findStudyInfoByMemberId(member.getId());

        //then
        Assertions.assertEquals(1, studyInfoByMemberId.size());
        Assertions.assertEquals(true, studyInfoByMemberId.get(0).getStudyRole().equals(StudyRole.CREATOR));
    }

    @Test
    @DisplayName("해당 회원이 스터디에 참여중인지 조회한다.")
    public void exist() throws Exception {
        //given
        Study study = studyRepository.findStudyByTitle("백엔드").get(0);
        Member member = memberRepository.findByEmail("xptmxm3!").get();

        //when
        Boolean exist = studyJoinRepository.exist(study.getId(), member.getId());

        //then
        Assertions.assertEquals(true, exist);
    }

    @Test
    @DisplayName("해당 스터디의 권한을 조회한다.")
    public void findStudyRole() throws Exception {
        //given
        Study study = studyRepository.findStudyByTitle("백엔드").get(0);
        Member member = memberRepository.findByEmail("xptmxm3!").get();

        //when
        StudyJoin studyJoin = studyJoinRepository.findStudyRole(member.getId(), study.getId()).get();

        //then
        Assertions.assertEquals(StudyRole.CREATOR, studyJoin.getStudyRole());
    }
}