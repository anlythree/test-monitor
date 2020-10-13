package top.anly.common.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;

/**
 * @author wangli
 * @date 2020/10/13 15:09
 */
public class StatementHandler implements InvocationHandler {

    private PreparedStatement preparedStatement;

    public StatementHandler(PreparedStatement preparedStatement) {
        this.preparedStatement = preparedStatement;
    }

    public StatementHandler() {
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        return null;
    }
}
