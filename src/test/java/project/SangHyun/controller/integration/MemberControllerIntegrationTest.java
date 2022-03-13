package project.SangHyun.controller.integration;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import project.SangHyun.TestDB;
import project.SangHyun.config.jwt.JwtTokenHelper;
import project.SangHyun.member.domain.Member;
import project.SangHyun.member.controller.dto.request.ChangePwRequestDto;
import project.SangHyun.member.controller.dto.request.MemberUpdateRequestDto;
import project.SangHyun.member.repository.MemberRepository;
import project.SangHyun.factory.member.MemberFactory;
import project.SangHyun.factory.sign.SignFactory;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class MemberControllerIntegrationTest {
    @Autowired
    WebApplicationContext context;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    MemberRepository memberRepository;
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
    @DisplayName("AccessToken을 이용해 회원의 정보를 로드한다.")
    public void getMemberInfo_Success() throws Exception {
        //given
        Member member = testDB.findGeneralMember();
        String accessToken = accessTokenHelper.createToken(member.getEmail());

        //when, then
        mockMvc.perform(post("/users/info")
                        .header("X-AUTH-TOKEN", accessToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("잘못된 AccessToken에 인해 회원의 유저 프로필을 실패한다.")
    public void getMemberInfo_Fail() throws Exception {
        //given
        String accessToken = "Wrong Token";

        //when, then
        mockMvc.perform(post("/users/info")
                        .header("X-AUTH-TOKEN", accessToken))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("회원의 유저 프로필을 로드한다.")
    public void getUserProfile_fail() throws Exception {
        //given
        Member member = testDB.findGeneralMember();
        String accessToken = accessTokenHelper.createToken(member.getEmail());

        //when, then
        mockMvc.perform(get("/users/{id}", member.getId())
                        .header("X-AUTH-TOKEN", accessToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("회원의 유저 프로필(닉네임)을 수정한다.")
    public void updateMember() throws Exception {
        //given
        Member member = testDB.findGeneralMember();
        String accessToken = accessTokenHelper.createToken(member.getEmail());
        MemberUpdateRequestDto requestDto = MemberFactory.makeUpdateRequestDto("철수", "철수입니다.");

        //when, then
        mockMvc.perform(multipart("/users/{id}", member.getId())
                        .file((MockMultipartFile) requestDto.getProfileImg())
                        .param("nickname", requestDto.getNickname())
                        .param("department", String.valueOf(requestDto.getDepartment()))
                        .param("introduction", requestDto.getIntroduction())
                        .with(requestPostProcessor -> {
                            requestPostProcessor.setMethod("PUT");
                            return requestPostProcessor;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header("X-AUTH-TOKEN", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nickname").value("철수"))
                .andExpect(jsonPath("$.data.introduction").value("철수입니다."));
    }

    @Test
    @DisplayName("관리자는 부적절한 회원의 유저 프로필(닉네임)을 수정한다.")
    public void updateMember_admin() throws Exception {
        //given
        Member member = testDB.findAdminMember();
        String accessToken = accessTokenHelper.createToken(member.getEmail());
        MemberUpdateRequestDto requestDto = MemberFactory.makeUpdateRequestDto("적절한닉네임", "적절한닉네임의 수정글입니다.");

        //when, then
        mockMvc.perform(multipart("/users/{id}", member.getId())
                        .file((MockMultipartFile) requestDto.getProfileImg())
                        .param("nickname", requestDto.getNickname())
                        .param("department", String.valueOf(requestDto.getDepartment()))
                        .param("introduction", requestDto.getIntroduction())
                        .with(requestPostProcessor -> {
                            requestPostProcessor.setMethod("PUT");
                            return requestPostProcessor;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header("X-AUTH-TOKEN", accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nickname").value("적절한닉네임"))
                .andExpect(jsonPath("$.data.introduction").value("적절한닉네임의 수정글입니다."));
    }

    @Test
    @DisplayName("회원은 다른 회원의 유저 프로필(닉네임)을 수정할 수 없다.")
    public void updateMember_fail() throws Exception {
        //given
        Member memberA = testDB.findGeneralMember();
        Member memberB = testDB.findAnotherGeneralMember();
        String accessToken = accessTokenHelper.createToken(memberB.getEmail());
        MemberUpdateRequestDto requestDto = MemberFactory.makeUpdateRequestDto("철수", "철수입니다.");

        //when, then
        mockMvc.perform(put("/users/{id}", memberA.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(new Gson().toJson(requestDto))
                        .header("X-AUTH-TOKEN", accessToken))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("회원을 삭제한다.")
    public void deleteMember() throws Exception {
        //given
        Member member = testDB.findGeneralMember();
        String accessToken = accessTokenHelper.createToken(member.getEmail());;

        //when, then
        mockMvc.perform(delete("/users/{id}", member.getId())
                        .header("X-AUTH-TOKEN", accessToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("관리자는 부적절한 회원을 삭제한다.")
    public void deleteMember_admin() throws Exception {
        //given
        Member member = testDB.findAdminMember();
        String accessToken = accessTokenHelper.createToken(member.getEmail());

        //when, then
        mockMvc.perform(delete("/users/{id}", member.getId())
                        .header("X-AUTH-TOKEN", accessToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("회원은 다른 회원을 삭제할 수 없다.")
    public void deleteMember_fail() throws Exception {
        //given
        Member memberA = testDB.findGeneralMember();
        Member memberB = testDB.findAnotherGeneralMember();
        String accessToken = accessTokenHelper.createToken(memberB.getEmail());

        //when, then
        mockMvc.perform(delete("/users/{id}", memberA.getId())
                        .header("X-AUTH-TOKEN", accessToken))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("비밀번호 변경을 진행한다.")
    public void changePW() throws Exception {
        //given
        ChangePwRequestDto requestDto = SignFactory.makeChangePwRequestDto("xptmxm1!", "change1!");

        //when, then
        mockMvc.perform(post("/users/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(new Gson().toJson(requestDto)))
                .andExpect(status().isOk());
    }
}