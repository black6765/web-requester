package com.blue.requester.exception;

import com.blue.requester.message.ExceptionMessage;

public class InvalidHttpMethodException extends RuntimeException{

    public InvalidHttpMethodException() {
        super(ExceptionMessage.HTTP_METHOD_UNAVAILABLE);
    }
}
