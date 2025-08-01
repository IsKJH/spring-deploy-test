package com.example.demo.company.repository;

import com.example.demo.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByAccountId(Long accountId);
}
