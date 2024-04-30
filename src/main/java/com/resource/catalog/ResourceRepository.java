package com.resource.catalog.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    Optional<com.resource.catalog.model.Resource> findByEmail(String email);
    // You can add custom query methods here if needed
}