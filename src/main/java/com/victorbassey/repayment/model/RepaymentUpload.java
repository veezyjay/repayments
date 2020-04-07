package com.victorbassey.repayment.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "repayment_uploads")
public class RepaymentUpload {
    @Id
    @GeneratedValue
    private Long id;
    private Long customerId;
    private Long seasonId;
    private LocalDate date;
    private Long amount;

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
