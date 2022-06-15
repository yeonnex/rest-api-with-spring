package me.yeonnex.restapi.event;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class EventTest {
    @Test
    void builder(){
        Event event = Event.builder()
                .name("spring rest api")
                .description("rest api development with Spring")
                .build();
        assertThat(event).isNotNull();
    }

    @Test
    void javaBean(){
        // Given
        String description = "java 8";
        String name = "spring boot";

        // When
        Event event = new Event();
        event.setName(description);
        event.setDescription(name);

        // Then
        assertThat(event.getDescription()).isEqualTo(name);
        assertThat(event.getName()).isEqualTo(description);
    }
}