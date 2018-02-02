package fr.woorib.tools.instrument.mbean; /**
 * Paquet de définition
 **/

import java.io.Serializable;
import java.util.Set;
import javax.management.Notification;
import javax.management.NotificationFilter;

/**
 * Description: Merci de donner une description du service rendu par cette classe
 **/
public class ClassLogFilter implements NotificationFilter, Serializable {

  private final Set<String> filterPatterns;

  public ClassLogFilter(Set<String> filterPatterns) {
    this.filterPatterns = filterPatterns;
  }

  @Override
  public boolean isNotificationEnabled(Notification notification) {
    if (filterPatterns == null || filterPatterns.isEmpty()) {
      return true;
    }
    LogLine source = (LogLine) notification.getSource();
    for (String filter : filterPatterns) {
      if (source.getClassName().contains(filter)) {
        return true;
      }
    }
    return false;
  }
}
 
