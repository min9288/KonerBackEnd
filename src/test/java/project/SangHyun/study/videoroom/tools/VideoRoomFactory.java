package project.SangHyun.study.videoroom.tools;

import project.SangHyun.BasicFactory;
import project.SangHyun.study.videoroom.service.dto.request.VideoRoomCreateDto;
import project.SangHyun.study.videoroom.service.dto.request.VideoRoomDeleteDto;
import project.SangHyun.study.videoroom.service.dto.request.VideoRoomUpdateDto;
import project.SangHyun.study.videoroom.service.dto.response.VideoRoomCreateResultDto;
import project.SangHyun.study.videoroom.service.dto.response.VideoRoomCreateResultDto.VideoRoomCreateResult;
import project.SangHyun.study.videoroom.service.dto.response.VideoRoomDeleteResultDto;
import project.SangHyun.study.videoroom.service.dto.response.VideoRoomDeleteResultDto.VideoRoomDestroyResult;
import project.SangHyun.study.videoroom.service.dto.response.VideoRoomUpdateResultDto;
import project.SangHyun.study.videoroom.service.dto.response.VideoRoomUpdateResultDto.VideoRoomEditResult;

public class VideoRoomFactory extends BasicFactory {

    public static VideoRoomCreateDto createDto() {
        return new VideoRoomCreateDto("create", "백엔드 화상회의", "22");
    }

    public static VideoRoomUpdateDto updateDto() {
        return new VideoRoomUpdateDto("edit", "프론트엔드 화상회의", "22");
    }

    public static VideoRoomDeleteDto deleteDto(Long number) {
        return new VideoRoomDeleteDto("destroy", number);
    }

    public static VideoRoomCreateResultDto createResultDto() {
        VideoRoomCreateResult result = new VideoRoomCreateResult(1234L, "백엔드 화상회의");
        return new VideoRoomCreateResultDto("success", "1234", result);
    }

    public static VideoRoomUpdateResultDto updateResultDto() {
        VideoRoomEditResult result = new VideoRoomEditResult(1234L, "프론트엔드 화상회의");
        return new VideoRoomUpdateResultDto("success", "1234", result);
    }

    public static VideoRoomDeleteResultDto deleteResultDto() {
        VideoRoomDestroyResult result = new VideoRoomDestroyResult(1234L, "프론트엔드 화상회의");
        return new VideoRoomDeleteResultDto("success", "1234", result);
    }
}
