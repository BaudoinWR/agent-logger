package fr.woorib.tools.jdbc.instrument.exception;

public class JDBCTransformException extends RuntimeException {
  public JDBCTransformException(Exception e) {
    super(e);
    System.err.println("JDBCTransformException thrown ");
    e.printStackTrace(System.err);
  }
}
 
