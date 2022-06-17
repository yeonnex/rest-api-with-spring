package me.yeonnex.restapi.event;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(JUnitParamsRunner.class)
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

    @ParameterizedTest
    @MethodSource("paramsForTestFree")
    @DisplayName("Event 엔티티에서 비즈니스 로직 점검 - price")
    void testFree(int basePrice, int maxPrice, boolean isFree){
        // Given
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice).build();

        // When
        event.update();

        // Then
        assertThat(event.isFree()).isEqualTo(isFree);
    }

    private static Object[] paramsForTestFree(){
        return new Object[] {
                new Object[] {0, 0, true},
                new Object[] {100, 0, false},
                new Object[] {100, 200, false}
        };
    }
    @Test
    @DisplayName("Event 엔티티에서 비즈니스 로직 점검 - Offline")
    void testOffline(){
        Event event = Event.builder()
                .location("숭실대학교 정보과학관 506호")
                .build();
        event.update();
        assertThat(event.isOffline()).isTrue();
    }


}