package com.example.demo.bread.entity;

import com.example.demo.account.entity.Account;
import com.example.demo.bread.entity.enums.Sort;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bread {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Enumerated(EnumType.STRING)
    private Sort sort;
    private Long price;
    private Long quantity;
    private String description;
    private String origin;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account accountId;
}
