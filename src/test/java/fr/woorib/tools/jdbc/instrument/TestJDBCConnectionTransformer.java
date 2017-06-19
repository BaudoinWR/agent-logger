package fr.woorib.tools.jdbc.instrument;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import javassist.CannotCompileException;
import javassist.NotFoundException;

public class TestJDBCConnectionTransformer {

  @Test
  public void testCompiledClassDifferent() throws ClassNotFoundException, NotFoundException, NoSuchMethodException, CannotCompileException, IOException {
    Class<?> aClass = Thread.currentThread().getContextClassLoader().loadClass("com.ibm.as400.access.AS400JDBCPreparedStatement");
    Class<?> aClass1 = new TransformingClassLoader().loadClassWithChange("com.ibm.as400.access.AS400JDBCPreparedStatement");
    Assert.assertNotEquals(aClass, aClass1);
  }

  @Test
  public void testCompiledClassSame() throws ClassNotFoundException {
    Class<?> aClass = Thread.currentThread().getContextClassLoader().loadClass("com.ibm.as400.access.AS400JDBCPreparedStatement");
    Class<?> aClass1 = new TransformingClassLoader().loadClass("com.ibm.as400.access.AS400JDBCPreparedStatement");
    Assert.assertEquals(aClass, aClass1);
  }
}
 
