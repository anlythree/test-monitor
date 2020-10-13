package top.anly.dynamicproxy.proxy;

import top.anly.dynamicproxy.HelloInterface;
import top.anly.dynamicproxy.Impl.AnlyHello1;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author wangli
 * @date 2020/10/13 11:31
 */
public class HelloInterfaceProxy implements InvocationHandler {

    private HelloInterface helloInterface = new AnlyHello1();

    public HelloInterfaceProxy(Object obj) {
        this.helloInterface = (HelloInterface) obj;
    }

    public HelloInterfaceProxy() {
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("Before invoke "+method.getName());
        method.invoke(helloInterface,args);
        System.out.println("After invoke "+method.getName());
        return null;
    }



}
