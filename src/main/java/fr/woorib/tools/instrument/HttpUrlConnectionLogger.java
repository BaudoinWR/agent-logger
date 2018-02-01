package fr.woorib.tools.instrument;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;

/**
 * Adds logging to Input and Output streams of classes extending java.net.URLConnection
 */
public class HttpUrlConnectionLogger implements ClassFileTransformer {
  private CtClass urlConnectionCtClass;

  public HttpUrlConnectionLogger() {
    try {
      urlConnectionCtClass = Descriptor.toCtClass("java.net.URLConnection", ClassPool.getDefault());
    }
    catch (NotFoundException e) {
      e.printStackTrace();
    }
  }

  @Override
  public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
    byte[] byteCode = classfileBuffer;
    try {
      CtClass ctClass = ClassPool.getDefault().makeClass(new ByteArrayInputStream(
        classfileBuffer));
      if (ctClass.subtypeOf(urlConnectionCtClass)) {
        byteCode = editURLConnection(className, classfileBuffer);
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

  /**
   * Adds a requestProperties map to store key/value for request headers.
   * Wraps InputStream and OutputStreams into logger class.
   * @param className
   * @param classfileBuffer
   * @return
   */
  private byte[] editURLConnection(String className, byte[] classfileBuffer) {
    byte[] byteCode = classfileBuffer;
    System.err.println("Found for instrumentalization : " + className);
    try {
      CtClass ctClass = ClassPool.getDefault().makeClass(new ByteArrayInputStream(
        classfileBuffer));
      CtField requestProperties = new CtField(Descriptor.toCtClass("java.util.Map", ClassPool.getDefault()), "requestProperties",ctClass);
      ctClass.addField(requestProperties, "new java.util.HashMap()");
      editGetInputStream(ctClass);
      editGetOutputStream(ctClass);
      editSetRequestProperty(ctClass);
      editAddRequestProperty(ctClass);
      byteCode = ctClass.toBytecode();
      ctClass.detach();
      System.err.println(className + " instrumentation complete.");
    }
    catch (Throwable ex) {
      System.err.println("Exception: " + ex);
      ex.printStackTrace();
    }
    return byteCode;
  }

  private void editSetRequestProperty(CtClass ctClass) throws CannotCompileException, NotFoundException {
    String methodName = "setRequestProperty";
    CtClass[] methodParams = {
      Descriptor.toCtClass("java.lang.String", ClassPool.getDefault()),
      Descriptor.toCtClass("java.lang.String", ClassPool.getDefault())
    };
    String src = "public void setRequestProperty(String key, String value) { " +
      "super.setRequestProperty(key, value);" +
      " }";

    CtMethod setRequestProperty = getCtMethodOrMake(ctClass, methodName, methodParams, src);
    setRequestProperty.insertAfter("this.requestProperties.put($1, $2);");
  }

  private void editAddRequestProperty(CtClass ctClass) throws CannotCompileException, NotFoundException {
    String methodName = "addRequestProperty";
    CtClass[] methodParams = {
      Descriptor.toCtClass("java.lang.String", ClassPool.getDefault()),
      Descriptor.toCtClass("java.lang.String", ClassPool.getDefault())
    };
    String src = "public void addRequestProperty(String key, String value) { " +
      "super.addRequestProperty(key, value);" +
      " }";

    CtMethod setRequestProperty = getCtMethodOrMake(ctClass, methodName, methodParams, src);
    setRequestProperty.insertAfter("this.requestProperties.put($1, $2);");
  }

  private CtMethod getCtMethodOrMake(CtClass ctClass, String methodName, CtClass[] methodParams, String src) throws CannotCompileException {
    CtMethod setRequestProperty;
    try {
      setRequestProperty = ctClass.getDeclaredMethod(methodName, methodParams
      );
    }
    catch (NotFoundException e) {
      setRequestProperty = CtNewMethod.make(
        src,
        ctClass);
      ctClass.addMethod(setRequestProperty);
    }
    return setRequestProperty;
  }

  private void editGetInputStream(CtClass ctClass) throws NotFoundException, CannotCompileException {
    CtMethod getInputStream = getCtMethodOrMake(ctClass, "getInputStream", null, "public java.io.InputStream getInputStream() { return super.getInputStream(); }");
    getInputStream.insertAfter("$_ = ($_ instanceof fr.woorib.tools.jdbc.instrument.InputStreamWrapper) ? $_ : new fr.woorib.tools.jdbc.instrument.InputStreamWrapper($_, this.url, this.requestProperties);");
  }

  private void editGetOutputStream(CtClass ctClass) throws NotFoundException, CannotCompileException {
    CtMethod getOutputStream = getCtMethodOrMake(ctClass, "getOutputStream", null, "public java.io.OutputStream getOutputStream() { return super.getOutputStream(); }");
    getOutputStream.insertAfter("$_ = ($_ instanceof fr.woorib.tools.jdbc.instrument.OutputStreamWrapper) ? $_ :  new fr.woorib.tools.jdbc.instrument.OutputStreamWrapper($_, this.url, this.requestProperties);");
  }

}

