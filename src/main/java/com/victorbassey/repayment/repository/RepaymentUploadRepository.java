package com.victorbassey.repayment.repository;


import com.victorbassey.repayment.model.RepaymentUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepaymentUploadRepository extends JpaRepository<RepaymentUpload, Long> {
}
