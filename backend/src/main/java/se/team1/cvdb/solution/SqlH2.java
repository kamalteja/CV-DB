package se.team1.cvdb.solution;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.*;

import javax.imageio.ImageIO;

public class SqlH2 {
	
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "org.h2.Driver";
	static final String DB_URL = "jdbc:h2:~/test";

	// Database credentials
	static final String USER = "grkt";
	static final String PASS = "kamal29";
	
	// To read the image Blob from the database
	// ResultSet.getBinaryStream
	
	Connection conn = null;
	Statement stmt = null;
	
	public void initializeConnections() {
		try {
			// STEP 1: Register JDBC driver
			Class.forName("org.h2.Driver");

			// STEP 2: Open a connection
			System.out.println("Connecting to a selected database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			System.out.println("Connected database successfully...");
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		}
	}
	
	public void closeConnections() {
		try {
			if (stmt != null)
				conn.close();
		} catch (SQLException se) {
			se.printStackTrace();
		} // do nothing
		
		try {
			if (conn != null)
				conn.close();
		} catch (SQLException se) {
			se.printStackTrace();
		} // end finally try
	}

	public void createNewTable() {
		try {
			// Execute a query
			System.out.println("Creating table in given database...");
			stmt = conn.createStatement();
			
			String sql = "CREATE TABLE IF NOT EXISTS CV_DB "
					+ "(id INT NOT NULL auto_increment,"
					+ "firstName VARCHAR(255) NOT NULL,"
					+ "lastName VARCHAR(255) NOT NULL,"
					+ "email VARCHAR(255) NOT NULL,"
					+ "password VARCHAR(255) NOT NULL,"
					+ "confirmPassword VARCHAR(255) NOT NULL,"
					+ "site VARCHAR(255) NOT NULL,"
					+ "designation VARCHAR(255) NOT NULL,"
					+ "image BLOB,"
					+ "PRIMARY KEY (id)) AUTO_INCREMENT=1";
			stmt.executeUpdate(sql);
			System.out.println("Created table in given database...");
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		}
	}
	
	public void insertRow(User user) {
		
		  try {
	          // Execute a query
	          System.out.println("Inserting records into the table...");

	      	// Convert the contents of the Image file to appropriate 'format'
	      	BufferedImage bImage = null;
	      	try {
	      	    bImage = ImageIO.read(new URL("file:///home/sh/Desktop/workspace/workspace-main/CV-DB/backend/src/main/java/se/team1/cvdb/solution/file.jpg"));
	      	} catch (IOException e) {
	      		System.out.println(e);
	      	}
	      	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	      	ImageIO.write(bImage, "png", baos);
	      	InputStream inStream = new ByteArrayInputStream(baos.toByteArray());
	      	
	      	PreparedStatement newUser = conn.prepareStatement ("INSERT INTO CV_DB (firstName, lastName, email, password, confirmPassword, site, designation, image) values(?,?,?,?,?,?,?,?)");	
	    	newUser.setString(1, user.getFirstName());
	    	newUser.setString(2, user.getLastName());
	    	newUser.setString(3, user.getEmail());
	    	newUser.setString(4, user.getPassword());
	    	newUser.setString(5, user.getConfirmPassword());
	    	newUser.setString(6, user.getSite());
	    	newUser.setString(7, user.getDesignation());
	    	newUser.setBinaryStream(8, inStream);
	    	newUser.executeUpdate();

	       System.out.println("Inserted records into the table...");

	      } catch (SQLException se) {
	          //Handle errors for JDBC
	          se.printStackTrace();
	      } catch (Exception e) {
	          //Handle errors for Class.forName
	          e.printStackTrace();
	      }
	}
	
	public User fetchDetails(User user) throws Exception{
		
		stmt = conn.createStatement();
		String sql = "SELECT * FROM CV_DB WHERE email="
				+ "'" + user.getEmail() + "' and password="
				+ "'" + user.getPassword() + "' and designation="
				+ "'" + user.getDesignation() + "'";
		ResultSet rs;
		rs = stmt.executeQuery(sql);
		User objU = new User();
        while (rs.next()) {
            objU.setEmail(rs.getString("email"));
            objU.setPassword(rs.getString("password"));;
            objU.setDesignation(rs.getString("designation"));
        }
        return objU;
}
}