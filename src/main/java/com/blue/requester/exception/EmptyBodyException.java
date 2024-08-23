package com.blue.requester.exception;

import com.blue.requester.message.ExceptionMessage;

public class EmptyBodyException extends RuntimeException {

    public EmptyBodyException() {
        super(ExceptionMessage.BODY_EMPTY);
    }
}
