package fr.woorib.tools.jdbc.instrument;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import com.ibm.as400.access.AS400JDBCResultSet;

/**
 * Holder class for content of AS400JDBCReslutSet and it's ResultSet contents
 */
public class QueryHolder {

  private final StackTraceElement[] stackTrace;
  private final String statement;
  private final Map<String, Object> parameters;
  private List<Map<String, Object>> result = new LinkedList<Map<String, Object>>();

  public QueryHolder(String statement, Map<String, Object> parameters) {
    this.statement = statement;
    this.parameters = parameters;
    this.stackTrace = new Exception().getStackTrace();
  }

  public QueryHolder(String statement, Map<String, Object> parameters, ResultSet resultSet) {
    this.statement = statement;
    this.parameters = parameters;
    this.stackTrace = new Exception().getStackTrace();
    boolean moved = false;
    try {
      ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
      while (resultSet.next()) {
        moved = true;
        Map<String, Object> values = new HashMap<String, Object>();
        for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
          String columnName = resultSetMetaData.getColumnName(i) + "("+i+")";
          values.put(columnName, resultSet.getObject(i));
        }
        result.add(values);
      }
    }
    catch (SQLException e) {
      System.err.println("Error in resultSet parsing for " + statement);
      e.printStackTrace();
    } finally {
      if (moved) {
        try {
          Field type_ = AS400JDBCResultSet.class.getDeclaredField("type_");
          type_.setAccessible(true);
          Object o = type_.get(resultSet);
          type_.set(resultSet, ResultSet.TYPE_SCROLL_INSENSITIVE);

          Field statement_ = AS400JDBCResultSet.class.getDeclaredField("statement_");
          statement_.setAccessible(true);
          Object st = statement_.get(resultSet);
          statement_.set(resultSet, null);

          resultSet.beforeFirst();
          type_.set(resultSet, o);
          statement_.set(resultSet, st);
        }
        catch (Exception e) {
          System.err.println("Error putting resultSet back to first row for " + statement);
          System.err.println(resultsToString());
          e.printStackTrace();
        }
      }
    }
  }




  @Override
  public String toString() {
    String s = "QueryHolder{" +
      "statement='" + statement + '\'' +
      ", parameters=" + parameters +
      ", trace=" + stackTrace[6] +
      ", result={";
    s += resultsToString();
    s+="}";
    return s +
      '}';
  }

  private String resultsToString() {
    StringBuilder s = new StringBuilder();
    for (Map<String, Object> map : result) {
      s.append("[");
      for (Map.Entry<String, Object> entry : map.entrySet()) {
        s.append(entry.getKey().trim())
          .append("=")
          .append(entry.getValue().toString().trim())
          .append(";");
      }
      int lastSemiColonIndex = s.lastIndexOf(";");
      if (lastSemiColonIndex > 0) {
        s.deleteCharAt(s.lastIndexOf(";"));
      }
      s.append("]");
    }
    return s.toString();
  }
}
 
