package MySQLAccess;


import java.io.Closeable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class InsertDb {
	private Connection connect = null;
	private PreparedStatement preparedStatement = null;
	private Statement statement = null;
	private ResultSet resultSet = null;

	public void insertDb(String name) throws Exception {

		try {
			// this will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// setup the connection with the DB.

			connect = DriverManager.getConnection(
					"jdbc:mysql://sql4.freemysqlhosting.net:3306/sql459503",
					"sql459503", "sJ3!sJ8!");

			preparedStatement = connect
					.prepareStatement("INSERT INTO Test (Name) VALUES ('"
							+ name + "')");
			preparedStatement.executeUpdate();

		} catch (Exception e) {
			throw e;
		} finally {
			close();
		}
	}

	public String insertUser(String name, Localization l) throws Exception {
		
		int local_id = 0;
		int points = 0;

		try {
			Class.forName("com.mysql.jdbc.Driver");

			connect = DriverManager.getConnection(
					"jdbc:mysql://sql4.freemysqlhosting.net:3306/sql459503",
					"sql459503", "sJ3!sJ8!");

			// Check if user is already created
			statement = connect.createStatement();
			resultSet = statement.executeQuery("select * from User");
			while (resultSet.next()) {
				if (resultSet.getString("username").equalsIgnoreCase(name)) {
					return name + ";existinguser";
				}
			}
			// If not, insert User
				local_id = createLocalization(l);
				preparedStatement = connect
						.prepareStatement("INSERT INTO User (username,localization_id,points) VALUES ('"
								+ name
								+ "', '"
								+ local_id
								+ "', '"
								+ points
								+ "')");
				preparedStatement.executeUpdate();
			return name + ";newuser";
		} catch (Exception e) {
			throw e;
		} finally {
			close();
		}
	}

	// Creates localization and returns its id
	public Integer createLocalization(Localization l) throws Exception {

		int local_id = 0;
		boolean check = false;

		try {
			// this will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// setup the connection with the DB.

			connect = DriverManager.getConnection(
					"jdbc:mysql://sql4.freemysqlhosting.net:3306/sql459503",
					"sql459503", "sJ3!sJ8!");

			statement = connect.createStatement();
			resultSet = statement
					.executeQuery("select id from Localization where longitude = '"
							+ l.longitude
							+ "' and latitude = '"
							+ l.latitude
							+ "'");

			while (resultSet.next()) {
				if (resultSet.getInt("id") != 0) {
					local_id = resultSet.getInt("id");
					check = true;
				}
			}

			if (!check) {

				preparedStatement = connect
						.prepareStatement("INSERT INTO Localization (longitude,latitude) VALUES ('"
								+ l.longitude + "', '" + l.latitude + "')");
				preparedStatement.executeUpdate();

				statement = connect.createStatement();
				resultSet = statement
						.executeQuery("select id from Localization where longitude = '"
								+ l.longitude
								+ "' and latitude = '"
								+ l.latitude + "'");
				while (resultSet.next()) {
					local_id = resultSet.getInt("id");
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			close();
		}

		return local_id;
	}

	public void createFriendship(int user1, int user2) throws Exception {

		boolean check = false;

		try {
			// this will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// setup the connection with the DB.

			connect = DriverManager.getConnection(
					"jdbc:mysql://sql4.freemysqlhosting.net:3306/sql459503",
					"sql459503", "sJ3!sJ8!");

			// Check if friendship is already created
			statement = connect.createStatement();
			resultSet = statement.executeQuery("select * from Friendship");
			while (resultSet.next()) {
				if (resultSet.getInt("user1") == user1
						&& resultSet.getInt("user2") == user2
						|| resultSet.getInt("user1") == user2
						&& resultSet.getInt("user2") == user1) {
					check = true;
					break;
				}
			}
			// If not, create Friendship
			if (!check) {
				preparedStatement = connect
						.prepareStatement("INSERT INTO Friendship (user1,user2) VALUES ('"
								+ user1 + "', '" + user2 + "')");
				preparedStatement.executeUpdate();
			}

		} catch (Exception e) {
			throw e;
		} finally {
			close();
		}
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
