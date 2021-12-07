package project.SangHyun.domain.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.SangHyun.domain.enums.StudyRole;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyJoin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "studyjoin_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    private Study study;

    @Enumerated(EnumType.STRING)
    private StudyRole studyRole;

    @Builder
    public StudyJoin(Member member, Study study, StudyRole studyRole) {
        this.member = member;
        this.study = study;
        this.studyRole = studyRole;
    }

    public void setStudy(Study study) {
        this.study = study;
    }
}
