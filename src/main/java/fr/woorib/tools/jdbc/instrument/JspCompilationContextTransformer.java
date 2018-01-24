/**
 * Paquet de définition
 **/
package fr.woorib.tools.jdbc.instrument;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

/**
 * Description: Merci de donner une description du service rendu par cette classe
 **/
public class JspCompilationContextTransformer implements ClassFileTransformer {
  private static final int THREASHOLD_MS = -1;
  private static final int OFFSET = 66;
  private static final String LOG_OFFSET;

  private static final String[] CLASSES_TO_INSTRUMENT = {
    "AbstractFindServiceAction",
    "DDEFindServiceAction",
    //"JspCompilationContext",
    //"Compiler"
  };

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
      if (classToInstrument(className)) {

        ctClass = ClassPool.getDefault().makeClass(new ByteArrayInputStream(
          classfileBuffer));
        if (ctClass.isInterface()) {
          ctClass.detach();

          return byteCode;
        }
        //System.err.println("Found for instrumentalization : " + className);
        byte[] bytes = addTimers(ctClass);
        //bytes = editCreateStatement(ClassPool.getDefault().makeClass(new ByteArrayInputStream(
        //  bytes)));
//        bytes = logAll(ClassPool.getDefault().makeClass(new ByteArrayInputStream(
//          bytes)));
        byteCode = bytes != null ? bytes : classfileBuffer;
        //System.err.println(className + " instrumentation complete.");

      }
    }
    catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (ctClass != null) {
        ctClass.detach();
      }
    }

    return byteCode;
  }

  private boolean classToInstrument(String className) {
    for (String name : CLASSES_TO_INSTRUMENT) {
      if (className.contains(name)) {
        return true;
      }
    }
    return false;
  }

  private byte[] addTimers(CtClass ctClass) {
    CtMethod[] methods = ctClass.getMethods();
    for(CtMethod method : methods) {
      try {
        method.addLocalVariable("startTime", CtClass.longType);
      }
      catch (CannotCompileException e) {
        //System.err.println("Erreur d'instrumentation {method="+method.getLongName()+";class="+ctClass.getName()+";location=addLocalVariable;message="+e.getMessage()+"}");
        continue;
      }
      try {
        method.insertBefore(getLogLine(method, ctClass, false) + "startTime = System.currentTimeMillis();");
      }
      catch (CannotCompileException e) {
        //System.err.println("Erreur d'instrumentation {method="+method.getLongName()+";class="+ctClass.getName()+";location=insertBefore;message="+e.getMessage()+"}");
        continue;
      }
      try {
        method.insertAfter(getLogLine(method, ctClass, true));
      }
      catch (CannotCompileException e) {
        //System.err.println("Erreur d'instrumentation {method="+method.getLongName()+";class="+ctClass.getName()+";location=insertAfter;message="+e.getMessage()+"}");
        continue;
      }
    }
    byte[] bytes = null;
    try {
      bytes = ctClass.toBytecode();
    }
    catch (Exception e) {
      System.err.println("Erreur d'instrumentation {class="+ctClass.getName()+";location=toByteCode;message="+e.getMessage());
    }
    return bytes;

  }

  private String getLogLine(CtMethod method, CtClass ctClass, boolean isEnd) {
    String ctClassName = ctClass.getName().length() > 50 ? ctClass.getName().substring(ctClass.getName().length() - 50):ctClass.getName();

    String result = "";
      if (isEnd) result += "if ((System.currentTimeMillis() - startTime)>" + THREASHOLD_MS + ") {";
      result += " String threadName = Thread.currentThread().getName();" +
      " String tName = threadName.length() > 5 ? threadName.substring(threadName.length()-5):threadName;" +
      " System.err.println(\"XX:XX:XX AGENT "+ ctClassName +" [\"+tName+\"] _TIMER_ "+(isEnd?"E":"S")+" XXXXXXXXXXXX "+ method.getName();
      //" {\"+java.util.Arrays.toString($args)+\"}" + No args as it makes things hard to read because of Strings with linebreaks
      if (isEnd) result +=" [\"+(System.currentTimeMillis() - startTime)+\" ms]";
      result +="\");";
      if (isEnd) result += "}";
      return result;
  }
}
 
