package application;
import java.util.Map;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
//The beginning of the DataBase class
public class DataBase {
//declare all the variables needed to parse the file
// and store into a hashMap
private File fileReader;
private HashMap<String, Profile> storedInfo;
private String fileName = "LoginCredentials";
private String firstName;
private String employeeIdentification;
private String password;
	private String lastName;
	private String userName;
	private String role;
	//constructor to create new file
    // and reader for the file
public DataBase(String fileName) {
	 this.fileName = fileName;
    this.storedInfo = new HashMap<>();
    this.fileReader = new File(this.fileName);
	
}

public void parse() {
try(Scanner scan = new Scanner(new File(fileName))) {
while (scan.hasNextLine()) {
String nextline = scan.nextLine();
String[] information = nextline.split(":");
//parse the code into different variables
if (information.length == 6) {
firstName = information[0];
lastName = information[1];
userName = information[2];
password = information[3];
employeeIdentification = information[4];
role = information[5];
//set up the profile to store the variables
Profile profile = new Profile(firstName, lastName, userName, password, employeeIdentification, role);
//call the hashmap, <name, profile>
storedInfo.put(profile.fName(), profile);
}
}
} catch (IOException e) {
e.printStackTrace();
}
}
//create a for loop to go through the map's contents
public void accessprofile() {
    for (Map.Entry<String, Profile> inmap : storedInfo.entrySet()) {
        System.out.println("FirstName: " + inmap.getKey());
        System.out.print(inmap.getValue().displayContents());
        System.out.print("\n\n\n");
    }
}
	
private class Profile{
	private String firstName;
	private String employeeIdentification;
	private String password;
	private String lastName;
	private String userName;
	private String role;
    //contructor for the new profile
	public Profile(String firstName, String lastName, String employeeIdentification, String userName, String password,String role) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.employeeIdentification = employeeIdentification;
		this.userName = userName;
		this.password = password;
		this.role = role;
	}

	public String fName() {
		return firstName;
	}

	public String UName() {
		return userName;
	}
    //string to display the map profile values
	public String displayContents(){
		String display ="Firstname: " + firstName +" Lastname: "+ lastName + "\n"
				+ "UserName: "+userName + " Password: "+ password + "\n"
				+"Employee ID: "+ employeeIdentification + " Position: "+ role;
		return display;
		
		
	}
}
	public static void main(String[] args) {
    //call the parse and the access profile method
    DataBase fileN = new DataBase("LoginCredentials");
    fileN.parse(); 
    fileN.accessprofile();
}
	
	
}