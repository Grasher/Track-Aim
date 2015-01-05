package MySQLAccess;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class UpdateDb {
	private Connection connect = null;
	private PreparedStatement preparedStatement = null;
	private Statement statement = null;
	private ResultSet resultSet = null;

	public void UpdateTeste(String name) throws Exception {

		try {
			// this will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// setup the connection with the DB.

			connect = DriverManager.getConnection(
					"jdbc:mysql://sql4.freemysqlhosting.net:3306/sql459503",
					"sql459503", "sJ3!sJ8!");

			preparedStatement = connect
					.prepareStatement("UPDATE Test SET Name = 'Ricardo Quaresma' WHERE Name = 'Teste1'");
			preparedStatement.executeUpdate();

		} catch (Exception e) {
			throw e;
		} finally {
			close();
		}
	}

	// Username whose points will be updated and how many more points will
	// he/she have
	public void UpdateScore(String username, int points) throws Exception {

		int points_0 = 0;
		int points_1 = 0;

		try {
			// this will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// setup the connection with the DB.

			connect = DriverManager.getConnection(
					"jdbc:mysql://sql4.freemysqlhosting.net:3306/sql459503",
					"sql459503", "sJ3!sJ8!");

			// Gets current User's points
			statement = connect.createStatement();
			resultSet = statement
					.executeQuery("select points from User where username ="
							+ username + "");
			while (resultSet.next()) {
				points_0 = resultSet.getInt("points");
			}

			// Add new points
			points_1 = points_0 + points;

			// Update score based on the points_id
			preparedStatement = connect
					.prepareStatement("UPDATE User SET points = '" + points_1
							+ "' WHERE username = '" + username + "'");
			preparedStatement.executeUpdate();

		} catch (Exception e) {
			throw e;
		} finally {
			close();
		}
	}

	public void UpdatePosition(String username, Localization l)
			throws Exception {

		int localid = 0;

		try {
			// this will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// setup the connection with the DB.

			connect = DriverManager.getConnection(
					"jdbc:mysql://sql4.freemysqlhosting.net:3306/sql459503",
					"sql459503", "sJ3!sJ8!");

			// Checks if localid already exists
			statement = connect.createStatement();
			resultSet = statement
					.executeQuery("select id from Localization where longitude='"
							+ l.longitude
							+ "' and latitude='"
							+ l.latitude
							+ "'");
			while (resultSet.next()) {
				if (resultSet.getInt("id") != 0) {
					localid = resultSet.getInt("id");
				}
			}
			if (localid != 0) {

				// Update localization_id
				preparedStatement = connect
						.prepareStatement("UPDATE User SET localization_id = '"
								+ localid + "' WHERE username = '" + username
								+ "'");
				preparedStatement.executeUpdate();
			} else {
				InsertDb idb = new InsertDb();
				int id = idb.createLocalization(l);

				// Update localization_id
				preparedStatement = connect
						.prepareStatement("UPDATE User SET localization_id = '"
								+ id + "' WHERE username = '" + username
								+ "'");
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

