import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

// This class is used to connect to the database by creating object of Properties class so that we can get the fields 
// stored in file
public class DBCredentials {
	
	Connection conn = null;

	public void myConnection() {
		
		try {
		
			Properties mypro = new Properties();
			FileInputStream input = new FileInputStream("credentials.properties");
			mypro.load(input);
			String Driver = mypro.getProperty("Driver");
			String ConnectionURL = mypro.getProperty("ConnectionURL");
			String Username = mypro.getProperty("Username");
			String Password = mypro.getProperty("Password");
			Class.forName(Driver);
			conn = DriverManager.getConnection(ConnectionURL, Username, Password);
		}

		catch (Exception e) {
			System.out.println("Unable to connect DB. Check DBCredentials.java : " + e);
		}
	}

}
