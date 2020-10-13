package top.anly.collect;

import javassist.*;
import lombok.extern.slf4j.Slf4j;
import top.anly.common.handler.ConnectionHandler;
import top.anly.domain.SqlBean;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Proxy;
import java.security.ProtectionDomain;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;

/**
 * @author wangli
 * @date 2020/10/13 13:55
 */
@Slf4j
public class JDBCCollect {

    private final static String TARGET_CLASS = "org.postgresql.Driver";

    private final static String TARGET_METHOD = "connect";

    private final static String VOID_SOURCE = "{\n" +
            "  %s  " +
            "        try {\n" +
            "            %s$agent($$);\n" +
            "        }catch (Throwable e){\n" +
            "  %s   " +
            "            throw e;\n" +
            "        }finally {\n" +
            "  %s   " +
            "        }\n" +
            "    }";

    private final static String SOURCE = "{\n" +
            "  %s  " +
            "        try {\n" +
            "            result = %s$agent($$);\n" +
            "        }catch (Throwable e){\n" +
            "  %s   " +
            "            throw e;\n" +
            "        }finally {\n" +
            "  %s   " +
            "        }\n" +
            "  return ($r) result; " +
            "    }";

    private final static String BEGIN_SOURCE = "top.anly.domain.MonitorBean monitorBean = top.anly.collect.HttpCollect.begin($args);";
    private final static String ERROR_SOURCE = "top.anly.collect.HttpCollect.error(e,monitorBean);";
    private final static String END_SOURCE = "top.anly.collect.HttpCollect.end(monitorBean);";

    public byte[] build(ClassLoader classLoader) throws Exception {
        ClassPool classPool = new ClassPool();
        classPool.insertClassPath(new LoaderClassPath(classLoader));
        // 查找目标类下的目标方法
        CtClass ctClass = classPool.get(TARGET_CLASS);
        CtMethod oldMethod = ctClass.getDeclaredMethod(TARGET_METHOD);
        CtMethod newMethod = CtNewMethod.copy(oldMethod, ctClass, null);
        oldMethod.setName(oldMethod.getName() + "$agent");
        String template = "void".equals(oldMethod.getReturnType().getName()) ? VOID_SOURCE : SOURCE;
        newMethod.setBody(String.format(
                template,
                BEGIN_SOURCE,
                TARGET_METHOD,
                ERROR_SOURCE,
                END_SOURCE
        ));
        ctClass.addMethod(newMethod);
        System.out.println(ctClass.getDeclaredMethod(TARGET_METHOD + "$agent"));
        return ctClass.toBytecode();
    }

    /**
     * 监控SQL入口
     *
     * @param instrumentation
     */
    public void transform(Instrumentation instrumentation) {
        instrumentation.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader,
                                    String className,
                                    Class<?> classBeingRedefined,
                                    ProtectionDomain protectionDomain,
                                    byte[] classfileBuffer) throws IllegalClassFormatException {
                if (null == className) {
                    return null;
                }
                String classNameByReplace = className.replaceAll("/", ".");
                if (!classNameByReplace.equals(TARGET_CLASS)) {
                    return null;
                }
                try {
                    System.out.println("成功加入sql的监控");
                    return build(loader);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

    public static Connection connectProxy(Connection connection) {
        // 这里代理JDBC类加载器中实现Connection接口的方法
        return (Connection) Proxy.newProxyInstance(JDBCCollect.class.getClassLoader(), new Class[]{Connection.class}, new ConnectionHandler());
    }

    public static PreparedStatement statementProxy(PreparedStatement preparedStatement) {
        // 这里代理JDBC类加载器中实现Connection接口的方法
        return (PreparedStatement) Proxy.newProxyInstance(JDBCCollect.class.getClassLoader(), new Class[]{Connection.class}, new ConnectionHandler());
    }


    /**
     * 监控前逻辑
     *
     * @param JDBCUrl
     * @param sql
     * @return
     */
    public static SqlBean begin(String JDBCUrl, String sql) {

        SqlBean sqlBean = new SqlBean();
        sqlBean.setJdbcUrl(JDBCUrl);
        sqlBean.setSql(sql);
        sqlBean.setStartTime(LocalDateTime.now());
        return sqlBean;
    }

    /**
     * sql执行出现异常处理逻辑
     *
     * @param e
     * @param sqlBean
     */
    public static void error(Throwable e, SqlBean sqlBean) {
        sqlBean.setError(e.getMessage());
    }

    /**
     * 监控结束处理逻辑
     *
     * @param sqlBean
     */
    public static void end(SqlBean sqlBean) {
        sqlBean.setEndTime(LocalDateTime.now());
        System.out.println("-------------------------------------------");
        System.out.println("sql执行情况：" + sqlBean.toString());
        log.debug("http执行情况：" + sqlBean.toString());
        System.out.println("-------------------------------------------");
    }
}
