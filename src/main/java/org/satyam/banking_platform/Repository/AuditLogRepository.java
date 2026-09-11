package org.satyam.banking_platform.Repository;

import org.satyam.banking_platform.Model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog,Long> {
}
