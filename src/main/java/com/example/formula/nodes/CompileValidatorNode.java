package com.example.formula.nodes;

import com.example.formula.service.FormulaCompilerService;
import com.example.formula.state.FormulaWorkflowState;
import org.bsc.langgraph4j.action.NodeAction;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class CompileValidatorNode implements NodeAction<FormulaWorkflowState> {
    private final FormulaCompilerService compilerService;

    public CompileValidatorNode(FormulaCompilerService compilerService) {
        this.compilerService = compilerService;
    }

    @Override
    public Map<String, Object> apply(FormulaWorkflowState state) {
        try {
            compilerService.compile(state.getGeneratedFormula());
            return Map.of("exceptionError", ""); 
        } catch (Exception e) {
            return Map.of("exceptionError", e.getMessage());
        }
    }
}
