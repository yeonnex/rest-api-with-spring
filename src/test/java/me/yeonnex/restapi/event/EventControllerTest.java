package me.yeonnex.restapi.event;

import me.yeonnex.restapi.common.BaseControllerTest;
import me.yeonnex.restapi.common.TestDescription;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class EventControllerTest extends BaseControllerTest {
    @Autowired
    EventRepository eventRepository; // 리포지토리를 모킹하자! 근데, mock 객체이기 때문에 save 를 하더라도 리턴되는 값이 전부  null이다.

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
                .content(objectMapper.writeValueAsString(eventDto)))
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
                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query events"),
                                linkWithRel("update-event").description("link to update an existing event"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description("access header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type")
                        ),
                        requestFields(
                                fieldWithPath("name").description(" Name of new event"),
                                fieldWithPath("description").description("Description of new event"),
                                fieldWithPath("beginEnrollmentDateTime"). description("Begin date of enrollment"),
                                fieldWithPath("beginEventDateTime").description("Begin Date of event"),
                                fieldWithPath("endEventDateTime").description("End Date of event"),
                                fieldWithPath("location").description("Location of event"),
                                fieldWithPath("closeEnrollmentDateTIme").description("Close date of enrollment"),
                                fieldWithPath("basePrice").description("Base Price of event"),
                                fieldWithPath("maxPrice").description("Max Price of event"),
                                fieldWithPath("limitOfEnrollment").description("Limit number of enrollment")
                        ),
                        // 응답헤더 문서화
                        responseHeaders(
                    headerWithName(HttpHeaders.LOCATION).description("Location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        relaxedResponseFields( // 그냥 responseFields 는 아주 엄격하게 응답으로 오는 모든 값에 대해 써주어야 했지만, "relaxed"ResponseFields 는 그 중 일부만 적어주어도 되는 편리함 제공.
                    fieldWithPath("id").description(" Identifier of new event"),
                                fieldWithPath("name").description(" Name of new event"),
                                fieldWithPath("description").description("Description of new event"),
                                fieldWithPath("beginEnrollmentDateTime"). description("Begin date of enrollment"),
                                fieldWithPath("beginEventDateTime").description("Begin Date of event"),
                                fieldWithPath("endEventDateTime").description("End Date of event"),
                                fieldWithPath("location").description("Location of event"),
                                fieldWithPath("closeEnrollmentDateTIme").description("Close date of enrollment"),
                                fieldWithPath("basePrice").description("Base Price of event"),
                                fieldWithPath("maxPrice").description("Max Price of event"),
                                fieldWithPath("limitOfEnrollment").description("Limit number of enrollment"),
                                fieldWithPath("free").description("It tells is this event free or not"),
                                fieldWithPath("offline").description("It tells is this event offline or not"),
                                fieldWithPath("eventStatus").description("It tells is this event offline or not")
                                // 링크정보들" 위에서 이미 따로 해주었으므로 생략함
                        )
                ))
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
                        .content(objectMapper.writeValueAsString(event)))
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
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
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
                .content(objectMapper.writeValueAsString(eventDto)))
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
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
//                .andExpect(jsonPath("$[0].objectName").exists()) // 에러의 "배열" 이 나올 것임.
//                .andExpect(jsonPath("$[0].code").exists())
//                .andExpect(jsonPath("$[0].field").exists())
//                .andExpect(jsonPath("$[0].defaultMessage").exists())
//                .andExpect(jsonPath("$[0].rejectedValue").exists())
        ;
    }

    @Test
    @DisplayName("기존의 이벤트를 하나 조회하기")
    void getEvent() throws Exception {
        Event event = generateEvent(100);
        mockMvc.perform(get("/api/events/{id}", event.getId()))
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("id").exists())
//                .andExpect(jsonPath("_links.self").exists())
//                .andExpect(jsonPath("_links.profile").exists())
        ;
    }

    @Test
    @DisplayName("없는 이벤트를 조회했을 때 404 응답받기")
    void getEvent404() throws Exception {
        mockMvc.perform(get("/api/events/11883"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
    void queryEvents() throws Exception {
        IntStream.range(0, 30).forEach(i -> {
            this.generateEvent(i);
        });
        mockMvc.perform(get("/api/events")
                .param("page", "1")
                .param("size", "10")
                .param("sort", "name,DESC")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("pageable").exists())
                .andDo(document("query-events"));

    }

    @Test
    @DisplayName("이벤트를 정상적으로 수정하기")
    void updateEvent() throws Exception {
        // Given
        Event event = generateEvent(200);
        String eventName = "Updated Event";
        EventDto eventDto = modelMapper.map(event, EventDto.class);
        eventDto.setName(eventName);

        //When & Then
        mockMvc.perform(put("/api/events/{id}", event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto))
                )
                .andDo(print())
                .andExpect(jsonPath("name").value(eventName));
//                .andExpect(jsonPath("_links.self").exists());
    }

    @Test
    @DisplayName("입력값이 비어있는 경우에 이벤트 수정 실패")
    void updateEvent_400_Empty() throws Exception {
        // Given
        Event event = generateEvent(200);
        EventDto eventDto = new EventDto();

        // When & Then
        mockMvc.perform(put("/api/events/{id}", event.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDto))
        )
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("입력값이 잘못된 경우에 이벤트 수정 실패")
    void updateEvent_400_Wrong() throws Exception {
        // Given
        Event event = generateEvent(200);
        EventDto eventDto = modelMapper.map(event, EventDto.class);

        eventDto.setBasePrice(20000);
        eventDto.setMaxPrice(10000);
        // When & Then
        mockMvc.perform(put("/api/events/{id}", event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("존재하지 않는 이벤트 수정 실패")
    void updateEvent404() throws Exception{
        Event event = generateEvent(200);
        EventDto eventDto = modelMapper.map(event, EventDto.class);

        mockMvc.perform(put("/api/events/123214213")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDto))
        )
                .andDo(print())
                .andExpect(status().isNotFound());
    }


    private Event generateEvent(int index) {
        Event event = Event.builder()
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
        return eventRepository.save(event);
    }

}
