package com.blue.requester.message;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ResponseMessage {
    String msg;
    String status;
    String body;
}
