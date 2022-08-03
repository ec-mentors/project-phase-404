package io.everyonecodes.cryptolog.repository;

import io.everyonecodes.cryptolog.data.CustomAssetAllocation;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;

@Configuration
public interface CustomAssetAllocationRepository extends JpaRepository<CustomAssetAllocation, Long> {
}
