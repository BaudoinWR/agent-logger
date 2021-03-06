package fr.woorib.tools.instrument;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Class Wrapping an OutputStream to add logging capabilities.
 */
public class InputStreamWrapper extends InputStream {
  private final InputStream s;
  StringBuilder builder;

  public InputStreamWrapper(InputStream s, URL url, Map<String,String> requestProperties) {
    this.s = s;
    builder = new StringBuilder("Input {url="+url+",request_headers="+requestProperties+"} : ");
  }

  @Override
  public int read() throws IOException {
    int read = s.read();
    if (read == -1) {
      builder.append("\n");
    } else {
      builder.append((char) read);
    }
    return read;
  }

  @Override
  public long skip(long n) throws IOException {
    return s.skip(n);
  }

  @Override
  public int available() throws IOException {
    return s.available();
  }

  @Override
  public void close() throws IOException {
    if (!(s instanceof InputStreamWrapper)) {
      StackTraceElement[] stackTrace = new Exception().getStackTrace();
      for (StackTraceElement element : stackTrace) {
        builder.insert(0, element.toString() + "\n");
        if (element.getClassName().contains("soprasteria")) {
          break;
        }
      }
      String format = DateFormat.getDateTimeInstance().format(new Date());
      System.out.print("[" + format + "]");
      System.out.println(builder.toString());
    }
    s.close();
  }

  @Override
  public void mark(int readlimit) {
    s.mark(readlimit);
  }

  @Override
  public void reset() throws IOException {
    s.reset();
  }

  @Override
  public boolean markSupported() {
    return s.markSupported();
  }

}