package project.SangHyun.study.study.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.SangHyun.study.study.domain.Study;
import project.SangHyun.study.study.domain.StudyOptions.RecruitState;
import project.SangHyun.study.study.domain.StudyOptions.StudyMethod;
import project.SangHyun.study.study.domain.StudyOptions.StudyState;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "스터디 찾기 요청 결과")
public class StudyFindResponseDto {
    @ApiModelProperty(value = "스터디 ID(PK)")
    private Long studyId;

    @ApiModelProperty(value = "회원 ID(PK)")
    private StudyMemberProfile creator;

    @ApiModelProperty(value = "제목")
    private String title;

    @ApiModelProperty(value = "스터디 주제")
    private List<String> tags;

    @ApiModelProperty(value = "스터디 내용")
    private String description;

    @ApiModelProperty(value = "스터디 시작 일정", notes = "스터디 시작 일정을 입력해주세요.", required = true, example = "2021-12-25")
    private String startDate;

    @ApiModelProperty(value = "스터디 종료 일정", notes = "스터디 종료 일정을 입력해주세요.", required = true, example = "2021-12-25")
    private String endDate;

    @ApiModelProperty(value = "스터디 참여수")
    private Long joinCount;

    @ApiModelProperty(value = "스터디 정원수")
    private Long headCount;

    @ApiModelProperty(value = "프로필 이미지", notes = "프로필 이미지를 업로드해주세요.", required = true, example = "")
    private String profileImg;

    @ApiModelProperty(value = "스터디 참여인원들")
    private List<StudyMemberProfile> studyMembers;

    @ApiModelProperty(value = "스터디 방법")
    private StudyMethod studyMethod;

    @ApiModelProperty(value = "스터디 상태")
    private StudyState studyState;

    @ApiModelProperty(value = "스터디 모집 상태")
    private RecruitState recruitState;

    public static StudyFindResponseDto create(Study study) {
        List<StudyMemberProfile> participants = study.getStudyJoins().stream()
                .map(StudyMemberProfile::create)
                .collect(Collectors.toList());

        return new StudyFindResponseDto(study.getId(),
                StudyMemberProfile.create(study), study.getTitle(), study.getTagNames(),
                study.getDescription(), study.getStartDate(), study.getEndDate(),
                (long) participants.size(), study.getHeadCount(), study.getProfileImgUrl(), participants,
                study.getStudyMethod(), study.getStudyState(), study.getRecruitState());
    }
}
