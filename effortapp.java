package application;

import javafx.application.Application;
import javafx.application.Platform;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.geometry.Pos;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.Animation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;


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
    
    
    //ADDED BY SULEIMAN
    private Button clearLogsButton;
    String currentSortChoice = "default";
    String LOG_FILE_PATH = "log.txt";

    @Override

    public void start(Stage primaryStage) {
        logEntries = new ArrayList<>();
        TabPane tabPane = new TabPane();
        initializeTheComponents();

        Tab effortLoggerTab = new Tab("EffortConsole", createEffortLoggerContent());
        effortLoggerTab.setClosable(false);

        Tab logDisplayTab = new Tab("Logs", createLogDisplayContent());
        logDisplayTab.setClosable(false);




	    
	//ADDED BY SULEIMAN 
        loadLogEntries();
        EffortEditorConsole effortLogEditor = new  EffortEditorConsole();
        Tab effortLogEditorTab = new Tab("Effort Log Editor", effortLogEditor.createEffortLogEditorContent());
        effortLogEditorTab.setClosable(false);
        tabPane.getTabs().add(effortLogEditorTab);
        redoLog();


	    


	    
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




//EDITED BY SULEIMAN
   
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
	 //ADDED BY SULEIMAN
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

	
     public static Pane createEffortLogEditorContent() {
        GridPane gridPane = new GridPane();
        EffortEditorConsole.inputEffort();
        return gridPane;
    }
    
    
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
		 currentSortChoice = ascending ? "Ascending" : "Descending";
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
	currentSortChoice = "Default";
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
	private void redoLog() {
	    Timer timer = new Timer(true); //give the timer and set the boolean to true
	    timer.scheduleAtFixedRate(new TimerTask() {
	        @Override
	        public void run() {
	        	//constantly run the refresh that uses permanently refresh through runLater on platform
	        	 permanentlyRefresh();
	        }
	    }, 0, 2000); //this will
	}

	public static void staticreload() {
	  //call the runlater platform to go throguh the permanently refresh method
	    Platform.runLater(() -> {
	       
	        new effortapp(). permanentlyRefresh();
	    });
	}
	//permanently refresh using run later
	private void permanentlyRefresh() {
	  Platform.runLater(() -> {
		  //constarnly clear the display and the log entries
	        logEntries.clear();
	        logDisplay.clear();
	        //call a new list of streings to get the log entries and upload as files
	        List<String>logfiles= getLogEntries();
	        logEntries.addAll (logfiles);
	        // using the switch case
	        //make them either ascending or descending or default
	        switch (currentSortChoice) {
	            case "Descending":
	                sortway(true);
	                break;
	            case "Ascending":
	                sortway(true);
	                break;
	            default:
	            	//call the default method
	                defaultorder();
	                break;
	        }
	    });
	}
   public static void main(String[] args) {
       launch(args);
   }
}

