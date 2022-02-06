package project.SangHyun.study.study.service.unit;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import project.SangHyun.common.dto.SliceResponseDto;
import project.SangHyun.common.helper.FileStoreHelper;
import project.SangHyun.member.domain.Member;
import project.SangHyun.study.study.domain.Study;
import project.SangHyun.study.study.domain.StudyCategory;
import project.SangHyun.study.study.dto.request.StudyCreateRequestDto;
import project.SangHyun.study.study.dto.request.StudyUpdateRequestDto;
import project.SangHyun.study.study.dto.response.StudyCreateResponseDto;
import project.SangHyun.study.study.dto.response.StudyDeleteResponseDto;
import project.SangHyun.study.study.dto.response.StudyFindResponseDto;
import project.SangHyun.study.study.dto.response.StudyUpdateResponseDto;
import project.SangHyun.study.study.repository.StudyRepository;
import project.SangHyun.study.study.service.impl.StudyServiceImpl;
import project.SangHyun.study.study.tools.StudyFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;


@ExtendWith(MockitoExtension.class)
class StudyServiceUnitTest {
    Member member;
    Study study;

    @InjectMocks
    StudyServiceImpl studyService;
    @Mock
    StudyRepository studyRepository;
    @Mock
    FileStoreHelper fileStoreHelper;

    @BeforeEach
    public void init() {
        member = StudyFactory.makeTestAuthMember();
        study = StudyFactory.makeTestStudy(member, new ArrayList<>(), new ArrayList<>());
    }

    @Test
    @DisplayName("스터디를 생성한다.")
    public void createStudy() throws Exception {
        //given
        StudyCreateRequestDto requestDto = StudyFactory.makeCreateRequestDto(member);
        Study createdStudy = requestDto.toEntity(fileStoreHelper.storeFile(requestDto.getProfileImg()));
        StudyCreateResponseDto ExpectResult = StudyFactory.makeCreateResponseDto(createdStudy);

        //mocking
        given(studyRepository.save(any())).willReturn(createdStudy);

        //when
        StudyCreateResponseDto ActualResult = studyService.createStudy(requestDto);

        //then
        Assertions.assertEquals(ExpectResult.getTitle(), ActualResult.getTitle());
        Assertions.assertEquals(ExpectResult.getHeadCount(), 2L);
    }

    @Test
    @DisplayName("모든 스터디 정보를 로드한다.")
    public void loadStudyInfo() throws Exception {
        //given
        Slice slice = new SliceImpl(List.of(study), Pageable.ofSize(6), false);
        SliceResponseDto ExpectResult = StudyFactory.makeFindAllResponseDto(slice);

        //mocking
        given(studyRepository.findAllOrderByStudyIdDesc(Long.MAX_VALUE, StudyCategory.CSE, Pageable.ofSize(6))).willReturn(slice);

        //
        SliceResponseDto ActualResult = studyService.findAllStudiesByDepartment(Long.MAX_VALUE, StudyCategory.CSE, 6);

        //then
        Assertions.assertEquals(ExpectResult.getNumberOfElements(), ActualResult.getNumberOfElements());
        Assertions.assertEquals(ExpectResult.getData(), ActualResult.getData());
    }

    @Test
    @DisplayName("스터디에 대한 세부정보를 로드한다.")
    public void loadStudyDetail() throws Exception {
        //given
        StudyFindResponseDto ExpectResult = StudyFactory.makeFindResponseDto(study);

        //mocking
        given(studyRepository.findStudyById(study.getId())).willReturn(study);

        //when
        StudyFindResponseDto ActualResult = studyService.findStudy(study.getId());

        //then
        Assertions.assertEquals(ExpectResult.getStudyId(), ActualResult.getStudyId());
        Assertions.assertEquals(ExpectResult.getHeadCount(), 2L);
    }

    @Test
    @DisplayName("스터디의 정보를 업데이트한다.")
    public void updateStudy() throws Exception {
        //given
        StudyUpdateRequestDto updateRequestDto = StudyFactory.makeUpdateRequestDto("테스트 스터디 변경", List.of("프론트엔드"));

        //mocking
        given(studyRepository.findById(study.getId())).willReturn(Optional.ofNullable(study));

        //when
        StudyUpdateResponseDto ActualResult = studyService.updateStudy(study.getId(), updateRequestDto);

        //then
        Assertions.assertEquals("테스트 스터디 변경", ActualResult.getTitle());
        Assertions.assertEquals("프론트엔드", ActualResult.getTags().get(0));
    }

    @Test
    @DisplayName("스터디의 정보를 삭제한다.")
    public void deleteStudy() throws Exception {
        //given
        StudyDeleteResponseDto ExpectResult = StudyDeleteResponseDto.create(study);

        //mocking
        given(studyRepository.findById(study.getId())).willReturn(Optional.ofNullable(study));
        willDoNothing().given(studyRepository).delete(study);

        //when
        StudyDeleteResponseDto ActualResult = studyService.deleteStudy(study.getId());

        //then
        Assertions.assertEquals(ExpectResult.getTitle(), ActualResult.getTitle());
        Assertions.assertEquals(ExpectResult.getStudyId(), ActualResult.getStudyId());
    }
}