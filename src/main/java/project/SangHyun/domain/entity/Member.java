package project.SangHyun.domain.entity;

import lombok.*;
import project.SangHyun.domain.enums.MemberRole;
import project.SangHyun.dto.request.MemberRegisterRequestDto;
import project.SangHyun.dto.request.MemberUpdateInfoRequestDto;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "MEMBERS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;
    private String email;
    private String password;
    private String nickname;
    private String department;
    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;

    public static Member createMember(MemberRegisterRequestDto requestDto) {
        return Member.builder()
                .email(requestDto.getEmail())
                .password(requestDto.getPassword())
                .nickname(requestDto.getNickname())
                .department(requestDto.getDepartment())
                .memberRole(MemberRole.ROLE_NOT_PERMITTED)
                .build();
    }

    public Member updateMemberInfo(MemberUpdateInfoRequestDto requestDto) {
        this.email = requestDto.getEmail();
        this.nickname = requestDto.getNickname();
        this.department = requestDto.getDepartment();

        return this;
    }

    public Member(Long id) {
        this.id = id;
    }

    @Builder
    public Member(String email, String password, String nickname, String department, MemberRole memberRole) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.department = department;
        this.memberRole = memberRole;
    }

    public void changeRole(MemberRole memberRole) {
        this.memberRole = memberRole;
    }

    public void changePassword(String password) {
        this.password = password;
    }
}
