package application;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.animation.Animation;
import javafx.util.Duration;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import java.util.List;

//Created by Suleiman Bashir
//This is the class used to edit log elements that have already been created.
public class EffortEditorConsole extends Application {
	
	
	//Declare variables needed
	private static List<String> effortLogs = new ArrayList<>();
	 private static final String Lfile = "logs.txt";
    private static ComboBox<String>editor;
    
    
    public void start(Stage primaryStage) {
    	//creates the new scene for effort log editor
        primaryStage.setScene(new Scene(createEffortLogEditorContent(), 800, 600));
        primaryStage.setTitle("EffortLog Editor");
        primaryStage.show();
    }
    
    
    static Pane createEffortLogEditorContent() {
    	//displaying the content used for the effort log editor. 
        effortLogs= new ArrayList<>();
        //load the logs into the combo box
        loadeffortLogs();
        
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));
        ComboBox<String>effortLogcombo =new ComboBox<>();
        //add all the information and buttons used in the interface and set the positions for it. 
        effortLogcombo.getItems().addAll( "Developmental Project","Business Project");
        gridPane.add(new Label("Select the Project"), 0, 0);
        gridPane.add(effortLogcombo, 0, 1, 2, 1);
        effortLogcombo.getSelectionModel().selectedItemProperty().addListener((mbs ,previous,latest) -> {uploadEntry(latest); });
        Button clearLogButton = new Button("Clear This Effort Log");
        clearLogButton.setOnAction(w -> deleteOrClearLog());
        gridPane.add(new Label(" Clear Effort Log."), 2, 0, 2, 1);
        gridPane.add(clearLogButton, 2, 1, 2, 1);
        editor = new ComboBox<>();
        editor.getItems().addAll(effortLogs);
        gridPane.add(new Label(" Select the entry that you would like to modify or change"), 0, 2, 4, 1);
        editor.setMaxWidth(Double.MAX_VALUE);
        gridPane.add(editor,0, 3,4,1);
        //add the formats for the times to be inserted
        TextField sttime =new TextField();
        sttime.setPromptText("hh:mm:ss");
        TextField sptime= new TextField();
        sptime.setPromptText("hh:mm:ss");
        gridPane.add(sttime,0, 5);
        gridPane.add(sptime , 1,5);
        gridPane.add(new Label(" Modify the effortlog and press \"Update This Entry\" when you are done."), 0, 4, 4, 1);
        ComboBox<String> cyclestep= new ComboBox<>(); 
        ComboBox<String> effortlogbox =new ComboBox<>();
        cyclestep.getItems().addAll("Verifying", "Information Gathering", "Information Understanding","Planning");
        effortlogbox.getItems().addAll("Interuptions", "Deliverables", "Plans","Defects", "Others");
        
        editor.setOnShowing(event -> refreshComboBoxItems());
       // Declare the buttons that will update the entry and call the action that will change the logs in the text file
        Button modifyingButton = new Button("Update Entry");
        modifyingButton.setDisable(true); 
       editor.getSelectionModel().selectedItemProperty().addListener((options , oldValue,newValue) -> {
          modifyingButton.setDisable(newValue== null);
   
        });
        modifyingButton.setOnAction(w->{
        	 String project = editor.getSelectionModel().getSelectedItem();
        	 String cycle1= cyclestep.getSelectionModel().getSelectedItem();
        	 String sttime1= sttime.getText();
             String sptime1 =sptime.getText();
             String effort1= effortlogbox.getSelectionModel().getSelectedItem();
             //call the combo box for the exact selection of the logs and update the log based off of that
            int choice = editor.getSelectionModel().getSelectedIndex();
            String defect = "None"; 
            if (project != null) {
                String[] parts = project.split(", ");
                for (String part : parts) {
                    if (part.startsWith("Defect:")) {
                        defect = part.substring("Defect:".length()).trim();
                        break; // Stop after finding the defect
                    }
                }
            }
            updateLogEntry(choice, sptime1, sttime1, cycle1, effort1, defect);
        });
        //add the labels and buttons needed to modify more elements inside the logs
        gridPane.add(modifyingButton, 3, 5);
        gridPane.add(new Label("Life Cycle Step:"),0, 6);
        gridPane.add(cyclestep, 1, 6, 3, 1); 
        gridPane.add(new Label("Effort Category:"),0,7);
        gridPane.add(effortlogbox, 1, 7, 2, 1); 
        //Declare and call the methods to split, delete or clear the log(s) selected
        Button deleteSelection = new Button("Delete the entry");
        deleteSelection.setOnAction(w -> deleteOrClearLog());
        gridPane.add(new Label("Delete the chosen log"), 0,9,2,1);
        gridPane.add(deleteSelection, 0, 10, 2, 1);
        Button splitButton = new Button("Split into two entries");
        gridPane.add(new Label("Split the entry"), 0, 11);
        gridPane.add(splitButton, 0, 12);
        splitButton.setOnAction(w -> {
        	//call choice to be the exact log the user wants from the editor combo box drop down
            int choice = editor.getSelectionModel().getSelectedIndex();
            //call the exact choice into the split method 
            duplicateEntry(choice);
        });
        //create new borderpane and return it so it can be used as a tab on effort console. 
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(gridPane);
        return new BorderPane(gridPane); 
    }
    
    //this method will  update the given results from the logs file 
    public static void restoreEffortLogs() {
    	//it calls the restorecombo method to give updates and call them 
        Timeline clock= new Timeline (new KeyFrame (Duration.seconds(5),e ->{ restorecombo(); }));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }
    
    
    //When a project is selected (either business or developmental
    private static void uploadEntry(String selectedlog) {
        List<String> filteredLogs = effortLogs.stream()
        	//it will give the user a choice to display the type of log and then clear
        	//then filter through to only show the type of project they were looking for 
            .filter(logEntry -> logEntry.contains("Project: " + selectedlog))
            .collect(Collectors.toList());
        Platform.runLater(() -> {
        	editor.requestLayout();
            editor.getItems().clear(); 
            
            editor.getItems().addAll(filteredLogs); 
            editor.requestLayout(); 
        });
    }
    
    //this will run in the background in case the user does not filter a type of project, it will display all of them
    private static void refreshComboBoxItems() {
        Platform.runLater(() -> {
            try {
                List<String> logEntries = Files.readAllLines(Paths.get(Lfile));
                editor.getItems().setAll(logEntries);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    //this method updates the logs based on what the user has entered
    private static void updateLogEntry(int userChoice, String stop, String start, String lifecycle1, String effort1, String defect) {
        if (userChoice >= 0 && userChoice < effortLogs.size()) {
            String logs = effortLogs.get(userChoice);
            String[] logparts = logs.split(", ");
            //if the selected null variables are not null it will piece together new variables 
            if (lifecycle1 != null && !lifecycle1.isEmpty()) {
                logparts[1] = "Step: " + lifecycle1;
            }
            
            if (effort1 != null && !effort1.isEmpty()) {
                logparts[2] = "Category: " + effort1;
            }
            if (start != null && !start.isEmpty()) {
                logparts[4] = "Start Time: " + start;
            }
            
            if (stop != null && !stop.isEmpty()) {
                logparts[5] = "Stop Time: " + stop;
            }
            if (defect != null && !defect.isEmpty()) {
                logparts[7] = "Defect: " + defect;
            }
            //then the duration will be recalculated
            if (start != null && !start.isEmpty() && stop != null && !stop.isEmpty()) {
                String finalTime = durationSolver(stop, start);
                logparts[6] = "Duration: " + finalTime;
            }
            //join the elements together and constantly update the logs file
            String usersEntry = String.join(", ", logparts);
            effortLogs.set(userChoice, usersEntry);
            inputEffort();
            restorecombo(); 
            Platform.runLater(() -> editor.getItems().set(userChoice, usersEntry));
        }
    }

   //because the logs file is constantly updating the combo box for the editor should be aswell
    private static void restorecombo() {
        Platform.runLater(() -> {
            effortLogs.clear();
            //clear and load the logs again
            loadeffortLogs(); 
            editor.getItems().setAll(effortLogs);
        });
    }
    
    //this is for the combo box to parse and read through individual logs before editing them
    public static void inputEffort(){
    	try (BufferedWriter analyze =new BufferedWriter (new FileWriter(Lfile ,false))){
    		//read line by line and write the new line in order to display it 
    		for (String swriterStr :effortLogs) {
    			analyze.write(swriterStr);
    			analyze.newLine();
    			}
    		//catch the problem if there are any errors
    		}catch(IOException w){
    			w.printStackTrace();
    			}
    	}
    
    //after it parses, it has to recognize each text line and parse it 
    public static void loadeffortLogs() {
        effortLogs.clear();
        //once it parses, it will add the new line to the created array list
        try (BufferedReader view = new BufferedReader(new FileReader(Lfile))) {
            String line;
            while ((line = view.readLine()) != null) {
                effortLogs.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //this method will delete or clear the log in the effort log editor
    private static void deleteOrClearLog(){
    	final int notdelete = -1;
    	//as long as it is not -1 the log file will remove the combo box selection
    	int deleteLog = editor.getSelectionModel().getSelectedIndex();
        if (deleteLog != notdelete) {
            effortLogs.remove(deleteLog);
            //read the lines and update the combo again
            inputEffort();
            restorecombo();
        }
    }
    
    //this will split the specific log from the logs.txt file
    public static void splitLog( int check  ) {
    	String exactLog;
    	//if the log does exist it will add it to the combo box so it will appear
    	if ( check <effortLogs.size()&&check>=0) {
    		exactLog=effortLogs.get(check);
    		effortLogs.add(exactLog);
    		//make sure to read the effort log again
    		inputEffort(); 
    		Platform.runLater(() -> {editor.getItems().add(exactLog);});		 
    		}
    }
    
    //this is the method that is used to split the log selected into two logs
    private static void duplicateEntry(int checking) {
    	String effortLogCheck;
    	// if the log exists then it will get the specific input from the combo box
    	//then add it again then read using the inputEffort method
        if (checking<effortLogs.size()) {
            effortLogCheck=effortLogs.get(checking);
            effortLogs.add(effortLogCheck);
            inputEffort();
            Platform.runLater(() -> editor.getItems().add(effortLogCheck));
        }else if (effortLogs == null) {
        	System.out.println("I'm sorry, there are no logs to split. Try again. ");
        } 
    }
    
    //duration Solver will be called to recalculate the time if it has been changed 
    private static String durationSolver(String start1, String stop1) {
        DateTimeFormatter desiredInput = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime start = LocalTime.parse(start1,desiredInput);
        LocalTime stop = LocalTime.parse(stop1 , desiredInput);
        //calculate the duration using ChronoUnit
        long durLength=ChronoUnit.SECONDS.between(start, stop);
        if ( durLength<0) { 
        	durLength+= 24 *3600;
        }
        //from the calculated seconds convert into desired time format and return to display it 
        long hours = durLength/ 3600;
        long minutes = (durLength%3600) /60;
        long seconds = durLength %60;
        String formation=String.format("%02d:%02d:%02d", hours, minutes, seconds);
        
        return formation;
    }
    
    //This refresh is used by the effortLog editor in order to find any defects and is called in the effort app method 
    public static void refreshEffortLogsComboBox() {
        //it will look for logs when they load in
        loadeffortLogs();
        Platform.runLater(() -> {
            editor.getItems().clear();
            //clear and add the logs to the array list
            editor.getItems().addAll(effortLogs);
        });
    }


}
