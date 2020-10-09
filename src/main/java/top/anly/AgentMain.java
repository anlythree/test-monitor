package top.anly;

import javassist.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.jws.WebParam;
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

    private static String getVoidStr(String methodName) {
        return "{long startTime = System.currentTimeMillis();\n" +
                "                        try {\n" +
                "                            %s$agent($$);\n" +
                "                        }catch (Exception e){\n" +
                "                            System.out.println(\"" + methodName + "发生异常\"+e.getStackTrace());\n" +
                "                        }finally {\n" +
                "                            System.out.println(\"" + methodName + "消耗时间：\"+(System.currentTimeMillis()-startTime)+\"毫秒\");" +
                "                        }}";
    }

    private static String getNotVoidStr(String methodName) {
        return "{                        long startTime = System.currentTimeMillis();\n" +
                "                        Object res = null;" +
                "                        try {\n" +
                "                            res = %s$agent($$);\n" +
                "                        }catch (Exception e){\n" +
                "                            System.out.println(\"" + methodName + "发生异常\"+e.getStackTrace());\n" +
                "                        }finally {\n" +
                "                            System.out.println(\"" + methodName + "消耗时间：\"+(System.currentTimeMillis()-startTime)+\"毫秒\");" +
                "                        }" +
                "                        return ($r) res;" +
                "}";
    }

    public static void premain(String args, Instrumentation instrumentation) {
        // 打印监控参数
        final ClassPool classPool = new ClassPool();
        classPool.appendSystemPath();
        instrumentation.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader,
                                    String className,
                                    Class<?> classBeingRedefined,
                                    ProtectionDomain protectionDomain,
                                    byte[] classfileBuffer) {
                className = className.replaceAll("/", ".");
                if (!className.contains("ServiceImpl") /*|| !className.contains("business")*/) {
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
                        if ("void".equals(declaredMethod.getReturnType().getName())) {
                            declaredMethod.setBody(String.format(getVoidStr(ctClass.getName() + "类中的" + declaredMethod.getName() + "方法"), declaredMethod.getName()));
                        } else {
                            declaredMethod.setBody(String.format(getNotVoidStr(ctClass.getName() + "类中的" + declaredMethod.getName() + "方法"), declaredMethod.getName()));
                        }

                    }
                    return ctClass.toBytecode();
                } catch (NotFoundException e) {
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });
    }


    /**
     * 构建类
     * @param classLoader
     * @param className
     * @throws NotFoundException
     */
    public void buildClass(ClassLoader classLoader, String className) throws NotFoundException {
        ClassPool classPool = new ClassPool();
        // 使用传参中的类加载器来填充类池
        classPool.insertClassPath(new LoaderClassPath(classLoader));
        CtClass ctClass = classPool.get(className);
        // 遍历所有方法，寻找目标方法
        CtMethod[] declaredMethods = ctClass.getDeclaredMethods();
        for (CtMethod ctMethod : declaredMethods) {
            // 屏蔽公共方法
            int modifiers = ctMethod.getModifiers();
            if (Modifier.isPublic(modifiers)) {
                continue;
            }
            // 屏蔽静态方法
            if (Modifier.isStatic(modifiers)){
                continue;
            }
            // 屏蔽本地方法
            if(Modifier.isNative(modifiers)){
                continue;
            }
            buildMethods();
        }
    }

}
