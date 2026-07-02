package com.example.formula.service;

import org.springframework.stereotype.Service;

@Service
public class FormulaCompilerService {
    public void compile(String formula) {
        if (formula == null || formula.trim().isEmpty()) {
            throw new IllegalArgumentException("Formula payload cannot be blank.");
        }
        if (formula.contains("'")) {
            throw new IllegalArgumentException("Grammar Violation: String literals must utilize double quotes (\") instead of single quotes (').");
        }
        if (!formula.startsWith("IF") && !formula.startsWith("SUM") && !formula.startsWith("AND")) {
            throw new IllegalArgumentException("Syntax Violation: Expression must instantiate with known keywords [IF, SUM, AND].");
        }
    }
}
