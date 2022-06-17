package me.yeonnex.restapi.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.yeonnex.restapi.common.TestDescription;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    EventRepository eventRepository; // 리포지토리를 모킹하자! 근데, mock 객체이기 때문에 save 를 하더라도 리턴되는 값이 전부  null이다.

    @Autowired
    ObjectMapper mapper;
    @Test
    @TestDescription("정석대로 이벤트 생성하기")
    void createEvent() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Rest api")
                .description("Rest api with Spring")
                .beginEnrollmentDateTime(LocalDateTime.now())
                .closeEnrollmentDateTIme(LocalDateTime.now().plusDays(7))
                .beginEventDateTime(LocalDateTime.now().plusDays(14))
                .endEventDateTime(LocalDateTime.now().plusDays(21))
                .basePrice(100)
                .maxPrice(200)
                .location("낙성대 오렌지연필")
                .limitOfEnrollment(100)
                .build();



        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                .content(mapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, HAL_JSON_VALUE))
                .andExpect(jsonPath("id").value(Matchers.not(200)))
                .andExpect(jsonPath("free").value(Matchers.not(true)))
                .andExpect(jsonPath("eventStatus").value(Matchers.not(EventStatus.PUBLISHED)))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-event").exists())
        ;
    }

    @Test
    @TestDescription("입력 받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
    void createEvent_Bad_Request_Event_Unknown_properties() throws Exception {
        Event event = Event.builder()
                .id(200) // EventDto 에 없는 필드
                .name("Rest api")
                .description("Rest api with Spring")
                .beginEnrollmentDateTime(LocalDateTime.now())
                .closeEnrollmentDateTIme(LocalDateTime.now().plusDays(7))
                .beginEventDateTime(LocalDateTime.now().plusDays(14))
                .endEventDateTime(LocalDateTime.now().plusDays(21))
                .basePrice(100)
                .maxPrice(200)
                .location("낙성대 오렌지연필")
                .isFree(true) // EventDto 에 없는 필드
                .isOffline(false) // EventDto 에 없는 필드
                .eventStatus(EventStatus.PUBLISHED) // EventDto 에 없는 필드
                .build();


        // Mockito.when(eventRepository.save(event)).thenReturn(event);

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(mapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("입력값이 비어있는 경우에 에러가 발생하는 테스트")
    void createEvent_Bad_Request_Event_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();
        this.mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(mapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("입력값이 잘못된 경우에 에러가 발생하는 테스")
    void createEvent_Bad_Request_Event_Wrong() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Rest api")
                .description("Rest api with Spring")
                .beginEnrollmentDateTime(LocalDateTime.now())
                .closeEnrollmentDateTIme(LocalDateTime.now().minusDays(7))
                .beginEventDateTime(LocalDateTime.now().plusDays(14))
                .endEventDateTime(LocalDateTime.now().plusDays(7))
                .basePrice(100000)
                .maxPrice(200)
                .location("낙성대 오렌지연필")
                .limitOfEnrollment(100)
                .build();

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(HAL_JSON)
                .content(mapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("응답 본문에 에러에 대한 정보를 담아 반환")
    void createEvent_Bad_Request_Event_Return_Body() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Rest api")
                .description("Rest api with Spring")
                .beginEnrollmentDateTime(LocalDateTime.now())
                .closeEnrollmentDateTIme(LocalDateTime.now().minusDays(7))
                .beginEventDateTime(LocalDateTime.now().plusDays(14))
                .endEventDateTime(LocalDateTime.now().plusDays(7))
                .basePrice(100000)
                .maxPrice(200)
                .location("낙성대 오렌지연필")
                .limitOfEnrollment(100)
                .build();

        this.mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(HAL_JSON)
                        .content(mapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].objectName").exists()) // 에러의 "배열" 이 나올 것임.
                .andExpect(jsonPath("$[0].code").exists())
                .andExpect(jsonPath("$[0].field").exists())
                .andExpect(jsonPath("$[0].defaultMessage").exists())
                .andExpect(jsonPath("$[0].rejectedValue").exists())
        ;
    }
}
