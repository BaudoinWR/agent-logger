package fr.woorib.tools.jdbc.instrument;

import java.lang.instrument.Instrumentation;

public class Agent {
  public static void premain(String agentArgs, Instrumentation inst) throws ClassNotFoundException {

    System.out.print("Executing premain.........");
    try {
//      inst.addTransformer(new GenericJDBCConnectionTransformer(), true);
//      inst.addTransformer(new HttpUrlConnectionLogger(), true);
//      inst.addTransformer(new ChemistryDefaultHttpInvokerLogger(), true);
      inst.addTransformer(new JspCompilationContextTransformer());
    }
    catch (Exception e) {
      System.err.println("failed");
      e.printStackTrace();
      return;
    }
    System.out.println("done");
  }

}
 
