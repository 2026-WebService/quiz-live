package com.seohamin.quiz_live.domain.user.repository;

import com.seohamin.quiz_live.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByProviderUserId(String providerUserId);
}
