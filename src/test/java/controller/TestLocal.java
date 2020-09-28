package controller;

import lombok.extern.slf4j.Slf4j;


/**
 * @author wangli
 * @date 2020/9/28 11:40
 */
@Slf4j
public class TestLocal {

    public static void print1(){
        System.out.println(1);
    }

    public static void main(String[] args) {
        print1();
        log.info("测试");
    }
}
