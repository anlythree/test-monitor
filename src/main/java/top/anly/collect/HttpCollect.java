package top.anly.collect;

import javassist.*;

/**
 * @author wangli
 * @date 2020/10/9 17:32
 */
public class HttpCollect {
    // 采集目标:拦截java.servlet.service()方法

    private static final String TARGET_CLASS = "java.servlet.http.HttpServlet";

    private static final String TARGET_METHOD = "service";

    private static final String VOID_METHOD_BODY = "";

    public HttpCollect(){
    }

    {
        try{
            long millisStart = System.currentTimeMillis();
            serviceCopy($$);
            long millisEnd = System.currentTimeMillis();
            System.out.println("http");
        }catch (Exception e){

        }
    }

    public Byte[] build(ClassLoader classLoader) throws Exception{
        ClassPool classPool = new ClassPool();
        classPool.insertClassPath(new LoaderClassPath(classLoader));
        // 查找目标类下的目标方法
        CtClass ctClass = classPool.get(TARGET_CLASS);
        CtMethod oldMethod = ctClass.getDeclaredMethod(TARGET_METHOD);
        CtMethod newMethod = CtNewMethod.copy(oldMethod, ctClass, null);
        String beginStr = "";
        String errorStr = "";
        String endStr = "";

        newMethod.setBody();
    }

}
