package org.satyam.banking_platform.Repository;

import org.satyam.banking_platform.Model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {
}
