package jco.jcosaprfclink.exception;

/**
 * JSON 파싱 관련 예외를 처리하기 위한 커스텀 예외 클래스
 */
public class JsonParsingException extends RuntimeException {
    
    public JsonParsingException(String message) {
        super(message);
    }

    public JsonParsingException(String message, Throwable cause) {
        super(message, cause);
    }
} 