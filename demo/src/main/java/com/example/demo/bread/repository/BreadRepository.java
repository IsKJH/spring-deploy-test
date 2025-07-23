package com.example.demo.bread.repository;

import com.example.demo.bread.entity.Bread;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BreadRepository extends JpaRepository<Bread, Long> {
}
