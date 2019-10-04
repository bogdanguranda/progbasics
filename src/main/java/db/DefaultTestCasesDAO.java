package db;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

@ManagedBean(name="defaultTestCasesDAO")
@SessionScoped
public class DefaultTestCasesDAO implements TestCasesDAO {

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

    public void readTestCases() {
        String fileName = "testCases.txt";

        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            stream.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Map<Integer, Integer>> getTestCases() {
        return testCases;
    }

    public void setTestCases(List<Map<Integer, Integer>> testCases) {
        this.testCases = testCases;
    }
}
