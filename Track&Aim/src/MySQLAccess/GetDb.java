package MySQLAccess;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class GetDb {

	private Connection connect = null;
	private Statement statement = null;
	private ResultSet resultSet = null;
	
	public List<String> readDataBase() throws Exception {
		List<String> list = new ArrayList<String>();
		try {
			// this will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// setup the connection with the DB.

			connect = DriverManager.getConnection(
					"jdbc:mysql://sql4.freemysqlhosting.net:3306/sql459503",
					"sql459503", "sJ3!sJ8!");

			// statements allow to issue SQL queries to the database
			statement = connect.createStatement();
			// resultSet gets the result of the SQL query
			resultSet = statement.executeQuery("select * from Test");

			while (resultSet.next()) {
				list.add(resultSet.getString("Name"));
			}

		} catch (Exception e) {
			throw e;
		} finally {
			close();
		}
		return list;
	}

	// Recebe o id do user cujos amigos devem ser descobertos (e retornados numa
	// lista de ids)
	public List<Integer> getFriends(int user1) throws Exception {

		List<Integer> list = new ArrayList<Integer>();

		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection(
					"jdbc:mysql://sql4.freemysqlhosting.net:3306/sql459503",
					"sql459503", "sJ3!sJ8!");

			statement = connect.createStatement();
			resultSet = statement
					.executeQuery("select user2 from Friendship where user1 ="
							+ user1 + "");
			while (resultSet.next()) {
				list.add(resultSet.getInt("user2"));
			}
			resultSet = statement
					.executeQuery("select user1 from Friendship where user2 ='"
							+ user1 + "'");
			while (resultSet.next()) {				
				list.add(resultSet.getInt("user1"));
			}

		} catch (Exception e) {
			throw e;
		} finally {
			close();
		}

		return list;
	}

	// Gets user name from a given id
	public String getUsername(int user) throws Exception {

		String username = "";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection(
					"jdbc:mysql://sql4.freemysqlhosting.net:3306/sql459503",
					"sql459503", "sJ3!sJ8!");

			statement = connect.createStatement();
			resultSet = statement
					.executeQuery("select username from User where id ='" + user
							+ "'");
			while (resultSet.next()) {
				username = resultSet.getString("username");
			}

		} catch (Exception e) {
			throw e;
		} finally {
			close();
		}

		return username;
	}

	// Gets an user's score
	public Integer getScore(String user) throws Exception {

		int points = 0;

		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection(
					"jdbc:mysql://sql4.freemysqlhosting.net:3306/sql459503",
					"sql459503", "sJ3!sJ8!");

			// First: Get user's points
			statement = connect.createStatement();
			resultSet = statement
					.executeQuery("select points from User where username ='"
							+ user + "'");
			while (resultSet.next()) {
				points = resultSet.getInt("points");
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			close();
		}

		return points;
	}

	private void close() {
	}

	private void close(Closeable c) {
		try {
			if (c != null) {
				c.close();
			}
		} catch (Exception e) {
			// don't throw now as it might leave following closables in
			// undefined state
		}
	}
}
