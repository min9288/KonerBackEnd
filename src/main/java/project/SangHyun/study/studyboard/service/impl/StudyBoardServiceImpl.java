package project.SangHyun.study.studyboard.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.SangHyun.advice.exception.StudyBoardNotFoundException;
import project.SangHyun.study.studyboard.domain.StudyBoard;
import project.SangHyun.study.studyboard.dto.request.StudyBoardCreateRequestDto;
import project.SangHyun.study.studyboard.dto.request.StudyBoardUpdateRequestDto;
import project.SangHyun.study.studyboard.dto.response.StudyBoardCreateResponseDto;
import project.SangHyun.study.studyboard.dto.response.StudyBoardDeleteResponseDto;
import project.SangHyun.study.studyboard.dto.response.StudyBoardFindResponseDto;
import project.SangHyun.study.studyboard.dto.response.StudyBoardUpdateResponseDto;
import project.SangHyun.study.studyboard.repository.StudyBoardRepository;
import project.SangHyun.study.studyboard.service.StudyBoardService;
import project.SangHyun.study.studyjoin.repository.StudyJoinRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyBoardServiceImpl implements StudyBoardService {

    private final StudyBoardRepository studyBoardRepository;
    private final StudyJoinRepository studyJoinRepository;

    @Override
    public List<StudyBoardFindResponseDto> findAllBoards(Long studyId) {
        List<StudyBoard> studyBoards = studyBoardRepository.findBoards(studyId);
        return studyBoards.stream()
                .map(studyBoard -> StudyBoardFindResponseDto.create(studyBoard))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StudyBoardCreateResponseDto createBoard(Long studyId, StudyBoardCreateRequestDto requestDto) {
        StudyBoard studyBoard = studyBoardRepository.save(requestDto.toEntity(studyId));
        return StudyBoardCreateResponseDto.create(studyBoard);
    }

    @Override
    @Transactional
    public StudyBoardUpdateResponseDto updateBoard(Long studyId, Long studyBoardId, StudyBoardUpdateRequestDto requestDto) {
        StudyBoard studyBoard = studyBoardRepository.findById(studyBoardId).orElseThrow(StudyBoardNotFoundException::new);
        studyBoard.changeTitle(requestDto.getTitle());
        return StudyBoardUpdateResponseDto.create(studyBoard);
    }

    @Override
    @Transactional
    public StudyBoardDeleteResponseDto deleteBoard(Long studyId, Long studyBoardId) {
        StudyBoard studyBoard = studyBoardRepository.findById(studyBoardId).orElseThrow(StudyBoardNotFoundException::new);
        studyBoardRepository.delete(studyBoard);
        return StudyBoardDeleteResponseDto.create(studyBoard);
    }
}