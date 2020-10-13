package top.anly.domain;

import java.time.LocalDateTime;

/**
 * @author wangli
 * @date 2020/10/13 15:17
 */
public class SqlBean {

    private String sql;

    private String jdbcUrl;

    private String error;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    public SqlBean(String sql, String jdbcUrl, String error, LocalDateTime startTime, LocalDateTime endTime) {
        this.sql = sql;
        this.jdbcUrl = jdbcUrl;
        this.error = error;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public SqlBean() {
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
