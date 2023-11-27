package application;

import javafx.application.Application;

import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.geometry.Pos;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.Animation;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.net.URL;

public class effortapp extends Application {

    private LocalDateTime startTime;
    private LocalDateTime stopTime;
    private Duration duration = Duration.ZERO;
    private Timeline timeline;
    private Label clockDisplay;
    private ArrayList<String> logEntries;
    private TextArea logDisplay;
    private Label timerStatus;
    private Button startButton;
    private Button stopButton;
    private ComboBox<String> projectDropdown;
    private ComboBox<String> lifeCycleStepDropdown;
    private ComboBox<String> effortCategoryDropdown;
    private ComboBox<String> deliverableDropdown;
    private Label avgBusinessTimeLabel;
    private Label avgDevelopmentalTimeLabel;
    private ArrayList<Double> totalBusinessTime;
    private ArrayList<Double> totalDevelopmentalTime;
    private TextField defectName;
    private Button updateDefect;
    private Button deleteDefect;
    private ComboBox<String> projectListDropdown;

    private String logFileName = "log.txt";
    @Override
    public void start(Stage primaryStage) {
    	 logEntries = new ArrayList<>();
    	    TabPane tabPane = new TabPane();
    	    initializeTheComponents();
    	    totalBusinessTime = new ArrayList<>();
    	    totalDevelopmentalTime = new ArrayList<>();

    	    Tab effortLoggerTab = new Tab("EffortConsole", createEffortLoggerContent());
    	    effortLoggerTab.setClosable(false);

    	    Tab logDisplayTab = new Tab("Logs", createLogDisplayContent());
    	    logDisplayTab.setClosable(false);

    	    // Add the new "DefectConsole" tab
    	    Tab defectConsoleTab = new Tab("DefectConsole", createDefectConsoleContent());
    	    defectConsoleTab.setClosable(false);

    	    tabPane.getTabs().addAll(effortLoggerTab, logDisplayTab, defectConsoleTab);

    	    Scene scene = new Scene(tabPane, 800, 600);
    	    scene.getStylesheets().add(getClass().getResource("colors.css").toExternalForm());

    	    // Load existing log entries from the file when the application starts
    	    loadLogEntriesFromFile();

    	    URL theStyleCssURL = getClass().getResource("/colors.css");
    	    if (theStyleCssURL != null) {
    	        scene.getStylesheets().add(theStyleCssURL.toExternalForm());
    	    }

    	    primaryStage.setTitle("Effort Logger App");
    	    primaryStage.setScene(scene);
    	    primaryStage.show();
    }

    private void initializeTheComponents() {
        startButton = new Button("Start");
        stopButton = new Button("Stop");
        timerStatus = new Label("Clock is stopped");

        projectDropdown = new ComboBox<>();
        projectDropdown.getItems().addAll("Business Project", "Developmental Project");

        lifeCycleStepDropdown = new ComboBox<>();
        lifeCycleStepDropdown.getItems().addAll("Planning", "Information Gathering", "Information Understanding", "Verifying");

        effortCategoryDropdown = new ComboBox<>();
        effortCategoryDropdown.getItems().addAll("Plans", "Deliverables", "Interuptions", "Defects");

        deliverableDropdown = new ComboBox<>();
        deliverableDropdown.getItems().addAll("Documentation", "Software Module", "Test Report");

        avgBusinessTimeLabel = new Label("0 seconds");
        avgDevelopmentalTimeLabel = new Label("0 seconds");
        
        defectName = new TextField();
        projectListDropdown = new ComboBox<>();
        
        updateDefect = new Button("Update Defect");
        deleteDefect = new Button("Delete Defect");
        
        updateDefect.setOnAction(e -> handleUpdateDefectAction());
        deleteDefect.setOnAction(e -> handleDeleteDefectAction());

        startButton.setOnAction(e -> handleStartAction());
        stopButton.setOnAction(e -> handleStopAction());

        clockDisplay = new Label("00:00:00");
    }

