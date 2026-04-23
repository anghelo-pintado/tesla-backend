package com.tesla.gamification.lesson.repository;

import com.tesla.gamification.lesson.entity.Alternativa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlternativaRepository extends JpaRepository<Alternativa, Long> {
}