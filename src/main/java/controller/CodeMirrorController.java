package controller;

import db.TestCasesDAO;
import org.primefaces.extensions.event.CompleteEvent;

import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.script.*;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.*;

@ManagedBean
@SessionScoped
public class CodeMirrorController implements Serializable {

    private static final long serialVersionUID = 20111020L;

    @ManagedProperty(value="#{testCasesDAO}")
    private TestCasesDAO testCasesDAO;

    private String content = "function main(N) { \n" +
            "  // write your code here\n" +
            "};";
    private String verify = "";

    private Integer level = 0;
    private List<String> instructionsText = new ArrayList<>(
            Arrays.asList(
                    "Compute the sum of first N positive numbers (where N is a parameter).",
                    "Compute the sum of first N odd positive numbers (where N is a parameter).",
                    "Compute factorial of N (where N is a parameter)."
            )
    );

    private String mode = "javascript";
    private String theme = "blackboard";
    private String keymap = "default";

    private String executeLineRegEx = "(?m)^main\\(.*\\);";

    private ScheduledExecutorService pool = Executors.newScheduledThreadPool(10);

    public void submitCode() {
        Future<?> scriptTask = pool.submit(this::runTestCases);
        pool.schedule(() -> {
            scriptTask.cancel(true);
        }, 3, TimeUnit.SECONDS);

        try {
            scriptTask.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            verify = "error: unknown execution error: " + Arrays.toString(e.getStackTrace());
        } catch (CancellationException e) {
            verify = "error: execution time exceeded 5s limit";
        } finally {
            content = content.replaceAll(executeLineRegEx, "");
            content = content.trim();
        }
    }

    private void runTestCases() {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript");
        ScriptContext scriptContext = new SimpleScriptContext();

        boolean allCasesPass = true;
        Integer failedInput = null;
        Integer failedRes = null;

        for (Map.Entry<Integer, Integer> testCase : testCasesDAO.getTestCases().get(level).entrySet()) {
            content = content.replaceAll(executeLineRegEx, "");
            content += "\nmain(" + testCase.getKey() + ");";

            CompiledScript compiledScript = null;
            try {
                compiledScript = ((Compilable)engine).compile(content);
            } catch (ScriptException e) {
                allCasesPass = false;
                break;
            }

            Object nullableRes = null;
            try {
                nullableRes = compiledScript.eval(scriptContext);
            } catch (ScriptException e) {
                allCasesPass = false;
                break;
            }

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

    @PreDestroy
    public void destroy() {
        pool.shutdown();
    }

    public TestCasesDAO getTestCasesDAO() {
        return testCasesDAO;
    }

    public void setTestCasesDAO(TestCasesDAO testCasesDAO) {
        this.testCasesDAO = testCasesDAO;
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
            