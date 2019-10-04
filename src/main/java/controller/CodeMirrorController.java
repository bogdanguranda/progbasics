package controller;

import org.primefaces.extensions.event.CompleteEvent;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.Serializable;
import java.util.*;

@ManagedBean
@SessionScoped
public class CodeMirrorController implements Serializable {

    private static final long serialVersionUID = 20111020L;

    private String content = "function main(inputN) { \n" +
            "  // write your code here\n" +
            "};";
    private String verify = "";

    private Integer level = 0;
    private List<String> instructionsText = new ArrayList<>(
            Arrays.asList(
                    "compute the sum of first N positive numbers (where N is a parameter)",
                    "compute the sum of first N odd positive numbers (where N is a parameter)",
                    "compute factorial of N (where N is a parameter)"
            )
    );
    private List<Map<Integer, Integer>> testCases = new ArrayList<>(
            Arrays.asList(
                    new HashMap<Integer, Integer>() {{
                        put(0, 0);
                        put(1, 1);
                        put(2, 3);
                        put(3, 6);
                        put(5, 15);
                        put(8, 36);
                    }},
                    new HashMap<Integer, Integer>() {{
                        put(0, 0);
                        put(1, 1);
                        put(2, 1);
                        put(3, 4);
                        put(5, 9);
                        put(8, 16);
                    }},
                    new HashMap<Integer, Integer>() {{
                        put(0, 1);
                        put(1, 1);
                        put(2, 2);
                        put(3, 6);
                        put(5, 120);
                        put(8, 40320);
                    }}
            )
    );

    private String mode = "javascript";
    private String theme = "blackboard";
    private String keymap = "default";

    public void submitCode() {
        try {
            runTestCases();
        } catch (ScriptException e) {
            verify = e.getMessage();
        }
    }

    private void runTestCases() throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript");

        String executeLineRegEx = "(?m)^main\\(.*\\);";
        boolean allCasesPass = true;
        Integer failedInput = null;
        Integer failedRes = null;

        for (Map.Entry<Integer, Integer> testCase : testCases.get(level).entrySet()) {
            content = content.replaceAll(executeLineRegEx, "");
            content += "\nmain(" + testCase.getKey() + ");";
            Object nullableRes = engine.eval(content);
            if (nullableRes == null || nullableRes == "") {
                allCasesPass = false;
                break;
            }
            Double resNumber;
            try {
                resNumber = Double.valueOf(nullableRes.toString());
            } catch (NumberFormatException e) {
                allCasesPass = false;
                break;
            }

            if (!testCase.getValue().equals(resNumber.intValue())) {
                allCasesPass = false;
                failedInput = testCase.getKey();
                failedRes = resNumber.intValue();
                break;
            }
        }
        content = content.replaceAll(executeLineRegEx, "");
        content = content.trim();

        if (allCasesPass) {
            verify = "success (all test cases passed)";
            if (level < instructionsText.size() - 1) {
                level++;
            }
        } else {
            verify = "failed (some test cases didn't pass)";
            if (failedInput != null && failedRes != null) {
                verify += ": input was " + failedInput + ", result was: " + failedRes;
            }
        }
    }

    public List<String> complete(final CompleteEvent event) {
        final ArrayList<String> suggestions = new ArrayList<>();

        suggestions.add("context: " + event.getContext());
        suggestions.add("token: " + event.getToken());

        return suggestions;
    }

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public String getVerify() {
        return verify;
    }

    public void setVerify(String verify) {
        this.verify = verify;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public List<String> getInstructionsText() {
        return instructionsText;
    }

    public void setInstructionsText(List<String> instructionsText) {
        this.instructionsText = instructionsText;
    }

    public List<Map<Integer, Integer>> getTestCases() {
        return testCases;
    }

    public void setTestCases(List<Map<Integer, Integer>> testCases) {
        this.testCases = testCases;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(final String mode) {
        this.mode = mode;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(final String theme) {
        this.theme = theme;
    }

    public String getKeymap() {
        return keymap;
    }

    public void setKeymap(final String keymap) {
        this.keymap = keymap;
    }
}
            