package top.anly.collect;

import javassist.*;
import lombok.extern.slf4j.Slf4j;
import top.anly.domain.MonitorBean;

import javax.servlet.http.HttpServletRequest;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 采集http请求的回应消耗时间
 *
 * @author wangli
 * @date 2020/10/9 17:32
 */
@Slf4j
public class HttpCollect {
    /**
     * 采集目标:拦截java.servlet.service()方法,不能直接采集DispatchServlet或者@Controller注解的类的方法
     * 这里可能不会使用Spring，但是所有的http请求一定都会进入到这个方法中
     */
    private static final String TARGET_CLASS = "javax.servlet.http.HttpServlet";

    private static final String TARGET_METHOD = "service";

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

    public HttpCollect() {
    }

    /**
     * 监控性能入口
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
                    System.out.println("成功加入http的监控");
                    return build(loader);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

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

    public static MonitorBean begin(Object args[]) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) args[0];
        ClassLoader classLoader = args[0].getClass().getClassLoader();
        MonitorBean monitorBean = new MonitorBean();
        monitorBean.setBeginTime(LocalDateTime.now());
        monitorBean.setUrl(httpServletRequest.getRequestURI());
        monitorBean.setModelType("http");
        return monitorBean;
    }

    public static void error(Throwable e, Object obj) {
        MonitorBean monitorBean = (MonitorBean) obj;
        monitorBean.setThrowableReason(e.getMessage());
    }

    public static void end(Object obj) {
        MonitorBean monitorBean = (MonitorBean) obj;
        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(monitorBean.getBeginTime(),endTime);
        monitorBean.setExecutionTime(duration.toMillis());
        System.out.println("-------------------------------------------");
        System.out.println("http执行情况：" + monitorBean.toString());
        log.debug("http执行情况：" + monitorBean.toString());
        System.out.println("-------------------------------------------");
    }

}
