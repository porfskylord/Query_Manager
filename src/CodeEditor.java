import javafx.scene.layout.VBox;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeEditor extends VBox {

    private final CodeArea queryTextArea;

    private static final String[] SQL_KEYWORDS = {
            "SELECT", "INSERT", "UPDATE", "DELETE", "VALUES",
            "FROM", "WHERE", "GROUP BY", "ORDER BY", "HAVING", "DISTINCT",
            "JOIN", "LEFT JOIN", "RIGHT JOIN", "FULL JOIN", "INNER JOIN", "CROSS JOIN",
            "ON", "USING", "SET", "CREATE", "ALTER", "DROP", "TRUNCATE", "TABLE",
            "DATABASE", "INDEX", "VIEW", "FUNCTION", "PROCEDURE", "TRIGGER",
            "BEGIN", "COMMIT", "ROLLBACK", "SAVEPOINT", "END", "GRANT", "REVOKE",
            "AND", "OR", "NOT", "IN", "BETWEEN", "LIKE", "EXISTS", "CASE", "WHEN",
            "THEN", "ELSE", "COALESCE", "NULLIF", "UNION", "INTERSECT", "EXCEPT",
            "BOOLEAN", "CHAR", "VARCHAR", "TEXT", "INTEGER", "BIGINT", "DECIMAL",
            "NUMERIC", "REAL", "DOUBLE PRECISION", "SERIAL", "TIMESTAMP", "DATE",
            "TIME", "INTERVAL", "UUID"
    };

    private static final Pattern SQL_PATTERN;

    static {
        String keywordPattern = "\\b(" + String.join("|", SQL_KEYWORDS) + ")\\b";  // Matches SQL keywords
        String stringPattern = "'([^']*)'";  // Matches single-quoted strings
        String quotedIdentifierPattern = "\"([^\"]*)\"";  // Matches double-quoted identifiers
        String numberPattern = "\\b\\d+\\b";  // Matches integers
        String singleLineCommentPattern = "--[^\n]*";  // Matches SQL single-line comments
        String multiLineCommentPattern = "/\\*.*?\\*/";  // Matches SQL multi-line comments

        // Combine all patterns (Ensure multi-line comment is non-greedy)
        String fullPattern = "(?i)(" + keywordPattern + ")|(" + stringPattern + ")|(" +
                quotedIdentifierPattern + ")|(" + numberPattern + ")|(" +
                singleLineCommentPattern + ")|(" + multiLineCommentPattern + ")";
        SQL_PATTERN = Pattern.compile(fullPattern, Pattern.DOTALL);  // Allows multi-line comment matching
    }




    public CodeEditor() {
        queryTextArea = new CodeArea();
        queryTextArea.setPrefHeight(600);
        queryTextArea.setWrapText(true);

        // Set Dark Theme Background
        queryTextArea.setStyle("-fx-background-color: #1E1E1E;");

        // Add line numbers
        queryTextArea.setParagraphGraphicFactory(LineNumberFactory.get(queryTextArea));

        // Ensure syntax highlighting is applied on text changes
        queryTextArea.textProperty().addListener((obs, oldText, newText) -> {
            queryTextArea.setStyleSpans(0, computeHighlighting(newText));
        });

        // Load CSS for syntax highlighting
        queryTextArea.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());

        // Add to this container
        this.getChildren().add(queryTextArea);
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = SQL_PATTERN.matcher(text);
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        int lastEnd = 0;

        while (matcher.find()) {
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastEnd);

            if (matcher.group(1) != null) { // SQL Keywords
                spansBuilder.add(Collections.singleton("keyword"), matcher.end() - matcher.start());
            } else if (matcher.group(2) != null) { // Single-quoted strings
                spansBuilder.add(Collections.singleton("string"), matcher.end() - matcher.start());
            } else if (matcher.group(3) != null) { // Double-quoted identifiers
                spansBuilder.add(Collections.singleton("quoted"), matcher.end() - matcher.start());
            } else if (matcher.group(4) != null) { // Numbers (integers)
                spansBuilder.add(Collections.singleton("number"), matcher.end() - matcher.start());
            } else if (matcher.group(5) != null) { // Single-line SQL Comments
                spansBuilder.add(Collections.singleton("comment"), matcher.end() - matcher.start());
            } else if (matcher.group(6) != null) { // Multi-line SQL Comments
                spansBuilder.add(Collections.singleton("comment"), matcher.end() - matcher.start());
            }

            lastEnd = matcher.end();
        }

        spansBuilder.add(Collections.emptyList(), text.length() - lastEnd);
        return spansBuilder.create();
    }




    public String getQueryText() {
        return queryTextArea.getText();
    }

    public void setQueryText(String text) {
        queryTextArea.replaceText(text);
    }
}
