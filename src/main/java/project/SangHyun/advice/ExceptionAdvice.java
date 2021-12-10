package project.SangHyun.advice;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import project.SangHyun.advice.exception.*;
import project.SangHyun.domain.response.Result;
import project.SangHyun.domain.service.Impl.ResponseServiceImpl;


@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionAdvice {

    private final ResponseServiceImpl responseService;

    @ExceptionHandler(MemberNicknameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result MemberNicknameAlreadyExistsException() {
        return responseService.getFailureResult(-100, "이미 존재하는 닉네임입니다.");
    }

    @ExceptionHandler(MemberEmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result MemberEmailAlreadyExistsException() {
        return responseService.getFailureResult(-101, "이미 존재하는 이메일입니다.");
    }

    @ExceptionHandler(LoginFailureException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result loginFailureException() {
        return responseService.getFailureResult(-102, "아이디 혹은 비밀번호가 틀립니다.");
    }

    @ExceptionHandler(AuthenticationEntryPointException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result authenticationEntryPointException() {
        return responseService.getFailureResult(-102, "인증이 필요합니다.");
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result accessDeniedException() {
        return responseService.getFailureResult(-103, "권한이 필요합니다.");
    }

    @ExceptionHandler(ExceedMaximumStudyMember.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result ExceedMaximumStudyMember() {
        return responseService.getFailureResult(-104, "스터디 정원을 초과할 수 없습니다.");
    }

    @ExceptionHandler(EmailNotAuthenticatedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result emailAuthenticationException() {
        return responseService.getFailureResult(-105, "이메일 인증이 필요합니다.");
    }

    @ExceptionHandler(EmailAuthTokenNotFountException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result emailAuthTokenNotFountException() {
        return responseService.getFailureResult(-106, "유효하지 않은 인증요청입니다.");
    }

    @ExceptionHandler(InvalidAuthCodeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result invalidAuthCodeException() {
        return responseService.getFailureResult(-107, "유효하지 않은 인증코드입니다.");
    }

    @ExceptionHandler(MemberNotFoundException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result MemberNotFoundException() {
        return responseService.getFailureResult(-108, "존재하지 않는 회원입니다.");
    }

    @ExceptionHandler(InvalidTokenException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result InvalidTokenException() {
        return responseService.getFailureResult(-109, "Token이 유효하지 않습니다.");
    }

    @ExceptionHandler(NotResourceOwnerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result NotResourceOwnerException() {
        return responseService.getFailureResult(-110, "자원에 대한 접근 권한이 없습니다.");
    }

    @ExceptionHandler(MemberSameNickNameException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result MemberSameNickNameException() {
        return responseService.getFailureResult(-111, "중복된 닉네임이 존재합니다.");
    }

    @ExceptionHandler(ChatRoomNotFoundException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result ChatRoomNotFoundException() {
        return responseService.getFailureResult(-112, "이미 존재하는 이메일입니다.");
    }

    @ExceptionHandler(InvalidWebSocketConnection.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result InvalidWebSocketConnection() {
        return responseService.getFailureResult(-113, "인증 후 웹소켓 연결을 진행해야합니다.");
    }

    @ExceptionHandler(StudyNotFoundException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result StudyNotFountException() {
        return responseService.getFailureResult(-114, "존재하지 않는 스터디입니다.");
    }

    @ExceptionHandler(StudyBoardNotFoundException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result StudyBoardNotFoundException() {
        return responseService.getFailureResult(-115, "존재하지 않는 게시판입니다.");
    }

    @ExceptionHandler(AlreadyJoinStudyMember.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result AlreadyJoinStudyMember() {
        return responseService.getFailureResult(-116, "이미 가입한 스터디입니다.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result methodArgumentNotValidException(MethodArgumentNotValidException e) {
        return responseService.getFailureResult(-117, e.getBindingResult().getFieldError().getDefaultMessage());
    }

    @ExceptionHandler(NotBelongStudyMemberException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result NotBelongStudyMemberException() {
        return responseService.getFailureResult(-118, "해당 스터디에 소속되어 있지 않습니다.");
    }

    @ExceptionHandler(StudyHasNoProperRoleException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result StudyHasNoProperRoleException() {
        return responseService.getFailureResult(-119, "해당 스터디의 행동에 대한 권한이 존재하지 않습니다.");
    }

    @ExceptionHandler(StudyArticleNotFoundException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result StudyArticleNotFoundException() {
        return responseService.getFailureResult(-120, "해당 스터디 게시글이 존재하지 않습니다.");
    }
}
