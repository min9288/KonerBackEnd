package project.SangHyun.study.studyjoin.domain;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import project.SangHyun.common.EntityDate;
import project.SangHyun.member.domain.Member;
import project.SangHyun.study.study.domain.Study;
import project.SangHyun.study.study.enums.StudyRole;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
public class StudyJoin extends EntityDate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "studyjoin_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Study study;

    @Enumerated(EnumType.STRING)
    private StudyRole studyRole;

    public StudyJoin(Long id) {
        this.id = id;
    }

    @Builder
    public StudyJoin(Member member, Study study, StudyRole studyRole) {
        this.member = member;
        this.study = study;
        this.studyRole = studyRole;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    public void acceptMember() {
        this.studyRole = StudyRole.MEMBER;
    }
}
