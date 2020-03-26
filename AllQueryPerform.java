import java.io.File;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

// Below class will perform all the query operations and will generate summary and creates a xml file.
public class AllQueryPerform {

	// dbc will help to connect to the database.
	DBCredentials dbc;

	// The constructor will create object of class DBCredentials and will allow
	// connecting to db
	AllQueryPerform() {
		dbc = new DBCredentials();
		dbc.myConnection();
	}

	public static boolean flag = true;

	// pos variable will keep count of spaces position while it writes in xml format
	public static int pos = 1;
	public static String provideIndent = "";

	// Below method will queryOperationscall the three queryOp methods and will write to the file
	public void queryOperations(String startDate, String endDate, String filename) {
		try {

			flag = true;
			// Creating File object and passed to PrintWriter object which will be used to
			// write to the file
			File myfile = new File(filename);
			PrintWriter pw = new PrintWriter(myfile);

			// Below method will write the tags which are same in the starting and also the
			// start date and end date
			intialTags(startDate, endDate, filename, pw);

			// Using SQL date format to avoid any errors and generate appropriate data
			Date sDate = Date.valueOf(startDate);
			Date eDate = Date.valueOf(endDate);

			// Calling three methods which will perform the SQL operations and write to the
			// file
			queryOp1(sDate, eDate, pw);
			queryOp2(sDate, eDate, pw);
			queryOp3(sDate, eDate, pw);

			pw.println("\n</year_end_summary>");
			pw.flush();
			pw.close();

			if (flag) {
				System.out.println("\n" + filename + " has successfully created!\n");
			}

		}

		catch (Exception e) {
			System.out.println("Problem while executing query. ");

		}

	}

