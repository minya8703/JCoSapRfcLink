package jco.jcosaprfclink.dto;

import jco.jcosaprfclink.type.ErrorCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseResult<T> {
	private String message;
	private ErrorCode errorCode;
	private T data;

	public ResponseResult(String message, ErrorCode errorCode) {
		this.message = message;
		this.errorCode = errorCode;
		this.data = null;
	}

	public static <T> ResponseResult<T> success() {
		ResponseResult<T> result = new ResponseResult<>();
		result.message = "처리 완료";
		result.errorCode = ErrorCode.SUCCESS;
		return result;
	}

	public static <T> ResponseResult<T> success(T data) {
		ResponseResult<T> result = new ResponseResult<>();
		result.message = "처리 완료";
		result.errorCode = ErrorCode.SUCCESS;
		result.data = data;
		return result;
	}

	public static <T> ResponseResult<T> error(String message, ErrorCode errorCode) {
		ResponseResult<T> result = new ResponseResult<>();
		result.message = message;
		result.errorCode = errorCode;
		return result;
	}
}