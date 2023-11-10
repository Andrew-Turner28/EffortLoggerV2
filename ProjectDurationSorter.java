package application;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;

//the class that will label the project data
class ProjectEntry implements Comparable<ProjectEntry> {
    String projecttype;
    String steptype;
    String effortCategory;
    String deliverables;
    String start;
    String stop;
    Duration duration;

    public ProjectEntry(String projecttype, String steptype, String effortCategory, String deliverables, String start, String stop, String durationStr) {
    	//set up the project details 
        this.projecttype = projecttype;
        this.steptype = steptype;
        this.effortCategory = effortCategory;
        this.deliverables = deliverables;
        this.start = start;
        this.stop = stop;
        //make a string to split it based off of ":"
        String[] hms = durationStr.split(":");
        this.duration = Duration.ofHours(Long.parseLong(hms[0]))
                .plusMinutes(Long.parseLong(hms[1]))
                .plusSeconds(Long.parseLong(hms[2]));
    }

 
    @Override
    //this is the way the information will be displayed based off of time
    public String toString() {
        return "Project: " + projecttype + ", Step: " + steptype + ", effortCategory: " + effortCategory + ", deliverables: " + deliverables +
                ", Start Time: " + start + ", Stop Time: " + stop + ", Duration: " + duration;
    }
    @Override
    //this will be called to compare the durations and list which one in either ascending or descending order
    public int compareTo(ProjectEntry other) {
        return this.duration.compareTo(other.duration);
    }

}//end of the project entry class
//this class will sort the project
public class ProjectDurationSorter {

    public static List<ProjectEntry> sortProjectsByDuration(String filePath, boolean ascending) {
        List<ProjectEntry> logdetails = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            //once it reads the lines of the text file it parses it based off of the commas 
            for (String line : lines) {
                String[] parts = line.split(", ");
                Map<String, String> data = new HashMap<>();
                for (String part : parts) {
                    String[] keyValue = part.split(": ");
                    data.put(keyValue[0].trim(), keyValue[1].trim());
                }
                //getting the data from the project entry
                logdetails.add(new ProjectEntry(
                        data.get("Project"),
                        data.get("Step"),
                        data.get("Category"),
                        data.get("Deliverable"),
                        data.get("Start Time"),
                        data.get("Stop Time"),
                        data.get("Duration")));
            }
            
            //sorting in either order
            logdetails.sort(ascending ? Comparator.naturalOrder() : Comparator.reverseOrder());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return logdetails;
    }
}


    

    
