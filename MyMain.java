import java.util.Scanner;

public class MyMain {
	
	// This is my main method and from here the program execution starts
	public static void main(String[] args) {
		
		// Scanner is used to take input from user through keyboard
		Scanner scan = new Scanner(System.in);
		String mycase;
		String sDate, eDate, filename;

		while (true) {

			System.out.println("Please make a choice :\n1. Summary Information \n2.Exit");
			mycase = scan.next();

			switch (mycase) {

			case "1": {
				System.out.println("Please enter the starting date (YYYY-MM-DD) : ");
				sDate = scan.next();
				
				
				// Checks the correct start date format provided by the user	
				if (sDate.equals("") || sDate.equals(null)) {
					System.out.println("Incorrect date format!");
					break;
				}
				String checksDate[] = sDate.split("-");
				if (checksDate.length != 3) {
					System.out.println("Incorrect date format!");
					break;
				} else if (sDate.contains("/")) {
					System.out.println("Incorrect date format!");
					break;
				}

				System.out.println("Please enter the ending date (YYYY-MM-DD) : ");
				eDate = scan.next();
				
				// Checks the correct end date format provided by the user
				if (eDate.equals("") || eDate.equals(null)) {
					System.out.println("Incorrect date format!");
					break;
				}
				String checkeDate[] = eDate.split("-");
				if (checkeDate.length != 3) {
					System.out.println("Incorrect date format!");
					break;
				} else if (eDate.contains("/")) {
					System.out.println("Incorrect date format!");
					break;
				}

				System.out.println("Please provide a file with extension .xml : ");
				filename = scan.next();

				// Checks whether user have provided file extension .xml
				int lastIndex = filename.lastIndexOf('.');
				if (filename == null || lastIndex == -1) {
					System.out.println("Invalid file format!");
					break;
				} else {
				
					String format = filename.substring(lastIndex);
					if (format.equalsIgnoreCase("xml") || format.equalsIgnoreCase(".xml")) {
						AllQueryPerform aqp = new AllQueryPerform();
						aqp.queryOperations(sDate, eDate, filename);
						break;
					} else {
						System.out.println("Invalid file format!");
						break;
					}
				}

			}

			case "2": {
				System.exit(0);
			}

			default: {
				System.out.println("Entered wrong choice \n");
			}

			}
		}
	}
}
