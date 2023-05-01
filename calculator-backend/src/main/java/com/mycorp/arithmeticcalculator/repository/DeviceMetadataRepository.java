package com.mycorp.arithmeticcalculator.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mycorp.arithmeticcalculator.domain.DeviceMetadata;

import java.util.List;

public interface DeviceMetadataRepository extends JpaRepository<DeviceMetadata, Long> {

    List<DeviceMetadata> findByUserId(Long userId);
}
