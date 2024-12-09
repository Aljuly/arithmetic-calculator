package com.mycorp.arithmeticcalculator.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mycorp.arithmeticcalculator.domain.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);

    List<Role> findByIdIn(List<Long> ids);
    
    List<Role> findByNameIn(List<String> names);
}
