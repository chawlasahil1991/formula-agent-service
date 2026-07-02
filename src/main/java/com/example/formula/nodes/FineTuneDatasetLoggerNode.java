package com.example.formula.nodes;

import com.example.formula.state.FormulaWorkflowState;
import org.bsc.langgraph4j.action.NodeAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

@Component
public class FineTuneDatasetLoggerNode implements NodeAction<FormulaWorkflowState> {
    private static final Logger log = LoggerFactory.getLogger(FineTuneDatasetLoggerNode.class);
    private final String datasetPath;

    public FineTuneDatasetLoggerNode(@Value("${app.logging.dataset-path}") String datasetPath) {
        this.datasetPath = datasetPath;
    }

    @Override
    public Map<String, Object> apply(FormulaWorkflowState state) {
        try {
            Files.createDirectories(Paths.get(datasetPath).getParent());
            try (FileWriter fw = new FileWriter(datasetPath, true);
                 PrintWriter pw = new PrintWriter(fw)) {
                String jsonlPayload = String.format(
                    "{\"prompt\": \"%s\", \"failed_generation\": \"%s\", \"compiler_error\": \"%s\"}",
                    state.getUserPrompt().replace("\"", "\\\""),
                    state.getGeneratedFormula().replace("\"", "\\\""),
                    state.getExceptionMessage().replace("\"", "\\\"")
                );
                pw.println(jsonlPayload);
                log.warn("💾 Audit Notification: Syntax anomaly recorded for fine-tuning extraction.");
            }
        } catch (Exception e) {
            log.error("Failed executing storage logging dump: ", e);
        }
        return Map.of();
    }
}
