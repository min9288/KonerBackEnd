package project.SangHyun.study.studyjoin.repository.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.SangHyun.study.study.enums.StudyRole;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudyMembersInfoDto {
    private Long memberId;
    private String memberName;
    private StudyRole studyRole;
}