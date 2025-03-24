package fr.insee.sabianedata.ws.controller.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.webjars.NotFoundException;

@ControllerAdvice
@Slf4j
public class AppExceptionController {

    //TODO migrate to an exception bubbling pattern

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> exception(Exception exception) {
        String exceptionMessage = String.format("Unknown error happened : %s", exception.getMessage());
        log.warn(exceptionMessage);
        return new ResponseEntity<>(exceptionMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<Object> entityNotFound(Exception exception) {
        String exceptionMessage =  exception.getMessage();
        log.warn(exceptionMessage);
        return new ResponseEntity<>(exceptionMessage, HttpStatus.NOT_FOUND);
    }


}