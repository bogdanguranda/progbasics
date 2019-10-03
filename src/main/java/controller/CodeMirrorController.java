package controller;

import org.primefaces.extensions.event.CompleteEvent;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
public class CodeMirrorController implements Serializable {

    private static final long serialVersionUID = 20111020L;

    private String content = "function test() { console.log('test'); }";
    private String result = "initial value";

    private String mode = "javascript";
    private String theme = "blackboard";
    private String keymap = "default";

    public void submitContent() {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript");

        try {
            result = engine.eval(content).toString();
        } catch (ScriptException e) {
            result = e.getMessage();
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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
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
            