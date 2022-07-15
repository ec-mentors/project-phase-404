package io.everyonecodes.cryptolog.repository;

import io.everyonecodes.cryptolog.data.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token,Long> {

}
