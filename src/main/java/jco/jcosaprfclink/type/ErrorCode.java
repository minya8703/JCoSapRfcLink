package jco.jcosaprfclink.type;

import lombok.Getter;

@Getter
public enum ErrorCode {
    SUCCESS("S000", "처리 완료"),
    INTERNAL_SERVER_ERROR("E000", "내부 서버 오류가 발생하였습니다."),
    INVALID_REQUEST("E001", "잘못된 요청입니다."),
    INPUT_DATA_NOT_FOUND("E002", "입력 데이터가 없습니다."),
    INPUT_DATA_NOT_JSON("E003", "입력 데이터가 Json형식이 아닙니다."),
    SAP_DATA_FIELD_ERROR("E004", "SAP 전달 데이터에 오류가 발생하였습니다."),
    INPUT_DATA_NULL_POINTER("E005", "입력 값이 없습니다."),
    POPBILL_SERVER_ERROR("E006", "POPBILL 세금 계산서 전송 서버 오류가 발생하였습니다."),
    INPUT_DATA_PARSE_ERROR("E007", "데이터 변환 에러"),
    NOT_CREATE_FILE_ERROR("E008", "대상 파일을 만들 수 없습니다."),
    NOT_CREATE_SAP_SERVER_ERROR("E009", "서버를 만들 수 없습니다."),
    
    /**
     * ******************************* Global Error CodeList ***************************************
     * HTTP Status Code
     * 400 : Bad Request
     * 401 : Unauthorized
     * 403 : Forbidden
     * 404 : Not Found
     * 500 : Internal Server Error
     * *********************************************************************************************
     */
    // 잘못된 서버 요청400
    BAD_REQUEST_ERROR("E010", "Bad Request Exception"),

    // @RequestBody 데이터 미 존재400
    REQUEST_BODY_MISSING_ERROR("E011", "Required request body is missing"),

    // 유효하지 않은 타입400
    INVALID_TYPE_VALUE("E012", "Invalid Type Value"),

    // Request Parameter 로 데이터가 전달되지 않을 경우400
    MISSING_REQUEST_PARAMETER_ERROR("E013", "Missing Servlet RequestParameter Exception"),

    // 입력/출력 값이 유효하지 않음400
    IO_ERROR("E014", "입출력 오류가 발생했습니다."),

    // com.google.gson JSON 파싱 실패400
    JSON_PARSE_ERROR("E015", "JsonParseException"),

    // com.fasterxml.jackson.core Processing Error400
    JACKSON_PROCESS_ERROR("E016", "com.fasterxml.jackson.core Exception"),

    // 권한이 없음403
    FORBIDDEN_ERROR("E017", "Forbidden Exception"),

    // 서버로 요청한 리소스가 존재하지 않음404
    NOT_FOUND_ERROR("E018", "Not Found Exception"),

    // NULL Point Exception 발생404
    NULL_POINT_ERROR("E019", "Null Point Exception"),

    // @RequestBody 및 @RequestParam, @PathVariable 값이 유효하지 않음404
    NOT_VALID_ERROR("E020", "handle Validation Exception"),

    // @RequestBody 및 @RequestParam, @PathVariable 값이 유효하지 않음404
    NOT_VALID_HEADER_ERROR("E021", "Header에 데이터가 존재하지 않는 경우"),

    /**
     * ******************************* Custom Error CodeList ***************************************
     * 200
     */
    // Transaction Insert Error
    INSERT_ERROR("E022", "Insert Transaction Error Exception"),

    // Transaction Update Error
    UPDATE_ERROR("E023", "Update Transaction Error Exception"),

    // Transaction Delete Error
    DELETE_ERROR("E024", "Delete Transaction Error Exception"),

    // 인증이 필요함401
    UNAUTHORIZED("E025", "인증이 필요합니다."),

    // 접근 권한이 없음403
    FORBIDDEN("E026", "접근 권한이 없습니다."),

    // 요청한 리소스를 찾을 수 없음404
    NOT_FOUND("E027", "요청한 리소스를 찾을 수 없습니다."),

    // 요청 시간이 초과되었음408
    REQUEST_TIMEOUT("E028", "요청 시간이 초과되었습니다."),

    // 서비스를 사용할 수 없음503
    SERVICE_UNAVAILABLE("E029", "서비스를 사용할 수 없습니다.");

    private final String code;
    private final String description;

    ErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getMessage() {
        return description;
    }
}
