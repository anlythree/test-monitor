package top.anly.common.handler;

import top.anly.collect.JDBCCollect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * @author wangli
 * @date 2020/10/13 14:17
 */
public class ConnectionHandler implements InvocationHandler {

    private Connection connection;


    public ConnectionHandler(Connection connection) {
        this.connection = connection;
    }

    public ConnectionHandler() {
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        method.invoke(connection, args);
        if ("connect".equals(method.getName())) {
            PreparedStatement preparedStatement = (PreparedStatement)result;
            // 进一步代理prepareStatement
            result = JDBCCollect.statementProxy(preparedStatement);
            // 执行begin
            JDBCCollect.begin((String)args[0],null);
        }
        return result;

    }
}
