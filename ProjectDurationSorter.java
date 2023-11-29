package application;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.BufferedWriter;
import java.io.IOException;



//the ProjectEntry class just looks for the specific data that is used in the text file
class ProjectEntry implements Comparable<ProjectEntry> {
    String exactLogProject;
    String steplogType;
    String effortlogType;
    String deliverablesLogs;
    String start;
    String stop;
    Duration duration;
    
    //it will declare the variables for each log input that may occur
    public ProjectEntry(String exactLogProject, String steplogType, String effortlogType, String deliverablesLogs, String start, String stop, String durationStr) {
        this.exactLogProject = exactLogProject;
        this.steplogType = steplogType;
        this.effortlogType = effortlogType;
        this.deliverablesLogs = deliverablesLogs;
        this.start = start;
        this.stop = stop;
        String[] hms = durationStr.split(":");
        //parse the duration into hours, minutes, and seconds
        this.duration = Duration.ofHours(Long.parseLong(hms[0]))
                .plusMinutes(Long.parseLong(hms[1]))
                .plusSeconds(Long.parseLong(hms[2]));
    }
    //This is the way that the individual log is formatted inside the text file
    public String toString() {
        return "Project: " + exactLogProject + ", Step: " + steplogType + ", Category: " + effortlogType + ", Deliverable: " + deliverablesLogs +
                ", Start Time: " + start + ", Stop Time: " + stop + ", Duration: " + durationstyle(duration);
    }
    
    //This method is called to repair the duration in case it is messed up
    private String durationstyle(Duration duration) {
    	//convert the variables to long
        long hs = duration.toHours();
        long ms = (duration.toMinutes() % 60);
        long sec = (duration.getSeconds() % 60);
        //return the format
        String replaceformat = String.format("%02d:%02d:%02d", hs, ms, sec);
        return replaceformat;
    }
    
    //the duration time will be called to be compared to another project entry to sort time
    public int compareTo(ProjectEntry other) {
    	//return the value
        return this.duration.compareTo(other.duration);
    }
}

//The purpose of this class is to compare which durations from the logs are longer than others 
//it will work with the time sort functionality for the logs
public class ProjectDurationSorter {
	//declare an arraylist method that will return the result with the given file and value of the log sort 
    public static List<ProjectEntry> organizetime(String logstextfile, boolean ascending) {
    	//collect all entries and sort in the ascending or descending order
        List<ProjectEntry> logEntries = everylogentry(logstextfile);
        logEntries.sort(ascending ? Comparator.naturalOrder() : Comparator.reverseOrder());
        //return the way the logs are listed
        return logEntries;
    }
    
    //just like other parts, if a new file is added it will parse it so it can be displayed
    public static void addontolist(String logstextfile, ProjectEntry currentLog) throws IOException {
    	//read the line and write as a string
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(logstextfile),StandardOpenOption.APPEND)) {
            writer.write(currentLog.toString());
            writer.newLine();
        }
    }
    
    //this method is responsible for getting all the logs that are in the text file
    public static List<ProjectEntry> everylogentry(String logstextfile) {
        List<ProjectEntry> entries = new ArrayList<>();
        try {
        	//read the lines of the text file and parse them into the method to be sorted
            List<String> lines = Files.readAllLines(Paths.get(logstextfile));
            for (String line : lines) {
                entries.add(recognizeLogs(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //return all the entries when this is called 
        return entries;
    }
   
    //This will parse the project entry element by element
    private static ProjectEntry recognizeLogs(String line) {
    	//declare the variable and remove based off of the commas 
        String[] parts = line.split(", ");
        Map<String, String> data = new HashMap<>();
        for (String part : parts) {
            String[] keyValue = part.split(": ");
            data.put(keyValue[0].trim(), keyValue[1].trim());
        }
        //return the different types of elements in the project
        return new ProjectEntry(
                data.get("Project"),
                data.get("Step"),
                data.get("Category"),
                data.get("Deliverable"),
                data.get("Start Time"),
                data.get("Stop Time"),
                data.get("Duration"));
    }
    
    //When a project entry is deleted it must be deleted in the array list too 
    public static void removeentry(String logstextfile, ProjectEntry removingLog) throws IOException {
        List<ProjectEntry> entries = everylogentry(logstextfile);
        //call to remove it 
        entries.remove(removingLog);
        saveentries(logstextfile, entries);
    }
    //In case the project is updated like it may have been in the effort log editor
    public static void newelementupdate(String logstextfile, ProjectEntry previousLog, ProjectEntry currentLog) throws IOException {
    	//if the list is not equal to -1, it will then store the logs
        List<ProjectEntry> entries = everylogentry(logstextfile);
        int index = entries.indexOf(previousLog);
        if (index != -1) {
            entries.set(index, currentLog);
            saveentries(logstextfile, entries);
        }
    }
    
    	//When the project has been added it will be stored with the rest of the logs to sort 
    private static void saveentries(String logstextfile, List<ProjectEntry> logEntriess) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(logstextfile))) {
        	//read the line of the strings and add
            for (ProjectEntry type1 : logEntriess) {
                writer.write(type1.toString());
                writer.newLine();
            }
        }
    }

   
}
