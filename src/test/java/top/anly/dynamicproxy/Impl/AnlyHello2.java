package top.anly.dynamicproxy.Impl;

import top.anly.dynamicproxy.HelloInterface;

/**
 * @author wangli
 * @date 2020/10/13 11:49
 */
public class AnlyHello2 implements HelloInterface {

    @Override
    public void sayHello(String name) {
        System.out.println("hello " + name);
    }
}
