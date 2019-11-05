package com.mycorp.arithmeticcalculator.security;

public interface ISecurityUserService {

    String validatePasswordResetToken(long id, String token);

}