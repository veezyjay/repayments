package com.victorbassey.repayment.model;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "repayments")
public class Repayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long repaymentId;
    private Long customerId;
    private Long seasonId;

    @CreationTimestamp
    private LocalDate date;
    private Long amount;
    private Long parentId;

    public Repayment() {
    }

    public Repayment(Long customerId, Long seasonId, Long amount) {
        this.customerId = customerId;
        this.seasonId = seasonId;
        this.amount = amount;
    }

    public Long getRepaymentId() {
        return repaymentId;
    }

    public void setRepaymentId(Long repaymentId) {
        this.repaymentId = repaymentId;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    @Override
    public String toString() {
        return "Repayment{" +
                "repaymentId=" + repaymentId +
                ", customerId=" + customerId +
                ", seasonId=" + seasonId +
                ", date=" + date +
                ", amount=" + amount +
                ", parentId=" + parentId +
                '}';
    }
}
