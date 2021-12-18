package project.SangHyun.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.SangHyun.member.domain.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByNickname(String nickname);
}