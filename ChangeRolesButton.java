/*
This is the front end button for the logic
its very basic - update a user's role if they are not admin
*/

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ChangeRolesButton extends Application {
    ChangeRoles c = new ChangeRoles();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("User Role Updater");

        Label userLabel = new Label("Username:");
        TextField userField = new TextField();

        Label roleLabel = new Label("New Role:");
        TextField roleField = new TextField();

        Button updateButton = new Button("Update User Role");
        updateButton.setOnAction(e -> 
        {
        	String user = userField.getText();
            String newRole = roleField.getText();

            if (! c.isUserAdmin(user)) {
                updateRole(user, newRole);
            } else {
                System.out.println("Permission denied. Admins cannot change their roles.");
            }
        	
        });

        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(userLabel, userField, roleLabel, roleField, updateButton);

        Scene scene = new Scene(vbox, 300, 200);
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    public void updateRole(String username, String newRole) {
        if (username.isEmpty() || newRole.isEmpty()) {
            System.out.println("Please enter both a username and a new role.");
        } else {
            c.updateFile(username, newRole);
        }
    }
}
