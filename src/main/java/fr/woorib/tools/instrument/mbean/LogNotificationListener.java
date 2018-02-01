package fr.woorib.tools.instrument.mbean; /**
 * Paquet de définition
 **/

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.management.Notification;
import javax.management.NotificationListener;

/**
 * Description: Merci de donner une description du service rendu par cette classe
 **/
public class LogNotificationListener implements NotificationListener {
  @Override
  public void handleNotification(Notification notification, Object handback) {
    LogLine source = (LogLine) notification.getSource();
    long timeStamp = notification.getTimeStamp();
    Date date = new Date(timeStamp);
    String format = new SimpleDateFormat("HH:mm:ss").format(date);
    String fullClassName = source.getClassName();
    String className = fullClassName.length() > 50 ? fullClassName.substring(fullClassName.length() - 50) : fullClassName;
    System.out.print(format + " AGENT " + className + " [" + source.getThreadName() + "] _TIMER_ " + (source.getEnd() ? "E" : "S") + " XXXXXXXXXXXX " + source.getMethodName());
    if (source.getEnd()) {
      String execution = new DecimalFormat().format(source.getExecution());
      System.out.print(" [" + execution + " ns]");
    }
    System.out.println();
  }
}
 
