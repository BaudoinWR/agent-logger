package fr.woorib.tools.instrument;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.SQLException;
import org.junit.Assert;
import org.junit.Test;
import javassist.CannotCompileException;
import javassist.NotFoundException;

public class TestJDBCConnectionTransformer {

  @Test
  public void testCompiledClassDifferent() throws ClassNotFoundException, NotFoundException, NoSuchMethodException, CannotCompileException, IOException, NoSuchFieldException, SQLException {
    Class<?> aClass = Thread.currentThread().getContextClassLoader().loadClass("com.ibm.as400.access.AS400JDBCPreparedStatement");
    TransformingClassLoader transformingClassLoader = new TransformingClassLoader(new AS400JDBCConnectionTransformer());
    Class<?> aClass2 = transformingClassLoader.loadClassWithChange("com.ibm.as400.access.AS400JDBCResultSet");
    Field holder = aClass2.getDeclaredField("holder");
    Method[] declaredMethods = aClass2.getDeclaredMethods();
    Class<?> aClass1 = transformingClassLoader.loadClassWithChange("com.ibm.as400.access.AS400JDBCPreparedStatement");
    Assert.assertNotEquals(aClass, aClass1);
  }

  @Test
  public void testCompiledClassSame() throws ClassNotFoundException, NoSuchMethodException, CannotCompileException, NotFoundException, IOException {
    Class<?> aClass = Thread.currentThread().getContextClassLoader().loadClass("com.ibm.as400.access.AS400JDBCPreparedStatement");
    Class<?> aClass1 = new TransformingClassLoader(new AS400JDBCConnectionTransformer()).loadClass("com.ibm.as400.access.AS400JDBCPreparedStatement");
    Assert.assertEquals(aClass, aClass1);
  }
}
 
