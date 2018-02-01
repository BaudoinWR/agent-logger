package fr.woorib.tools.instrument;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;

/**
 * Class used to instrument JDBC drivers in order to log
 * requests with their arguments and execution time.
 */
public class GenericJDBCConnectionTransformer implements ClassFileTransformer {

  private static CtClass connectionClass;

  static {
    try {
      connectionClass = Descriptor.toCtClass("java.sql.Connection", ClassPool.getDefault());
    }
    catch (NotFoundException e) {
      e.printStackTrace();
    }
  }
  public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
    byte[] byteCode = classfileBuffer;
    try {
      CtClass ctClass = ClassPool.getDefault().makeClass(new ByteArrayInputStream(
        classfileBuffer));

      if (!ctClass.isInterface() && ctClass.subtypeOf(connectionClass)) {
        System.err.println("Found for instrumentalization by interface : " + className);
        byte[] bytes = editPrepareStatement(ctClass);
        bytes = editCreateStatement(ClassPool.getDefault().makeClass(new ByteArrayInputStream(
          bytes)));
//        bytes = logAll(ClassPool.getDefault().makeClass(new ByteArrayInputStream(
//          bytes)));
        byteCode = bytes != null ? bytes : classfileBuffer;
        System.err.println(className + " instrumentation complete.");
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    catch (NotFoundException e) {
      e.printStackTrace();
    }

    return byteCode;
  }

  private byte[] editPrepareStatement(CtClass ctClass) {
    byte[] byteCode = null;
    try {
//      CtMethod methodExecuteQuery = ctClass.getMethod("prepareStatement", Descriptor.ofMethod(Descriptor.toCtClass("java.sql.PreparedStatement", ClassPool.getDefault()), new CtClass[]{Descriptor.toCtClass("java.lang.String", ClassPool.getDefault())
//      }));
      CtMethod[] methodsPrepareStatement = getDeclaredMethods(ctClass, "prepareStatement");
      for (CtMethod method : methodsPrepareStatement)
        method.insertAfter("$_ = ($_ instanceof fr.woorib.tools.instrument.PreparedStatementWrapper) ? $_ :  new fr.woorib.tools.instrument.PreparedStatementWrapper($_, $1);");
      byteCode = ctClass.toBytecode();
      ctClass.detach();
    }
    catch (Throwable ex) {
      System.err.println("Exception: " + ex);
      ex.printStackTrace();
    }
    return byteCode;
  }

  private byte[] editCreateStatement(CtClass ctClass) {
    byte[] byteCode = null;
    try {
      CtMethod[] methodsCreateStatement = getDeclaredMethods(ctClass, "createStatement");
      for (CtMethod method : methodsCreateStatement)
        method.insertAfter("$_ = ($_ instanceof fr.woorib.tools.instrument.StatementWrapper) ? $_ :  new fr.woorib.tools.instrument.StatementWrapper($_);");
      byteCode = ctClass.toBytecode();
      ctClass.detach();
    }
    catch (Throwable ex) {
      System.err.println("Exception: " + ex);
      ex.printStackTrace();
    }
    return byteCode;
  }

  private CtMethod[] getDeclaredMethods(CtClass ctClass, String methodName) throws NotFoundException {
    CtMethod[] declaredMethods = ctClass.getDeclaredMethods();
    List<CtMethod> ctMethods = new ArrayList<CtMethod>();
    for (CtMethod method : declaredMethods) {
      if (methodName.equals(method.getName())) {
        ctMethods.add(method);
      }
    }
    return ctMethods.toArray(new CtMethod[0]);
  }

  private byte[] logAll(CtClass ctClass) {
    byte[] byteCode = null;
    try {
      CtMethod[] methodsPrepareStatement = ctClass.getDeclaredMethods();
      for (CtMethod method : methodsPrepareStatement)
        method.insertBefore("System.err.println(\"method_execution{name="+method.getName()+", class="+ctClass.getName()+"}\");");
      byteCode = ctClass.toBytecode();
      ctClass.detach();
    }
    catch (Throwable ex) {
      System.err.println("Exception: " + ex);
      ex.printStackTrace();
    }
    return byteCode;
  }
}
 
