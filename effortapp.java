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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.geometry.Pos;
import java.util.ArrayList;
import java.util.Comparator;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.Animation;
import java.io.*;
import java.util.List;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;


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
   private static final String LOG_FILE_PATH = "log.txt";
   private Button clearLogsButton;  
   private ScheduledExecutorService scheduler;
   @Override
   public void start(Stage primaryStage) {
       logEntries = new ArrayList<>();
       TabPane tabPane = new TabPane();
       
       // Initialize and start the scheduler
       initializeAndStartScheduler();
       
       initializeTheComponents();
       // Initialize the clearLogsButton before creating the tab content
       clearLogsButton = new Button("Clear Logs");
       clearLogsButton.setOnAction(event -> clearLogs());
       Tab logDisplayTab = new Tab("Logs", createLogDisplayContent());
       logDisplayTab.setClosable(false);
       loadLogEntries(); 
       
       
       
       Tab effortLoggerTab = new Tab("EffortConsole", createEffortLoggerContent());
       effortLoggerTab.setClosable(false);
       tabPane.getTabs().addAll(effortLoggerTab, logDisplayTab);
       
       
       Scene scene = new Scene(tabPane, 800, 600);
       
       scene.getStylesheets().add(getClass().getResource("colors.css").toExternalForm());

       // ... rest of code
       primaryStage.setTitle("Effort Logger App");
       primaryStage.setScene(scene);
       primaryStage.show();
   }
   
   
   private void initializeAndStartScheduler() {
       scheduler = Executors.newScheduledThreadPool(1);

       // Schedule a task to run every minute
       scheduler.scheduleAtFixedRate(() -> {
           if (isMidnight()) {
               Platform.runLater(() -> {
                   if (timeline != null && timeline.getStatus() == Animation.Status.RUNNING) {
                       handleStopAction();
                   }
                   handleStartAction(); // this is to start a new log for the new day
               });
           }
       }, 0, 1, TimeUnit.MINUTES);
   }

   private boolean isMidnight() {
       LocalDateTime now = LocalDateTime.now();
       return now.getHour() == 0 && now.getMinute() == 0;
   }
   


   @Override
   public void stop() throws Exception {
       super.stop();
       if (scheduler != null) {
           scheduler.shutdownNow();
       }
   }
  
  
