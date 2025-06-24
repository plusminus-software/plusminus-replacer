package software.plusminus.replacer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExpressionUtilTest {

    @Test
    void trueExpression() {
        String expression = "'one' == 'one'";
        boolean result = ExpressionUtil.process(expression);
        assertTrue(result);
    }

    @Test
    void falseExpression() {
        String expression = "'one' == 'two'";
        boolean result = ExpressionUtil.process(expression);
        assertFalse(result);
    }

    @Test
    void incorrectExpression() {
        String expression = "'one'";
        assertThrows(IllegalArgumentException.class, () -> ExpressionUtil.process(expression));
    }
}