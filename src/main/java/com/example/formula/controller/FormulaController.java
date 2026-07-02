package com.example.formula.controller;

import com.example.formula.dto.FormulaRequest;
import com.example.formula.state.FormulaWorkflowState;
import org.bsc.langgraph4j.CompiledGraph;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/formulas")
public class FormulaController {
    private final CompiledGraph<FormulaWorkflowState> formulaGraph;

    public FormulaController(CompiledGraph<FormulaWorkflowState> formulaGraph) {
        this.formulaGraph = formulaGraph;
    }

    @PostMapping("/compile-flow")
    public ResponseEntity<Map<String, Object>> processQuery(@RequestBody FormulaRequest request) {
        if (request.prompt() == null || request.prompt().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Input prompt cannot be empty."));
        }
        Map<String, Object> initialContext = Map.of("userPrompt", request.prompt());
        FormulaWorkflowState outputContext = formulaGraph.invoke(initialContext)
                .orElseThrow(() -> new RuntimeException("Workflow execution failed to return a valid terminal state."));

        return ResponseEntity.ok(Map.of(
            "status", "VERIFIED",
            "formula", outputContext.getGeneratedFormula()
        ));
    }
}
