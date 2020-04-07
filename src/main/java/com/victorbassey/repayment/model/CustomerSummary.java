package com.victorbassey.repayment.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "customer_summaries")
public class CustomerSummary {
    @Id
    @GeneratedValue
    private Long id;
    private Long customerId;
    private Long seasonId;
    private Long totalRepaid;
    private Long totalCredit;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(Long seasonId) {
        this.seasonId = seasonId;
    }

    public Long getTotalRepaid() {
        return totalRepaid;
    }

    public void setTotalRepaid(Long totalRepaid) {
        this.totalRepaid = totalRepaid;
    }

    public Long getTotalCredit() {
        return totalCredit;
    }

    public void setTotalCredit(Long totalCredit) {
        this.totalCredit = totalCredit;
    }

    @Override
    public String toString() {
        return "CustomerSummary{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", seasonId=" + seasonId +
                ", totalRepaid=" + totalRepaid +
                ", totalCredit=" + totalCredit +
                '}';
    }
}
