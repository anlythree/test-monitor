package top.anly;

import javassist.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

/**
 * 监听启动类
 *
 * @author wangli
 * @date 2020/9/27 14:38
 */
@Data
@Slf4j
public class AgentMain {

    public static void premain(String args, Instrumentation instrumentation) {
        // 打印监控参数
        final ClassPool classPool = new ClassPool();
        classPool.appendSystemPath();
        instrumentation.addTransformer(new ClassFileTransformer() {
            public byte[] transform(ClassLoader loader,
                                    String className,
                                    Class<?> classBeingRedefined,
                                    ProtectionDomain protectionDomain,
                                    byte[] classfileBuffer) {
                className = className.replaceAll("/",".");
                if (!className.contains("controller")) {
                    return null;
                }
                CtClass ctClass = null;
                try {
                    ctClass = classPool.get(className);
                    // 统计这个方法的执行时间
                    for (CtMethod declaredMethod : ctClass.getDeclaredMethods()) {
                        // 打印所有方法的执行时间至日志中
                        CtMethod copyMethod = CtNewMethod.copy(declaredMethod, ctClass, null);
                        copyMethod.setName(declaredMethod.getName() + "$agent");
                        ctClass.addMethod(copyMethod);
                        declaredMethod.setBody(String.format("{long startTime = System.currentTimeMillis();\n" +
                                "                        try {\n" +
                                "                            %s$agent($$);\n" +
                                "                        }catch (Exception e){\n" +
                                "                            log.debug(\"发生异常\"+e.toString());\n" +
                                "                        }finally {\n" +
                                "                            System.out.println(\""+declaredMethod.getName()+"消耗时间：\"+(System.currentTimeMillis()-startTime)+\"毫秒\");" +
//                                "                            log.debug(\"消耗时间：\"+(System.currentTimeMillis()-startTime));"+
                                "                        }}",declaredMethod.getName()));
                    }
                    return ctClass.toBytecode();
                }catch (Exception e){
                    e.printStackTrace();
                    return null;
                }
            }
        });
    }

}
