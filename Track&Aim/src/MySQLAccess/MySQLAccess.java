package MySQLAccess;


import java.io.Closeable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import android.R.string;

public class MySQLAccess {
  private Connection connect = null;
  private Statement statement = null;
  private PreparedStatement preparedStatement = null;
  private ResultSet resultSet = null;

  public String readDataBase() throws Exception {
	  String namex = "";
	  try {
      // this will load the MySQL driver, each DB has its own driver
      Class.forName("com.mysql.jdbc.Driver");
      // setup the connection with the DB.
     
      connect = DriverManager
          .getConnection("jdbc:mysql://sql4.freemysqlhosting.net:3306/sql459503","sql459503", "sJ3!sJ8!");

      // statements allow to issue SQL queries to the database
      statement = connect.createStatement();
      // resultSet gets the result of the SQL query
      resultSet = statement
          .executeQuery("select * from Test");
      	namex = writeResultSet(resultSet);
            
    } catch (Exception e) {
      throw e;
    } finally {
      close();
    }
	return namex;    
  }

  private String writeResultSet(ResultSet resultSet) throws SQLException {
    // resultSet is initialised before the first data set
	  String name = "";
    while (resultSet.next()) {
      // it is possible to get the columns via name
      // also possible to get the columns via the column number
      // which starts at 1
      // e.g., resultSet.getSTring(2);
     name = resultSet.getString("Name");     
    }
	return name;
  }

  // you need to close all three to make sure
  private void close() {

  }
  private void close(Closeable c) {
    try {
      if (c != null) {
        c.close();
      }
    } catch (Exception e) {
    // don't throw now as it might leave following closables in undefined state
    }
  }
} 