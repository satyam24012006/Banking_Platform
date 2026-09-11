package org.satyam.banking_platform.Repository;

import org.satyam.banking_platform.Model.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeneficiaryRepository extends JpaRepository<Beneficiary,Long> {
}
