package fr.woorib.tools.instrument;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Map;

/**
 * Class Wrapping an OutputStream to add logging capabilities.
 */
public class OutputStreamWrapper extends OutputStream {
  private OutputStream s;
  StringBuilder builder;

  public OutputStreamWrapper(OutputStream s, URL url, Map<String,String> requestProperties) {
    this.s = s;
    builder = new StringBuilder("Output {url="+url+",request_headers="+requestProperties+"} : ");
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
    if (!(s instanceof OutputStreamWrapper)) {
      StackTraceElement[] stackTrace = new Exception().getStackTrace();
      for (StackTraceElement element : stackTrace) {
        builder.insert(0, element.toString() + "\n");
        if (element.getClassName().contains("soprasteria")) {
          break;
        }
      }
      System.out.println(builder.toString());
    }
    s.close();
  }
}
 
