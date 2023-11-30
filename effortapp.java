package application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class effortapp extends Application {
	
//	most of the variables and classes are self explanatory
   private LocalDateTime startTime;
   private LocalDateTime stopTime;
   private Duration duration;
   private Timeline timeline;
   private Label clockDisplay;
   private ArrayList<String> logEntries;
   private TextArea logDisplay; // This will be Text area for displaying logged information
   private TextArea logSearchDisplay;
   private Label timerStatus; // Status label for the timer
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
   private ComboBox<String> tagDropdown;
   private static final String LOG_FILE_PATH = "logs.txt";
   private Button clearLogsButton;
   private Button refreshButton;
   private ComboBox<String> tagDropdownSearch = new ComboBox<>();
   private ComboBox<String> projectListDropdown; // This dropdown in DefectConsole lets user access any project logged
   private TextField defectName;
   private Button updateDefect;
   private Button deleteDefect;
   private String currentSortChoice = "default";

   
   private VBox logEntriesContainer;
   @Override
   public void start(Stage primaryStage) {
       logEntries = new ArrayList<>();
       TabPane tabPane = new TabPane();
       initializeTheComponents();
       totalBusinessTime = new ArrayList<>();
       totalDevelopmentalTime = new ArrayList<>();
       // Initialize the clearLogsButton before creating the tab content
       clearLogsButton = new Button("Clear Logs");
       clearLogsButton.setOnAction(event -> clearLogs());
       Tab logDisplayTab = new Tab("Logs", createLogDisplayContent());
       logDisplayTab.setClosable(false);
       Tab logSearchTab = new Tab("Search");
       logSearchTab = new Tab("Search", createLogSearchDisplayContent());
       logSearchTab.setClosable(false);
       Tab newLogTab = new Tab ("Logs");
       newLogTab = new Tab("Log-New", createNewLogDisplayContent());
       newLogTab.setClosable(false);
       //loadLogEntries(); // loadLogEntries is called after createLogDisplayContent
       Tab effortLoggerTab = new Tab("EffortConsole", createEffortLoggerContent());
       effortLoggerTab.setClosable(false);
       
	    // Adding the DefectConsole tab
	    Tab defectConsoleTab = new Tab("DefectConsole", createDefectConsoleContent());
	    defectConsoleTab.setClosable(false);
	    loadLogEntriesFromFile();

	 defectConsoleTab.setOnSelectionChanged(event -> {
	        if (defectConsoleTab.isSelected()) {
	        	refreshdefect();
	        }
	    });
	    logDisplayTab.setOnSelectionChanged(event -> {
	        if (logDisplayTab.isSelected()) {
	        	renewLogtextdisplay();
	        }
	    });
	   
       //AbdallaIntegration
       PlanningPokerApp planningPokerApp = new PlanningPokerApp();
       Tab planningPokerTab = planningPokerApp.createPlanningPokerTab();
       planningPokerTab.setClosable(false);
       
       //Sulieman Integration
       //ADDED BY SULEIMAN 
       EffortEditorConsole effortLogEditor = new  EffortEditorConsole();
       Tab effortLogEditorTab = new Tab("Effort Log Editor", effortLogEditor.createEffortLogEditorContent());
       effortLogEditorTab.setClosable(false);
       
       tabPane.getTabs().addAll(effortLoggerTab, effortLogEditorTab, defectConsoleTab, newLogTab, logSearchTab, planningPokerTab);
       Scene scene = new Scene(tabPane, 800, 600);
       
       scene.getStylesheets().add(getClass().getResource("colors.css").toExternalForm());

       primaryStage.setTitle("Effort Logger App");
       primaryStage.setScene(scene);
       primaryStage.show();
   }
  
  
//  This is where the components will be initialized
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
       startButton.setOnAction(e -> handleStartAction());
       stopButton.setOnAction(e -> handleStopAction());
       updateDefect = new Button("Update Defect");
       deleteDefect = new Button("Delete Defect");
       updateDefect.setOnAction(e -> handleUpdateDefectAction());
       deleteDefect.setOnAction(e -> handleDeleteDefectAction());
       clockDisplay = new Label("00:00:00");
       defectName = new TextField();
       projectListDropdown = new ComboBox<>();
       tagDropdown = new ComboBox<>();
       tagDropdown.getItems().addAll("UI/UX", "Database", "API", "Mobile", "DevOps", "Cloud", "Security", "Data Science", "Machine Learning", "AI", "Analytics", "Network", "Authentication", "Infastructure", "Payment Processing", "System Admin.", "Version Control", "SEO(Search Engine Optimization)", "E-commerce", "Performance Optimization", "QA", "Testing", "Embedded Systems");
       
       
      
   }
  
   //handles the start button action when pressed
   private void handleStartAction() {
       startTime = LocalDateTime.now();
       timerStatus.setText("Clock is running");
      
       timerStatus.getStyleClass().clear();
       timerStatus.getStyleClass().add("label-timer-running");
      
       if (timeline != null) {
           timeline.stop();
       }
       timeline = new Timeline(new KeyFrame(javafx.util.Duration.seconds(1), e -> updateTheTimer()));
       timeline.setCycleCount(Animation.INDEFINITE);
       timeline.play();
   }

   //handles the stop button action when pressed
   private void handleStopAction() {
       stopTime = LocalDateTime.now();
       if (timeline != null) {
           timeline.stop();
       }
       duration = Duration.between(startTime, stopTime);
       timerStatus.setText("Clock is stopped");

       
       timerStatus.getStyleClass().clear();
       timerStatus.getStyleClass().add("label-timer-stopped");
       
       double numDuration = 0;
       String numRegex = "[+-]?([0-9]*[.])?[0-9]+";
       String extractedNum = String.valueOf(duration).replaceAll("[^0-9.]", "");

       if (extractedNum.matches(numRegex)) {
           numDuration = Double.parseDouble(extractedNum);
       } else {
           System.out.print("Error. Please try again!");
       }
       
       // adding duration of each kind of project to respective arrays
       if (projectDropdown.getValue().equals("Business Project")) {
           totalBusinessTime.add(numDuration);
       } else {
           totalDevelopmentalTime.add(numDuration);
       }

       double sumB = 0;
       double sumD = 0;
       double avgB = 0;
       double avgD = 0;
       
       // calculating averages for both kinds of projects
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
       
       // displaying average time with accuracy of two digit milliseconds
       avgBusinessTimeLabel.setText(String.format("%.2f", avgB) + " seconds");
       avgDevelopmentalTimeLabel.setText(String.format("%.2f", avgD) + " seconds");

       timerStatus.getStyleClass().remove("label-timer-running");
       timerStatus.getStyleClass().add("label-timer-stopped");

       String logEntry = createTheLogEntry();
       addTheLogEntry(logEntry);
   }
 
   // this updates the timer
  private void updateTheTimer() {
      Duration timeRunning = Duration.between(startTime, LocalDateTime.now());
      long hours = timeRunning.toHours();
      long minutes = timeRunning.toMinutes() % 60;
      long seconds = timeRunning.getSeconds() % 60;
      clockDisplay.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
  }
  
  // this is how a log entry is created
  private String createTheLogEntry() {
	    String project = projectDropdown.getValue() != null ? projectDropdown.getValue() : "None";
	    String lifeCycleStep = lifeCycleStepDropdown.getValue() != null ? lifeCycleStepDropdown.getValue() : "None";
	    String effortCategory = effortCategoryDropdown.getValue() != null ? effortCategoryDropdown.getValue() : "None";
	    String deliverable = deliverableDropdown.getValue() != null ? deliverableDropdown.getValue() : "None";
	    String projectTag = tagDropdown.getValue() != null ? tagDropdown.getValue() : "None";
	    String defect = "None";
	   
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
	    return String.format("Project: %s, Step: %s, Category: %s, Deliverable: %s, %s, Defect: %s, %s",
	                         project, lifeCycleStep, effortCategory, deliverable, timeInfo, defect, projectTag);
	}
  	//	displaying information stored in logs file on the Logs tab
	  private void loadLogEntriesFromFile() {
	      try (BufferedReader reader = new BufferedReader(new FileReader(LOG_FILE_PATH))) {
	          String line;
	          while ((line = reader.readLine()) != null) {
	        	  
	        	  
	              logEntries.add(line);
	              logDisplay.appendText(line + "\n");
	              
	             
	              projectListDropdown.getItems().addAll(line);
	            
	          }
	          
	      } catch (IOException e) {
	          e.printStackTrace(); // Handling the exception just in case
	      }
	  
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
      grid.add(new Label("Project Tag:"), 0, 4);
      grid.add(tagDropdown, 1, 4);
      grid.add(new Label("Average Business Project time:"), 0, 5);
      grid.add(avgBusinessTimeLabel, 1, 5);
      grid.add(new Label("Average Developmental Project time:"), 0, 6);
      grid.add(avgDevelopmentalTimeLabel, 1, 6);
     
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
	   
	    // Initialize the clearLogsButton here
	    clearLogsButton = new Button("Clear Logs");
	    clearLogsButton.setOnAction(event -> clearLogs()); // Assuming clearLogs() is a method that handles log clearing
	   
	    // Create the HBox for the button
	    HBox buttonBox = new HBox(clearLogsButton);
	    buttonBox.setAlignment(Pos.CENTER);
	    buttonBox.setPadding(new Insets(10));
	   
	    // Create the BorderPane for the log display
	    BorderPane logDisplayContent = new BorderPane();
	    logDisplayContent.setPadding(new Insets(10));
	    logDisplayContent.setTop(logDisplayLabel); // Set the label at the top of the BorderPane
	    logDisplayContent.setCenter(logDisplay); // Set the TextArea in the center
	    logDisplayContent.setBottom(buttonBox); // Set the HBox with the button at the bottom
	   
	    return logDisplayContent;
	}
  
  // this code ensures the defect is updated correctly when Update Defect button is pressed
  private void handleUpdateDefectAction() {
  	String selectedProject = projectListDropdown.getValue();
      String defectToUpdate = defectName.getText();
     // checking if a project is selected or not and the defect name is empty or not
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

      
      // updating the logs file accordingly
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE_PATH))) {
          for (String entry : logEntries) {
              writer.write(entry + "\n");
          }
      } catch (IOException e) {
          e.printStackTrace(); // Handling the exception just in case
      }
  }
  
  
  
  private void updateProjectListDropdown() {
  	projectListDropdown.getItems().clear();
  	
      // Update the project list dropdown in DefectConsole tab with the complete log entries for each project
      for (String entry : logEntries) {
    	  
          if (!projectListDropdown.getItems().contains(entry)) {
              projectListDropdown.getItems().add(entry);
          }
      }
  }
  
  
  // this code ensures that the current defect is deleted correctly when Delete Defect button is pressed
  private void handleDeleteDefectAction() {
  	String selectedProject = projectListDropdown.getValue();
  	// checking if the user selected a project or not
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
	      EffortEditorConsole.reloadlogsanddisplayeditor();
      }
     
      // updating the logs file accordingly
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE_PATH))) {
          for (String entry : logEntries) {
              writer.write(entry + "\n");
          }
      } catch (IOException e) {
          e.printStackTrace(); // Handling the exception just in case
      }
  }
  
  
  private BorderPane createNewLogDisplayContent() {
	  //Create borderpane for display
	  BorderPane newLogDisplayContent = new BorderPane();
	  //add padding
	  newLogDisplayContent.setPadding(new Insets(10));
	  
	  //Add Top Label
	  Label newLogDisplayLabel = new Label("Log Information - New");
	  newLogDisplayLabel.setFont(new Font("Arial", 16)); // Set font and size for the label
	  newLogDisplayLabel.setPadding(new Insets(5)); // Add some padding to the label for aesthetics
	  //newLogDisplayContent.setTop(newLogDisplayLabel); // Set the label at the top of the BorderPane
	  
	  //instansiate VBox
	  VBox logEntiresContainer = new VBox(5);
	  logEntiresContainer.setPadding(new Insets(10));
	  

	  //call funcition passing in VBox and File
	  loadLogEntires(logEntiresContainer, LOG_FILE_PATH);
	  
	  //Create a place for the VBox to go
	  ScrollPane scrollPane = new ScrollPane(logEntiresContainer);
	  scrollPane.setFitToWidth(true);
	  
	  //allign to the middle
	  newLogDisplayContent.setCenter(scrollPane);
	  
	   // Initialize the clearLogsButton here
	  	refreshButton = new Button("Refresh");
	    clearLogsButton = new Button("Clear Logs");
	    
	    //Set up re-fresh button
	    refreshButton.setOnAction(event -> {
		    logEntiresContainer.getChildren().clear();
		    loadLogEntires(logEntiresContainer, LOG_FILE_PATH);
	    });
	    
	    //Set up clear log button
	    clearLogsButton.setOnAction(event -> {
		    clearLogs();
		    logEntiresContainer.getChildren().clear();
		    loadLogEntires(logEntiresContainer, LOG_FILE_PATH);
	    });
	    
	    	
	    ComboBox<String> dropdown = new ComboBox<>();
	    //it can be in default, ascending, or descending
	    dropdown.getItems().addAll("Default", "Ascending", "Descending");
	    dropdown.setValue("Default");
	    
	    //gives dropdown functionality
	    dropdown.setOnAction(event -> {
	        String sortChoice = dropdown.getValue();
	        // Call updateLogDisplay with the selected sort choice and the VBox container
	        updateLogDisplay(sortChoice, logEntiresContainer);
	    });
	    
	    dropdown.setOnAction(event -> {
	        String sortChoice = dropdown.getValue();
	        updateLogDisplay(sortChoice, logEntiresContainer);
	    });

	    //Adds to HBox containers to add to top of the Pane
	    HBox sortControls = new HBox(5, refreshButton, clearLogsButton, dropdown);
	    sortControls.setAlignment(Pos.CENTER_LEFT);
	    HBox topLayout = new HBox(5, newLogDisplayLabel, sortControls);
	    HBox.setHgrow(sortControls, Priority.ALWAYS);
	    
	    newLogDisplayContent.setTop(topLayout);
	    
	  return newLogDisplayContent;
  }
  
  //this method will update the logdisplay given sortchoice that is available in the logs tab
 private void updateLogDisplay(String sortChoice, VBox logEntriesContainer) {
	  // if the user selects descending it will call false and ascending will be true inside the projectdurationsorter class
	    switch (sortChoice) {
	        case "Descending":
	        	//call sortway
	            sortway(false, logEntriesContainer);
	            break;
	        case "Ascending":
	        	//call sortway
	            sortway(true, logEntriesContainer);
	            break;
	        default:
	        	//default will just call the default method
	            defaultorder(logEntriesContainer);
	            break;
	    }
  }
 
 
  private void defaultorder(VBox logEntriesContainer) {
	  //if the currentsortchoice was selected at default 
	    currentSortChoice = "Default";
	    if (logEntriesContainer != null) {
	    	//clear the log entries and then display them from the text file again
	    	logEntriesContainer.getChildren().clear();
		    loadLogEntires(logEntriesContainer,LOG_FILE_PATH);
	    }
	}

  
	private void sortway(boolean ascending, VBox logEntriesContainer) {
		//if sortway is called it will be used in either ascending or descending 
		//by using the storted string to get the list of items and collect them
	    currentSortChoice = ascending ? "Ascending" : "Descending";
	    List<String> sorted = getLogEntries().stream()
	        .sorted(getresult(ascending))
	        .collect(Collectors.toList());
	    //this clear the vbox and format it the way it should
	    logEntriesContainer.getChildren().clear();
	    sorted.forEach(log -> logEntriesContainer.getChildren().add(new LogEntryFormated(log)));
	}
	
	//when this method is used, it will check the ascending descending
	private Comparator<String> getresult(boolean ascending) {
		
		//there could be only two options for the duration
	    return(one,two) -> {
	        String first = one.substring(one.lastIndexOf(", Duration: ") + ", Duration: ".length());
	        String second = two.substring(two.lastIndexOf(", Duration: ") + ", Duration: ".length());
	        //return the ascending and compare it to seconds
	        return ascending ? first.compareTo(second) : second.compareTo(first);
	    };
	}
	
  // this method will read the log entries 
  private List<String> getLogEntries() { 
	    try {
	    	//return all the files in logs.txt
	        return Files.readAllLines(Paths.get(LOG_FILE_PATH));
	    } catch (IOException e) {
	        e.printStackTrace(); 
	        //if there is an error it will return the array list
	        return new ArrayList<>(); 
	    }
	}
  
  //this method will clear the logs and print null
	private void clearLogFile1() {
		
	    try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE_PATH))) {
	    	//write nothing in the file and erase
	        writer.print(""); 
	    } catch (IOException e) {
	        e.printStackTrace(); 
	    }
	}
		
	//constantly refresh the logs in the background
	private void redoLog1() {
		Timer timer = new Timer(true); 
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				//use the permanently refresh method
				permanentlyRefresh();
		        }
		    }, 0, 2000); 
		}
	//refresh the effort app class and start from a new variable
	public static void staticreload1() {
		Platform.runLater(() -> {
			new effortapp(). permanentlyRefresh();
			});
		}
	private void loadLogEntires(VBox container, String logFilePath) {
	  	//this logfile will be declared as the logs.txt file
	    File logFile = new File(logFilePath);
	    //if the file exits it will load the file in and read it line by line
	    if (logFile.exists()) {
	        try (BufferedReader load = new BufferedReader(new FileReader(logFile))) {
	            String line;
	            //runs through entire file 
	            while ((line = load.readLine()) != null) {
	            	//Creates new object
	            	LogEntryFormated logEntryFormatted = new LogEntryFormated(line);
	            	//adds to the vbox for the new display
	                container.getChildren().add(logEntryFormatted);
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}
  private BorderPane createLogSearchDisplayContent() {
	  //Sets up BorderPane for display
	  	BorderPane logSearchDisplayContent = new BorderPane();
	    logSearchDisplayContent.setPadding(new Insets(10));

	    // Create a VBox to hold the formatted log entries
	    VBox logEntriesContainer = new VBox(10);
	    logEntriesContainer.setPadding(new Insets(10));
	    ScrollPane scrollPane = new ScrollPane(logEntriesContainer);
	    scrollPane.setFitToWidth(true);

	    Label logDisplayLabel = new Label("Log Information"); //Create Label
	    logDisplayLabel.setFont(new Font("Arial", 16)); // Set font and size for the label
	    logDisplayLabel.setPadding(new Insets(5)); // Add some padding to the label for aesthetics
	  
	    
	    //Fill dropdown
	    tagDropdownSearch.getItems().addAll("UI/UX", "Database", "API", "Mobile", "DevOps", "Cloud", "Security", "Data Science", "Machine Learning", "AI", "Analytics", "Network", "Authentication", "Infastructure", "Payment Processing", "System Admin.", "Version Control", "SEO(Search Engine Optimization)", "E-commerce", "Performance Optimization", "QA", "Testing", "Embedded Systems");
	    tagDropdownSearch.setPromptText("Select Tag");
	    
	    //create searchbutton
	    Button searchButton = new Button("Search");
	    
	    //Give Button functionality
	    searchButton.setOnAction(e -> loadSearchedLines(logEntriesContainer, tagDropdownSearch.getValue())); 
	    
	    //Create container for menu and button
	    HBox searchHbox = new HBox(10);
	    searchHbox.setPadding(new Insets(10,10,10,10));
	    
	    //Put fields into hbox
	    searchHbox.getChildren().addAll(tagDropdownSearch, searchButton);
	    
	    //Add HBox and ScrollPane to scene
	    logSearchDisplayContent.setTop(searchHbox);
	    logSearchDisplayContent.setCenter(scrollPane);
	    
	  return logSearchDisplayContent;
  }
 
  
  private void loadSearchedLines(VBox container, String selectedTag) {
	    container.getChildren().clear(); // Clear the container of the vbox
	    try {
	        //write the file lines and use the selected tag
	        Files.lines(Paths.get(LOG_FILE_PATH)).filter(line -> line.endsWith(selectedTag)).forEach(line -> {
	        		//Creates object for new entry
	            	 LogEntryFormated entry = new LogEntryFormated(line);
	            	 //adds new entry to the VBox container
	                 container.getChildren().add(entry);
	             });
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
  
  private void loadLogEntries() {
	  //while the log file exists
      File logFile = new File(LOG_FILE_PATH);
      if (logFile.exists()) {
    	  //try loading and adding the parsed lines into the logdisplay and log entries
          try (BufferedReader load = new BufferedReader(new FileReader(logFile))) {
              String line;
              while ((line = load.readLine()) != null) {
                  logEntries.add(line);
                  logDisplay.appendText(line + "\n");
              }
          } catch (IOException e) {
              e.printStackTrace();
          }
      }
  }
 
  //this will save to the new log entries
  private void saveLogEntry(String logEntry) {
	  //print out the entries from the file path 
      try (PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE_PATH, true))) {
          out.println(logEntry);
      } catch (IOException e) {
          e.printStackTrace();
      }
  }
 
  //add the new entries
  private void addTheLogEntry(String logEntry) {
      logEntries.add(logEntry);
      logDisplay.appendText(logEntry + "\n");
      // call it to the savelogentry method
      saveLogEntry(logEntry);
  }
 
  private void clearLogs() {
	  //clear both the array list and the text field 
      logEntries.clear();
      logDisplay.clear(); 
     // call to clear the file
      clearLogFile1(); 
  }

	public static void staticreload() {
	  //call the run later platform to go through the permanently refresh method
	    Platform.runLater(() -> {
	       
	        new effortapp(). permanentlyRefresh();
	    });
	}
	
	//permanently refresh the logs and the choices of the sort
	private void permanentlyRefresh() {
	  Platform.runLater(() -> {
		 //clear the arraylist and the text field display
	        logEntries.clear();
	        logDisplay.clear();
	        List<String>logfiles= getLogEntries();
	        logEntries.addAll (logfiles);
	        //add the files and make them sort between ascending descending default 
	        //from within the vbox
	        switch (currentSortChoice) {
            case "Descending":
                sortway(false, logEntriesContainer);
                break;
            case "Ascending":
                sortway(true, logEntriesContainer);
                break;
            default:
                defaultorder(logEntriesContainer);
                break;
	        }
	    });
	}
	
	//refresh the combobox of the defect console
	private void refreshdefect() {
		//clear the project items
		projectListDropdown.getItems().clear();
	    try (BufferedReader defectReader = new BufferedReader(new FileReader(LOG_FILE_PATH))) {
	    	//read through the file and while it is not null, upload the current list
	        String line;
	        while ((line = defectReader.readLine()) != null) {
	            projectListDropdown.getItems().add(line);
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	//this will ensure that the log display always constantly updates
	private void renewLogtextdisplay() {
		//clear the text field
	    logDisplay.clear(); 
	    try (BufferedReader logdisplayer = new BufferedReader(new FileReader(LOG_FILE_PATH))) {
	        String line;
	        //read through the line and while it is not null append each line 
	        while ((line =logdisplayer.readLine()) != null) {
	            logDisplay.appendText(line + "\n"); 
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
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
