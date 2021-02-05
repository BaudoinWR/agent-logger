package fr.woorib.tools.instrument.mbean;

import java.io.Serializable;
import java.util.Arrays;

public class LogLine implements Serializable {
  private final String methodName;
  private final String className;
  private final Boolean isEnd;
  private final String threadName;
  private final String[] args;
  private final Long execution;

  LogLine(String methodName, String className, Boolean isEnd, String threadName, Object[] args, Long execution) {
    this.methodName = methodName;
    this.className = className;
    this.isEnd = isEnd;
    this.threadName = threadName;
    this.args = argsToStringArray(args);
    this.execution = execution;
  }

  private String[] argsToStringArray(Object[] args) {
    String[] result;
    if (args != null) {
      result = new String[args.length];
      for (int i = 0; i < args.length; i++) {
        if (args[i] != null) {
          if (args[i].getClass().isArray()) {
            result[i] = Arrays.toString((Object[]) args[i]);
          }
          else {
            result[i] = args[i].toString();
          }
        }
      }
    }
    else {
      result = new String[0];
    }
    return result;
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

  public Object[] getArgs() {
    return args;
  }
}
