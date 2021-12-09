package project.SangHyun.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.SangHyun.domain.entity.Member;
import project.SangHyun.domain.entity.StudyArticle;
import project.SangHyun.domain.entity.StudyBoard;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudyArticleCreateRequestDto {
    private Long memberId;
    private String title;
    private String content;

    public StudyArticle toEntity(Long boardId) {
        return StudyArticle.builder()
                .member(new Member(memberId))
                .studyBoard(new StudyBoard(boardId))
                .title(title)
                .content(content)
                .build();
    }
}
