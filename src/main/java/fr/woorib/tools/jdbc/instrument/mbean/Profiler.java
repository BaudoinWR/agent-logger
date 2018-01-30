/**
 * Paquet de définition
 **/
package fr.woorib.tools.jdbc.instrument.mbean;

import javax.management.*;

/**
 * Description: Merci de donner une description du service rendu par cette classe
 **/
public class Profiler implements ProfilerMBean, NotificationBroadcaster {
  private static NotificationBroadcasterSupport notificationBroadcasterSupport = new NotificationBroadcasterSupport();

  private static int notification = 0;

  public static void methodIn(String methodName, String className) {
    notificationBroadcasterSupport.sendNotification(new Notification("logLine",
            new LogLine(methodName, className, false, Thread.currentThread().getName(), 0l),
            notification++
    ));
  }

  public static void methodOut(String methodName, String className, long execution) {
    notificationBroadcasterSupport.sendNotification(new Notification("logLine",
            new LogLine(methodName, className, true, Thread.currentThread().getName(), execution),
            notification++
    ));
  }

  @Override
  public void addNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback) throws IllegalArgumentException {
    notificationBroadcasterSupport.addNotificationListener(listener, filter, handback);
  }

  @Override
  public void removeNotificationListener(NotificationListener listener) throws ListenerNotFoundException {
    notificationBroadcasterSupport.removeNotificationListener(listener);

  }

  @Override
  public MBeanNotificationInfo[] getNotificationInfo() {
    return notificationBroadcasterSupport.getNotificationInfo();
  }
}
 
