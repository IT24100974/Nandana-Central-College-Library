package com.example.demo.repository;

import com.example.demo.entity.LibraryRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LibraryRuleRepository extends JpaRepository<LibraryRule, Long> {
    Optional<LibraryRule> findByRuleName(String ruleName);
    List<LibraryRule> findByRuleType(LibraryRule.RuleType ruleType);
}
