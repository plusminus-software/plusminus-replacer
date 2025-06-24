package software.plusminus.replacer;

import lombok.experimental.UtilityClass;
import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;

@UtilityClass
public class ExpressionUtil {
    
    private static final JexlEngine JEXL = new JexlBuilder().safe(true).create();

    public boolean process(String expression) {
        try {
            String preparedStatement = EnvReplacer.replaceEnvVariables(expression);
            JexlExpression jexlExpression = JEXL.createExpression(preparedStatement);
            Object result = jexlExpression.evaluate(new MapContext());
            if (!(result instanceof Boolean)) {
                throw new IllegalArgumentException("Incorrect expression: " + expression);
            }
            return (Boolean) result;
        } catch (Exception e) {
            throw new IllegalArgumentException("Incorrect expression: " + expression);
        }
    }
}
