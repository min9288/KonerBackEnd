package project.SangHyun.study.studyarticle.controller.integration;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import project.SangHyun.TestDB;
import project.SangHyun.advice.exception.MemberNotFoundException;
import project.SangHyun.config.jwt.JwtTokenHelper;
import project.SangHyun.member.repository.MemberRepository;
import project.SangHyun.study.study.enums.StudyRole;
import project.SangHyun.utils.service.RedisService;
import project.SangHyun.member.domain.Member;
import project.SangHyun.study.study.domain.Study;
import project.SangHyun.study.study.repository.StudyRepository;
import project.SangHyun.study.studyarticle.dto.request.StudyArticleCreateRequestDto;
import project.SangHyun.study.studyarticle.dto.request.StudyArticleUpdateRequestDto;
import project.SangHyun.study.studyarticle.domain.StudyArticle;
import project.SangHyun.study.studyarticle.repository.StudyArticleRepository;
import project.SangHyun.study.studyboard.domain.StudyBoard;
import project.SangHyun.study.studyjoin.domain.StudyJoin;
import project.SangHyun.study.studyjoin.repository.StudyJoinRepository;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class StudyArticleControllerIntegrationTest {
    @Autowired
    WebApplicationContext context;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    StudyRepository studyRepository;
    @Autowired
    StudyArticleRepository studyArticleRepository;
    @Autowired
    StudyJoinRepository studyJoinRepository;
    @Autowired
    RedisService redisService;
    @Autowired
    JwtTokenHelper accessTokenHelper;
    @Autowired
    TestDB testDB;

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        testDB.init();
    }

    @Test
    @DisplayName("스터디의 한 카테고리에 해당하는 모든 게시글을 로드한다.")
    public void loadArticles() throws Exception {
        //given
        String accessToken = accessTokenHelper.createToken("xptmxm3!");
        Study study = studyRepository.findStudyByTitle("백엔드").get(0);
        StudyBoard studyBoard = study.getStudyBoards().get(0);

        //when, then
        mockMvc.perform(get("/study/{studyId}/board/{boardId}/article", study.getId(), studyBoard.getId())
                        .header("X-AUTH-TOKEN", accessToken))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("스터디에 참여하지 않은 회원은 해당 스터디의 한 카테고리에 해당하는 모든 게시글을 로드할 수 없다.")
    public void loadArticles_fail() throws Exception {
        //given
        String accessToken = accessTokenHelper.createToken("xptmxm1!");
        Study study = studyRepository.findStudyByTitle("백엔드").get(0);
        StudyBoard studyBoard = study.getStudyBoards().get(0);

        //when, then
        mockMvc.perform(get("/study/{studyId}/board/{boardId}/article", study.getId(), studyBoard.getId())
                        .header("X-AUTH-TOKEN", accessToken))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("스터디의 한 카테고리에 해당하는 게시글을 조회한다.")
    public void loadArticle() throws Exception {
        //given
        String accessToken = accessTokenHelper.createToken("xptmxm3!");
        Study study = studyRepository.findStudyByTitle("백엔드").get(0);
        StudyBoard studyBoard = study.getStudyBoards().get(0);
        StudyArticle studyArticle = studyArticleRepository.findAllArticles(studyBoard.getId()).get(0);
        StudyArticleUpdateRequestDto requestDto = new StudyArticleUpdateRequestDto("공지사항 수정 글", "수정 글");

        //when, then
        mockMvc.perform(get("/study/{studyId}/board/{boardId}/article/{articleId}", study.getId(), studyBoard.getId(), studyArticle.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(new Gson().toJson(requestDto))
                        .header("X-AUTH-TOKEN", accessToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("스터디에 속하지 않은 회원은 해당 스터디의 한 카테고리에 해당하는 게시글을 조회할 수 없다.")
    public void loadArticle_fail() throws Exception {
        //given
        String accessToken = accessTokenHelper.createToken("xptmxm1!");
        Study study = studyRepository.findStudyByTitle("백엔드").get(0);
        StudyBoard studyBoard = study.getStudyBoards().get(0);
        StudyArticle studyArticle = studyArticleRepository.findAllArticles(studyBoard.getId()).get(0);
        StudyArticleUpdateRequestDto requestDto = new StudyArticleUpdateRequestDto("공지사항 수정 글", "수정 글");

        //when, then
        mockMvc.perform(get("/study/{studyId}/board/{boardId}/article/{articleId}", study.getId(), studyBoard.getId(), studyArticle.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(new Gson().toJson(requestDto))
                        .header("X-AUTH-TOKEN", accessToken))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("스터디의 한 카테고리에 해당하는 게시글을 생성한다.")
    public void createArticle() throws Exception {
        //given
        String accessToken = accessTokenHelper.createToken("xptmxm3!");
        Member member = memberRepository.findByEmail("xptmxm3!").orElseThrow(MemberNotFoundException::new);
        Study study = studyRepository.findStudyByTitle("백엔드").get(0);
        StudyBoard studyBoard = study.getStudyBoards().get(0);
        StudyArticleCreateRequestDto requestDto = new StudyArticleCreateRequestDto(member.getId(), "테스트 글", "테스트 글");

        //when, then
        mockMvc.perform(post("/study/{studyId}/board/{boardId}/article", study.getId(), studyBoard.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(new Gson().toJson(requestDto))
                        .header("X-AUTH-TOKEN", accessToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("스터디에 참여하지 않은 회원은 해당 스터디의 한 카테고리에 해당하는 게시글을 생성할 수 없다.")
    public void createArticle_fail() throws Exception {
        //given
        String accessToken = accessTokenHelper.createToken("xptmxm1!");
        Member member = memberRepository.findByEmail("xptmxm3!").orElseThrow(MemberNotFoundException::new);
        Study study = studyRepository.findStudyByTitle("백엔드").get(0);
        StudyBoard studyBoard = study.getStudyBoards().get(0);
        StudyArticleCreateRequestDto requestDto = new StudyArticleCreateRequestDto(member.getId(), "테스트 글", "테스트 글");

        //when, then
        mockMvc.perform(post("/study/{studyId}/board/{boardId}/article", study.getId(), studyBoard.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(new Gson().toJson(requestDto))
                        .header("X-AUTH-TOKEN", accessToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("스터디의 한 카테고리에 해당하는 게시글을 수정한다.")
    public void updateArticle() throws Exception {
        //given
        String accessToken = accessTokenHelper.createToken("xptmxm3!");
        Study study = studyRepository.findStudyByTitle("백엔드").get(0);
        StudyBoard studyBoard = study.getStudyBoards().get(0);
        StudyArticle studyArticle = studyArticleRepository.findAllArticles(studyBoard.getId()).get(0);
        StudyArticleUpdateRequestDto requestDto = new StudyArticleUpdateRequestDto("공지사항 수정 글", "수정 글");

        //when, then
        mockMvc.perform(put("/study/{studyId}/board/{boardId}/article/{articleId}", study.getId(), studyBoard.getId(), studyArticle.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(new Gson().toJson(requestDto))
                        .header("X-AUTH-TOKEN", accessToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("웹 관리자는 스터디의 한 카테고리에 해당하는 게시글이 부적절하다면 수정할 수 있다.")
    public void updateArticle_webAdmin() throws Exception {
        //given
        String accessToken = accessTokenHelper.createToken("xptmxm4!");
        Study study = studyRepository.findStudyByTitle("백엔드").get(0);
        StudyBoard studyBoard = study.getStudyBoards().get(0);
        StudyArticle studyArticle = studyArticleRepository.findAllArticles(studyBoard.getId()).get(0);
        StudyArticleUpdateRequestDto requestDto = new StudyArticleUpdateRequestDto("올바른 글", "수정 글");

        //when, then
        mockMvc.perform(put("/study/{studyId}/board/{boardId}/article/{articleId}", study.getId(), studyBoard.getId(), studyArticle.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(new Gson().toJson(requestDto))
                        .header("X-AUTH-TOKEN", accessToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("스터디 관리자는 스터디의 한 카테고리에 해당하는 게시글이 부적절하다면 수정할 수 있다.")
    public void updateArticle_studyAdmin() throws Exception {
        //given
        String accessToken = accessTokenHelper.createToken("xptmxm5!");
        Study study = studyRepository.findStudyByTitle("백엔드").get(0);
        StudyBoard studyBoard = study.getStudyBoards().get(0);
        StudyArticle studyArticle = studyArticleRepository.findAllArticles(studyBoard.getId()).get(0);
        StudyArticleUpdateRequestDto requestDto = new StudyArticleUpdateRequestDto("올바른 글", "수정 글");

        //when, then
        mockMvc.perform(put("/study/{studyId}/board/{boardId}/article/{articleId}", study.getId(), studyBoard.getId(), studyArticle.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(new Gson().toJson(requestDto))
                        .header("X-AUTH-TOKEN", accessToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("권한이 없는 사람에 의한 게시글 수정은 실패한다.")
    public void updateArticle_fail() throws Exception {
        //given
        String accessToken = accessTokenHelper.createToken("xptmxm1!");
        Study study = studyRepository.findStudyByTitle("백엔드").get(0);
        StudyBoard studyBoard = study.getStudyBoards().get(0);
        StudyArticle studyArticle = studyArticleRepository.findAllArticles(studyBoard.getId()).get(0);
        StudyArticleUpdateRequestDto requestDto = new StudyArticleUpdateRequestDto("공지사항 수정 글", "수정 글");

        //when, then
        mockMvc.perform(put("/study/{studyId}/board/{boardId}/article/{articleId}", study.getId(), studyBoard.getId(), studyArticle.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(new Gson().toJson(requestDto))
                        .header("X-AUTH-TOKEN", accessToken))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("스터디의 한 카테고리에 해당하는 게시글을 삭제한다.")
    public void deleteArticle() throws Exception {
        //given
        String accessToken = accessTokenHelper.createToken("xptmxm3!");
        Study study = studyRepository.findStudyByTitle("백엔드").get(0);
        StudyBoard studyBoard = study.getStudyBoards().get(0);
        StudyArticle studyArticle = studyArticleRepository.findAllArticles(studyBoard.getId()).get(0);

        //when, then
        mockMvc.perform(delete("/study/{studyId}/board/{boardId}/article/{articleId}", study.getId(), studyBoard.getId(), studyArticle.getId())
                        .header("X-AUTH-TOKEN", accessToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("스터디 관리자는 스터디의 한 카테고리에 해당하는 게시글이 부적절하다면 삭제할 수 있다.")
    public void deleteArticle_studyAdmin() throws Exception {
        //given
        String accessToken = accessTokenHelper.createToken("xptmxm5!");
        Study study = studyRepository.findStudyByTitle("백엔드").get(0);
        StudyBoard studyBoard = study.getStudyBoards().get(0);
        StudyArticle studyArticle = studyArticleRepository.findAllArticles(studyBoard.getId()).get(0);

        //when, then
        mockMvc.perform(delete("/study/{studyId}/board/{boardId}/article/{articleId}", study.getId(), studyBoard.getId(), studyArticle.getId())
                        .header("X-AUTH-TOKEN", accessToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("웹 관리자는 스터디의 한 카테고리에 해당하는 게시글이 부적절하다면 삭제할 수 있다.")
    public void deleteArticle_webAdmin() throws Exception {
        //given
        String accessToken = accessTokenHelper.createToken("xptmxm4!");
        Study study = studyRepository.findStudyByTitle("백엔드").get(0);
        StudyBoard studyBoard = study.getStudyBoards().get(0);
        StudyArticle studyArticle = studyArticleRepository.findAllArticles(studyBoard.getId()).get(0);

        //when, then
        mockMvc.perform(delete("/study/{studyId}/board/{boardId}/article/{articleId}", study.getId(), studyBoard.getId(), studyArticle.getId())
                        .header("X-AUTH-TOKEN", accessToken))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("권한이 없는 사람에 의한 게시글 삭제는 실패한다.")
    public void deleteArticle_fail() throws Exception {
        //given
        String accessToken = accessTokenHelper.createToken("xptmxm1!");
        Member member = memberRepository.findByEmail("xptmxm3!").orElseThrow(MemberNotFoundException::new);
        Study study = studyRepository.findStudyByTitle("백엔드").get(0);
        studyJoinRepository.save(new StudyJoin(member, study, StudyRole.MEMBER));
        StudyBoard studyBoard = study.getStudyBoards().get(0);
        StudyArticle studyArticle = studyArticleRepository.findAllArticles(studyBoard.getId()).get(0);

        //when, then
        mockMvc.perform(delete("/study/{studyId}/board/{boardId}/article/{articleId}", study.getId(), studyBoard.getId(), studyArticle.getId())
                        .header("X-AUTH-TOKEN", accessToken))
                .andExpect(status().is3xxRedirection());
    }
}