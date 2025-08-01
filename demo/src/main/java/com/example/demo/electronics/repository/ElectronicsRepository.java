package com.example.demo.electronics.repository;

import com.example.demo.electronics.entity.Electronics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElectronicsRepository extends JpaRepository<Electronics, Long> {
}
