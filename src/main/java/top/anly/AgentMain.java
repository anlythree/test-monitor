package top.anly;

import javassist.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import top.anly.collect.HttpCollect;

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



    public static void premain(String args, Instrumentation instrumentation) {
        // 监控http性能
        new HttpCollect().transform(instrumentation);
    }


}
