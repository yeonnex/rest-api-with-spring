package me.yeonnex.restapi.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
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
    void createEvent() throws Exception {
        EventDto event = EventDto.builder()
//                .id(200)
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
//                .isFree(true) // 말이 안되는 값. Dto 사용함으로써 백에서 걸러줄 것.
//                .isOffline(false) // 말이 안되는 값. Dto 사용함으로써 백에서 걸러줄 것.
//                .eventStatus(EventStatus.PUBLISHED) // 말이 안되는 값. Dto 사용함으로써 백에서 걸러줄 것.
                .build();


        // Mockito.when(eventRepository.save(event)).thenReturn(event);

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
                .andExpect(jsonPath("id").value(Matchers.not(200)))
                .andExpect(jsonPath("free").value(Matchers.not(true)))
                .andExpect(jsonPath("eventStatus").value(Matchers.not(EventStatus.PUBLISHED)));
    }

    @Test
    void createBadEvent() throws Exception {
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


}
