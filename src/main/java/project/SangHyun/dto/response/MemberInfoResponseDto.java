package project.SangHyun.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.SangHyun.domain.entity.Member;
import project.SangHyun.domain.entity.Study;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class MemberInfoResponseDto {
    private Long id;
    private String email;
    private String nickname;
    private String department;
    private List<Long> studyIds;

    public static MemberInfoResponseDto createDto(Member member, List<Study> studies) {
        List<Long> studyIds = studies.stream()
                .map(study -> study.getId())
                .collect(Collectors.toList());

        return MemberInfoResponseDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .department(member.getDepartment())
                .studyIds(studyIds)
                .build();
    }

    @Builder
    public MemberInfoResponseDto(Long id, String email, String nickname, String department, List<Long> studyIds) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.department = department;
        this.studyIds = studyIds;
    }
}
