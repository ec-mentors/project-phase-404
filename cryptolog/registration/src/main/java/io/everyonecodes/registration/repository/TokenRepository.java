package io.everyonecodes.registration.repository;

import io.everyonecodes.registration.data.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token,Long> {

}
