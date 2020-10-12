package top.anly.common.adapter;

import java.lang.reflect.Method;

/**
 * 使用反射机制实现的适配器，用于获取httpServletRequest对象的相关方法
 * @author wangli
 * @date 2020/10/12 11:46
 */
public class HttpServletRequestAdapter {
    /*private final Object obj;

    private final Method _getRequestURI;

    private final Method _getRequestURL;

//    private final Method _getParameterMap;

//    private final Method _getMethod;

    private final Method _getHeader;

    private final Method _getRemoteAddr;

    private final static String targetClassName = "javax.servlet.http.HttpServletRequest";

    public HttpServletRequestAdapter(Object target){
        this.obj = target;
        try {
            Class<?> targetClass = target.getClass().getClassLoader().loadClass(targetClassName);
            _getRequestURI = targetClass.getMethod("getRequestURI");
            _getRequestURL = targetClass.getMethod("getRequestURL");
            _getHeader = targetClass.getMethod("getHeader", String.class);
            _getRemoteAddr = targetClass.getMethod("getRemoteAddr");
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public String getRemoteAddr(){
        try {
            return (String)_getRemoteAddr.invoke(targetClassName);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }*/
}
