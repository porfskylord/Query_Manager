
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
            // Data Manipulation (DML)
            "SELECT", "INSERT", "UPDATE", "DELETE", "VALUES",
            "FROM", "WHERE", "GROUP BY", "ORDER BY", "HAVING", "DISTINCT",
            "JOIN", "LEFT JOIN", "RIGHT JOIN", "FULL JOIN", "INNER JOIN", "CROSS JOIN", "NATURAL JOIN",
            "ON", "USING" ,"SET",

            // Data Definition (DDL)
            "CREATE", "ALTER", "DROP", "RENAME", "TRUNCATE",
            "TABLE", "DATABASE", "SCHEMA", "INDEX", "SEQUENCE", "VIEW", "MATERIALIZED VIEW",
            "FUNCTION", "PROCEDURE", "TRIGGER", "EXTENSION", "AGGREGATE", "DOMAIN",
            "CAST", "TYPE", "OPERATOR", "ROLE", "USER", "COLUMN", "CONSTRAINT",

            // Data Control (DCL)
            "GRANT", "REVOKE", "PRIVILEGES",

            // Transaction Control (TCL)
            "BEGIN", "COMMIT", "ROLLBACK", "SAVEPOINT", "RELEASE SAVEPOINT", "START TRANSACTION",
            "SET TRANSACTION", "END",

            // Logical & Comparison Operators
            "AND", "OR", "NOT", "IN", "BETWEEN", "LIKE", "ILIKE", "EXISTS", "ALL", "ANY", "SOME",
            "IS", "IS NULL", "IS NOT NULL", "SIMILAR TO", "OVERLAPS",

            // Conditional Expressions
            "CASE", "WHEN", "THEN", "ELSE", "END", "COALESCE", "NULLIF",

            // Functions & Expressions
            "EXTRACT", "POSITION", "SUBSTRING", "TRIM", "OVER", "PARTITION BY", "FILTER", "LAG", "LEAD",
            "ROW_NUMBER", "RANK", "DENSE_RANK",

            // Constraints
            "PRIMARY KEY", "FOREIGN KEY", "UNIQUE", "CHECK", "DEFAULT", "REFERENCES",

            // Set Operations
            "UNION", "UNION ALL", "INTERSECT", "EXCEPT",

            // Window Functions
            "WINDOW", "LAG", "LEAD", "NTILE", "FIRST_VALUE", "LAST_VALUE",

            // JSON & Array
            "JSON", "JSONB", "ARRAY", "UNNEST", "JSON_OBJECT", "JSON_ARRAY",

            // Data Types
            "BOOLEAN", "CHAR", "VARCHAR", "TEXT", "INTEGER", "BIGINT", "SMALLINT",
            "DECIMAL", "NUMERIC", "REAL", "DOUBLE PRECISION", "SERIAL", "BIGSERIAL",
            "TIMESTAMP", "DATE", "TIME", "INTERVAL", "UUID", "BYTEA", "XML", "CIDR", "INET",

            // Full-Text Search
            "TSVECTOR", "TSQUERY", "TO_TSVECTOR", "TO_TSQUERY", "PLAINTO_TSQUERY",

            // Extensions & Other Features
            "FOREIGN DATA WRAPPER", "SERVER", "TABLESPACE", "LOGGED", "UNLOGGED",
            "CHECKPOINT", "VACUUM", "ANALYZE", "REINDEX", "CLUSTER"
    };

    private static final Pattern SQL_PATTERN;

    static {
        String keywordPattern = "\\b(" + String.join("|", SQL_KEYWORDS) + ")\\b";
        String stringPattern = "'([^']*)'";  // Matches single-quoted strings
        String quotedIdentifierPattern = "\"([^\"]*)\"";  // Matches double-quoted identifiers

        // Combine all patterns
        String fullPattern = "(?i)" + keywordPattern + "|" + stringPattern + "|" + quotedIdentifierPattern;
        SQL_PATTERN = Pattern.compile(fullPattern);
    }


    public CodeEditor() {
        queryTextArea = new CodeArea();
        queryTextArea.setPrefHeight(600);
        queryTextArea.setWrapText(true);

        // Add line numbers
        queryTextArea.setParagraphGraphicFactory(LineNumberFactory.get(queryTextArea));

        // Apply syntax highlighting dynamically
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