    private void handleStartAction() {
        startTime = LocalDateTime.now();
        timerStatus.setText("Clock is running");

        timerStatus.getStyleClass().remove("label-timer-stopped");
        timerStatus.getStyleClass().add("label-timer-running");

        if (timeline != null) {
            timeline.stop();
        }
        timeline = new Timeline(new KeyFrame(javafx.util.Duration.seconds(1), e -> updateTheTimer()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void handleStopAction() {
        stopTime = LocalDateTime.now();
        if (timeline != null) {
            timeline.stop();
        }
        duration = Duration.between(startTime, stopTime);
        timerStatus.setText("Clock is stopped");

        double numDuration = 0;
        String numRegex = "[+-]?([0-9]*[.])?[0-9]+";
        String extractedNum = String.valueOf(duration).replaceAll("[^0-9.]", "");

        if (extractedNum.matches(numRegex)) {
            numDuration = Double.parseDouble(extractedNum);
        } else {
            System.out.print("Error. Please try again!");
        }

        if (projectDropdown.getValue().equals("Business Project")) {
            totalBusinessTime.add(numDuration);
        } else {
            totalDevelopmentalTime.add(numDuration);
        }

        double sumB = 0;
        double sumD = 0;
        double avgB = 0;
        double avgD = 0;

        if (totalBusinessTime == null || totalBusinessTime.isEmpty()) {
            avgB = 0;
        } else {
            for (double t : totalBusinessTime) {
                sumB += t;
            }
            avgB = sumB / totalBusinessTime.size();
        }

        if (totalDevelopmentalTime == null || totalDevelopmentalTime.isEmpty()) {
            avgD = 0;
        } else {
            for (double t : totalDevelopmentalTime) {
                sumD += t;
            }
            avgD = sumD / totalDevelopmentalTime.size();
        }

        avgBusinessTimeLabel.setText(String.format("%.2f", avgB) + " seconds");
        avgDevelopmentalTimeLabel.setText(String.format("%.2f", avgD) + " seconds");

        timerStatus.getStyleClass().remove("label-timer-running");
        timerStatus.getStyleClass().add("label-timer-stopped");

        String logEntry = createTheLogEntry();
        addTheLogEntry(logEntry);
    }

    private void updateTheTimer() {
        Duration timeRunning = Duration.between(startTime, LocalDateTime.now());
        long hours = timeRunning.toHours();
        long minutes = (timeRunning.getSeconds() % 3600) / 60;
        long seconds = timeRunning.getSeconds() % 60;
        clockDisplay.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
    }

    private String createTheLogEntry() {
        String project = projectDropdown.getValue() != null ? projectDropdown.getValue() : "None";
        String lifeCycleStep = lifeCycleStepDropdown.getValue() != null ? lifeCycleStepDropdown.getValue() : "None";
        String effortCategory = effortCategoryDropdown.getValue() != null ? effortCategoryDropdown.getValue() : "None";
        String deliverable = deliverableDropdown.getValue() != null ? deliverableDropdown.getValue() : "None";
        String defect = "None";

        long hours = duration.toHours();
        long minutes = (duration.getSeconds() % 3600) / 60;
        long seconds = duration.getSeconds() % 60;

        String timeInfo = String.format("Start Time: %s, Stop Time: %s, Duration: %02d:%02d:%02d",
                startTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                stopTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                hours, minutes, seconds);

        return String.format("Project: %s, Step: %s, Category: %s, Deliverable: %s, %s, Defect: %s",
                project, lifeCycleStep, effortCategory, deliverable, timeInfo, defect);
    }

    private void addTheLogEntry(String logEntry) {
        logEntries.add(logEntry);
        logDisplay.appendText(logEntry + "\n");
        projectListDropdown.getItems().addAll(logEntry);

        // Save the log entry to the log file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFileName, true))) {
            writer.write(logEntry + "\n");
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception according to your needs
        }
    }
    
    private void loadLogEntriesFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(logFileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logEntries.add(line);
                logDisplay.appendText(line + "\n");
                projectListDropdown.getItems().addAll(line);
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception according to your needs
        }
    }


    private BorderPane createEffortLoggerContent() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.add(new Label("Project:"), 0, 0);
        grid.add(projectDropdown, 1, 0);
        grid.add(new Label("Life Cycle Step:"), 0, 1);
        grid.add(lifeCycleStepDropdown, 1, 1);
        grid.add(new Label("Effort Category:"), 0, 2);
        grid.add(effortCategoryDropdown, 1, 2);
        grid.add(new Label("Deliverable:"), 0, 3);
        grid.add(deliverableDropdown, 1, 3);
        grid.add(new Label("Average Business Project time:"), 0, 4);
        grid.add(avgBusinessTimeLabel, 1, 4);
        grid.add(new Label("Average Developmental Project time:"), 0, 5);
        grid.add(avgDevelopmentalTimeLabel, 1, 5);

        HBox theButtonBox = new HBox(10, startButton, stopButton);
        theButtonBox.setPadding(new Insets(10));

        timerStatus.setId("timerStatus");

        VBox theMainContent = new VBox(10, timerStatus, clockDisplay, grid, theButtonBox);
        theMainContent.setPadding(new Insets(10));

        BorderPane effortLoggerContent = new BorderPane();
        effortLoggerContent.setTop(theMainContent);

        return effortLoggerContent;
    }

