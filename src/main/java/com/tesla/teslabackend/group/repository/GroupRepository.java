package com.tesla.gamification.group.repository;

import com.tesla.gamification.group.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    Optional<Group> findByCode(String code);

    boolean existsByNameIgnoreCase(String name);
}