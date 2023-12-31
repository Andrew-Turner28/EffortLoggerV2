package application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class LoginScreen extends Application {
	private boolean isAdmin = false;
	private AtomicInteger failedAttempts = new AtomicInteger(0);

	 @Override
	
	 public void start(Stage loginStage) {
	
	 loginStage.setTitle("EffortLoggerV2.0 - Login");
	
	 //setting a stage for the application
	 GridPane grid = new GridPane();
	 grid.setAlignment(Pos.CENTER);
	 grid.setHgap(10);
	 grid.setVgap(10);
	 grid.setPadding(new Insets(25, 25, 25, 25));
	
	
	 //label for their username
	 Label usernameLabel = new Label("Username:");
	 grid.add(usernameLabel, 0, 1);
	 //text field for username
	 TextField usernameField = new TextField();
	 grid.add(usernameField, 1, 1);
	 //label for password
	 Label passwordLabel = new Label("Password:");
	 grid.add(passwordLabel, 0, 2);
	 //field for password
	 PasswordField passwordField = new PasswordField();
	 grid.add(passwordField, 1, 2);
	
	 //Result Label
	 Label resultLabel = new Label();
	 grid.add(resultLabel, 0, 4);
	
	 //setting up buttons
	 VBox vbBtn = new VBox(10);
	 vbBtn.setAlignment(Pos.BOTTOM_RIGHT);
	 grid.add(vbBtn, 1, 3);
	
	 //login button
	 Button loginBtn = new Button("Login");
	 vbBtn.getChildren().add(loginBtn);
	
	 //Create account button
	 Button newAccBtn = new Button("Create a New Account");
	 vbBtn.getChildren().add(newAccBtn);
	
	 //login button functionality
	 loginBtn.setOnAction(event -> {
		 //retrieve the strings entered into the fields
		 String enteredUsername = usernameField.getText();
		 String enteredPassword = passwordField.getText();
		
		 if(authenticateLogin(enteredUsername, enteredPassword)) {
			 System.out.print("Login Successful");
			 loginStage.close();
			 
			 if (isAdmin) {
			
				 //rayan code
				 effortapp effort= new effortapp();
				 Stage effortStage = new Stage();
				 effort.start(effortStage);
				 
			 } else {
				 //vansh code
				 effortapp effort= new effortapp();
				 Stage effortStage = new Stage();
				 effort.start(effortStage);
			 }
			 
		 }
		
		 else {
		 //Display error message
		 int currAttempts = failedAttempts.incrementAndGet();
		 resultLabel.setText("Login attempt " + currAttempts + " failed. Please check your login information");
		 }
	 });
	
	 newAccBtn.setOnAction(even -> {
		 loginStage.close();
		 openNewAccount();
	 });
	
	 Scene scene = new Scene(grid, 500, 300);
		 loginStage.setScene(scene);
		 loginStage.show();
	
	 }
	
	 public void openMainApp() {
		 Stage mainStage = new Stage();
		
		 //setting stage
		 mainStage.setTitle ("EffortLogger V2.0");
		
		 //Setup basic GridPane
		 GridPane mainGrid = new GridPane();
		 mainGrid.setAlignment(Pos.CENTER);
		 mainGrid.setHgap(10);
		 mainGrid.setVgap(10);
		 mainGrid.setPadding(new Insets(25, 25, 25, 25));
		
		 Label tempMessageLabel = new Label("Here is where the main program will be");
		 mainGrid.add(tempMessageLabel, 0, 1);
		
		 //create main scene
		 Scene mainScene = new Scene(mainGrid, 690, 300);
		 //set main scene
		 mainStage.setScene(mainScene);
		 //display main stage
		 mainStage.show();
	
	 }
	
	 
	
	 public void openNewAccount() {
		 Stage accountStage = new Stage();
		 accountStage.setTitle("EffortLoggerV2.0 - Account Creation");
		
		 //basic set up
		 GridPane gridNewAcc = new GridPane();
		 gridNewAcc.setAlignment(Pos.CENTER);
		 gridNewAcc.setHgap(10);
		 gridNewAcc.setVgap(10);
		 gridNewAcc.setPadding(new Insets(25, 25, 25, 25));
		 
		 //First Name label
		 Label firstNameLabel = new Label("First Name");
		 gridNewAcc.add(firstNameLabel, 0, 0);
		
		 //First Name field
		 TextField firstNameInput = new TextField();
		 gridNewAcc.add(firstNameInput, 1, 0);
		
		 //Last Name label
		 Label lastNameLabel = new Label("Last Name");
		 gridNewAcc.add(lastNameLabel, 0, 1);
		
		 //Last Name field
		 TextField lastNameInput = new TextField();
		 gridNewAcc.add(lastNameInput, 1, 1);
		
		 //Employee ID label
		 Label empIDLabel = new Label("Employee ID");
		 gridNewAcc.add(empIDLabel, 0, 2);
		
		 //Employee ID field
		 TextField empIDInput = new TextField();
		 gridNewAcc.add(empIDInput, 1, 2);
		
		 //Username label
		 Label usernameLabel = new Label("Username");
		 gridNewAcc.add(usernameLabel, 0, 3);
		
		 //username field
		 TextField usernameInput = new TextField();
		 gridNewAcc.add(usernameInput, 1, 3);
		
		 //Password label
		 Label passwordLabel = new Label("Password");
		 gridNewAcc.add(passwordLabel, 0, 4);
		
		 //Password field
		 TextField passwordInput = new TextField();
		 gridNewAcc.add(passwordInput, 1, 4);
		
		 //create account button
		 Button createAccBtn = new Button ("Create Account");
		 gridNewAcc.add(createAccBtn, 1, 5);
		
		 //Warning Labels
		 Label warningLabelFirstName = new Label();
		 gridNewAcc.add(warningLabelFirstName, 2, 0);
		 Label warningLabelLastName = new Label();
		 gridNewAcc.add(warningLabelLastName, 2, 1);
		 Label warningLabelIDNum = new Label();
		 gridNewAcc.add(warningLabelIDNum, 2, 2);
		 Label warningLabelUsername = new Label();
		 gridNewAcc.add(warningLabelUsername, 2, 3);
		 Label warningLabelPassword = new Label();
		 gridNewAcc.add(warningLabelPassword, 2, 4);
		
		 //create account button functionality 
		 createAccBtn.setOnAction(event -> {
		
		 //gather values inputed
			String firstName = firstNameInput.getText();
			String lastName = lastNameInput.getText();
			String employeeID = empIDInput.getText();
			String username = usernameInput.getText();
			String password = passwordInput.getText();
			String position = "User";
		
			//check validity of inputs
			boolean validFirstName = checkFirstName(firstName);
			boolean validLastName = checkLastName(lastName);
			boolean validIDNum = checkIDNum(employeeID);
			boolean validUsername = checkUsername(username);
			boolean validPassword = checkPassword(password);
		
			//Issues With Registration
			if(validFirstName == false) {
			warningLabelFirstName.setText("Ensure you have entered your first name");
			}
			else {
			warningLabelFirstName.setText("");
			}
		
			if(validLastName == false) {
				warningLabelLastName.setText("Ensure you have entered your last name");
			}
			else {
				warningLabelLastName.setText("");
			}
		
			if(validIDNum == false) {
				warningLabelIDNum.setText("Invalid Employee ID Number");
			}
			else {
				warningLabelIDNum.setText("");
			}
		
			if(validUsername == false) {
				warningLabelUsername.setText("Ensure your username is between 6 and 32 characters");
			}
			else {
				warningLabelUsername.setText("");
			}
		
			if(validPassword == false) {
				warningLabelPassword.setText("Ensure your password is at least 6 characters and contains a number");
			}
			else {
				warningLabelPassword.setText("");
			}
		
			if(validFirstName == true && validLastName == true && validIDNum == true && validUsername == true && validPassword == true) {
				//String to write
				String newUser = firstName + ":" + lastName + ":" + employeeID + ":" + username + ":" + password + ":" + position + "\n";
				//push string to notes doc	
				String fileName = "LoginCredentials";
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true)); 
				writer.write(newUser);
				writer.close();
				}
			catch (IOException e) {
				e.printStackTrace();
			}
		
			//close scene and return to login
			accountStage.close();
			Platform.runLater(() -> new LoginScreen().start(new Stage()));
			}
			});
		 
		 Scene accountScene = new Scene(gridNewAcc, 650, 550);
		 accountStage.setScene(accountScene);
		 accountStage.show();
	 }
	
		 private boolean authenticateLogin(String username, String password) {
		
			 //reads through LoginCredentials file
		 try (BufferedReader br = new BufferedReader(new FileReader("LoginCredentials"))) {
		 String line;
			 while ((line = br.readLine()) != null) {
				 //parses the string
			 String[] parts = line.split(":");
			 	//compares the string
				 if (parts[3].equals(username) && parts[4].equals(password)) {
					 if (parts[5].equals("admin")) isAdmin = true;
					 return true;
				 }
			 }
		 } 
		 catch (IOException e) {
		 e.printStackTrace();
		 }
		 return false;
	 }

	 //Check validity of strings through a series of if statements
	 private boolean checkFirstName (String firstName) {
		 if(firstName.length() < 1) {
			 return false;
		 }
		 else {
			 return true;
		 }
	 }

	 private boolean checkLastName (String lastName) {
		 if(lastName.length() < 1) {	
			 return false;
		 }
		 else {
			 return true;
		 }
	 }
	
	 private boolean checkIDNum (String employeeID) {
		 if(employeeID.length() == 6) {
			 return true;
		 }
		 else {
			 return false;
		 }
	 }

	 private boolean checkUsername (String username) {
		 if(username.length() < 6 || username.length() > 32) {
			 return false;
		 }
		 else {
			 return true;
		 }
	 }

	 private boolean checkPassword (String password) {
		 boolean containsNumber = false;
		
		 //scan string for number
		 String regex = ".*\\d.*";	
		 Pattern pattern = Pattern.compile(regex);	
		 Matcher matcher = pattern.matcher(password);
		 containsNumber = matcher.matches();

		 //ensure length of password
		 if(password.length() < 6 || containsNumber == false) {
			 return false;
		 }
		 else {
			 return true;
		 }
	 }
	 public static void main(String[] args) {
		 launch(args);
	}
}
