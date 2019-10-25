package com.mycorp.arithmeticcalculator.service;

public interface ISecurityUserService {

    String validatePasswordResetToken(long id, String token);

}