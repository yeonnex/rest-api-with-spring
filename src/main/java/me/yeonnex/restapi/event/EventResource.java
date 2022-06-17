package me.yeonnex.restapi.event;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.RepresentationModel;

public class EventResource extends RepresentationModel {
    @JsonUnwrapped
    private final Event event;

    public EventResource(Event event){
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }
}
