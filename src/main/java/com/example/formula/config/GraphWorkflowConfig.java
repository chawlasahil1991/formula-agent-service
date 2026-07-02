package com.example.formula.config;

import com.example.formula.nodes.CompileValidatorNode;
import com.example.formula.nodes.FineTuneDatasetLoggerNode;
import com.example.formula.nodes.ProcessFormulaNode;
import com.example.formula.state.FormulaWorkflowState;
import org.bsc.langgraph4j.GraphStateException;
import org.bsc.langgraph4j.StateGraph;
import org.bsc.langgraph4j.CompiledGraph;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

// Crucial: Import the graph control flow constants
import static org.bsc.langgraph4j.StateGraph.START;
import static org.bsc.langgraph4j.StateGraph.END;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;
import static org.bsc.langgraph4j.action.AsyncEdgeAction.edge_async;

@Configuration
public class GraphWorkflowConfig {

    @Bean
    public CompiledGraph<FormulaWorkflowState> formulaGraph(
            ProcessFormulaNode processNode,
            CompileValidatorNode validatorNode,
            FineTuneDatasetLoggerNode loggerNode) throws GraphStateException {

        // Assuming your State uses default map initialization or a predefined schema Map
        // If your state has a SCHEMA Map, use: new StateGraph<>(FormulaWorkflowState.SCHEMA, FormulaWorkflowState::new);
        StateGraph<FormulaWorkflowState> graph = new StateGraph<>(Map.of(), FormulaWorkflowState::new);

        return graph
                // 1. Register Nodes using node_async wrapper
                .addNode("process_formula", node_async(processNode))
                .addNode("compile_check", node_async(validatorNode))
                .addNode("log_failure", node_async(loggerNode))

                // 2. Route from START (instead of setEntryPoint)
                .addEdge(START, "process_formula")
                .addEdge("process_formula", "compile_check")

                // 3. Setup Conditional Routing with edge_async and a key mapping
                .addConditionalEdges(
                        "compile_check",
                        edge_async(state -> {
                            if (state.hasError()) {
                                return "fail";
                            }
                            return "success";
                        }),
                        Map.of(
                                "fail", "log_failure",
                                "success", END
                        )
                )

                // Loop back to try again after logging
                .addEdge("log_failure", "process_formula")

                // 4. Compile the configuration
                .compile();
    }
}