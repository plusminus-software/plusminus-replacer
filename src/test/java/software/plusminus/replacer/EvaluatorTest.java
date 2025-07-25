package software.plusminus.replacer;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EvaluatorTest {

    @Test
    void trueCondition() {
        String expression = "'one' == 'one'";
        boolean result = Evaluator.condition(expression, Collections.emptyMap());
        assertTrue(result);
    }

    @Test
    void falseCondition() {
        String expression = "'one' == 'two'";
        boolean result = Evaluator.condition(expression, Collections.emptyMap());
        assertFalse(result);
    }

    @Test
    void conditionWithVariables() {
        String expression = "'one' == my_variable";
        boolean result = Evaluator.condition(expression, Collections.singletonMap("my-variable", "one"));
        assertTrue(result);
    }

    @Test
    void incorrectExpression() {
        String expression = "'one'";
        Map<String, String> emptyMap = Collections.emptyMap();
        assertThrows(IllegalArgumentException.class, () -> Evaluator.condition(expression, emptyMap));
    }
}