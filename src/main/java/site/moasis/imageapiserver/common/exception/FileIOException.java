package site.moasis.imageapiserver.common.exception;

import lombok.Getter;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

@Getter
public class FileIOException extends IOException {
    private ErrorCode errorCode;
    private String path;
    private List<String> arguments;

    public FileIOException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public FileIOException(ErrorCode errorCode, String path, List<String> arguments) {
        this.errorCode = errorCode;
        this.path = path;
        this.arguments = arguments;
    }

    public FileIOException(ErrorCode errorCode, String path, List<String> arguments, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
        this.path = path;
        this.arguments = arguments;
    }

    /**
     * {@link FileIOException#errorCode} 와 {@link FileIOException#path}를 조합해 오류 메시지를 찾기 위해 사용되는 검색 키 code를 반환한다.
     * 예를 들어 errorCode가 ErrorCode.NOT_FOUND 이고 path가 board.byId인 경우 "notFound.board.byId"라는 코드의 오류 메시지를 찾는다.
     * 오류 메시지에 플레이스 홀더가 있는 경우 해당 플레이스 홀더를 arguments가 가지고 있는 값으로 순서대로 치환한다.
     *
     * @return 오류 메시지를 찾기 위한 code
     */
    public String getCode() {
        return StringUtils.hasText(path) ? errorCode.getCode() + '.' + path : errorCode.getCode();
    }
}
