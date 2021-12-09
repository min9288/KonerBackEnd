package project.SangHyun.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import project.SangHyun.domain.response.MultipleResult;
import project.SangHyun.domain.response.SingleResult;
import project.SangHyun.domain.response.StudyArticleCreateResponseDto;
import project.SangHyun.domain.service.Impl.ResponseServiceImpl;
import project.SangHyun.domain.service.StudyArticleService;
import project.SangHyun.dto.request.StudyArticleCreateRequestDto;
import project.SangHyun.dto.response.StudyArticleFindResponseDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/study/{studyId}/board/{boardId}/article")
public class StudyArticleController {

    public final StudyArticleService studyArticleService;
    public final ResponseServiceImpl responseService;

    @GetMapping
    public MultipleResult<StudyArticleFindResponseDto> findAllArticles(@PathVariable Long studyId, @PathVariable Long boardId) {
        return responseService.getMultipleResult(studyArticleService.findAllArticles(boardId));
    }

    @PostMapping
    public SingleResult<StudyArticleCreateResponseDto> createArticle(@PathVariable Long studyId, @PathVariable Long boardId,
                                                                     @RequestBody StudyArticleCreateRequestDto requestDto) {
        return responseService.getSingleResult(studyArticleService.createArticle(requestDto, boardId));
    }
}