//  this method initializes all of the components
   private void initializeTheComponents() {
	   

   	
       startButton = new Button("Start an Activity"); //start button
       stopButton = new Button("Stop this Activity"); // stop button
       timerStatus = new Label("Clock is stopped"); // shows clocked stopped
       
       //drop down menus
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
       clockDisplay = new Label("00:00:00"); // starts at zero
       
       
      
   }
  // this handles when you press start
   private void handleStartAction() {
       startTime = LocalDateTime.now();
       timerStatus.setText("Clock is running");
      
       timerStatus.getStyleClass().remove("label-timer-stopped");
       timerStatus.getStyleClass().add("label-timer-running");
       

       
       
       if (timeline != null) {
           timeline.stop();
       }
       timeline = new Timeline(new KeyFrame(javafx.util.Duration.seconds(1), e -> update_TheTimer()));
       timeline.setCycleCount(Animation.INDEFINITE);
       timeline.play();
   }
   
  // this handles when you press stop
   private void handleStopAction() {
       stopTime = LocalDateTime.now();
       if (timeline != null) {
           timeline.stop();
       }
       duration = Duration.between(startTime, stopTime);
       timerStatus.setText("Clock is stopped");
      
       timerStatus.getStyleClass().remove("label-timer-running");
       timerStatus.getStyleClass().add("label-timer-stopped");
       

       
//        This will include the time and duration of the timer
       String logEntry = createTheLogEntry();
       addTheLogEntry(logEntry);
   }
 
   
   //this how the timer will be updated
  private void update_TheTimer() {
      Duration timeRunning = Duration.between(startTime, LocalDateTime.now());
      long hours = timeRunning.toHours();
      long minutes = timeRunning.toMinutes() % 60;
      long seconds = timeRunning.getSeconds() % 60;
      clockDisplay.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
  }
  
  
  // this is when you are creating a log entry
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
 
  private BorderPane createEffortLoggerContent() {
      BorderPane effortLoggerContent = new BorderPane();
      
      
    
      // This will layout the dropdowns and buttons in a GridPane
      GridPane grid = new GridPane();
      
      grid.setAlignment(Pos.CENTER);
      
      

      
      
      
      // Create an HBox for the Start Button
      HBox startButtonBox = new HBox(startButton);
      startButtonBox.setAlignment(Pos.CENTER_LEFT);
      startButtonBox.setPadding(new Insets(10));

      // Create an HBox for the Stop Button
      HBox stopButtonBox = new HBox(stopButton);
      stopButtonBox.setAlignment(Pos.CENTER_LEFT);
      stopButtonBox.setPadding(new Insets(10));
      
      startButton.setPrefSize(150, 30); // Width: 150, Height: 40
      stopButton.setPrefSize(150, 30);  // Width: 150, Height: 40

      
      grid.setHgap(10);
      grid.setVgap(10);
      grid.setPadding(new Insets(20));
      grid.add(new Label("Project:"), 0, 0);
      grid.add(projectDropdown, 1, 0);
      grid.add(new Label("Life Cycle Step:"), 0, 1);
      grid.add(lifeCycleStepDropdown, 1, 1);
      grid.add(new Label("Effort Category:"), 2, 0);
      grid.add(effortCategoryDropdown, 3, 0);
      grid.add(new Label("Deliverable:"), 2, 1);
      grid.add(deliverableDropdown, 3, 1);
     
     
      // Create a label to display the timer status
      timerStatus.setId("timerStatus"); // Set an ID for potential styling or retrieval
    

      
      VBox theMainContent = new VBox(10, timerStatus, clockDisplay, startButtonBox, grid, stopButtonBox);

      
      
      theMainContent.setAlignment(Pos.CENTER);
      
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
	   
	    // Initialize the clearLogsButton here
	    clearLogsButton = new Button("Clear Logs");
	    clearLogsButton.setOnAction(event -> clearLogs()); 
	   
	    //this will have the dropdown selections for the sort method
	    ComboBox<String> dropdown = new ComboBox<>();
	    //it can be in default, ascending, or descending
	    dropdown.getItems().addAll("Default", "Ascending", "Descending");
	    dropdown.setValue("Default");
	    //create a string to choose any of the options provided
	    
	    
	    

	    dropdown.setOnAction(event -> {
	    	String sortchoice = dropdown.getValue();
	    	//based on the choices of the user
	       if ("Descending".equals(sortchoice)) {
	    	   sortway(false);
	       }else if("Ascending".equals(sortchoice)) {
	    	   sortway(true);
	       }else {
	    	   defaultorder();
	       }
	       
	    });
	    
	    
	    HBox sortControls = new HBox(5, dropdown, clearLogsButton);
	    sortControls.setAlignment(Pos.CENTER_RIGHT); 
	    HBox topLayout = new HBox(5, logDisplayLabel, sortControls);
	    topLayout.setAlignment(Pos.CENTER_LEFT); 
	    HBox.setHgrow(sortControls, Priority.ALWAYS);
	    BorderPane logDisplayContent = new BorderPane();
	    logDisplayContent.setTop(topLayout); 
	    logDisplayContent.setCenter(logDisplay); 

	    
	    
	    
	    return logDisplayContent;
	}
 
 
 
  private void loadLogEntries() {
	  //check for the file
      File logFile = new File(LOG_FILE_PATH);
      //if the file exists, parse it and print it out
      if (logFile.exists()) {
          try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
              String line;
              while ((line = reader.readLine()) != null) {
                  logEntries.add(line);
                  logDisplay.appendText(line + "\n");
              }
          } catch (IOException e) {
              e.printStackTrace();
          }
      }
  }
  
  
  
  
  
//this method will load the log entries 
  private List<String> getLogEntries() {
	    try {
	        return Files.readAllLines(Paths.get(LOG_FILE_PATH));
	    } catch (IOException e) {
	        e.printStackTrace();
	        return new ArrayList<>(); // if there is an exception it will return an empty list
	    }
	}
//this right here will sort it
	private void sortway(boolean ascending) {
	    List<String> logs = getLogEntries(); // Get the unsorted log entries

	    Comparator<String> comparator = (entry1, entry2) -> {
	       //this will compare the durations in real time and if it is ascending it will print the smallest duration first
	        String dur1 = entry1.substring(entry1.lastIndexOf(", Duration: ") + ", Duration: ".length());
	        String dur2 = entry2.substring(entry2.lastIndexOf(", Duration: ") + ", Duration: ".length());
	        return ascending ? dur1.compareTo(dur2) : dur2.compareTo(dur1);
	    };
	    
	    List<String> sorted = logs.stream().sorted(comparator).collect(Collectors.toList());
	    //updating the sorts
	    logDisplay.clear();
	    sorted.forEach(log -> logDisplay.appendText(log + "\n"));
	}
	
private void defaultorder() {
	//clear the log formatting
	logDisplay.clear();
	//display how they were originally inserted
	for (String logz : logEntries) {
		logDisplay.appendText(logz + "\n");
	}
	
}

private void saveLogEntry(String logEntry) {
      try (PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE_PATH, true))) {
          out.println(logEntry);
      } catch (IOException e) {
          e.printStackTrace();
      }
  }
 
  private void addTheLogEntry(String logEntry) {
      logEntries.add(logEntry);
      logDisplay.appendText(logEntry + "\n");
      // Save the log entry to the file
      saveLogEntry(logEntry);
  }
 
  private void clearLogs() {
      logEntries.clear(); // Clear the in-memory log list
      logDisplay.clear(); // Clear the TextArea
      clearLogFile(); // Clear the logs.txt file
  }
  // This method clears the logs.txt file
  private void clearLogFile() {
      try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE_PATH))) {
          writer.print(""); // Clears the content of the file
      } catch (IOException e) {
          e.printStackTrace();
      }
  }
  public static void main(String[] args) {
      launch(args);
  }
}																															
