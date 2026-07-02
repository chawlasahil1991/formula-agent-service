package com.example.formula.state;

import org.bsc.langgraph4j.state.AgentState;
import java.util.Map;

public class FormulaWorkflowState extends AgentState {
    public FormulaWorkflowState(Map<String, Object> initData) {
        super(initData);
    }
    public String getUserPrompt() {
        return (String) data().get("userPrompt");
    }
    public String getGeneratedFormula() {
        return (String) data().get("generatedFormula");
    }
    public String getExceptionMessage() {
        return (String) data().getOrDefault("exceptionError", "");
    }
    public boolean hasError() {
        String err = getExceptionMessage();
        return err != null && !err.trim().isEmpty();
    }
}
