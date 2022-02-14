package project.SangHyun.study.videoroom.service.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("화상회의 방 파괴 결과 Janus Response DTO")
public class VideoRoomDeleteResultDto {

    @ApiModelProperty(name = "Janus 성공 여부")
    private String janus;

    @ApiModelProperty(name = "Janus Transaction ID")
    private String transaction;

    @ApiModelProperty(name = "화상회의 방 파괴 결과")
    private VideoRoomDestroyResult result;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VideoRoomDestroyResult {
        private Long room;
        private String videoroom;
    }
}
