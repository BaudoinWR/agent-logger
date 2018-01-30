/**
 * Paquet de définition
 **/
package fr.woorib.tools.jdbc.instrument.mbean;

/**
 * Description: Merci de donner une description du service rendu par cette classe
 **/
public class Profiler {
  private static StringBuilder sBuilder = new StringBuilder();

  public static void methodIn(String methodName, String className) {
    synchronized (sBuilder) {
      logLine(methodName, className, false);
      sBuilder.append("\n");
    }
  }

  private static void logLine(String methodName, String className, boolean isEnd) {
    String threadName = Thread.currentThread().getName();
    String tName = threadName.length() > 5 ? threadName.substring(threadName.length()-5):threadName;
    sBuilder.append("XX:XX:XX AGENT "+ className +" ["+tName+"] _TIMER_ "+(isEnd?"E":"S")+" XXXXXXXXXXXX "+ methodName);

  }

  public static void methodOut(String methodName, String className, long execution) {
    synchronized (sBuilder) {
      logLine(methodName, className, true);
      sBuilder.append(" ["+execution+" ms]\n");
    }
  }

  public static void printProfiling() {
    synchronized (sBuilder) {
      System.out.println(sBuilder.toString());
      sBuilder = new StringBuilder();
    }
  }
}
 
