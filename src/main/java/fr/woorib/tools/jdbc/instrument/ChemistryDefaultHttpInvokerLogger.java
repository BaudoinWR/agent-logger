package fr.woorib.tools.jdbc.instrument;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import fr.woorib.tools.jdbc.instrument.exception.JDBCTransformException;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;

public class ChemistryDefaultHttpInvokerLogger implements ClassFileTransformer {

  @Override
  public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
    byte[] byteCode = classfileBuffer;
    if (className.equals("org/apache/chemistry/opencmis/client/bindings/spi/http/DefaultHttpInvoker")) {
      try {
        byteCode = editDefaultHttpInvokern(className, classfileBuffer);
      }
      catch (Exception e) {
        throw new JDBCTransformException(e);
      }
    }
    return byteCode;
  }

  private byte[] editDefaultHttpInvokern(String className, byte[] classfileBuffer) {
    byte[] byteCode = classfileBuffer;
    System.err.println("Found for instrumentalization : " + className);
    try {
      CtClass ctClass = ClassPool.getDefault().makeClass(new ByteArrayInputStream(
        classfileBuffer));

      editInvoke(ctClass);

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

  private void editInvoke(CtClass ctClass) throws NotFoundException, CannotCompileException {
    CtMethod invoke = ctClass.getMethod("invoke", Descriptor.ofMethod(
      Descriptor.toCtClass("org.apache.chemistry.opencmis.client.bindings.spi.http.Response", ClassPool.getDefault()), new CtClass[] {
        Descriptor.toCtClass("org.apache.chemistry.opencmis.commons.impl.UrlBuilder", ClassPool.getDefault()),
        Descriptor.toCtClass("java.lang.String", ClassPool.getDefault()),
        Descriptor.toCtClass("java.lang.String", ClassPool.getDefault()),
        Descriptor.toCtClass("java.util.Map", ClassPool.getDefault()),
        Descriptor.toCtClass("org.apache.chemistry.opencmis.client.bindings.spi.http.Output", ClassPool.getDefault()),
        Descriptor.toCtClass("org.apache.chemistry.opencmis.client.bindings.spi.BindingSession", ClassPool.getDefault()),
        Descriptor.toCtClass("java.math.BigInteger", ClassPool.getDefault()),
        Descriptor.toCtClass("java.math.BigInteger", ClassPool.getDefault())
      }
    ));
    invoke.insertBefore("System.out.println(\"{url=\"+$1+\",method=\"+$2+\",contentType=\"+$3+\",headers=\"+$4+\"}\");");
  }

}

