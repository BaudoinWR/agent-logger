/**
 * Paquet de définition
 **/

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.junit.Test;
import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import fr.woorib.tools.instrument.Agent;
import fr.woorib.tools.instrument.mbean.ClassLogFilter;
import fr.woorib.tools.instrument.mbean.LogNotificationListener;
import sun.management.ConnectorAddressLink;

/**
 * Description: Merci de donner une description du service rendu par cette classe
 **/
public class Main {
  public static HashSet<String> LOG_FILTER = new HashSet<String>();

  private static String PID = "6896";

  @Test
  public void test() throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
    VirtualMachine vm = VirtualMachine.attach(PID);
    try {
      vm.loadAgent(System.getProperty("java.home") + File.separator + "lib"
        + File.separator + "management-agent.jar");
      vm.loadAgent("D:\\project\\agent-logger\\target\\agentlogger-jar-with-dependencies.jar");
    }
    finally {
      vm.detach();
    }
    System.out.println(" ran for " + PID);
  }

  public static void main(String[] args) throws IOException, AttachNotSupportedException, MalformedObjectNameException, InstanceNotFoundException, InterruptedException {
    URL[] urls = ((URLClassLoader) Thread.currentThread().getContextClassLoader()).getURLs();
    System.out.println(Arrays.toString(urls));


    //    String connectorAddress = ConnectorAddressLink.importFrom(Integer.parseInt(PID));
//    VirtualMachine vm;
//    vm = VirtualMachine.attach(PID);
    int pid = getPid();
    VirtualMachine vm = VirtualMachine.attach(Integer.toString(pid));
    //String connectorAddress = vm.startLocalManagementAgent();
    String connectorAddress = ConnectorAddressLink.importFrom(pid);
    if (connectorAddress == null) {
    }
    JMXServiceURL url = new JMXServiceURL(connectorAddress);

    JMXConnector connector = null;
    try {
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

  private static int getPid() {
    List<VirtualMachineDescriptor> list = VirtualMachine.list();
    int i = 0;
    for (VirtualMachineDescriptor desc : list) {
      System.out.println(i++ + " - " + desc.id() + " - " + desc.displayName());
    }
    Scanner s = new Scanner(System.in);
    String s1 = s.nextLine();
    VirtualMachineDescriptor virtualMachineDescriptor = list.get(Integer.parseInt(s1));
    return Integer.parseInt(virtualMachineDescriptor.id());
  }

}
