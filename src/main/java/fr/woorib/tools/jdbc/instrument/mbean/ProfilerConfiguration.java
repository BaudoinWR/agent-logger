/**
 * Paquet de définition
 **/
package fr.woorib.tools.jdbc.instrument.mbean;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.HashSet;
import java.util.Set;

/**
 * Description: Merci de donner une description du service rendu par cette classe
 **/
public class ProfilerConfiguration implements ProfilerConfigurationMBean {
  public static ClassLoader classLoader;
  private Instrumentation inst;
  public static Set<String> classesToInstrument = new HashSet<String>(){
    {
      //add("StartupMultiClasspathServiceProvider");
      //add("AbstractFindServiceAction");
      //add("AbstractServiceAction");
      //add("DDEFindServiceAction");
      //add("ServiceTools");
      //"JspCompilationContext",
      //"Compiler"
      add("Beacon");
    }};

  public static Set<String> instrumented = new HashSet<String>();

  public static Set<String> unInstrumented = new HashSet<String>();

  public ProfilerConfiguration(Instrumentation inst) {
    this.inst = inst;
  }

  @Override
  public String addClassPattern(String s) {
    String result = "Adding to patterns to instrument : "+s+"\n";
    classesToInstrument.add(s.replace(".", "/"));
    try {
      inst.retransformClasses(Class.forName(s, true, classLoader));
      result += "retransformed "+s;
    }
    catch (ClassNotFoundException e) {
      result += "Not retransforming not found class "+s;
    }
    catch (UnmodifiableClassException e) {
      result += "Can't retransform "+s;
    }
    unInstrumented.remove(s.replace(".", "/"));
    return result;
  }

  @Override
  public String removeClassPatern(String s) {
    classesToInstrument.remove(s.replace(".", "/"));
    return "Removing from patterns to instrument : "+s;
  }

  @Override
  public String removeInstrumentation(String s) {
    String result = "Removing from patterns to instrument : "+s+"\n";
    try {
      unInstrumented.add(s.replace(".", "/"));
      inst.retransformClasses(Class.forName(s, true, classLoader));
      result += "Transformed "+s;
    }
    catch (UnmodifiableClassException e) {
      result += "Can't retransform "+s;
    }
    catch (ClassNotFoundException e) {
      result += "Not retransforming not found class "+s;
    }
    return result;
  }

  @Override
  public String printAllClasses() {
    String result = "Instrumented classes includes : \n";
    for (String c : instrumented) {
      result += ">>> " + c+"\n";
    }
    return result;
  }

  @Override
  public String printPatterns() {
    String result = "Instrumentation patterns : ";
    for (String c : classesToInstrument) {
      result += ">>> " + c +"\n";
    }
    return result;
  }

  @Override
  public String loader() {
    return "Loaded class loader : " +classLoader.toString()+"\n"
    + "system cl "+ClassLoader.getSystemClassLoader().toString();
  }
}
