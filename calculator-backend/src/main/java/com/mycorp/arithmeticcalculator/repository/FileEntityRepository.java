package com.mycorp.arithmeticcalculator.repository;

import org.springframework.data.repository.CrudRepository;
import com.mycorp.arithmeticcalculator.domain.FileEntity;

public interface FileEntityRepository extends CrudRepository<FileEntity, String> {

}
