package fr.woorib.tools.instrument.mbean;

import java.io.Serializable;

public class LogLine implements Serializable {
    private final String methodName;
    private final String className;
    private final Boolean isEnd;
    private final String threadName;
    private final Long execution;

    LogLine(String methodName, String className, Boolean isEnd, String threadName, Long execution) {
        this.methodName = methodName;
        this.className = className;
        this.isEnd = isEnd;
        this.threadName = threadName;
        this.execution = execution;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getClassName() {
        return className;
    }

    public Boolean getEnd() {
        return isEnd;
    }

    public String getThreadName() {
        return threadName;
    }

    public Long getExecution() {
        return execution;
    }
}
