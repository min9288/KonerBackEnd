package project.SangHyun.web.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import project.SangHyun.domain.response.MultipleResult;
import project.SangHyun.domain.response.SingleResult;
import project.SangHyun.domain.service.StudyBoardService;
import project.SangHyun.domain.service.StudyService;
import project.SangHyun.domain.service.Impl.ResponseServiceImpl;
import project.SangHyun.domain.service.StudyJoinService;
import project.SangHyun.dto.request.StudyCreateRequestDto;
import project.SangHyun.dto.request.StudyJoinRequestDto;
import project.SangHyun.dto.response.StudyBoardFindResponseDto;
import project.SangHyun.dto.response.StudyCreateResponseDto;
import project.SangHyun.dto.response.StudyFindResponseDto;
import project.SangHyun.dto.response.StudyJoinResponseDto;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/study")
public class StudyController {

    private final StudyService studyService;
    private final StudyBoardService studyBoardService;
    private final StudyJoinService studyJoinService;
    private final ResponseServiceImpl responseService;

    @ApiOperation(value = "스터디 정보 로드", notes = "모든 스터디 정보를 얻어온다.")
    @GetMapping
    public MultipleResult<StudyFindResponseDto> findAllStudies() {
        return responseService.getMultipleResult(studyService.findAllStudies());
    }

    @ApiOperation(value = "스터디 세부 정보 로드", notes = "스터디 세부 정보를 얻어온다.")
    @GetMapping("/{studyId}")
    public SingleResult<StudyFindResponseDto> findStudy(@PathVariable Long studyId) {
        return responseService.getSingleResult(studyService.findStudy(studyId));
    }

    @ApiOperation(value = "스터디 생성", notes = "스터디를 생성한다.")
    @PostMapping
    public SingleResult<StudyCreateResponseDto> createStudy(@RequestBody StudyCreateRequestDto requestDto) {
        return responseService.getSingleResult(studyService.createStudy(requestDto));
    }

    @ApiOperation(value = "스터디 참여", notes = "스터디에 참여한다.")
    @PostMapping("/join")
    public SingleResult<StudyJoinResponseDto> join(@RequestBody StudyJoinRequestDto requestDto) {
        return responseService.getSingleResult(studyJoinService.join(requestDto));
    }

    @ApiOperation(value = "스터디 게시판 로드", notes = "스터디에 포함된 게시판의 목록을 로드한다.")
    @GetMapping("/{studyId}/board")
    public MultipleResult<StudyBoardFindResponseDto> findStudyBoards(@PathVariable Long studyId) {
        return responseService.getMultipleResult(studyBoardService.findBoards(studyId));
    }
}
