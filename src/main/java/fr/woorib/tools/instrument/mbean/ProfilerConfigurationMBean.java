/**
 * Paquet de définition
 **/
package fr.woorib.tools.instrument.mbean;

/**
 * Description: Merci de donner une description du service rendu par cette interface
 **/
public interface ProfilerConfigurationMBean {

  String addClassPattern(String s);

  String removeClassPatern(String s);

  String removeInstrumentation(String s);

  String printAllClasses();

  String printPatterns();

  String retransformAll();

}
 
