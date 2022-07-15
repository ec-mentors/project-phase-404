package io.everyonecodes.cryptolog.repository;

import io.everyonecodes.cryptolog.data.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken,Long> {
        Optional<ConfirmationToken> findByToken(String token);
}
