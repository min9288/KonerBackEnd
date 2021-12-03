package project.SangHyun;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import project.SangHyun.domain.entity.Member;
import project.SangHyun.domain.entity.Role;
import project.SangHyun.domain.repository.MemberRepository;
import project.SangHyun.domain.service.MemberService;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
@Transactional
public class TestData {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        Member member1 = new Member("test", passwordEncoder.encode("test"), "승범", "컴공", Role.ROLE_MEMBER);
        memberRepository.save(member1);

        Member member2 = new Member("test2", passwordEncoder.encode("test2"), "상현", "컴공", Role.ROLE_MEMBER);
        memberRepository.save(member2);
    }
}