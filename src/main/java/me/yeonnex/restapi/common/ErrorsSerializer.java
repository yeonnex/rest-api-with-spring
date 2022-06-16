package me.yeonnex.restapi.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.validation.Errors;

import java.io.IOException;

/**
 * 에러에는 두가지 종류가 있다.
 * 1. 필드 에러
 * 2. 글로벌 에러
 */
@JsonComponent // 이렇게 등록을 해줌으로써, ObjectMapper 는 객체를 json 으로 변환시 ErrorSerializer 를 사용하게 된다.
public class ErrorsSerializer extends JsonSerializer<Errors> { // Errors 타입을 위한 시리얼라이저
    @Override
    public void serialize(Errors errors, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        /*
          json 안에는 에러 메시지가 여러개일 것임. 따라서 해야할 일의 순서는 다음과 같다.
          1) 메시지가 여러개니, 배열 쓰기를 시작 - writeStartArray
          2) 배열의 원소마다 jsonObject 로 만들어줄 것이다 - writeStartObject
         */
        jsonGenerator.writeStartArray();
        errors.getFieldErrors().forEach(e -> {
            try {
                jsonGenerator.writeStartObject();

                jsonGenerator.writeStringField("field", e.getField());
                jsonGenerator.writeStringField("objectName", e.getObjectName());
                jsonGenerator.writeStringField("code", e.getCode());
                jsonGenerator.writeStringField("defaultMessage", e.getDefaultMessage());

                if (e.getRejectedValue() != null){ // rejectValue 는 있을 수도 없을수도. 있다면 넣어주자
                   jsonGenerator.writeStringField("rejectedValue", e.getRejectedValue().toString());
                }
                jsonGenerator.writeEndObject();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        errors.getGlobalErrors().forEach(e -> {
            try {
                jsonGenerator.writeStartObject();

                jsonGenerator.writeStringField("objectName", e.getObjectName());
                jsonGenerator.writeStringField("code", e.getCode());
                jsonGenerator.writeStringField("defaultMessage", e.getDefaultMessage());

                jsonGenerator.writeEndObject();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        jsonGenerator.writeEndArray();
    }
}
