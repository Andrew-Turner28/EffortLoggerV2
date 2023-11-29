//import the package and pretty much all the items involved in the code 
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
//this is the class that will run all of the things necessay to edit
//the text files, therefore the application
public class EffortEditorConsole extends Application {
	//these declare the combobox, effortlogs(as an array)and the file that we will edit, save or delete from 
	private static List<String> effortLogs = new ArrayList<>();
	 private static final String Lfile = "logs.txt";
    private static ComboBox<String>editor;
    
    //here is what is run when the code displays
    //it will create the page and show it 
    public void start(Stage primaryStage) {
    	//creates the new scene for effortlog editor
        primaryStage.setScene(new Scene(createEffortLogEditorContent(), 800, 600));
        //creates the title for the page and shows it 
        primaryStage.setTitle("EffortLog Editor");
        primaryStage.show();
    }
    //this will display the gui for the effortlogger
    static Pane createEffortLogEditorContent() {
    	
    	//start by reinitializing what is needed
        effortLogs= new ArrayList<>();
        //this is called to read the line for the log.txt file one by one 
        //it will display just in case 
        loadeffortLogs();
        //set the new needed GridPane
        GridPane gridPane = new GridPane();
        
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        
        gridPane.setPadding(new Insets(10));
       
        //declare the effortLogCombo 
        ComboBox<String>effortLogcombo =new ComboBox<>();
        //the drop down will include what is needed depending on the type of project the user selects
        effortLogcombo.getItems().addAll( "Developmental Project","Business Project");
        //this will be the name of the question the user will be given 
        gridPane.add(new Label("Select the Project"), 0, 0);
        //set the position for it 
        gridPane.add(effortLogcombo, 0, 1, 2, 1);
        //this will select the exact project the user clicks on and update it by using uploadEntry
        effortLogcombo.getSelectionModel().selectedItemProperty().addListener((mbs ,previous,latest) -> {uploadEntry(latest); });
        //the clear log button will erase the project that has been stored on the effortF
        Button clearLogButton = new Button("Clear This Effort Log");
        clearLogButton.setOnAction(w -> deleteOrClearLog());
        //set the question so the user understands what the interface is 
        gridPane.add(new Label(" Clear Effort Log."), 2, 0, 2, 1);
        //place the button for the clear log to display 
        gridPane.add(clearLogButton, 2, 1, 2, 1);
        //clear the buttons and generate the new comboBox
        editor = new ComboBox<>();
        //get the effortLogs logs and add them to the drop down down when you select
        editor.getItems().addAll(effortLogs);
        //give the dropdown menu and the directions to the user 
        gridPane.add(new Label(" Select the entry that you would like to modify or change"), 0, 2, 4, 1);
        //setting the width of the dropdown as the whole entire page to look nice
        editor.setMaxWidth(Double.MAX_VALUE);
        //place the location onto the page
        gridPane.add(editor,0, 3,4,1);
        //next place the text field and the format that it is supposed to be in for the times 
        TextField sttime =new TextField();
        //textField set as a new time
        sttime.setPromptText("hh:mm:ss");
        //sttime.setMaxWidth(Double.MAX_VALUE);
        //format the times as the way it is on the log.txt
        TextField sptime= new TextField();
        sptime.setPromptText("hh:mm:ss");
        //sptime.setMaxWidth(Double.MAX_VALUE);

        //the locations of the start time and stop time
        gridPane.add(sttime,0, 5);
        gridPane.add(sptime , 1,5);
        //next direction to give on the gui (interface) 
        gridPane.add(new Label(" Modify the effortlog and press \"Update This Entry\" when you are done."), 0, 4, 4, 1);
        //this will make the drop downs that are necessary 
        ComboBox<String> cyclestep= new ComboBox<>(); //the new combo box for the life cycle step and for the effort category combo box 
        ComboBox<String> effortlogbox =new ComboBox<>();
        cyclestep.getItems().addAll("Verifying", "Information Gathering", "Information Understanding","Planning");
        effortlogbox.getItems().addAll("Interuptions", "Deliverables", "Plans","Defects", "Others");
        
        editor.setOnShowing(event -> refreshComboBoxItems());
        
        //set a button to update the entry
        Button modifyingButton = new Button("Update Entry");
        modifyingButton.setDisable(true); //set as true so when you complete the options it will allow you to update 
       editor.getSelectionModel().selectedItemProperty().addListener((options , oldValue,newValue) -> {
        	//this will update the button and set it to be disabled if it is null
          modifyingButton.setDisable(newValue== null);
   
        });
       
        //this will make the functionality for the button that will modify the the information inside the logs 
        modifyingButton.setOnAction(w->{
        	//get the exact variables the user wants to change
        	//call the exact log
        	 String project = editor.getSelectionModel().getSelectedItem();
        	 String cycle1= cyclestep.getSelectionModel().getSelectedItem();
        	 String sttime1= sttime.getText();
             String sptime1 =sptime.getText();
             String effort1= effortlogbox.getSelectionModel().getSelectedItem();
             //call the combobox for the exact selection of the logs and update the log based off of that
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
        //add the placement for the modifying button, the cycles, the effort logs
        gridPane.add(modifyingButton, 3, 5);
        gridPane.add(new Label("Life Cycle Step:"),0, 6);
        gridPane.add(cyclestep, 1, 6, 3, 1); 
        gridPane.add(new Label("Effort Category:"),0,7);
        gridPane.add(effortlogbox, 1, 7, 2, 1); 
        //this is the button that will be called in order to clear/ delete the certain log 
        Button deleteSelection = new Button("Delete the entry");
        //set the action to call the method 
        deleteSelection.setOnAction(w -> deleteOrClearLog());
        //set the button to remove the log and the position for it
        gridPane.add(new Label("Delete the chosen log"), 0,9,2,1);
        
        gridPane.add(deleteSelection, 0, 10, 2, 1);
        //split the entries if needed and you can call the other functions to modify this entry which is awesome
        Button splitButton = new Button("Split into two entries");
       
      //being able to move to the effort log console, so you can create logs
        gridPane.add(new Label("Split the entry"), 0, 11);
        gridPane.add(splitButton, 0, 12);
        
        splitButton.setOnAction(w -> {
        	//call choice to be the exact log the user wants from the editor combobox drop down
            int choice = editor.getSelectionModel().getSelectedIndex();
            //call the exact choice into the split method 
            duplicateEntry(choice);
        });
        //this will take you to the effortconsole to allow you to create more projects 
       
        //create the border pane and set it to the gridPane that was used
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(gridPane);
        return new BorderPane(gridPane); //return that BorderPane
    }
    //this method is in charge of updating and storing what happened inside the logs to constantly run
    public static void restoreEffortLogs() {
    	//using Timeline processes information at a certain time so within the 5 second duration mark
    	//the metho iwll call the restorecombo to update the combo box
        Timeline clock= new Timeline (new KeyFrame (Duration.seconds(5),e ->{ restorecombo(); }));
        //this right here will set the count until it stops
        clock.setCycleCount(Animation.INDEFINITE);
        //this will play until the desired stop time
        clock.play();
    }
    //this will upload a specific log
    private static void uploadEntry(String selectedlog) {
        // Filter the logs on what type of project is wanted
    	
        List<String> filteredLogs = effortLogs.stream()
        			//once the selected log type is found, it will display it 
            .filter(logEntry -> logEntry.contains("Project: " + selectedlog))
            .collect(Collectors.toList());
        //use platform run to always clear and add the logs to refresh the combobox 
        Platform.runLater(() -> {
        	editor.requestLayout();
            editor.getItems().clear(); //erase
            
            editor.getItems().addAll(filteredLogs); // get the filtered
            editor.requestLayout(); // refresh
        });
    }
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

    //updating certain log parts and info 
    private static void updateLogEntry(int userChoice, String stop, String start, String lifecycle1, String effort1, String defect) {
        if (userChoice >= 0 && userChoice < effortLogs.size()) {
            String logs = effortLogs.get(userChoice);
            String[] logparts = logs.split(", ");

            //if a user does select info to change update it 
            if (lifecycle1 != null && !lifecycle1.isEmpty()) {
                logparts[1] = "Step: " + lifecycle1;
            }
            //else it will not update it and take it from previous
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
            //if the duration has been changed, you must calculate it and givr the time
            if (start != null && !start.isEmpty() && stop != null && !stop.isEmpty()) {
                String finalTime = durationSolver(stop, start);
                logparts[6] = "Duration: " + finalTime;
            }
            //put it back together and insert it 

            String usersEntry = String.join(", ", logparts);
            effortLogs.set(userChoice, usersEntry);
            //update the selection system and the restoration
            inputEffort();
            restorecombo(); // Refresh the combo box
            Platform.runLater(() -> editor.getItems().set(userChoice, usersEntry));
        }
    }

    //Esentially, because of updating the textfile for each updated log, we have to update the combo box as well
    private static void restorecombo() {
    	//this means that it will constanly run in the background, the application does not need to be run again
        Platform.runLater(() -> {
            effortLogs.clear();
            loadeffortLogs(); //when you clear the effortlogs, you load them again and put them in the editor
            editor.getItems().setAll(effortLogs);
        });
    }
    public static void inputEffort(){
    	//this will save the input if there are errors 
    	try (BufferedWriter analyze =new BufferedWriter (new FileWriter(Lfile ,false))){
    		//creating a string to read the effortlogs as an output
    		for (String swriterStr :effortLogs) {
    			//call it to write in the string and go to the next line
    			analyze.write(swriterStr);
    			analyze.newLine();
    			}
    		//catch the problem if there are any
    		}catch(IOException w){
    			w.printStackTrace();
    			}
    	}
    public static void loadeffortLogs() {
        effortLogs.clear();
        try (BufferedReader view = new BufferedReader(new FileReader(Lfile))) {
            String line;
            while ((line = view.readLine()) != null) {
                effortLogs.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void deleteOrClearLog(){
    	final int notdelete = -1;
    	//the log selected by the combo box will be deleted
    	int deleteLog = editor.getSelectionModel().getSelectedIndex();
    	//if the given value is not -1, this will work
        if (deleteLog != notdelete) {
        	//remove the log from the arry
            effortLogs.remove(deleteLog);
            //update the stored log.txt and the combobox
            inputEffort();
            restorecombo();
        }
    }
    //the check will be the exact choice the user inputer
    public static void splitLog( int check  ) {
    	String exactLog;
    	//if it fits the parameters than it will get the logs
    	if ( check <effortLogs.size()&&check>=0) {
    		exactLog=effortLogs.get(check);
    		//store the logs through the inputEffort
    		effortLogs.add(exactLog);
    		inputEffort(); //constantly have the Platform runlater tool
    		Platform.runLater(() -> {editor.getItems().add(exactLog);});		 
    		}
    }
    private static void duplicateEntry(int checking) {
    	//declare all necessary varuables and check the size of the choice
    	String effortLogCheck;
  
        if (checking<effortLogs.size()) {
        	//get the exact log in array list and add in order to duplicate
            effortLogCheck=effortLogs.get(checking);
            effortLogs.add(effortLogCheck);
            inputEffort();
            //store and use platform run
            Platform.runLater(() -> editor.getItems().add(effortLogCheck));
        }else if (effortLogs == null) {
        	//if the entire arraylist is empty it will print the statement
        	System.out.println("I'm sorry, there are no logs to split. Try again. ");
        }
        
    }
    private static String durationSolver(String start1, String stop1) {
    	//calculate the new duration of the effortLog and format it 
        DateTimeFormatter desiredInput = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime start = LocalTime.parse(start1,desiredInput);
        LocalTime stop = LocalTime.parse(stop1 , desiredInput);
        //when the localTime parsing variables are set, get the seconds in between by using chronoUnit
        long durLength=ChronoUnit.SECONDS.between(start, stop);
        if ( durLength<0) { //if it is less than 0 then the time is on a different day 
            // If stop time is the next day, it will need to be recalculated 
        	durLength+= 24 *3600;
        }
        //using the long type for hours, seconds and minutes and diving them accordingly to how many seconds are in each
        long hours = durLength/ 3600;
        long minutes = (durLength%3600) /60;
        long seconds = durLength %60;
        //set the display duration to the formation it is supposed to be and return it
        String formation=String.format("%02d:%02d:%02d", hours, minutes, seconds);
        
        return formation;
    }
    //refresh effortlogs combobox 
    public static void refreshEffortLogsComboBox() {
        // Load the logs from file again
        loadeffortLogs(); // Load the logs in
        Platform.runLater(() -> {
            editor.getItems().clear(); // Clear the current items
            editor.getItems().addAll(effortLogs); // Add the new items
        });
    }


}
