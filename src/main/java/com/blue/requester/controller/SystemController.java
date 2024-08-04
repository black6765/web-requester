package com.blue.requester.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class SystemController {

    @Autowired
    private ApplicationContext context;

    public void initiateAppShutdown(int returnCode) {
        log.info("Called application SHUTDOWN operation by user");
        SpringApplication.exit(context, () -> returnCode);
    }

    @RequestMapping("/shutdown")
    public void shutdown() {
        initiateAppShutdown(0);
    }
}
