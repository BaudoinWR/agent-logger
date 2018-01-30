/**
 * Paquet de définition
 **/

import java.io.IOException;
import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

/**
 * Description: Merci de donner une description du service rendu par cette classe
 **/
public class Main {
  private static String PID = "4908";

  public static void main(String[] args) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
    VirtualMachine vm = VirtualMachine.attach(PID);
    try {
      vm.loadAgent("D:\\project\\agent-logger\\target\\agentlogger-jar-with-dependencies.jar");
    }
    finally {
      vm.detach();
    }
    System.out.println(" ran for " + PID);
  }

}
