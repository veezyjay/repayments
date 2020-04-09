package com.victorbassey.repayment.model;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "repayment_uploads")
public class RepaymentUpload {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Customer ID is required")
    @Min(value = 1, message = "Customer ID must be positive")
    private Long customerId;

    @Min(value = 0, message = "Season ID must not be negative")
    private Long seasonId;

    @CreationTimestamp
    private LocalDate date;

    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount must be positive")
    private Long amount;

    public RepaymentUpload() {
    }

    public RepaymentUpload(Long customerId, Long amount) {
        this.customerId = customerId;
        this.amount = amount;
    }

    public RepaymentUpload(Long customerId, Long amount, Long seasonId) {
        this.customerId = customerId;
        this.seasonId = seasonId;
        this.amount = amount;
    }

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

    @Override
    public String toString() {
        return "RepaymentUpload{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", seasonId=" + seasonId +
                ", date=" + date +
                ", amount=" + amount +
                '}';
    }
}
