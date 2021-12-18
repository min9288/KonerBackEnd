package project.SangHyun.member.controller.integration;

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
import project.SangHyun.member.domain.Member;
import project.SangHyun.member.repository.MemberRepository;
import project.SangHyun.member.dto.request.MemberUpdateRequestDto;
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
        String accessToken = accessTokenHelper.createToken("xptmxm1!");
        Member member = memberRepository.findByEmail("xptmxm1!").orElseThrow(MemberNotFoundException::new);

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
        String accessToken = accessTokenHelper.createToken("xptmxm1!");
        Member member = memberRepository.findByEmail("xptmxm1!").orElseThrow(MemberNotFoundException::new);

        //when, then
        mockMvc.perform(get("/users/{id}", member.getId())
                        .header("X-AUTH-TOKEN", accessToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("인증하지 않는 회원은 프로필 로드에 실패한다.")
    public void getUserProfile() throws Exception {
        //given
        String accessToken = accessTokenHelper.createToken("xptmxm2!");
        Member member = memberRepository.findByEmail("xptmxm2!").orElseThrow(MemberNotFoundException::new);

        //when, then
        mockMvc.perform(get("/users/{id}", member.getId())
                        .header("X-AUTH-TOKEN", accessToken))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("회원의 유저 프로필(닉네임)을 수정한다.")
    public void updateMember() throws Exception {
        //given
        String accessToken = accessTokenHelper.createToken("xptmxm1!");
        MemberUpdateRequestDto requestDto = new MemberUpdateRequestDto("xptmxm1!", "철수", "기공");
        Member member = memberRepository.findByEmail("xptmxm1!").orElseThrow(MemberNotFoundException::new);

        //when, then
        mockMvc.perform(put("/users/{id}", member.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(new Gson().toJson(requestDto))
                        .header("X-AUTH-TOKEN", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nickname").value("철수"));
    }

    @Test
    @DisplayName("관리자는 부적절한 회원의 유저 프로필(닉네임)을 수정한다.")
    public void updateMember_admin() throws Exception {
        //given
        String accessToken = accessTokenHelper.createToken("xptmxm4!");
        MemberUpdateRequestDto requestDto = new MemberUpdateRequestDto("xptmxm1!", "적절한닉네임", "컴공");
        Member member = memberRepository.findByEmail("xptmxm1!").orElseThrow(MemberNotFoundException::new);

        //when, then
        mockMvc.perform(put("/users/{id}", member.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(new Gson().toJson(requestDto))
                        .header("X-AUTH-TOKEN", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nickname").value("적절한닉네임"));
    }

    @Test
    @DisplayName("회원은 다른 회원의 유저 프로필(닉네임)을 수정할 수 없다.")
    public void updateMember_fail() throws Exception {
        //given
        String accessToken = accessTokenHelper.createToken("xptmxm3!");
        MemberUpdateRequestDto requestDto = new MemberUpdateRequestDto("xptmxm1!", "철수", "기공");
        Member member = memberRepository.findByEmail("xptmxm1!").orElseThrow(MemberNotFoundException::new);

        //when, then
        mockMvc.perform(put("/users/{id}", member.getId())
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
        String accessToken = accessTokenHelper.createToken("xptmxm1!");
        Member member = memberRepository.findByEmail("xptmxm1!").orElseThrow(MemberNotFoundException::new);

        //when, then
        mockMvc.perform(delete("/users/{id}", member.getId())
                        .header("X-AUTH-TOKEN", accessToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("관리자는 부적절한 회원을 삭제한다.")
    public void deleteMember_admin() throws Exception {
        //given
        String accessToken = accessTokenHelper.createToken("xptmxm4!");
        Member member = memberRepository.findByEmail("xptmxm1!").orElseThrow(MemberNotFoundException::new);

        //when, then
        mockMvc.perform(delete("/users/{id}", member.getId())
                        .header("X-AUTH-TOKEN", accessToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("회원은 다른 회원을 삭제할 수 없다.")
    public void deleteMember_fail() throws Exception {
        //given
        String accessToken = accessTokenHelper.createToken("xptmxm3!");
        Member member = memberRepository.findByEmail("xptmxm1!").orElseThrow(MemberNotFoundException::new);

        //when, then
        mockMvc.perform(delete("/users/{id}", member.getId())
                        .header("X-AUTH-TOKEN", accessToken))
                .andExpect(status().is3xxRedirection());
    }
}