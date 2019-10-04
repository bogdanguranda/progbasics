package db;

import java.util.List;
import java.util.Map;

public interface TestCasesDAO {
    List<Map<Integer, Integer>> getTestCases();
}
