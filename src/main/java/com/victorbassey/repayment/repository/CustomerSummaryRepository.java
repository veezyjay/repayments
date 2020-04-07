package com.victorbassey.repayment.repository;



import com.victorbassey.repayment.model.CustomerSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerSummaryRepository extends JpaRepository<CustomerSummary, Long> {

    Optional<CustomerSummary> findByCustomerIdAndSeasonId(Long customerId, Long seasonId);

    @Query("SELECT c FROM CustomerSummary c INNER JOIN Season s ON c.seasonId = s.seasonId " +
            "WHERE c.customerId = :customerId AND c.totalCredit > c.totalRepaid ORDER BY s.startDate")
    List<CustomerSummary> findCustomerSummariesWithDebts(@Param("customerId") Long customerId);

    @Query(value = "SELECT * FROM customer_summaries AS c INNER JOIN seasons AS s ON c.season_id = s.season_id " +
            "WHERE c.customer_id = :customerId ORDER BY s.start_date DESC LIMIT 1", nativeQuery = true)
    Optional<CustomerSummary> findMostRecentCustomerSummary(@Param("customerId") Long customerId);
}
