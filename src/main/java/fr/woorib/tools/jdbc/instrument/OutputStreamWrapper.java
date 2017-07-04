package fr.woorib.tools.jdbc.instrument;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

/**
 * Class Wrapping an OutputStream to add logging capabilities.
 */
public class OutputStreamWrapper extends OutputStream {
  private OutputStream s;
  StringBuilder builder;

  public OutputStreamWrapper(OutputStream s, URL url) {
    this.s = s;
    builder = new StringBuilder("Output {url="+url+"} : ");
  }
  @Override
  public void write(int b) throws IOException {
    builder.append((char) b);
    s.write(b);
  }

  @Override
  public void flush() throws IOException {
    s.flush();
  }

  @Override
  public void close() throws IOException {
    StackTraceElement[] stackTrace = new Exception().getStackTrace();
    for (StackTraceElement element : stackTrace) {
      if (element.getClassName().contains("soprasteria")) {
        builder.insert(0, element.toString() + "\n");
        break;
      }
    }
    System.out.println(builder.toString());
    s.close();
  }
}
 
