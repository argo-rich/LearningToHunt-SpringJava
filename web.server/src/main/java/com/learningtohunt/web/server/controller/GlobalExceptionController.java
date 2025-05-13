package com.learningtohunt.web.server.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;

@Slf4j
@ControllerAdvice(annotations = Controller.class)
public class GlobalExceptionController {

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(Exception exception) throws IOException {
        System.out.println("handleNotFound() called");
        return "forward:/";
    }

    /*
    @ExceptionHandler will register the given method for a given
    exception type, so that ControllerAdvice can invoke this method
    logic if a given exception type is thrown inside the web application.
    * */
    @ExceptionHandler({Exception.class})
    public ModelAndView exceptionHandler(Exception exception){
        String errorMsg = null;
        ModelAndView errorPage = new ModelAndView();
        errorPage.setViewName("error");
        if(exception.getMessage()!=null){
            errorMsg = exception.getMessage();
        }else if (exception.getCause()!=null){
            errorMsg = exception.getCause().toString();
        }else if (exception!=null){
            errorMsg = exception.toString();
        }
        errorPage.addObject("errormsg", errorMsg);
        System.out.println("exceptionHandler() called. Exception: " + exception);
        return errorPage;
    }

}

