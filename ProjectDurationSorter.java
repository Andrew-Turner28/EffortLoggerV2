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


class ProjectEntry implements Comparable<ProjectEntry> {
    String projectType;
    String stepType;
    String effortCategory;
    String deliverables;
    String start;
    String stop;
    Duration duration;

    public ProjectEntry(String projectType, String stepType, String effortCategory, String deliverables, String start, String stop, String durationStr) {
        this.projectType = projectType;
        this.stepType = stepType;
        this.effortCategory = effortCategory;
        this.deliverables = deliverables;
        this.start = start;
        this.stop = stop;
        String[] hms = durationStr.split(":");
        this.duration = Duration.ofHours(Long.parseLong(hms[0]))
                .plusMinutes(Long.parseLong(hms[1]))
                .plusSeconds(Long.parseLong(hms[2]));
    }

    @Override
    public String toString() {
        return "Project: " + projectType + ", Step: " + stepType + ", Category: " + effortCategory + ", Deliverable: " + deliverables +
                ", Start Time: " + start + ", Stop Time: " + stop + ", Duration: " + formatDuration(duration);
    }

    
    
    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = (duration.toMinutes() % 60);
        long seconds = (duration.getSeconds() % 60);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    
    
    

    public int compareTo(ProjectEntry other) {
        return this.duration.compareTo(other.duration);
    }
}


public class ProjectDurationSorter {

    public static List<ProjectEntry> sortDuration(String filePath, boolean ascending) {
        List<ProjectEntry> logEntries = getAllEntries(filePath);
        logEntries.sort(ascending ? Comparator.naturalOrder() : Comparator.reverseOrder());
        return logEntries;
    }

    public static List<ProjectEntry> getAllEntries(String filePath) {
        List<ProjectEntry> entries = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            for (String line : lines) {
                entries.add(parseLineToProjectEntry(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entries;
    }

    public static void addNewProjectEntry(String filePath, ProjectEntry newEntry) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath), StandardOpenOption.APPEND)) {
            writer.write(newEntry.toString());
            writer.newLine();
        }
    }

    public static void updateProjectEntry(String filePath, ProjectEntry oldEntry, ProjectEntry newEntry) throws IOException {
        List<ProjectEntry> entries = getAllEntries(filePath);
        int index = entries.indexOf(oldEntry);
        if (index != -1) {
            entries.set(index, newEntry);
            storeLogEntries(filePath, entries);
        }
    }

    public static void deleteProjectEntry(String filePath, ProjectEntry entryToDelete) throws IOException {
        List<ProjectEntry> entries = getAllEntries(filePath);
        entries.remove(entryToDelete);
        storeLogEntries(filePath, entries);
    }

    private static void storeLogEntries(String filePath, List<ProjectEntry> entries) throws IOException {
    	
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath))) {
        	
            for (ProjectEntry entry : entries) {
                writer.write(entry.toString());
                writer.newLine();
            }
        }
    }

    private static ProjectEntry parseLineToProjectEntry(String line) {
        String[] parts = line.split(", ");
        Map<String, String> data = new HashMap<>();
        for (String part : parts) {
            String[] keyValue = part.split(": ");
            data.put(keyValue[0].trim(), keyValue[1].trim());
        }
        return new ProjectEntry(
                data.get("Project"),
                data.get("Step"),
                data.get("Category"),
                data.get("Deliverable"),
                data.get("Start Time"),
                data.get("Stop Time"),
                data.get("Duration"));
    }
}

