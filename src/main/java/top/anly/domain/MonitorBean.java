package top.anly.domain;

import java.time.LocalDateTime;

/**
 * 记录http相应实体类
 * @author wangli
 * @date 2020/10/10 9:23
 */
public class MonitorBean {

    /**
     * http的url
     */
    private String url;

    /**
     * 开始时间
     */
    private LocalDateTime beginTime;

    /**
     * 执行时间（秒）
     */
    private long executionTime;

    /**
     * 报错原因（没报错为null）
     */
    private String throwableReason;

    /**
     * 记录类型
     */
    private String modelType;


    public MonitorBean(String url, LocalDateTime beginTime, long executionTime, String throwableReason,String modelType) {
        this.url = url;
        this.beginTime = beginTime;
        this.executionTime = executionTime;
        this.throwableReason = throwableReason;
        this.modelType = modelType;
    }

    public MonitorBean() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDateTime getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(LocalDateTime beginTime) {
        this.beginTime = beginTime;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    public String getThrowableReason() {
        return throwableReason;
    }

    public void setThrowableReason(String throwableReason) {
        this.throwableReason = throwableReason;
    }

    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    @Override
    public String toString() {
        return "MonitorBean{" +
                "url='" + url + '\'' +
                ", beginTime=" + beginTime +
                ", executionTime=" + executionTime +
                ", throwableReason='" + throwableReason + '\'' +
                ", modelType='" + modelType + '\'' +
                '}';
    }
}
