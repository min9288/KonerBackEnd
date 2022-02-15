package project.SangHyun.study.videoroom.tools;

import project.SangHyun.BasicFactory;
import project.SangHyun.study.dto.MemberProfile;
import project.SangHyun.study.videoroom.controller.dto.request.VideoRoomCreateRequestDto;
import project.SangHyun.study.videoroom.controller.dto.request.VideoRoomUpdateRequestDto;
import project.SangHyun.study.videoroom.controller.dto.response.VideoRoomResponseDto;
import project.SangHyun.study.videoroom.domain.VideoRoom;
import project.SangHyun.study.videoroom.service.dto.request.VideoRoomCreateDto;
import project.SangHyun.study.videoroom.service.dto.request.VideoRoomDeleteDto;
import project.SangHyun.study.videoroom.service.dto.request.VideoRoomUpdateDto;
import project.SangHyun.study.videoroom.service.dto.response.VideoRoomCreateResultDto;
import project.SangHyun.study.videoroom.service.dto.response.VideoRoomDeleteResultDto;
import project.SangHyun.study.videoroom.service.dto.response.VideoRoomDto;
import project.SangHyun.study.videoroom.service.dto.response.VideoRoomUpdateResultDto;
import project.SangHyun.study.videoroom.service.dto.response.result.VideoRoomResult;

public class VideoRoomFactory extends BasicFactory {

    //Request
    public static VideoRoomCreateRequestDto createRequestDto(Long memberId) {
        return new VideoRoomCreateRequestDto("create", memberId, "백엔드 화상회의", "22");
    }

    public static VideoRoomCreateDto createDto(Long memberId) {
        return new VideoRoomCreateDto("create", memberId, "백엔드 화상회의", "22");
    }

    public static VideoRoomUpdateRequestDto updateRequestDto(String title) {
        return new VideoRoomUpdateRequestDto("create", title, "22");
    }

    public static VideoRoomUpdateDto updateDto(String title) {
        return new VideoRoomUpdateDto("edit", title, "22");
    }

    public static VideoRoomDeleteDto deleteDto(Long number) {
        return new VideoRoomDeleteDto("destroy", number);
    }

    //Response
    public static VideoRoomCreateResultDto createResultDto() {
        VideoRoomResult result = new VideoRoomResult(1234L, "백엔드 화상회의");
        return new VideoRoomCreateResultDto("success", "1234", result);
    }

    public static VideoRoomUpdateResultDto updateResultDto() {
        VideoRoomResult result = new VideoRoomResult(1234L, "프론트엔드 화상회의");
        return new VideoRoomUpdateResultDto("success", "1234", result);
    }

    public static VideoRoomDeleteResultDto deleteResultDto() {
        VideoRoomResult result = new VideoRoomResult(1234L, "프론트엔드 화상회의");
        return new VideoRoomDeleteResultDto("success", "1234", result);
    }

    public static VideoRoomDto makeDto(VideoRoom videoRoom) {
        return new VideoRoomDto(1234L, "백엔드 화상회의", "22", MemberProfile.create(videoRoom));
    }

    public static VideoRoomResponseDto makeResponseDto(VideoRoomDto videoRoomDto) {
        return VideoRoomResponseDto.create(videoRoomDto);
    }
}
