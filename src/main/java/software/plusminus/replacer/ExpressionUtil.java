package software.plusminus.replacer;

import lombok.experimental.UtilityClass;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

@UtilityClass
public class ExpressionUtil {
    
    private static final ExpressionParser PARSER = new SpelExpressionParser();

    public boolean process(String expression) {
        try {
            String preparedStatement = EnvReplacer.replaceEnvVariables(expression);
            Expression parsedExpression = PARSER.parseExpression(preparedStatement);
            Boolean result = parsedExpression.getValue(Boolean.class);
            if (result == null) {
                throw new IllegalArgumentException("Incorrect expression: " + expression);
            }
            return result;
        } catch (Exception e) {
            throw new IllegalArgumentException("Incorrect expression: " + expression);
        }
    }
}
