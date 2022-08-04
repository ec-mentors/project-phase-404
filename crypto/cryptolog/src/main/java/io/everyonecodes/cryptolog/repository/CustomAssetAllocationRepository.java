package io.everyonecodes.cryptolog.repository;

import io.everyonecodes.cryptolog.data.CustomAssetAllocation;
import io.everyonecodes.cryptolog.data.User;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Configuration
public interface CustomAssetAllocationRepository extends JpaRepository<CustomAssetAllocation, Long> {
    Optional<CustomAssetAllocation> findByCustomAllocationNameAndUser(String allocation, User user);
}
