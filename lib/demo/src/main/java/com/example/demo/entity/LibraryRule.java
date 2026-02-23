package com.example.demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "library_rules")
public class LibraryRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String ruleName;
    
    @Column(nullable = false)
    private String ruleValue;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RuleType ruleType;
    
    @Column(name = "updated_by")
    private String updatedBy;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum RuleType {
        BORROW_LIMIT, LOAN_DURATION, FINE_RATE, RESERVATION_LIMIT, RENEWAL_LIMIT
    }
    
    // Constructors
    public LibraryRule() {
        this.updatedAt = LocalDateTime.now();
    }
    
    public LibraryRule(String ruleName, String ruleValue, String description, RuleType ruleType) {
        this();
        this.ruleName = ruleName;
        this.ruleValue = ruleValue;
        this.description = description;
        this.ruleType = ruleType;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getRuleName() {
        return ruleName;
    }
    
    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }
    
    public String getRuleValue() {
        return ruleValue;
    }
    
    public void setRuleValue(String ruleValue) {
        this.ruleValue = ruleValue;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public RuleType getRuleType() {
        return ruleType;
    }
    
    public void setRuleType(RuleType ruleType) {
        this.ruleType = ruleType;
    }
    
    public String getUpdatedBy() {
        return updatedBy;
    }
    
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Helper methods
    public Integer getIntValue() {
        try {
            return Integer.parseInt(ruleValue);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public BigDecimal getDecimalValue() {
        try {
            return new BigDecimal(ruleValue);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
