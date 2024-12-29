package com.example.emailservice.repositories;

import com.example.emailservice.models.Email;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {
    @Query("SELECT e FROM Email e LEFT JOIN e.attachments a " +
            "WHERE LOWER(e.subject) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(e.message) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(a.fileName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Email> findBySubjectContainingIgnoreCaseOrMessageContainingIgnoreCaseOrAttachmentsFileNameContainingIgnoreCase(
            @Param("keyword") String keyword, Pageable pageable);
}
