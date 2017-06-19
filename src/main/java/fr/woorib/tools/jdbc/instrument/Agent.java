package fr.woorib.tools.jdbc.instrument;

import java.lang.instrument.Instrumentation;

public class Agent {
  public static void premain(String agentArgs, Instrumentation inst) throws ClassNotFoundException {
    System.out.print("Executing premain.........");
    try {
      inst.addTransformer(new JDBCConnectionTransformer(), true);
    }
    catch (Exception e) {
      System.err.println("faied");
      e.printStackTrace();
      return;
    }
    System.out.println("done");
  }

}
 
