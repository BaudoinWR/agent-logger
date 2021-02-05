/**
 * Paquet de définition
 **/
package fr.woorib.tools.instrument;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Description: Merci de donner une description du service rendu par cette classe
 **/
public class PreparedStatementWrapper extends StatementWrapper implements PreparedStatement {

  public PreparedStatementWrapper(PreparedStatement wrapped, Object statement) {
    super(wrapped);
    this.statement = statement.toString();
  }

  @Override
  public ResultSet executeQuery() throws SQLException {
    long start = System.currentTimeMillis();
    ResultSet resultSet = ((PreparedStatement) this.wrapped).executeQuery();
    duration = System.currentTimeMillis() - start;
    return new ResultSetWrapper(resultSet, statement, map);
  }

  @Override
  public int executeUpdate() throws SQLException {
    return ((PreparedStatement) wrapped).executeUpdate();
  }

  @Override
  public void setNull(int parameterIndex, int sqlType) throws SQLException {
    map.put(parameterIndex, sqlType);
    ((PreparedStatement) wrapped).setNull(parameterIndex, sqlType);
  }

  @Override
  public void setBoolean(int parameterIndex, boolean x) throws SQLException {
    map.put(parameterIndex, x);
    ((PreparedStatement) wrapped).setBoolean(parameterIndex, x);
  }

  @Override
  public void setByte(int parameterIndex, byte x) throws SQLException {
    map.put(parameterIndex, x);
    ((PreparedStatement) wrapped).setByte(parameterIndex, x);
  }

  @Override
  public void setShort(int parameterIndex, short x) throws SQLException {
    map.put(parameterIndex, x);
    ((PreparedStatement) wrapped).setShort(parameterIndex, x);
  }

  @Override
  public void setInt(int parameterIndex, int x) throws SQLException {
    map.put(parameterIndex, x);
    ((PreparedStatement) wrapped).setInt(parameterIndex, x);
  }

  @Override
  public void setLong(int parameterIndex, long x) throws SQLException {
    map.put(parameterIndex, x);
    ((PreparedStatement) wrapped).setLong(parameterIndex, x);
  }

  @Override
  public void setFloat(int parameterIndex, float x) throws SQLException {
    map.put(parameterIndex, x);
    ((PreparedStatement) wrapped).setFloat(parameterIndex, x);
  }

  @Override
  public void setDouble(int parameterIndex, double x) throws SQLException {
    map.put(parameterIndex, x);
    ((PreparedStatement) wrapped).setDouble(parameterIndex, x);
  }