	// This method will write the initial tags which will remain same and also will
	// write the starting and ending date
	public void intialTags(String sDate, String eDate, String filename, PrintWriter pw) {
		pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" + "<year_end_summary>\n" + "\t<year>\n"
				+ "\t\t<start_date>" +sDate+ " </start_date>\n" + "\t\t<end_date>" +eDate+ " </end_date>\n"
				+ "\t</year>");
		pw.flush();
	}

	// Below method will create indentation of lines with the help of \t based on
	// position
	public void getIndent() {
		provideIndent = "";
		for (int i = 0; i < pos; i++) {
			provideIndent += "\t";
		}
	}

	// Below method will fire the query 1 and generate summary of customers.
	// I have used Statement to execute the query
	public void queryOp1(Date sDate, Date eDate, PrintWriter pw) {
		try {

			String query_1 = "select sub2.CompanyName as customer_name, sub2.Address as street_address, \r\n"
					+ "sub2.City as city, sub2.Region as region, sub2.PostalCode as postal_code,\r\n"
					+ "sub2.Country as country, count(sub2.CustomerID) as num_orders, sum(sub2.intermediate_sum) as order_value\r\n"
					+ "from (select sub1.CustomerID, sub1.CompanyName, sub1.Address, sub1.City, \r\n"
					+ "sub1.Region, sub1.PostalCode, sub1.Country, sub1.OrderID, sum(sub1.mul) as\r\n"
					+ "intermediate_sum from\r\n"
					+ "(select c.CompanyName, c.Address, c.City, c.Region, c.PostalCode, c.Country ,o.CustomerID, od.OrderID, \r\n"
					+ "od.Quantity, od.UnitPrice, (od.UnitPrice * od.Quantity) as mul from orderdetails od, orders o, \r\n"
					+ "customers as c where o.OrderID = od.OrderID and o.CustomerID = c.CustomerID and o.OrderDate \r\n"
					+ "between '" + sDate + "' and '" + eDate + "') as sub1\r\n"
					+ "group by sub1.OrderID) as sub2 group by sub2.CompanyName;";

			// Defining the variables to store the values in it and use these variables to
			// write to the xml file
			String customer_name, street_address, city, region, postal_code, country, num_orders, order_value;

			Statement st = dbc.conn.createStatement();

			ResultSet rs1 = st.executeQuery(query_1);

			ResultSetMetaData rsmd1 = rs1.getMetaData();

			getIndent();
			pw.println(provideIndent + "<customer_list>");

			pos++;
			getIndent();

			// Loop through each row to get values and write it in xml file
			while (rs1.next()) {

				pw.println(provideIndent + "<customer>");

				// Increasing the count of position to get the indent in file
				pos++;

				getIndent();

				// getting the value column and store it to variables
				customer_name = rs1.getString("customer_name");
				street_address = rs1.getString("street_address");
				city = rs1.getString("city");
				region = rs1.getString("region");
				postal_code = rs1.getString("postal_code");
				country = rs1.getString("country");
				num_orders = rs1.getString("num_orders");
				order_value = rs1.getString("order_value");
				
			

				// Writing to the xml file
				pw.println(provideIndent + "<" + rsmd1.getColumnLabel(1).toLowerCase() + ">" + customer_name + "</"
						+ rsmd1.getColumnLabel(1) + ">");
				pw.println(provideIndent + "<address>");

				pos++;
				getIndent();

				pw.println(provideIndent + "<" + rsmd1.getColumnLabel(2).toLowerCase() + ">" + street_address + "</"
						+ rsmd1.getColumnLabel(2).toLowerCase() + ">");

				pw.println(provideIndent + "<" + rsmd1.getColumnName(3).toLowerCase() + ">" + city + "</"
						+ rsmd1.getColumnName(3).toLowerCase() + ">");
				pw.println(provideIndent + "<" + rsmd1.getColumnName(4).toLowerCase() + ">" + region + "</"
						+ rsmd1.getColumnName(4).toLowerCase() + ">");
				pw.println(provideIndent + "<" + rsmd1.getColumnLabel(5).toLowerCase() + ">" + postal_code + "</"
						+ rsmd1.getColumnLabel(5).toLowerCase() + ">");
				pw.println(provideIndent + "<" + rsmd1.getColumnName(6).toLowerCase() + ">" + country + "</"
						+ rsmd1.getColumnName(6).toLowerCase() + ">");

				pos--;
				getIndent();
				pw.println(provideIndent + "</address>");

				pw.println(provideIndent + "<" + rsmd1.getColumnLabel(7).toLowerCase() + ">" + num_orders + "</"
						+ rsmd1.getColumnLabel(7).toLowerCase() + ">");
				pw.println(provideIndent + "<" + rsmd1.getColumnLabel(8) + ">" + order_value + "</"
						+ rsmd1.getColumnLabel(8).toLowerCase() + ">");

				pos--;
				getIndent();
				pw.println(provideIndent + "</customer>");

			}

			pos--;
			getIndent();
			pw.print(provideIndent + "</customer_list>");

			pw.flush();

			// Closing the result set and statement
			rs1.close();
			st.close();
		}

		catch (Exception e) {
			flag = false;
			System.out.println("Error while doing query 1");
		}
	}

	// Below method will fire the query 2 and generate summary of products.
	// I have used Statement to execute the query
	public void queryOp2(Date sDate, Date eDate, PrintWriter pw) {
		try {

			String query_2 = "select temp.CategoryName as category_name, temp.ProductName as product_name, temp.CompanyName as \r\n"
					+ "supplier_name, SUM(temp.Quantity) as units_sold, sum(temp.cost) as sale_value from \r\n"
					+ "(select cat.CategoryID, cat.CategoryName, od.ProductID, pr.ProductName, sup.CompanyName , od.Quantity, od.UnitPrice, \r\n"
					+ "(od.Quantity * od.UnitPrice) as cost from orderdetails od, products pr, suppliers sup, orders o, categories cat\r\n"
					+ "where od.ProductID = pr.ProductID and sup.SupplierID = pr.SupplierID and od.OrderID = o.OrderID and \r\n"
					+ "pr.CategoryID = cat.CategoryID and o.OrderDate between '" + sDate + "' and '" + eDate
					+ "') as temp group by \r\n" + "temp.ProductID order by temp.CategoryName asc;";

			// Defining the variables to store the values in it and use these variables to
			// write to the xml file
			String category_name, product_name, supplier_name, units_sold, sale_value;

			Statement st = dbc.conn.createStatement();

			ResultSet rs1 = st.executeQuery(query_2);

			ResultSetMetaData rsmd1 = rs1.getMetaData();

			getIndent();
			pw.println("\n" + provideIndent + "<product_list>");

			pos++;
			getIndent();
			
			Boolean catChange = false;
			String prevCat = "";
			
			// Loop through each row to get values and write it in xml file
			while (rs1.next()) {
				if(! rs1.getString("category_name").equalsIgnoreCase(prevCat)) {
					if(!prevCat.equalsIgnoreCase("")) {
				pw.println(provideIndent + "</category>");}
				pw.println(provideIndent + "<category>");
				catChange = true;
				}
				else {
					catChange = false;
				}

				pos++;
				getIndent();

				// getting the value of column and store it to variables
				category_name = rs1.getString("category_name");
				product_name = rs1.getString("product_name");
				supplier_name = rs1.getString("supplier_name");
				units_sold = rs1.getString("units_sold");
				sale_value = rs1.getString("sale_value");
				
				
				

				// Writing to the xml file
				if(! rs1.getString("category_name").equalsIgnoreCase(prevCat)) {
				pw.println(provideIndent + "<" + rsmd1.getColumnLabel(1) + ">" + category_name + "</"
						+ rsmd1.getColumnLabel(1) + ">");}
				pw.println(provideIndent + "<product>");

				pos++;
				getIndent();

				pw.println(provideIndent + "<" + rsmd1.getColumnLabel(2) + ">" + product_name + "</"
						+ rsmd1.getColumnLabel(2) + ">");

				pw.println(provideIndent + "<" + rsmd1.getColumnName(3) + ">" + supplier_name + "</"
						+ rsmd1.getColumnName(3) + ">");
				pw.println(provideIndent + "<" + rsmd1.getColumnName(4) + ">" + units_sold + "</"
						+ rsmd1.getColumnName(4) + ">");
				pw.println(provideIndent + "<" + rsmd1.getColumnLabel(5) + ">" + sale_value + "</"
						+ rsmd1.getColumnLabel(5) + ">");

				pos--;
				getIndent();
				pw.println(provideIndent + "</product>");

				pos--;
				getIndent();
				
				prevCat = category_name; 

			}
			if(!prevCat.equalsIgnoreCase("")) {
		pw.println(provideIndent + "</category>");}
			pos--;
			getIndent();
			pw.print(provideIndent + "</product_list>");

			pw.flush();

			// Closing the result set and statement
			rs1.close();
			st.close();
		}

		catch (Exception e) {
			flag = false;
			System.out.println("Error while doing query 2");
		}
	}

	// Below method will fire the query 3 and generate summary of supplier.
	// I have used Statement to execute the query
	public void queryOp3(Date sDate, Date eDate, PrintWriter pw) {
		try {

			String query_3 = "select m1.CompanyName as supplier_name, m1.Address as street_address, m1.City as city, m1.Region as region, \r\n"
					+ "m1.PostalCode as postal_code, m1.Country as country, COUNT(m1.SupplierID) as num_products, SUM(m1.sales) as\r\n"
					+ "product_value from \r\n"
					+ "(select od.OrderID, od.ProductID, o.OrderDate, od.UnitPrice, od.Quantity, od.Discount, sup.SupplierID, \r\n"
					+ "(od.UnitPrice * od.Quantity) as sales, sup.CompanyName, sup.Address, sup.City, sup.Region,\r\n"
					+ "sup.PostalCode, sup.Country  from \r\n"
					+ "orderdetails as od, products as pr, suppliers as sup, orders as o where od.ProductID = pr.ProductID and \r\n"
					+ "sup.SupplierID = pr.SupplierID and o.OrderID = od.OrderId and o.OrderDate between '" + sDate
					+ "' and '" + eDate + "') \r\n" + "as m1 group by m1.SupplierID;";

			// Defining the variables to store the values in it and use these variables to
			// write to the xml file
			String supplier_name, street_address, city, region, postal_code, country, num_products, product_value;

			Statement st = dbc.conn.createStatement();

			ResultSet rs1 = st.executeQuery(query_3);

			ResultSetMetaData rsmd1 = rs1.getMetaData();

			getIndent();
			pw.println("\n" + provideIndent + "<supplier_list>");

			pos++;
			getIndent();

			// Loop through each row to get values and write it in xml file
			while (rs1.next()) {

				pw.println(provideIndent + "<supplier>");

				pos++;

				getIndent();

				// getting the value of column and store it to variables
				supplier_name = rs1.getString("supplier_name");
				street_address = rs1.getString("street_address");
				city = rs1.getString("city");
				region = rs1.getString("region");
				postal_code = rs1.getString("postal_code");
				country = rs1.getString("country");
				num_products = rs1.getString("num_products");
				product_value = rs1.getString("product_value");
				
				// Cleaning the data that is received from the database.
		
				street_address = street_address.replace("\n", "");
			

				// Writing to the xml file
				pw.println(provideIndent + "<" + rsmd1.getColumnLabel(1) + ">" + supplier_name + "</"
						+ rsmd1.getColumnLabel(1) + ">");
				pw.println(provideIndent + "<address>");

				pos++;
				getIndent();

				street_address = street_address.replace("\n", "");

				pw.println(provideIndent + "<" + rsmd1.getColumnLabel(2) + ">" + street_address + "</"
						+ rsmd1.getColumnLabel(2) + ">");

				pw.println(provideIndent + "<" + rsmd1.getColumnName(3) + ">" + city + "</" + rsmd1.getColumnName(3)
						+ ">");
				pw.println(provideIndent + "<" + rsmd1.getColumnName(4) + ">" + region + "</" + rsmd1.getColumnName(4)
						+ ">");
				pw.println(provideIndent + "<" + rsmd1.getColumnLabel(5) + ">" + postal_code + "</"
						+ rsmd1.getColumnLabel(5) + ">");
				pw.println(provideIndent + "<" + rsmd1.getColumnName(6) + ">" + country + "</" + rsmd1.getColumnName(6)
						+ ">");

				pos--;
				getIndent();
				pw.println(provideIndent + "</address>");

				pw.println(provideIndent + "<" + rsmd1.getColumnLabel(7) + ">" + num_products + "</"
						+ rsmd1.getColumnLabel(7) + ">");
				pw.println(provideIndent + "<" + rsmd1.getColumnLabel(8) + ">" + product_value + "</"
						+ rsmd1.getColumnLabel(8) + ">");

				pos--;
				getIndent();
				pw.println(provideIndent + "</supplier>");

			}

			pos--;
			getIndent();
			pw.print(provideIndent + "</supplier_list>");

			pw.flush();

			// Closing the result set and statement
			rs1.close();
			st.close();
		}

		catch (Exception e) {
			flag = false;
			System.out.println(e.getMessage());
		}
	}

}
