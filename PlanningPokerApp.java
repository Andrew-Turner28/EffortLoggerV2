package application;

import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class PlanningPokerApp extends Application {
    private final ObservableList<User> users = FXCollections.observableArrayList();
    private TableView<User> tableView;
    private Label averageScoreLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        TabPane tabPane = new TabPane();
        Tab planningPokerTab = createPlanningPokerTab();

        tabPane.getTabs().addAll(planningPokerTab);

        Scene scene = new Scene(tabPane, 600, 400);
        primaryStage.setTitle("Planning Poker App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public Tab createPlanningPokerTab() {
        Tab tab = new Tab("Planning Poker");
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        Label usersLabel = new Label("Number of Users:");
        TextField usersTextField = new TextField();
        Button startSessionButton = new Button("Start Session");

        gridPane.add(usersLabel, 0, 0);
        gridPane.add(usersTextField, 1, 0);
        gridPane.add(startSessionButton, 2, 0);

        tableView = new TableView<>();
        TableColumn<User, String> userNameColumn = new TableColumn<>("User");
        userNameColumn.setCellValueFactory(cellData -> cellData.getValue().userNameProperty());

        TableColumn<User, Integer> voteColumn = new TableColumn<>("Vote");
        voteColumn.setCellValueFactory(cellData -> cellData.getValue().voteProperty().asObject());

        tableView.getColumns().addAll(userNameColumn, voteColumn);
        gridPane.add(tableView, 0, 1, 3, 1);

        ComboBox<Integer> voteComboBox = new ComboBox<>(FXCollections.observableArrayList(1, 2, 3, 5, 8, 13));
        Button voteButton = new Button("Vote");
        Button nextRoundButton = new Button("Next Round");
        Button endRoundButton = new Button("End Round");

        gridPane.add(voteComboBox, 0, 2);
        gridPane.add(voteButton, 1, 2);
        gridPane.add(nextRoundButton, 2, 2);
        gridPane.add(endRoundButton, 0, 3);

        averageScoreLabel = new Label();
        gridPane.add(averageScoreLabel, 1, 3);

        startSessionButton.setOnAction(event -> {
            int numberOfUsers = Integer.parseInt(usersTextField.getText());
            initializeUsers(numberOfUsers);
            tableView.setItems(users);
        });

        voteButton.setOnAction(event -> {
            User selectedUser = tableView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                selectedUser.setVote(voteComboBox.getValue());
            }
        });

        nextRoundButton.setOnAction(event -> {
            if (checkSimilarVotes()) {
                // If the votes are similar, end the voting process or take appropriate action.
                System.out.println("Voting process ended - Similar votes!");
            } else {
                // Otherwise, continue the voting process.
                System.out.println("Next round...");
            }
        });

        endRoundButton.setOnAction(event -> {
            int averageScore = calculateAverageScore();
            averageScoreLabel.setText("Average Score: " + averageScore);
        });

        tab.setContent(gridPane);
        return tab;
    }

    private void initializeUsers(int numberOfUsers) {
        users.clear();
        for (int i = 1; i <= numberOfUsers; i++) {
            users.add(new User("User " + i));
        }
    }

    private boolean checkSimilarVotes() {
        int referenceVote = users.get(0).getVote();
        for (User user : users) {
            if (user.getVote() != referenceVote) {
                return false;
            }
        }
        return true;
    }

    private int calculateAverageScore() {
        int totalScore = 0;
        for (User user : users) {
            totalScore += user.getVote();
        }
        return Math.round((float) totalScore / users.size());
    }

    public static class User {
        private final SimpleStringProperty userName;
        private final SimpleIntegerProperty vote;

        public User(String userName) {
            this.userName = new SimpleStringProperty(userName);
            this.vote = new SimpleIntegerProperty(0);
        }

        public String getUserName() {
            return userName.get();
        }

        public SimpleStringProperty userNameProperty() {
            return userName;
        }

        public int getVote() {
            return vote.get();
        }

        public void setVote(int vote) {
            this.vote.set(vote);
        }

        public SimpleIntegerProperty voteProperty() {
            return vote;
        }
    }
}
