package project.SangHyun.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.SangHyun.domain.rediskey.RedisKey;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberEmailAuthRequestDto {
    private String email;
    private String redisKey;
}
