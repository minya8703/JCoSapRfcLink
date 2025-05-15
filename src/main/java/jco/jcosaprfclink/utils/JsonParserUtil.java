package jco.jcosaprfclink.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jco.jcosaprfclink.exception.JsonParsingException;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * JSON 데이터 변환을 위한 유틸리티 클래스
 * 
 * @author JCoSapRfcLink
 * @version 1.0
 */
@Slf4j
public class JsonParserUtil {
    private static final ObjectMapper objectMapper;
    private static final JSONParser jsonParser;
    static {
        objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        jsonParser = new JSONParser();
        new ConcurrentHashMap<>();
    }

    /**
     * Map을 JSONString으로 변환
     *
     * @param map 변환할 Map 객체
     * @return JSON 문자열
     * @throws JsonParsingException JSON 변환 중 오류 발생 시
     * @throws IllegalArgumentException map이 null인 경우
     */
    @SuppressWarnings("unchecked")
    public static String getJsonStringFromMap(Map<String, Object> map) {
        validateNotNull(map, "Map");
        try {
            return objectMapper.writeValueAsString(map);
        } catch (IOException e) {
            log.error("Map을 JSON 문자열로 변환 실패: {}", e.getMessage());
            throw new JsonParsingException("Map을 JSON 문자열로 변환 실패", e);
        }
    }

    /**
     * Map을 JSONObject로 변환
     *
     * @param map 변환할 Map 객체
     * @return JSONObject
     * @throws JsonParsingException JSON 변환 중 오류 발생 시
     * @throws IllegalArgumentException map이 null인 경우
     */
    @SuppressWarnings("unchecked")
    public static JSONObject getJsonObjectFromMap(Map<String, Object> map) {
        validateNotNull(map, "Map");
        try {
            String jsonString = objectMapper.writeValueAsString(map);
            return (JSONObject) jsonParser.parse(jsonString);
        } catch (IOException | ParseException e) {
            log.error("Map을 JSONObject로 변환 실패: {}", e.getMessage());
            throw new JsonParsingException("Map을 JSONObject로 변환 실패", e);
        }
    }

    /**
     * List<Map>을 JSONString으로 변환
     *
     * @param list 변환할 List<Map> 객체
     * @return JSON 문자열
     * @throws JsonParsingException JSON 변환 중 오류 발생 시
     * @throws IllegalArgumentException list가 null인 경우
     */
    @SuppressWarnings("unchecked")
    public static String getJsonStringFromList(List<Map<String, Object>> list) {
        validateNotNull(list, "List");
        try {
            return objectMapper.writeValueAsString(list);
        } catch (IOException e) {
            log.error("List를 JSON 문자열로 변환 실패: {}", e.getMessage());
            throw new JsonParsingException("List를 JSON 문자열로 변환 실패", e);
        }
    }

    /**
     * String을 JSONArray로 변환
     *
     * @param str 변환할 JSON 문자열
     * @return JSONArray
     * @throws JsonParsingException JSON 파싱 중 오류 발생 시
     * @throws IllegalArgumentException str이 null이거나 빈 문자열인 경우
     */
    public static JSONArray getJsonArrayFromString(String str) {
        validateNotEmpty(str, "JSON String");
        try {
            return (JSONArray) jsonParser.parse(str);
        } catch (ParseException e) {
            log.error("문자열을 JSONArray로 변환 실패: {}", e.getMessage());
            throw new JsonParsingException("문자열을 JSONArray로 변환 실패", e);
        }
    }

    /**
     * List<Map>을 JSONArray로 변환
     *
     * @param list 변환할 List<Map> 객체
     * @return JSONArray
     * @throws JsonParsingException JSON 변환 중 오류 발생 시
     * @throws IllegalArgumentException list가 null인 경우
     */
    public static JSONArray getJsonArrayFromList(List<Map<String, Object>> list) {
        validateNotNull(list, "List");
        try {
            String jsonString = objectMapper.writeValueAsString(list);
            return (JSONArray) jsonParser.parse(jsonString);
        } catch (IOException | ParseException e) {
            log.error("List를 JSONArray로 변환 실패: {}", e.getMessage());
            throw new JsonParsingException("List를 JSONArray로 변환 실패", e);
        }
    }

    /**
     * String을 JSONObject로 변환
     *
     * @param jsonStr 변환할 JSON 문자열
     * @return JSONObject
     * @throws JsonParsingException JSON 파싱 중 오류 발생 시
     * @throws IllegalArgumentException jsonStr이 null이거나 빈 문자열인 경우
     */
    public static JSONObject getJsonObjectFromString(String jsonStr) {
        validateNotEmpty(jsonStr, "JSON String");
        try {
            return (JSONObject) jsonParser.parse(jsonStr);
        } catch (ParseException e) {
            log.error("문자열을 JSONObject로 변환 실패: {}", e.getMessage());
            throw new JsonParsingException("문자열을 JSONObject로 변환 실패", e);
        }
    }

