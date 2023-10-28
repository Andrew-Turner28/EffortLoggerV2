/*
Abdalla
 This is the backend for the ChangeRoles Button
 CHanges the file directly
 */
//Abdalla
import java.io.*;
import java.util.*;

public class ChangeRoles {
	DataBase users;
	//String user;
	Scanner scanner;
	
	public ChangeRoles() {
		scanner = new Scanner(System.in);
		this.users = new DataBase("LoginCredentials");
		users.parse();
		//this.user = user;
	}
	
	public void changeRole(String user) {
		if (! users.storedInfo.containsKey(user)) {
			System.out.println("User does not exist");
			return;
		}
		if (users.storedInfo.get(user).getRole().equals("admin")) {
			System.out.println("You are not allowed to edit this persons role");
			return;
		}
		DataBase.Profile p = users.storedInfo.get(user);
		System.out.println("What do you want this user's new role to be?\nAdmin or user?");
		String newRole = scanner.nextLine().toLowerCase();
		if (newRole.equals("admin") || newRole.equals("user")) {
			updateFile(user, newRole);
		}
	}
	
	public void updateFile(String user, String newRole) {
	    File file = new File("LoginCredentials");

	    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
	        List<String> lines = new ArrayList<>();
	        boolean userUpdated = false;

	        String line;
	        while ((line = reader.readLine()) != null) {
	            String[] userData = line.split(":");
	            String username = userData[0].toLowerCase();

	            // Check if the line contains the user to update
	            if (username.equals(user)) {
	                // Modify the role (assuming it's in the 6th position in the line)
	                userData[5] = newRole;
	                userUpdated = true;
	            }
	            lines.add(String.join(":", userData));
	        }

	        // Check if the user was found and updated
	        if (userUpdated) {
	            System.out.println("Task completed successfully. " + user + "'s role is now " + newRole);
	        } else {
	            System.out.println("User not found.");
	        }

	        // Write the modified content back to the file
	        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
	            for (String updatedLine : lines) {
	                writer.write(updatedLine);
	                writer.newLine();
	            }
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public boolean isUserAdmin(String user) {
		return users.storedInfo.get(user).getRole().equals("admin");
	}
	
	public static void main(String[] args) {
	    //call the parse and the access profile method
	    ChangeRoles c = new ChangeRoles();
	    c.changeRole("a");
	}
	

}
