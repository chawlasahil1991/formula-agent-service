package com.example.formula.nodes;

import com.example.formula.state.FormulaWorkflowState;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.bsc.langgraph4j.action.NodeAction;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class ProcessFormulaNode implements NodeAction<FormulaWorkflowState> {
    private final ChatLanguageModel chatModel;

    public ProcessFormulaNode(ChatLanguageModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public Map<String, Object> apply(FormulaWorkflowState state) {
        String grammarRules = "Grammar: IF(condition, value_if_true, value_if_false), SUM(args), AND(args). Strings require double quotes.";
        StringBuilder prompt = new StringBuilder();
        prompt.append("Convert user request into a precise functional grammar formula.\n")
              .append("Rules: ").append(grammarRules).append("\n\n")
              .append("Target Request: ").append(state.getUserPrompt()).append("\n");

        if (state.hasError()) {
            prompt.append("\n⚠️ ALERT: Your previous generation broken layout rule: ")
                  .append(state.getGeneratedFormula()).append("\n")
                  .append("Compiler Refusal Exception: ").append(state.getExceptionMessage()).append("\n")
                  .append("Analyze compilation break trace, correct syntax errors, and output the patched formula exclusively.");
        }

        String targetOutput = chatModel.generate(prompt.toString()).trim();
        return Map.of("generatedFormula", targetOutput);
    }
}
