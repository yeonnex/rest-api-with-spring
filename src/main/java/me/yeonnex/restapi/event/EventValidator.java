package me.yeonnex.restapi.event;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

/**
 * 이벤트 입력값을 검증한다. <br/>
 * 1. basePrice 와 maxPrice<br/>
 * 2. 이벤트 시작일시보다 이벤트 종료일시가 더 빠른 경우<br/>
 *
 * errors.reject - 글로벌 에러로 처리<br/>
 * errors.rejectValue - 필드 에러로 처리
 */
@Component
public class EventValidator {
    public void validate(EventDto eventDto, Errors errors){
        if (((eventDto.getMaxPrice()) > 0) && (eventDto.getBasePrice() > eventDto.getMaxPrice())){ // 값 최대값이 0 보다 큰데,  base 가 max 보다 큰 이상한 상황일 경우. 비즈니스 로직에 위배되는 경우임.
//            errors.rejectValue("basePrice", "wrongValue", "basePrice is wrong");
//            errors.rejectValue("maxPrice", "wrongValue", "maxPrice is wrong");
            // 글로벌 에러
            errors.reject("wrongPrices", "Values for prices are wrong!");
        }

        LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
        if (endEventDateTime.isBefore(eventDto.getBeginEventDateTime() )||
            endEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTIme()))
            {
            // 필드 에러
            errors.rejectValue("endEventDateTime", "wrongValue", "endEventTime is earlier then beginEventTime");
        }

        //TODO BeginEventDateTime
        //TODO CloseEnrollmentDateTime
    }
}
