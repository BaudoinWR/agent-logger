package fr.woorib.tools.instrument;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import javassist.CannotCompileException;
import javassist.NotFoundException;

/**
 * ClassLoader using the ClassFileTransformer to be used for unit testing.
 */
public class TransformingClassLoader extends ClassLoader {
  ClassFileTransformer jdbcConnectionTransformer;

  public TransformingClassLoader(ClassFileTransformer transformer) throws NoSuchMethodException, CannotCompileException, NotFoundException, IOException {
    jdbcConnectionTransformer = transformer;
  }

  /**
   * Method that loads a Class and expects the transformer to make changes to it.
   * If no change is detected (same byteCode as the same class loaded without going
   * through the transformer) will throw a runtime exception to fail tests.
   * @param name
   * @return
   * @throws ClassNotFoundException
   */
  public Class<?> loadClassWithChange(String name) throws ClassNotFoundException, NotFoundException, NoSuchMethodException, CannotCompileException, IOException {
    Class<?> aClass = Thread.currentThread().getContextClassLoader().loadClass(name);
    String className = aClass.getName();
    String replace = className.replace('.', '/');
    String classAsPath = replace + ".class";
    InputStream stream = this.getResourceAsStream(classAsPath);
    try {
      byte[] bytes = getBytes(stream);
      byte[] transform = jdbcConnectionTransformer.transform(this, replace, aClass, null, bytes);
      if (bytes != transform) {
        aClass = this.defineClass(name, transform, 0, transform.length);
      } else {
        throw new RuntimeException("Change didn't occur as expected for class "+name);
      }
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
    catch (IllegalClassFormatException e) {
      throw new RuntimeException(e);
    }
    return aClass;
  }

  private byte[] getBytes(InputStream stream) throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    int nRead;
    byte[] data = new byte[16384];

    while ((nRead = stream.read(data, 0, data.length)) != -1) {
      buffer.write(data, 0, nRead);
    }

    buffer.flush();

    return buffer.toByteArray();
  }

}
 
