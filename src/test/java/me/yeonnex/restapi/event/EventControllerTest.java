package me.yeonnex.restapi.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class) // 슬라이스 테스트이기때문에, 웹용 빈만 등록해줄 뿐 리포지토리 빈은 등록해주지 않음.
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    EventRepository eventRepository; // 리포지토리를 모킹하자! 근데, mock 객체이기 때문에 save 를 하더라도 리턴되는 값이 전부  null이다.

    @Autowired
    ObjectMapper mapper;
    @Test
    void createEvent() throws Exception {
        Event event = Event.builder()
                .name("Rest api")
                .description("Rest api with Spring")
                .beginEventDateTime(LocalDateTime.now())
                .endEventDateTime(LocalDateTime.now())
                .build();
        event.setId(24);
        Mockito.when(eventRepository.save(event)).thenReturn(event);

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                .content(mapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated())
//                .andExpect(header().exists("Location"))
//                .andExpect(header().string("Content-Type", "application/hal+json"))
                // 타입세이프한 방법
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, HAL_JSON_VALUE))
                .andExpect(jsonPath("id").exists());
    }


}
