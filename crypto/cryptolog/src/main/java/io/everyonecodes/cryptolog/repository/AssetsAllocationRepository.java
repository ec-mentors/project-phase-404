package io.everyonecodes.cryptolog.repository;

import io.everyonecodes.cryptolog.data.AssetsAllocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssetsAllocationRepository extends JpaRepository<AssetsAllocation, Long> {
    Optional<AssetsAllocation> findByAllocationName(String allocationName);

}
