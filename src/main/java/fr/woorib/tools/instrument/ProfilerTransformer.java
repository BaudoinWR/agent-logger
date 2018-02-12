/**
 * Paquet de définition
 **/
package fr.woorib.tools.instrument;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import fr.woorib.tools.instrument.mbean.ProfilerConfiguration;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

/**
 * Description: Merci de donner une description du service rendu par cette classe
 **/
public class ProfilerTransformer implements ClassFileTransformer {
  private static final int THREASHOLD_MS = -1;
  private static final int OFFSET = 66;
  private static final String LOG_OFFSET;

  static {
    StringBuffer buf = new StringBuffer("");
    for (int i = 0; i < OFFSET; i++) {
      buf.append(" ");
    }
    LOG_OFFSET = buf.toString();
  }

  @Override
  public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
    byte[] byteCode = classfileBuffer;
    CtClass ctClass = null;
    try {
      boolean b = classToInstrument(className);
      if (b) {
        ctClass = ClassPool.getDefault().makeClass(new ByteArrayInputStream(
          classfileBuffer));
        if (ctClass.isInterface()) { // || isLogInfo(ctClass)) {

          return byteCode;
        }
        byte[] bytes = addTimers(ctClass);
        //bytes = editCreateStatement(ClassPool.getDefault().makeClass(new ByteArrayInputStream(
        //  bytes)));
//        bytes = logAll(ClassPool.getDefault().makeClass(new ByteArrayInputStream(
//          bytes)));
        byteCode = bytes != null ? bytes : classfileBuffer;
        if (bytes != null) {
          ProfilerConfiguration.instrumented.add(className);
        }
      }
    }
    catch (Throwable e) {
      System.err.println("ERROR FOR " + className);
      e.printStackTrace();
    }
    finally {
      if (ctClass != null) {
        ctClass.detach();
      }
    }

    return byteCode;
  }

  private boolean isLogInfo(CtClass ctClass) {
    try {
      return ctClass.getDeclaredMethod("logInfo") != null;
    }
    catch (NotFoundException e) {
      return false;
    }
  }

  private boolean classToInstrument(String className) {
    if (className == null || className.contains("Profiler")) {
      return false;
    }
    if (ProfilerConfiguration.unInstrumented.contains(className)) {
      return false;
    }
    for (String name : ProfilerConfiguration.classesToInstrument) {
      if (className.contains(name)) {
        return true;
      }
    }

    return false;
  }

  private byte[] addTimers(CtClass ctClass) {
    CtMethod[] nonPrivateMethods = ctClass.getMethods();
    CtMethod[] declaredMethods = ctClass.getDeclaredMethods();
    Set<CtMethod> methods = new HashSet<CtMethod>();
    methods.addAll(Arrays.asList(nonPrivateMethods));
    methods.addAll(Arrays.asList(declaredMethods));
    for (CtMethod method : methods) {
      if (!method.getDeclaringClass().getName().equals("java.lang.Object")) {
        try {
          method.addLocalVariable("startTime", CtClass.longType);
        }
        catch (CannotCompileException e) {
          System.err.println("Erreur d'instrumentation {method=" + method.getLongName() + ";class=" + ctClass.getName() + ";location=addLocalVariable;message=" + e.getMessage() + "}");
          continue;
        }
        try {
          method.insertBefore("fr.woorib.tools.instrument.mbean.Profiler.methodIn(\"" + method.getName() + "\",\"" + ctClass.getName() + "\", $args); startTime = System.nanoTime();");
        }
        catch (CannotCompileException e) {
          System.err.println("Erreur d'instrumentation {method=" + method.getLongName() + ";class=" + ctClass.getName() + ";location=insertBefore;message=" + e.getMessage() + "}");
          continue;
        }
        try {
          method.insertAfter("fr.woorib.tools.instrument.mbean.Profiler.methodOut(\"" + method.getName() + "\",\"" + ctClass.getName() + "\", System.nanoTime() - startTime);");
        }
        catch (CannotCompileException e) {
          System.err.println("Erreur d'instrumentation {method=" + method.getLongName() + ";class=" + ctClass.getName() + ";location=insertAfter;message=" + e.getMessage() + "}");
          continue;
        }
      }
    }
    byte[] bytes = null;
    try {
      bytes = ctClass.toBytecode();
    }
    catch (Exception e) {
      System.err.println("Erreur d'instrumentation {class=" + ctClass.getName() + ";location=toByteCode;message=" + e.getMessage());
    }
    return bytes;

  }

  private String getLogLine(CtMethod method, CtClass ctClass, boolean isEnd) {

    String result = "";
    if (isEnd) {
      result += "if ((System.currentTimeMillis() - startTime)>" + THREASHOLD_MS + ") {";
    }
    result += " String threadName = Thread.currentThread().getName();" +
      " String tName = threadName.length() > 5 ? threadName.substring(threadName.length()-5):threadName;" +
      " logInfo(\"_TIMER_ " + (isEnd ? "E" : "S") + " XXXXXXXXXXXX " + method.getName();
    //" {\"+java.util.Arrays.toString($args)+\"}" + No args as it makes things hard to read because of Strings with linebreaks
    if (isEnd) {
      result += " [\"+(System.currentTimeMillis() - startTime)+\" ms]";
    }
    result += "\");";
    if (isEnd) {
      result += "}";
    }
    return result;
  }
}
 
