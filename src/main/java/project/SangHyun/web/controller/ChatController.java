package project.SangHyun.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import project.SangHyun.domain.dto.ChatFindResponseDto;
import project.SangHyun.domain.dto.ChatMessageResponseDto;
import project.SangHyun.domain.dto.ChatRoomCreateResponseDto;
import project.SangHyun.domain.dto.ChatRoomFindResponseDto;
import project.SangHyun.domain.response.MultipleResult;
import project.SangHyun.domain.response.SingleResult;
import project.SangHyun.domain.service.ChatRoomService;
import project.SangHyun.domain.service.ChatService;
import project.SangHyun.domain.service.ResponseService;
import project.SangHyun.web.dto.ChatMessageRequestDto;
import project.SangHyun.web.dto.ChatRoomCreateRequestDto;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatRoomService chatRoomService;
    private final ChatService chatService;
    private final ResponseService responseService;

    @MessageMapping("/chat/{roomId}")
    @SendTo("/sub/{roomId}")
    public ChatMessageResponseDto sendChat(ChatMessageRequestDto message) {
        return chatService.createChat(message);
    }

    @GetMapping("/room")
    public MultipleResult<ChatRoomFindResponseDto> findAllRooms() {
        return responseService.getMultipleResult(chatRoomService.findAllRooms());
    }

    @PostMapping("/room")
    public SingleResult<ChatRoomCreateResponseDto> createRoom(@RequestBody ChatRoomCreateRequestDto requestDto) {
        ChatRoomCreateResponseDto room = chatRoomService.createRoom(requestDto);
        return responseService.getSingleResult(room);
    }

    @GetMapping("/room/{roomId}")
    public MultipleResult<ChatFindResponseDto> findAllChats(@PathVariable Long roomId) {
        return responseService.getMultipleResult(chatService.findAllChats(roomId));
    }
}