  @Override
  public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
    map.put(parameterIndex, x);
    ((PreparedStatement) wrapped).setBigDecimal(parameterIndex, x);
  }

  @Override
  public void setString(int parameterIndex, String x) throws SQLException {
    map.put(parameterIndex, x);
    ((PreparedStatement) wrapped).setString(parameterIndex, x);
  }

  @Override
  public void setBytes(int parameterIndex, byte[] x) throws SQLException {
    map.put(parameterIndex, x);
    ((PreparedStatement) wrapped).setBytes(parameterIndex, x);
  }

  @Override
  public void setDate(int parameterIndex, Date x) throws SQLException {
    map.put(parameterIndex, x);
    ((PreparedStatement) wrapped).setDate(parameterIndex, x);
  }

  @Override
  public void setTime(int parameterIndex, Time x) throws SQLException {
    map.put(parameterIndex, x);
    ((PreparedStatement) wrapped).setTime(parameterIndex, x);
  }

  @Override
  public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
    map.put(parameterIndex, x);
    ((PreparedStatement) wrapped).setTimestamp(parameterIndex, x);
  }

  @Override
  public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
    map.put(parameterIndex, x);
    ((PreparedStatement) wrapped).setAsciiStream(parameterIndex, x, length);
  }

  @Override
  public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
    map.put(parameterIndex, x);
    ((PreparedStatement) wrapped).setUnicodeStream(parameterIndex, x, length);
  }

  @Override
  public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
    map.put(parameterIndex, x);
    ((PreparedStatement) wrapped).setBinaryStream(parameterIndex, x, length);
  }

  @Override
  public void clearParameters() throws SQLException {
    map.clear();
    ((PreparedStatement) wrapped).clearParameters();
  }

  @Override
  public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
    map.put(parameterIndex, x);
    ((PreparedStatement) wrapped).setObject(parameterIndex, x, targetSqlType);
  }

  @Override
  public void setObject(int parameterIndex, Object x) throws SQLException {
    map.put(parameterIndex, x);
    ((PreparedStatement) wrapped).setObject(parameterIndex, x);
  }

  @Override
  public boolean execute() throws SQLException {
    return ((PreparedStatement) wrapped).execute();
  }

  @Override
  public void addBatch() throws SQLException {
    ((PreparedStatement) wrapped).addBatch();
  }

  @Override
  public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
    ((PreparedStatement) wrapped).setCharacterStream(parameterIndex, reader, length);
  }

  @Override
  public void setRef(int parameterIndex, Ref x) throws SQLException {
    map.put(parameterIndex, x);
    ((PreparedStatement) wrapped).setRef(parameterIndex, x);
  }

  @Override
  public void setBlob(int parameterIndex, Blob x) throws SQLException {
    map.put(parameterIndex, x);
    ((PreparedStatement) wrapped).setBlob(parameterIndex, x);
  }

  @Override
  public void setClob(int parameterIndex, Clob x) throws SQLException {
    map.put(parameterIndex, x);
    ((PreparedStatement) wrapped).setClob(parameterIndex, x);
  }

  @Override
  public void setArray(int parameterIndex, Array x) throws SQLException {
    map.put(parameterIndex, x);
    ((PreparedStatement) wrapped).setArray(parameterIndex, x);
  }

  @Override
  public ResultSetMetaData getMetaData() throws SQLException {
    return ((PreparedStatement) wrapped).getMetaData();
  }

  @Override
  public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
    map.put(parameterIndex, x);
    ((PreparedStatement) wrapped).setDate(parameterIndex, x, cal);
  }

  @Override
  public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
    map.put(parameterIndex, x);
    ((PreparedStatement) wrapped).setTime(parameterIndex, x, cal);
  }

  @Override
  public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
    map.put(parameterIndex, x);
    ((PreparedStatement) wrapped).setTimestamp(parameterIndex, x, cal);
  }

  @Override
  public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
    map.put(parameterIndex, "null");
    ((PreparedStatement) wrapped).setNull(parameterIndex, sqlType, typeName);
  }

  @Override
  public void setURL(int parameterIndex, URL x) throws SQLException {
    map.put(parameterIndex, x);
    ((PreparedStatement) wrapped).setURL(parameterIndex, x);
  }

  @Override
  public ParameterMetaData getParameterMetaData() throws SQLException {
    return ((PreparedStatement) wrapped).getParameterMetaData();
  }

  @Override
  public void setRowId(int parameterIndex, RowId x) throws SQLException {
    map.put(parameterIndex, x);
    ((PreparedStatement) wrapped).setRowId(parameterIndex, x);
  }

  @Override
  public void setNString(int parameterIndex, String value) throws SQLException {
    map.put(parameterIndex, value);
    ((PreparedStatement) wrapped).setNString(parameterIndex, value);
  }

  @Override
  public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
    ((PreparedStatement) wrapped).setNCharacterStream(parameterIndex, value, length);
  }

  @Override
  public void setNClob(int parameterIndex, NClob value) throws SQLException {
    map.put(parameterIndex, value);
    ((PreparedStatement) wrapped).setNClob(parameterIndex, value);
  }

  @Override
  public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
    ((PreparedStatement) wrapped).setClob(parameterIndex, reader, length);
  }

  @Override
  public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
    ((PreparedStatement) wrapped).setBlob(parameterIndex, inputStream, length);
  }

  @Override
  public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
    ((PreparedStatement) wrapped).setNClob(parameterIndex, reader, length);
  }

  @Override
  public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
    map.put(parameterIndex, xmlObject);
    ((PreparedStatement) wrapped).setSQLXML(parameterIndex, xmlObject);
  }

  @Override
  public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
    map.put(parameterIndex, x);
    ((PreparedStatement) wrapped).setObject(parameterIndex, x, targetSqlType, scaleOrLength);
  }

  @Override
  public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
    map.put(parameterIndex, x);
    ((PreparedStatement) wrapped).setAsciiStream(parameterIndex, x, length);
  }

  @Override
  public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
    map.put(parameterIndex, x);
    ((PreparedStatement) wrapped).setBinaryStream(parameterIndex, x, length);
  }

  @Override
  public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
    map.put(parameterIndex, reader);
    ((PreparedStatement) wrapped).setCharacterStream(parameterIndex, reader, length);
  }

  @Override
  public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
    map.put(parameterIndex, x);
    ((PreparedStatement) wrapped).setAsciiStream(parameterIndex, x);
  }

  @Override
  public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
    map.put(parameterIndex, x);
    ((PreparedStatement) wrapped).setBinaryStream(parameterIndex, x);
  }

  @Override
  public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
    map.put(parameterIndex, reader);
    ((PreparedStatement) wrapped).setCharacterStream(parameterIndex, reader);
  }

  @Override
  public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
    map.put(parameterIndex, value);
    ((PreparedStatement) wrapped).setNCharacterStream(parameterIndex, value);
  }

  @Override
  public void setClob(int parameterIndex, Reader reader) throws SQLException {
    ((PreparedStatement) wrapped).setClob(parameterIndex, reader);
  }

  @Override
  public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
    ((PreparedStatement) wrapped).setBlob(parameterIndex, inputStream);
  }

  @Override
  public void setNClob(int parameterIndex, Reader reader) throws SQLException {
    ((PreparedStatement) wrapped).setNClob(parameterIndex, reader);
  }


}
 
