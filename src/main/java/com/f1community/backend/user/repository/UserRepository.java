package com.f1community.backend.user.repository;

import com.f1community.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);
    @Modifying
    @Transactional
    @Query("DELETE FROM User u WHERE u.status = 'DEACTIVATED' AND u.deactivatedAt <= :cutoff")
    void deleteDeactivatedUsersBefore(@Param("cutoff") LocalDateTime cutoff);
}
