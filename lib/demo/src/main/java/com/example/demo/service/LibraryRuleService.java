package com.example.demo.service;

import com.example.demo.entity.LibraryRule;
import com.example.demo.repository.LibraryRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class LibraryRuleService {
    
    @Autowired
    private LibraryRuleRepository libraryRuleRepository;
    
    // Default rule values
    private static final int DEFAULT_BORROW_LIMIT = 5;
    private static final int DEFAULT_LOAN_DURATION = 14;
    private static final BigDecimal DEFAULT_FINE_RATE = new BigDecimal("1.00");
    private static final int DEFAULT_RESERVATION_LIMIT = 3;
    private static final int DEFAULT_RENEWAL_LIMIT = 2;
    
    public LibraryRule saveRule(LibraryRule rule) {
        return libraryRuleRepository.save(rule);
    }
    
    public Optional<LibraryRule> findById(Long id) {
        return libraryRuleRepository.findById(id);
    }
    
    public Optional<LibraryRule> findByRuleName(String ruleName) {
        return libraryRuleRepository.findByRuleName(ruleName);
    }
    
    public List<LibraryRule> getAllRules() {
        return libraryRuleRepository.findAll();
    }
    
    public List<LibraryRule> getRulesByType(LibraryRule.RuleType ruleType) {
        return libraryRuleRepository.findByRuleType(ruleType);
    }
    
    // Rule value getters with defaults
    public int getBorrowLimit() {
        return findByRuleName("BORROW_LIMIT")
                .map(LibraryRule::getIntValue)
                .orElse(DEFAULT_BORROW_LIMIT);
    }
    
    public int getLoanDurationDays() {
        return findByRuleName("LOAN_DURATION_DAYS")
                .map(LibraryRule::getIntValue)
                .orElse(DEFAULT_LOAN_DURATION);
    }
    
    public BigDecimal getFineRatePerDay() {
        return findByRuleName("FINE_RATE_PER_DAY")
                .map(LibraryRule::getDecimalValue)
                .orElse(DEFAULT_FINE_RATE);
    }
    
    public int getReservationLimit() {
        return findByRuleName("RESERVATION_LIMIT")
                .map(LibraryRule::getIntValue)
                .orElse(DEFAULT_RESERVATION_LIMIT);
    }
    
    public int getRenewalLimit() {
        return findByRuleName("RENEWAL_LIMIT")
                .map(LibraryRule::getIntValue)
                .orElse(DEFAULT_RENEWAL_LIMIT);
    }
    
    // Rule setters
    public void setBorrowLimit(int limit, String updatedBy) {
        updateOrCreateRule("BORROW_LIMIT", String.valueOf(limit), 
                          "Maximum number of books a user can borrow", 
                          LibraryRule.RuleType.BORROW_LIMIT, updatedBy);
    }
    
    public void setLoanDurationDays(int days, String updatedBy) {
        updateOrCreateRule("LOAN_DURATION_DAYS", String.valueOf(days), 
                          "Number of days a book can be borrowed", 
                          LibraryRule.RuleType.LOAN_DURATION, updatedBy);
    }
    
    public void setFineRatePerDay(BigDecimal rate, String updatedBy) {
        updateOrCreateRule("FINE_RATE_PER_DAY", rate.toString(), 
                          "Fine amount per day for overdue books", 
                          LibraryRule.RuleType.FINE_RATE, updatedBy);
    }
    
    public void setReservationLimit(int limit, String updatedBy) {
        updateOrCreateRule("RESERVATION_LIMIT", String.valueOf(limit), 
                          "Maximum number of books a user can reserve", 
                          LibraryRule.RuleType.RESERVATION_LIMIT, updatedBy);
    }
    
    public void setRenewalLimit(int limit, String updatedBy) {
        updateOrCreateRule("RENEWAL_LIMIT", String.valueOf(limit), 
                          "Maximum number of times a book can be renewed", 
                          LibraryRule.RuleType.RENEWAL_LIMIT, updatedBy);
    }
    
    private void updateOrCreateRule(String ruleName, String ruleValue, String description, 
                                  LibraryRule.RuleType ruleType, String updatedBy) {
        Optional<LibraryRule> existingRule = findByRuleName(ruleName);
        
        if (existingRule.isPresent()) {
            LibraryRule rule = existingRule.get();
            rule.setRuleValue(ruleValue);
            rule.setUpdatedBy(updatedBy);
            rule.preUpdate();
            saveRule(rule);
        } else {
            LibraryRule newRule = new LibraryRule(ruleName, ruleValue, description, ruleType);
            newRule.setUpdatedBy(updatedBy);
            saveRule(newRule);
        }
    }
    
    public void deleteRule(Long id) {
        libraryRuleRepository.deleteById(id);
    }
    
    public void initializeDefaultRules() {
        if (libraryRuleRepository.count() == 0) {
            saveRule(new LibraryRule("BORROW_LIMIT", String.valueOf(DEFAULT_BORROW_LIMIT), 
                    "Maximum number of books a user can borrow", LibraryRule.RuleType.BORROW_LIMIT));
            
            saveRule(new LibraryRule("LOAN_DURATION_DAYS", String.valueOf(DEFAULT_LOAN_DURATION), 
                    "Number of days a book can be borrowed", LibraryRule.RuleType.LOAN_DURATION));
            
            saveRule(new LibraryRule("FINE_RATE_PER_DAY", DEFAULT_FINE_RATE.toString(), 
                    "Fine amount per day for overdue books", LibraryRule.RuleType.FINE_RATE));
            
            saveRule(new LibraryRule("RESERVATION_LIMIT", String.valueOf(DEFAULT_RESERVATION_LIMIT), 
                    "Maximum number of books a user can reserve", LibraryRule.RuleType.RESERVATION_LIMIT));
            
            saveRule(new LibraryRule("RENEWAL_LIMIT", String.valueOf(DEFAULT_RENEWAL_LIMIT), 
                    "Maximum number of times a book can be renewed", LibraryRule.RuleType.RENEWAL_LIMIT));
        }
    }
}
