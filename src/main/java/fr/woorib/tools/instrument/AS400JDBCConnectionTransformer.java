package fr.woorib.tools.instrument;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import fr.woorib.tools.instrument.exception.JDBCTransformException;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;

/**
 * Class used to instrument an AS400 JDBC driver in order to log
 * requests with their arguments and execution time.
 */
public class AS400JDBCConnectionTransformer implements ClassFileTransformer {
  ClassPool classPool;
  List<String> alreadyLoaded = new ArrayList<String>();
  public AS400JDBCConnectionTransformer() throws NotFoundException, IOException, CannotCompileException, NoSuchMethodException {
    classPool = ClassPool.getDefault();
    addAllClasspath(classPool);
  }

  /**
   * Instantiates a Class object and calls the defineClass and resolveClass on the ClassLoader object.
   * @param classname
   * @param loader
   * @throws IOException
   * @throws CannotCompileException
   * @throws NotFoundException
   * @throws NoSuchMethodException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   */
  private void injectClassIntoLoader(String classname, ClassLoader loader) throws IOException, CannotCompileException, NotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, IllegalClassFormatException {
    if (alreadyLoaded.contains(classname)) {
      return;
    }
    alreadyLoaded.add(classname);
    byte[] bytes = classPool.getCtClass(classname).toBytecode();
    Method method = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
    method.setAccessible(true);
    bytes = transform(loader, classname.replaceAll(".", "/"), null, null, bytes);
    if (bytes != null) {
      Class<?> r = (Class<?>) method.invoke(loader, classname, bytes, 0, bytes.length);
    }
//    method = ClassLoader.class.getDeclaredMethod("resolveClass", Class.class);
//    method.setAccessible(true);
//    method.invoke(loader, r);
  }

  public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                          ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
    byte[] byteCode = classfileBuffer;
    if (className.equals("com/ibm/as400/access/AS400JDBCPreparedStatement")) {
      try {
        //injectClassIntoLoader("fr.woorib.tools.instrument.QueryHolder", loader);
//        injectClassIntoLoader("com.ibm.as400.access.AS400JDBCResultSet", loader);
      }
      catch (Exception e) {
        throw new JDBCTransformException(e);
      }
      byteCode = editAS400JDBCPreparedStatement(className, classfileBuffer, loader);
    }
    if (className.equals("com/ibm/as400/access/AS400JDBCResultSet")) {
      try {
        //injectClassIntoLoader("fr.woorib.tools.instrument.QueryHolder", loader);
      }
      catch (Exception e) {
        throw new JDBCTransformException(e);
      }
      //byteCode = editAS400JDBCResultSet(className, classfileBuffer, loader);
    }
    else {
//      System.out.println("Not Instrumenting " + className);
    }
    return byteCode;
  }

  private byte[] editAS400JDBCResultSet(String className, byte[] classfileBuffer, ClassLoader loader) {
    byte[] byteCode = classfileBuffer;
    System.err.println("Found for instrumentalization : " + className);
    try {
      CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(
        classfileBuffer));
      CtField holder = new CtField(Descriptor.toCtClass("fr.woorib.tools.instrument.QueryHolder", classPool), "holder", ctClass);
      holder.setModifiers(Modifier.PUBLIC);
      CtMethod setHolder = CtNewMethod.setter("setHolder", holder);
      ctClass.addField(holder);
      ctClass.addMethod(setHolder);
      transformGetValue(classPool, ctClass);
      //transformExecuteQuery(classPool, ctClass);
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

  private void transformGetValue(ClassPool classPool, CtClass ctClass) throws NotFoundException, CannotCompileException {
    CtMethod methodExecuteQuery = ctClass.getMethod("getValue", Descriptor.ofMethod(Descriptor.toCtClass("com.ibm.as400.access.SQLData", classPool), new CtClass[]{CtClass.intType}));
    methodExecuteQuery.insertAfter("System.err.println(\"getValue called with (\"+$1+\") returning \"+(($_ == null || $_.getObject() == null) ? \"null\" : $_.getObject().toString()));");
  }


  /**
   * Transforms the AS400JDBCPreparedStatement by adding functionality to its methods.
   * @param className
   * @param classfileBuffer
   * @param loader
   * @return
   */
  private byte[] editAS400JDBCPreparedStatement(String className, byte[] classfileBuffer, ClassLoader loader) {
    byte[] byteCode = classfileBuffer;
    System.err.println("Found for instrumentalization : " + className);
    try {
      CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(
        classfileBuffer));
      transformSetValue(classPool, ctClass);
      transformExecuteQuery(classPool, ctClass);
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

  /**
   * Adds a System.err output to log the prepared statement and its the execution time (in milliseconds)</br>
   * Also uses the map managed by setValue method in order to list all parameters of the statement.
   * @param classPool
   * @param ctClass
   * @throws CannotCompileException
   * @throws NotFoundException
   */
  private void transformExecuteQuery(ClassPool classPool, CtClass ctClass) throws CannotCompileException, NotFoundException {
    CtMethod methodExecuteQuery = ctClass.getMethod("executeQuery", Descriptor.ofMethod(Descriptor.toCtClass("java.sql.ResultSet", classPool), null));
    //methodExecuteQuery.insertBefore("System.err.println(\"Executing statment : [\"+sqlStatement_+\"]\" + map.toString());");
    methodExecuteQuery.addLocalVariable("startTime", CtClass.longType);
    methodExecuteQuery.insertBefore("startTime = System.currentTimeMillis();");
    methodExecuteQuery.insertAfter("System.err.println(\"jdbc_request_log[query=\\\"\"+sqlStatement_+\"\\\", parameters=\\\"\" + map.toString()+\"\\\" execution_time=\"+ (System.currentTimeMillis() - startTime)+\"]\" );");
    //methodExecuteQuery.insertAfter("com.ibm.as400.access.AS400JDBCResultSet res = $_; res.holder = new fr.woorib.tools.instrument.QueryHolder(sqlStatement_.toString(), map);");
    //methodExecuteQuery.insertAfter("com.ibm.as400.access.AS400JDBCResultSet res = $_;");
    methodExecuteQuery.insertAfter("$_ = ($_ instanceof fr.woorib.tools.instrument.ResultSetWrapper) ? $_ :  new fr.woorib.tools.instrument.ResultSetWrapper($_, sqlStatement_.toString(), map);");

  }

  /**
   * Adds a map to store the arguments and indexes added to the prepared statement.
   * @param classPool
   * @param ctClass
   * @throws NotFoundException
   * @throws CannotCompileException
   */
  private void transformSetValue(ClassPool classPool, CtClass ctClass) throws NotFoundException, CannotCompileException {
    CtField map = new CtField(Descriptor.toCtClass("java.util.HashMap", classPool), "map", ctClass);
    ctClass.addField(map, "new java.util.HashMap()");
    CtMethod methodSetValue = ctClass.getMethod("setValue", Descriptor.ofMethod(CtClass.voidType, new CtClass[]{
      CtClass.intType,
      Descriptor.toCtClass("java.lang.Object", classPool),
      Descriptor.toCtClass("java.util.Calendar", classPool),
      CtClass.intType
    }));
    methodSetValue.insertBefore("map.put(new java.lang.Integer($1), $2);");
  }

  private void addAllClasspath(ClassPool classPool) throws NotFoundException {
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    URL[] urls = ((URLClassLoader) cl).getURLs();
    for (URL url : urls) {
      classPool.insertClassPath(url.getFile());
      System.err.println(url.getFile());
    }
  }
}
 
