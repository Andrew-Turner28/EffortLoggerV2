package application;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class LogEntryFormated extends VBox {
	public LogEntryFormated(String logEntry) {
		
		if (logEntry == null || logEntry.isEmpty()) {
	        return; // Ends the method if the string is null or empty
	    }
		else {
		//Add padding for everything
		//super(1);
		
		//parses string and splits based upon commas
		String[] parts = logEntry.split(", ");
		
		//Create Labels
		Label projectLabel = new Label("Project: " + parts[0].split(": ")[1]);
        Label stepLabel = new Label("Step: " + parts[1].split(": ")[1]);
        Label categoryLabel = new Label("Category: " + parts[2].split(": ")[1]);
        Label deliverableLabel = new Label("Deliverable: " + parts[3].split(": ")[1]);
        Label startTimeLabel = new Label("Start Time: " + parts[4].split(": ")[1]);
        Label stopTimeLabel = new Label("Stop Time: " + parts[5].split(": ")[1]);
        Label durationLabel = new Label("Duration: " + parts[6].split(": ")[1]);
        Label defectLabel = new Label("Defect: "+ parts[7].split(": ")[1]);
        Label tagLabel = new Label("Tag: " + parts[8]);
		
		//Create HBoxes
        HBox projectBox = new HBox(projectLabel);
        HBox stepBox = new HBox(stepLabel);
        HBox categoryBox = new HBox(categoryLabel);
        HBox deliverableBox = new HBox(deliverableLabel);
        HBox startTimeBox = new HBox(startTimeLabel);
        HBox stopTimeBox = new HBox(stopTimeLabel);
        HBox durationBox = new HBox(durationLabel);
        HBox defectBox = new HBox(defectLabel);
        HBox tagBox = new HBox(tagLabel);
        
        //Create Outline
        this.setStyle("-fx-border-color: grey; -fx-border-width: 2; -fx-border-style: solid; -fx-padding: 5;");

		//Add to a VBox
        this.getChildren().addAll(projectBox, stepBox, categoryBox, deliverableBox, startTimeBox, stopTimeBox, durationBox, defectBox, tagBox);
		}
	}
}
