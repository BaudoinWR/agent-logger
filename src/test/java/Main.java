/**
 * Paquet de définition
 **/

import java.io.IOException;
import java.util.HashSet;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.junit.Test;
import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import fr.woorib.tools.instrument.Agent;
import fr.woorib.tools.instrument.mbean.ClassLogFilter;
import fr.woorib.tools.instrument.mbean.LogNotificationListener;
import sun.management.ConnectorAddressLink;

/**
 * Description: Merci de donner une description du service rendu par cette classe
 **/
public class Main {
  public static HashSet<String> LOG_FILTER = new HashSet<String>();

  private static String PID = "4964";

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
//    String connectorAddress = ConnectorAddressLink.importFrom(Integer.parseInt(PID));
    VirtualMachine vm;
    vm = VirtualMachine.attach(PID);
    String connectorAddress = vm.startLocalManagementAgent();

    if (connectorAddress == null) {
    }
    JMXServiceURL url = new JMXServiceURL(connectorAddress);

    JMXConnector connector = null;
    try {
      LOG_FILTER.add("Beacon");
      connector = JMXConnectorFactory.connect(url);
      MBeanServerConnection mbeanConn = connector.getMBeanServerConnection();
      ObjectName objectName = new ObjectName(Agent.PROFILER_MBEAN_NAME);
      mbeanConn.addNotificationListener(objectName, new LogNotificationListener(),
        new ClassLogFilter(LOG_FILTER), null);

      //mbeanConn.invoke(new ObjectName(Agent.PROFILER_CONFIGURATION_MBEAN_NAME), "addClassPattern", new Object[]{"DDE"}, new String[]{"java.lang.String"});

      while (true) {
        Thread.sleep(10000);
      }
    }
    finally {
      if (connector != null) {
        connector.close();
      }
    }
  }

}
