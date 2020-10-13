package top.anly.dynamicproxy;

import top.anly.dynamicproxy.Impl.AnlyHello1;
import top.anly.dynamicproxy.proxy.HelloInterfaceProxy;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;


/**
 * @author wangli
 * @date 2020/9/28 11:40
 */
@Slf4j
public class TestLocal {

    public static void print1() {
        System.out.println(1);
    }

    public static void main(String[] args) {
        // 无论这个实现类的类型是什么，只要这个类继承自目标接口，都可以用下面的anlyHelloProxy去添加代理逻辑，相比较静态代理更加灵活
        // 下面的两个anlyHello无论放开哪一个代码都可以正常运行
        AnlyHello1 anlyHello = new AnlyHello1();
//        AnlyHello2 anlyHello = new AnlyHello2();
        HelloInterfaceProxy helloInterfaceProxy = new HelloInterfaceProxy(anlyHello);
        HelloInterface helloInterface = (HelloInterface)Proxy.newProxyInstance(anlyHello.getClass().getClassLoader(), anlyHello.getClass().getInterfaces(), helloInterfaceProxy);
        helloInterface.sayHello("王力");
    }


}
