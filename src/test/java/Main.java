/**
 * Paquet de définition
 **/

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Set;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import fr.woorib.tools.jdbc.instrument.Agent;
import fr.woorib.tools.jdbc.instrument.mbean.LogLine;
import fr.woorib.tools.jdbc.instrument.mbean.Profiler;
import fr.woorib.tools.jdbc.instrument.mbean.ProfilerMBean;
import org.junit.Test;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

/**
 * Description: Merci de donner une description du service rendu par cette classe
 **/
public class Main {
  private static String PID = "2164";

  public static void main(String[] args) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
    VirtualMachine vm = VirtualMachine.attach(PID);
    try {
      vm.loadAgent("D:\\git\\agent-logger\\target\\agentlogger-jar-with-dependencies.jar");
    }
    finally {
      vm.detach();
    }
    System.out.println(" ran for " + PID);
  }

  @Test
  public void test() throws IOException, AttachNotSupportedException, MalformedObjectNameException, InstanceNotFoundException, InterruptedException {
    VirtualMachine vm;
    vm = VirtualMachine.attach(PID);
    String connectorAddress = vm.startLocalManagementAgent();
    if (connectorAddress == null) {
    }
    JMXServiceURL url = new JMXServiceURL(connectorAddress);

    try (JMXConnector connector = JMXConnectorFactory.connect(url)){
      MBeanServerConnection mbeanConn = connector.getMBeanServerConnection();
      ObjectName objectName = new ObjectName(Agent.PROFILER_MBEAN_NAME);
      mbeanConn.addNotificationListener(objectName, new NotificationListener() {
        @Override
        public void handleNotification(Notification notification, Object handback) {
          LogLine source = (LogLine) notification.getSource();
          long timeStamp = notification.getTimeStamp();
          Date date = new Date(timeStamp);
          String format = new SimpleDateFormat("HH:mm:ss").format(date);
          System.out.print(format+" AGENT "+ source.getClassName() +" ["+source.getThreadName()+"] _TIMER_ "+(source.getEnd()?"E":"S")+" XXXXXXXXXXXX "+ source.getMethodName());
          if (source.getEnd()) {
            System.out.print(" ["+source.getExecution()+" ms]");
          }
          System.out.println();
        }
      }, null, null);

      while(true) {
        Thread.sleep(10000);
      }
    }
  }

}
