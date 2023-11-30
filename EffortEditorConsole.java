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
import java.util.Optional;


//This is the class used to edit log elements that have already been created.
//it works with effortapp

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
        //set the button to have an action to clear the log and enter the choice 
        clearLogButton.setOnAction(w -> {
            int choice = editor.getSelectionModel().getSelectedIndex();
            deleteOrClearLog(choice); 
        });
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
            updateLogEntries(choice, sptime1, sttime1, cycle1, effort1, defect);
           
        });
        //add the labels and buttons needed to modify more elements inside the logs
        gridPane.add(modifyingButton, 3, 5);
        gridPane.add(new Label("Life Cycle Step:"),0, 6);
        gridPane.add(cyclestep, 1, 6, 3, 1); 
        gridPane.add(new Label("Effort Category:"),0,7);
        gridPane.add(effortlogbox, 1, 7, 2, 1); 
        
        //Declare and call the methods to split, delete or clear the log(s) selected
        Button deleteSelection = new Button("Delete the entry");
        //it will refresh and clear the logs 
        deleteSelection.setOnAction(w -> {
            int choice = editor.getSelectionModel().getSelectedIndex();
            deleteOrClearLog(choice); 
        });
        
        gridPane.add(new Label("Delete the chosen log"), 0,9,2,1);
        gridPane.add(deleteSelection, 0, 10, 2, 1);
        Button splitButton = new Button("Split into two entries");
        gridPane.add(new Label("Split the entry"), 0, 11);
        gridPane.add(splitButton, 0, 12);
        gridPane.add(new Label("*Must fill all fields when editing*"), 0, 15);
        splitButton.setOnAction(w -> {
        	
        	//call choice to be the exact log the user wants from the editor combo box drop down
            int choice = editor.getSelectionModel().getSelectedIndex();
            //call the exact choice into the split method 
           
            
            split(choice);
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

    //this method will update the log and and the entry elements
    static void updateLogEntries(int selection, String stoptime, String starttime, String lifecycle1, String effort1, String defect) {
        try {
            //this will put the entry into the arraylist from the file
            List<String> lentry = Files.readAllLines(Paths.get(Lfile));
            //update the specific log editions 
            if (selection >= 0 &&selection<lentry.size()) {
                String[] lparts = lentry.get(selection).split(", ");
                //parse the strings into the specific parts you want
                lparts[1] = "Step: " + Optional.ofNullable(lifecycle1).orElse(lparts[1].substring(6));
                lparts[2] = "Category: " + Optional.ofNullable(effort1).orElse(lparts[2].substring(10));
                lparts[4] = "Start Time: " + (starttime != null ? starttime :lparts[4].substring("Start Time: ".length()));
                lparts[5] = "Stop Time: " + (stoptime != null ? stoptime :lparts[5].substring("Stop Time: ".length()));
                lparts[7] = "Defect: " + Optional.ofNullable(defect).orElse(lparts[7].substring("Defect: ".length()));
                //right here this will make the start and stop for the duration calculated again
                if (starttime!= null && stoptime!= null) {
                    String finalTime = durationSolver(stoptime, starttime);
                    lparts[6] = "Duration: " + finalTime;
                }
                //set the User selection and join them together
                lentry.set(selection, String.join(", ",lparts));
            }
            //update and read the file again
            try (BufferedWriter reader = new BufferedWriter(new FileWriter(Lfile, false))) {
                for (String logEntry:lentry) {
                    reader.write(logEntry);
                    reader.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
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
    public static void inputEffort() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Lfile, false))) { // false to overwrite
            for (String logEntry : effortLogs) {
                writer.write(logEntry);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
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
    
    //this method will be called by the delete and clear logs button
    private static void deleteOrClearLog(int userChoice) {
    	try {
            // declare the array list thats going to be used and check the user selection
            List<String> lEntries = Files.readAllLines(Paths.get(Lfile));
            if (userChoice >= 0 && userChoice <  lEntries.size()) {
            	 lEntries.remove(userChoice);
            }
            //this will read the lines and update the textfile 
            try (BufferedWriter read =new BufferedWriter(new FileWriter(Lfile, false))) {
                for (String lEntry : lEntries) {
                    read.write(lEntry);
                    read.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
     }
    

    private static void split( int selection) {
        try {
            //this will split the lentries and read them together
            List<String> lEntries = Files.readAllLines(Paths.get(Lfile));
            //if the user selection qualifies 
            if (selection < lEntries.size()  && selection >= 0 ) {
                String selected =lEntries.get(selection);
                //push the specific entry in
                lEntries.add(selected);
            }
            //try to read the file and write line by line 
            try (BufferedWriter read = new BufferedWriter(new FileWriter(Lfile, false))) {
                for (String lEntry :lEntries) {
                    read.write(lEntry);
                    read.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    //these two methods are going to be used as failsafe incase the duplicates do not work as expected
    
    private static void splitintotwoentries(int c) {
    	//if the checking is greater or equal than 0, it will work and the log will be called
        if (c >= 0 && c <effortLogs.size()) {
            String chosenlog=effortLogs.get(c);
            //Failsafe is called
            failsafe(chosenlog);
        }
    }
    
    //this method will not allow accidental duplicates to occur
    private static void failsafe(String templog) {
    	//set up the boolean used as false 
        boolean w;
        w = false;
        //while the entry is in effortlogs and it is the string
        for (String existingEntry : effortLogs) {
            if (existingEntry.equals(templog)) {
                w = true;
                break;
            }
        }
        //if it is not w then it will just automatically log it 
        if (!w) {
            effortLogs.add(templog);
            inputEffort(); // Save the updated logs
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
 
    public static void reloadlogsanddisplayeditor() {
        Platform.runLater(() -> {
            try {
               //clear the class
                editor.getItems().clear();
                // read the entries from the file
                List<String> newLogEntries = Files.readAllLines(Paths.get(Lfile));
                // Update the entries and clear then add
                effortLogs.clear();
                effortLogs.addAll(newLogEntries);
                editor.getItems().addAll(effortLogs);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}