    private BorderPane createLogDisplayContent() {
        logDisplay = new TextArea();
        logDisplay.setEditable(false);
        logDisplay.setWrapText(true);
        logDisplay.setPromptText("Log entries will be displayed here...");
        Label logDisplayLabel = new Label("Log Information");
        logDisplayLabel.setFont(new Font("Arial", 16));
        logDisplayLabel.setPadding(new Insets(5));

        BorderPane logDisplayContent = new BorderPane();
        logDisplayContent.setPadding(new Insets(10));
        logDisplayContent.setTop(logDisplayLabel);
        logDisplayContent.setCenter(logDisplay);

        BorderPane.setAlignment(logDisplayLabel, Pos.CENTER);
        return logDisplayContent;
    }
    
    private void handleUpdateDefectAction() {
    	String selectedProject = projectListDropdown.getValue();
        String defectToUpdate = defectName.getText();

        if (selectedProject != null && !selectedProject.isEmpty() && defectToUpdate != null && !defectToUpdate.isEmpty()) {
            for (int i = logEntries.size() - 1; i >= 0; i--) {
                String logEntry = logEntries.get(i);
                if (logEntry.contains(selectedProject)) {
                    String[] parts = logEntry.split(", ");
                    for (String part : parts) {
                        if (part.startsWith("Defect:")) {
                            // Update the defect information
                            String updatedLogEntry = logEntry.replace(part, "Defect: " + defectToUpdate);
                            logEntries.set(i, updatedLogEntry);
                            logDisplay.clear();
                            // Update the log display with the modified log entries
                            for (String entry : logEntries) {
                                logDisplay.appendText(entry + "\n");
                            }
                            updateProjectListDropdown();
                            break;
                        }
                    }
                    break;
                }
            }
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFileName))) {
            for (String entry : logEntries) {
                writer.write(entry + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception according to your needs
        }
    }
    
    private void updateProjectListDropdown() {
    	projectListDropdown.getItems().clear();
        // Update the project list dropdown with the complete log entries for each project
        for (String entry : logEntries) {
            if (!projectListDropdown.getItems().contains(entry)) {
                projectListDropdown.getItems().add(entry);
            }
        }
    }
    
    private void handleDeleteDefectAction() {
    	String selectedProject = projectListDropdown.getValue();

        if (selectedProject != null && !selectedProject.isEmpty()) {
            for (int i = logEntries.size() - 1; i >= 0; i--) {
                String logEntry = logEntries.get(i);
                if (logEntry.contains(selectedProject)) {
                    // Update the defect information to "None"
                    String updatedLogEntry = logEntry.replaceAll("Defect: .*?(,|$)", "Defect: None$1");
                    logEntries.set(i, updatedLogEntry);
                    logDisplay.clear();
                    // Update the log display with the modified log entries
                    for (String entry : logEntries) {
                        logDisplay.appendText(entry + "\n");
                    }
                    updateProjectListDropdown();
                    break;
                }
            }
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFileName))) {
            for (String entry : logEntries) {
                writer.write(entry + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception according to your needs
        }
    }

    private BorderPane createDefectConsoleContent() {
    	// create the Defect Console tab
    	GridPane grid = new GridPane();
    	grid.setHgap(10);
    	grid.setVgap(10);
    	grid.setPadding(new Insets(20));
    	grid.add(new Label("Choose a project:"), 0, 0);
    	grid.add(projectListDropdown, 0, 1);
    	grid.add(new Label("Defect's name:"), 0, 2);
    	grid.add(defectName, 0, 3);
    	grid.add(updateDefect, 0, 4);
    	grid.add(deleteDefect, 0, 5);
    	
        BorderPane defectConsoleContent = new BorderPane();
        defectConsoleContent.setTop(grid);

        return defectConsoleContent;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