    /**
     * JSONObject를 Map으로 변환
     *
     * @param jsonObject 변환할 JSONObject
     * @return Map<String, Object>
     * @throws JsonParsingException JSON 변환 중 오류 발생 시
     * @throws IllegalArgumentException jsonObject가 null인 경우
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getMapFromJsonObject(JSONObject jsonObject) {
        validateNotNull(jsonObject, "JSONObject");
        try {
            return objectMapper.readValue(jsonObject.toJSONString(), Map.class);
        } catch (IOException e) {
            log.error("JSONObject를 Map으로 변환 실패: {}", e.getMessage());
            throw new JsonParsingException("JSONObject를 Map으로 변환 실패", e);
        }
    }

    /**
     * JSONArray를 List<Map>으로 변환
     *
     * @param jsonArray 변환할 JSONArray
     * @return List<Map<String, Object>>
     * @throws JsonParsingException JSON 변환 중 오류 발생 시
     * @throws IllegalArgumentException jsonArray가 null인 경우
     */
    public static List<Map<String, Object>> getListMapFromJsonArray(JSONArray jsonArray) {
        validateNotNull(jsonArray, "JSONArray");
        try {
            return objectMapper.readValue(jsonArray.toJSONString(), 
                objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class));
        } catch (IOException e) {
            log.error("JSONArray를 List<Map>으로 변환 실패: {}", e.getMessage());
            throw new JsonParsingException("JSONArray를 List<Map>으로 변환 실패", e);
        }
    }

    /**
     * String을 Map<String, String>으로 변환
     *
     * @param str 변환할 문자열 (key=value,key=value 형식)
     * @return Map<String, String>
     * @throws JsonParsingException 문자열 파싱 중 오류 발생 시
     * @throws IllegalArgumentException str이 null이거나 빈 문자열인 경우
     */
    public static Map<String, String> getMapFromString(String str) {
        validateNotEmpty(str, "String");
        try {
            return Arrays.stream(str.replace("{","").replace("}","").split(","))
                    .map(entry -> entry.split("="))
                    .collect(Collectors.toMap(
                        entry -> entry[0].trim(),
                        entry -> entry[1].trim(),
                        (v1, v2) -> v1 // 중복 키가 있을 경우 첫 번째 값 유지
                    ));
        } catch (Exception e) {
            log.error("문자열을 Map으로 변환 실패: {}", e.getMessage());
            throw new JsonParsingException("문자열을 Map으로 변환 실패", e);
        }
    }

    /**
     * JSON 문자열을 지정된 타입의 객체로 변환
     *
     * @param <T> 변환할 객체의 타입
     * @param json JSON 문자열
     * @param clazz 변환할 클래스
     * @return 변환된 객체
     * @throws JsonParsingException JSON 파싱 중 오류 발생 시
     * @throws IllegalArgumentException json이 null이거나 빈 문자열인 경우
     */
    public static <T> T parseJson(String json, Class<T> clazz) {
        validateNotEmpty(json, "JSON String");
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            log.error("JSON을 {} 타입으로 변환 실패: {}", clazz.getSimpleName(), e.getMessage());
            throw new JsonParsingException("JSON 파싱 실패", e);
        }
    }

    /**
     * 객체를 JSON 문자열로 변환
     *
     * @param obj 변환할 객체
     * @return JSON 문자열
     * @throws JsonParsingException JSON 변환 중 오류 발생 시
     * @throws IllegalArgumentException obj가 null인 경우
     */
    public static String toJson(Object obj) {
        validateNotNull(obj, "Object");
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (IOException e) {
            log.error("객체를 JSON으로 변환 실패: {}", e.getMessage());
            throw new JsonParsingException("JSON 변환 실패", e);
        }
    }

    /**
     * null 체크
     *
     * @param obj 체크할 객체
     * @param name 객체 이름
     * @throws IllegalArgumentException obj가 null인 경우
     */
    private static void validateNotNull(Object obj, String name) {
        if (obj == null) {
            throw new IllegalArgumentException(name + " cannot be null");
        }
    }

    /**
     * 빈 문자열 체크
     *
     * @param str 체크할 문자열
     * @param name 문자열 이름
     * @throws IllegalArgumentException str이 null이거나 빈 문자열인 경우
     */
    private static void validateNotEmpty(String str, String name) {
        if (!StringUtils.hasText(str)) {
            throw new IllegalArgumentException(name + " cannot be null or empty");
        }
    }
}
