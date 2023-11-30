package application;
import java.util.Map;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
//The beginning of the DataBase class which stores user information into a hashMap
public class DataBase {
	//make a file reader and a hashmap with a string and profile
	private File fileReader;
	public HashMap<String, Profile> storedInfo;
	private String fileName = "LoginCredentials";
	private String firstName;
	private String employeeIdentification;
	private String password;
	private String lastName;
	private String userName;
	private String role;
	
	//declare the new variables needed
	public DataBase(String fileName) {
	 this.fileName = fileName;
    this.storedInfo = new HashMap<>();
    this.fileReader = new File(this.fileName);
    }
	//parse the fileName (logincredentials) and get the user information that was stored into the file
	public void parse() {
		try(Scanner scan = new Scanner(new File(fileName))) {
			while (scan.hasNextLine()) {
				String nextline = scan.nextLine();
				String[] information = nextline.split(":");
				//split and receive the information
				if (information.length == 6) {
					firstName = information[0];
					lastName = information[1];
					userName = information[2];
					password = information[3];
					employeeIdentification = information[4];
					role = information[5];
					//create the new Profile instance so it can be stored inside the hashmap
					Profile profile = new Profile(firstName, lastName, userName, password, employeeIdentification, role);
					//when its created it will store the first name and the profile
					storedInfo.put(profile.fName(), profile);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				}
		}
	//this will display the users first name and the contents of the profile when called
	public void accessprofile() {
		for (Map.Entry<String, Profile> inmap : storedInfo.entrySet()) {
        System.out.println("FirstName: " + inmap.getKey());
        System.out.print(inmap.getValue().displayContents());
        System.out.print("\n\n\n");
        }
	}
	//this is the profile class with all the variables 
	public class Profile{
		private String firstName;
		private String employeeIdentification;
		private String password;
		private String lastName;
		private String userName;
		private String role;
		//set the variables of the constructor equal to the variables being used in the class 
		public Profile(String firstName, String lastName, String employeeIdentification, String userName, String password,String role) {
			this.firstName = firstName;
			this.lastName = lastName;
			this.employeeIdentification = employeeIdentification;
			this.userName = userName;
			this.password = password;
			this.role = role;
			}
		//return basic information like the first name and user name
		public String fName() {
			return firstName;
			}
		public String UName() {
			return userName;
			}
	
	//this function was created by abdalla
	public String getRole() {
		return this.role;
	}
	
	//this function was created by abdalla
	public void setRole(String newRole) {
		this.role = newRole;
	}
	
    //string to display the information of the user when ran 
	public String displayContents(){
		String display ="Firstname: " + firstName +" Lastname: "+ lastName + "\n"
				+ "UserName: "+userName + " Password: "+ password + "\n"
				+"Employee ID: "+ employeeIdentification + " Position: "+ role;
		return display;
	}
}
	
	public static void main(String[] args) {
    //call the dataBase as fileN and the new database will be the login credentials file
    DataBase fileN = new DataBase("LoginCredentials");
    //call the parse and access profile to see the hashmap
    fileN.parse(); 
    fileN.accessprofile();
    }
}
