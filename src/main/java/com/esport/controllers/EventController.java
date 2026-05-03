package com.esport.controllers;

import com.esport.models.Event;
import com.esport.models.EventType;
import com.esport.service.EventService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EventController {

    // These variables match the fx:id in the FXML file exactly
    @FXML
    private TextField titleInput;
    
    @FXML
    private TextField gameInput;
    
    @FXML
    private TextField priceInput;
    
    @FXML
    private Label feedbackLabel;

    // We create our service to talk to the database
    private final EventService eventService = new EventService();

    // This method matches the "onAction" property of our Save button
    @FXML
    public void handleSaveEvent(ActionEvent actionEvent) {
        // 1. Read what the user typed
        String title = titleInput.getText();
        String game = gameInput.getText();
        String priceText = priceInput.getText();

        // 2. Simple validation (check if empty)
        if (title.isEmpty() || game.isEmpty() || priceText.isEmpty()) {
            feedbackLabel.setText("Error: Please fill all fields!");
            feedbackLabel.setStyle("-fx-text-fill: red;"); // Make text red
            return;
        }

        try {
            // 3. Convert price text to a BigDecimal number
            BigDecimal price = new BigDecimal(priceText);

            // 4. Create the Event object
            Event newEvent = new Event(
                    title,
                    EventType.LAN, // Defaulting to LAN for simplicity
                    game,
                    LocalDateTime.now().plusDays(5), // Starts in 5 days
                    LocalDateTime.now().plusDays(6),
                    price
            );

            // 5. Save to database using our Service
            eventService.addEvent(newEvent);

            // 6. Give feedback to the user and clear the inputs
            feedbackLabel.setText("Success! Event '" + title + "' saved.");
            feedbackLabel.setStyle("-fx-text-fill: green;"); // Make text green
            
            titleInput.clear();
            gameInput.clear();
            priceInput.clear();

        } catch (NumberFormatException e) {
            feedbackLabel.setText("Error: Price must be a valid number!");
            feedbackLabel.setStyle("-fx-text-fill: red;");
        }
    }
}
