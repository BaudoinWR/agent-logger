package fr.woorib.tools.jdbc.instrument;

import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import fr.woorib.tools.jdbc.instrument.mbean.ProfilerConfiguration;
import fr.woorib.tools.jdbc.instrument.mbean.ProfilerConfigurationMBean;

public class Agent {
  public static void premain(String agentArgs, Instrumentation inst) throws ClassNotFoundException, MalformedObjectNameException, NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException {
    System.out.print("Executing premain.........");
    try {
//      inst.addTransformer(new GenericJDBCConnectionTransformer(), true);
//      inst.addTransformer(new HttpUrlConnectionLogger(), true);
//      inst.addTransformer(new ChemistryDefaultHttpInvokerLogger(), true);
      inst.addTransformer(new ProfilerTransformer(), true);
    }
    catch (Exception e) {
      System.err.println("failed");
      e.printStackTrace();
      return;
    }

    MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
    ObjectName name = new ObjectName("fr.woorib:type=Profiler");
    ProfilerConfigurationMBean mBean = new ProfilerConfiguration(inst);
    mbs.registerMBean(mBean, name);

    System.out.println("done");
  }


  public static void agentmain(String agentArgs, Instrumentation inst) throws ClassNotFoundException, MalformedObjectNameException, NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException {
    System.out.print("Executing premain.........");

    MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
    ObjectName name = new ObjectName("fr.woorib:type=Profiler");
    ProfilerConfigurationMBean mBean = new ProfilerConfiguration(inst);
    mbs.registerMBean(mBean, name);

    try {
//      inst.addTransformer(new GenericJDBCConnectionTransformer(), true);
//      inst.addTransformer(new HttpUrlConnectionLogger(), true);
//      inst.addTransformer(new ChemistryDefaultHttpInvokerLogger(), true);
      inst.addTransformer(new ProfilerTransformer(), true);
    }
    catch (Exception e) {
      System.err.println("failed");
      e.printStackTrace();
      return;
    }

    System.out.println("done");
  }

}
 
