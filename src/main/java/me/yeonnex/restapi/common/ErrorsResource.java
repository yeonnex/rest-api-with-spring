package me.yeonnex.restapi.common;

import me.yeonnex.restapi.index.IndexController;
import org.h2.index.Index;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.validation.Errors;

import java.util.Arrays;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class ErrorsResource extends EntityModel<Errors> {
    public ErrorsResource(Errors content, Link... links){
//         super(content, Arrays.asList(links));
        add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
    }
}
