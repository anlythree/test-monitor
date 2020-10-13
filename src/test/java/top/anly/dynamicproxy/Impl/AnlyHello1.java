package top.anly.dynamicproxy.Impl;

import top.anly.dynamicproxy.HelloInterface;

/**
 * @author wangli
 * @date 2020/10/13 11:28
 */
public class AnlyHello1 implements HelloInterface {

    @Override
    public void sayHello(String name) {
        System.out.println("你好" + name);
    }


}
