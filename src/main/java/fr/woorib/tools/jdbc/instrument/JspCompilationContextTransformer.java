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
  @Override
  public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
    byte[] byteCode = classfileBuffer;
    CtClass ctClass = null;
    try {
      if (className.contains("JspCompilationContext") || className.contains("Compiler")) {

        ctClass = ClassPool.getDefault().makeClass(new ByteArrayInputStream(
          classfileBuffer));
        if (ctClass.isInterface()) {
          ctClass.detach();

          return byteCode;
        }
        System.err.println("Found for instrumentalization : " + className);
        byte[] bytes = addTimers(ctClass);
        //bytes = editCreateStatement(ClassPool.getDefault().makeClass(new ByteArrayInputStream(
        //  bytes)));
//        bytes = logAll(ClassPool.getDefault().makeClass(new ByteArrayInputStream(
//          bytes)));
        byteCode = bytes != null ? bytes : classfileBuffer;
        System.err.println(className + " instrumentation complete.");

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

  private byte[] addTimers(CtClass ctClass) {
    CtMethod[] methods = ctClass.getMethods();
    for(CtMethod method : methods) {
      try {
        method.addLocalVariable("startTime", CtClass.longType);
      }
      catch (CannotCompileException e) {
        System.err.println("Erreur d'instrumentation {method="+method.getLongName()+";class="+ctClass.getName()+";location=addLocalVariable;message="+e.getMessage()+"}");
        continue;
      }
      try {
        method.insertBefore("startTime = System.currentTimeMillis();");
      }
      catch (CannotCompileException e) {
        System.err.println("Erreur d'instrumentation {method="+method.getLongName()+";class="+ctClass.getName()+";location=insertBefore;message="+e.getMessage()+"}");
        continue;
      }
      try {
        method.insertAfter("System.err.println(\"Method "+ctClass.getName() +"_"+ method.getLongName()+" took \"+(System.currentTimeMillis() - startTime)+\"ms\");");
      }
      catch (CannotCompileException e) {
        System.err.println("Erreur d'instrumentation {method="+method.getLongName()+";class="+ctClass.getName()+";location=insertAfter;message="+e.getMessage()+"}");
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
}
 
