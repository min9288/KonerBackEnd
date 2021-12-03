package project.SangHyun.domain.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.SangHyun.web.dto.ChatRoomCreateRequestDto;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String roomName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public ChatRoom(String roomName, Member member) {
        this.roomName = roomName;
        this.member = member;
    }

    public static ChatRoom createRoom(String roomName, Member member) {
        return ChatRoom.builder()
                .roomName(roomName)
                .member(member)
                .build();
    }
}