
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.Animation;

import java.net.URL;


public class effortapp extends Application {

//	most of the variables are self explanatory
	
    private LocalDateTime startTime;
    private LocalDateTime stopTime;
    private Duration duration;
    private Timeline timeline;
    private Label clockDisplay;
    private ArrayList<String> logEntries;
    private TextArea logDisplay; // This will be Text area for displaying logged information
    private Label timerStatus; // Status label for the timer
    private Button startButton;
    private Button stopButton;
    private ComboBox<String> projectDropdown;
    private ComboBox<String> lifeCycleStepDropdown;
    private ComboBox<String> effortCategoryDropdown;
    private ComboBox<String> deliverableDropdown;

    @Override

    public void start(Stage primaryStage) {
        logEntries = new ArrayList<>();
        TabPane tabPane = new TabPane();
        initializeTheComponents();

        Tab effortLoggerTab = new Tab("EffortConsole", createEffortLoggerContent());
        effortLoggerTab.setClosable(false);

        Tab logDisplayTab = new Tab("Logs", createLogDisplayContent());
        logDisplayTab.setClosable(false);

        tabPane.getTabs().addAll(effortLoggerTab, logDisplayTab);

        Scene scene = new Scene(tabPane, 800, 600);
        // called "resources" at the root of your classpath, you will need to adjust this.
        
        scene.getStylesheets().add(getClass().getResource("colors.css").toExternalForm());

        URL theStyleCssURL = getClass().getResource("/colors.css");
        if (theStyleCssURL != null) {
            scene.getStylesheets().add(theStyleCssURL.toExternalForm());
        } else {
            System.out.println("The css file is not found. Make sure the file colors.css is in the correct location please.");
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
        
        timerStatus.getStyleClass().remove("label-timer-running");
        timerStatus.getStyleClass().add("label-timer-stopped");

//        This will include the time and duration
        String logEntry = createTheLogEntry();
        addTheLogEntry(logEntry);
    }

  
  
   private void updateTheTimer() {
       Duration timeRunning = Duration.between(startTime, LocalDateTime.now());
       long hours = timeRunning.toHours();
       long minutes = timeRunning.toMinutes() % 60;
       long seconds = timeRunning.getSeconds() % 60;
       clockDisplay.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
   }
  
   
   private String createTheLogEntry() {
	    String project = projectDropdown.getValue() != null ? projectDropdown.getValue() : "None";
	    String lifeCycleStep = lifeCycleStepDropdown.getValue() != null ? lifeCycleStepDropdown.getValue() : "None";
	    String effortCategory = effortCategoryDropdown.getValue() != null ? effortCategoryDropdown.getValue() : "None";
	    String deliverable = deliverableDropdown.getValue() != null ? deliverableDropdown.getValue() : "None";
	    
//	     Format the duration to include hours, minutes, and seconds
	    
	    long hours = duration.toHours();
	    long minutes = (duration.getSeconds() % 3600) / 60;
	    long seconds = duration.getSeconds() % 60;

	    
//	     Include the time information in the log entry
	    String timeInfo = String.format("Start Time: %s, Stop Time: %s, Duration: %02d:%02d:%02d",
	                                    startTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
	                                    stopTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
	                                    hours, minutes, seconds);
	    
//	     Combine all the information into the log entry
//	    makes things easier
	    return String.format("Project: %s, Step: %s, Category: %s, Deliverable: %s, %s",
	                         project, lifeCycleStep, effortCategory, deliverable, timeInfo);
	}

   private void addTheLogEntry(String logEntry) {
       logEntries.add(logEntry);
       logDisplay.appendText(logEntry + "\n"); // Append the log entry to the TextArea
   }
  
  
   private BorderPane createEffortLoggerContent() {
       BorderPane effortLoggerContent = new BorderPane();
      
      
       // This will layout the dropdowns and buttons in a GridPane
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
       
       // Layout the start and stop buttons in an HBox
       HBox theButtonBox = new HBox(10, startButton, stopButton);
       theButtonBox.setPadding(new Insets(10));
      
       
       // Create a label to display the timer status
       timerStatus.setId("timerStatus"); // Set an ID for potential styling or retrieval
      
       // Add components to effortLoggerContent
       VBox theMainContent = new VBox(10, timerStatus, clockDisplay, grid, theButtonBox);
       theMainContent.setPadding(new Insets(10));
       effortLoggerContent.setTop(theMainContent);
       
       return effortLoggerContent;
   }
   
   private BorderPane createLogDisplayContent() {
	   
       logDisplay = new TextArea();
       logDisplay.setEditable(false); // This makes sure the text area is non-editable
       logDisplay.setWrapText(true);  // This ensures lines are wrapped in the TextArea
       logDisplay.setPromptText("Log entries will be displayed here...");
       Label logDisplayLabel = new Label("Log Information");
       logDisplayLabel.setFont(new Font("Arial", 16)); // Set font and size for the label
       logDisplayLabel.setPadding(new Insets(5)); // Add some padding to the label for aesthetics
       BorderPane logDisplayContent = new BorderPane();
       logDisplayContent.setPadding(new Insets(10)); // Add some padding for aesthetics
       logDisplayContent.setTop(logDisplayLabel); // Set the label at the top of the BorderPane
       logDisplayContent.setCenter(logDisplay);
       
       BorderPane.setAlignment(logDisplayLabel, Pos.CENTER);
       return logDisplayContent;
       
   }

   public static void main(String[] args) {
       launch(args);
   }
}

