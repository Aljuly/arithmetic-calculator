package com.mycorp.arithmeticcalculator.domain;

import javax.persistence.Column;

public class User {
    // ...
    @Column(name = "enabled")
    private boolean enabled;
     
    public User() {
        super();
        this.enabled=false;
    }
    // ...
}
