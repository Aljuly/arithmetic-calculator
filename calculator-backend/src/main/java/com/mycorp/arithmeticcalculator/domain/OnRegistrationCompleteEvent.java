package com.mycorp.arithmeticcalculator.domain;

import java.util.Locale;

import org.springframework.context.ApplicationEvent;

import lombok.Data;

public class OnRegistrationCompleteEvent extends ApplicationEvent {
    private String appUrl;
    private Locale locale;
    private User user;
 
    public OnRegistrationCompleteEvent(
      User user, Locale locale, String appUrl) {
        super(user);
         
        this.user = user;
        this.locale = locale;
        this.appUrl = appUrl;
    }
     
    // standard getters and setters
}