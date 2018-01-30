/**
 * Paquet de définition
 **/
package fr.woorib.tools.jdbc.instrument.mbean;

import java.lang.instrument.UnmodifiableClassException;

/**
 * Description: Merci de donner une description du service rendu par cette interface
 **/
public interface ProfilerConfigurationMBean {

  String addClassPattern(String s) throws ClassNotFoundException, UnmodifiableClassException;

  String removeClassPatern(String s);

  String removeInstrumentation(String s);

  String printAllClasses();

  String printPatterns();

  String loader();
}
 
