/*
Date: 10/29/24
Purpose: Simple Weather Tracker with JavaFX
 */

/* notes for aryaa:
requirements: temp class, average temp, high & low, recs, introduction, instructions, prompt user, summary, exit, GUI, error handling
CAN'T FORGET PLAYLAB
 */

package com.weathertracker.weathertemperaturetracker;
// pop up
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
// graphics

public class TemperatureTrackerApp extends Application {
    private double totalTemperature = 0.0;
    private double highestTemperature = Double.MIN_VALUE;
    private double lowestTemperature = Double.MAX_VALUE;
    private int count = 0;
    private double latestTemperature = 0.0;
    private String scale = "Celsius"; // default temp scale

    public static void main(String[] args) {
        launch(args);
    }
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Temperature Tracker");

        // question for temperature scale
        selectTemperatureScale();

        // show introduction
        showIntroductoryMessage();

        // layout and aesthetics (color)
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #ffffff;");

        Button addTempButton = new Button("Add Temperature");
        addTempButton.setStyle("-fx-background-color: #add8e6; -fx-text-fill: black;");
        Button summaryButton = new Button("Show Summary");
        summaryButton.setStyle("-fx-background-color: #add8e6; -fx-text-fill: black;");
        Button exitButton = new Button("Exit");
        exitButton.setStyle("-fx-background-color: #add8e6; -fx-text-fill: black;");

        Label summaryLabel = new Label();
        summaryLabel.setWrapText(true);
        summaryLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #333;");

        layout.getChildren().addAll(addTempButton, summaryButton, summaryLabel, exitButton);
        // used github copilot for structuring this
        // error checking and validation
        addTempButton.setOnAction(event -> showTemperatureEntryDialog(summaryLabel));

        summaryButton.setOnAction(event -> {
            if (count > 0) {
                String summary = String.format(
                        "Temperature Summary:\nAverage: %.2f°%s\nHighest: %.2f°%s\nLowest: %.2f°%s\nRecommendation: %s",
                        calculateAverage(), getTemperatureUnit(),
                        convertFromCelsius(highestTemperature), getTemperatureUnit(),
                        convertFromCelsius(lowestTemperature), getTemperatureUnit(), getRecommendation());
                summaryLabel.setText(summary);
            } else {
                summaryLabel.setText("No temperatures recorded yet.");
            }
        });

        exitButton.setOnAction(event -> primaryStage.close());

        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    // user UI/UIX
    private void selectTemperatureScale() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Celsius", "Celsius", "Fahrenheit", "Kelvin");
        dialog.setTitle("Select Temperature Scale");
        dialog.setHeaderText("Choose your preferred temperature scale:");
        dialog.setContentText("Temperature Scale:");

        dialog.showAndWait().ifPresent(selectedScale -> scale = selectedScale);
    }
    // introductions
    private void showIntroductoryMessage() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Welcome to the Temperature Tracker!");
        alert.setHeaderText(null);
        alert.setContentText("This application allows you to track daily temperatures.\n\n"
                + "Instructions:\n"
                + "- Enter a temperature, day, month, and time for each entry.\n"
                + "- Click 'Add Temperature' to record a new entry.\n"
                // summary
                + "- Click 'Show Summary' to view the average, highest, and lowest recorded temperatures.\n"
                + "- Recommendations will be provided based on the latest temperature entered.\n"
                + "- Click 'Exit' to close the application.");
        alert.showAndWait();
    }
    // switch
    private String getTemperatureUnit() {
        String unit;
        switch (scale) {
            case "Celsius":
                unit = "C";
                break;
            case "Fahrenheit":
                unit = "F";
                break;
            case "Kelvin":
                unit = "K";
                break;
            default:
                unit = "C"; // default case = celsius for all conversions
                break;
        }
        return unit;
    }
    // encapulation
    private void showTemperatureEntryDialog(Label summaryLabel) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Enter Temperature Details");

        VBox dialogContent = new VBox(10);
        dialogContent.setPadding(new Insets(10));

        TextField tempInput = new TextField();
        tempInput.setPromptText("Temperature (" + scale + ")");
        TextField dayInput = new TextField();
        dayInput.setPromptText("Day (e.g., Monday)");
        TextField monthInput = new TextField();
        monthInput.setPromptText("Month (e.g., January)");
        TextField timeInput = new TextField();
        timeInput.setPromptText("Time (e.g., 10:00)");
        // instead of setting it to just text in a lighter color, using PromptText

        ChoiceBox<String> amPmChoiceBox = new ChoiceBox<>();
        amPmChoiceBox.getItems().addAll("AM", "PM");
        amPmChoiceBox.setValue("AM"); // Default to AM

        dialogContent.getChildren().addAll(new Label("Temperature"), tempInput,
                new Label("Day"), dayInput,
                new Label("Month"), monthInput,
                new Label("Time"), timeInput,
                new Label("Select AM or PM"), amPmChoiceBox);
        // dropdown for am or pm

        dialog.getDialogPane().setContent(dialogContent);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        // using buttons instead of chatbox

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // most important variable
                    double temp = Double.parseDouble(tempInput.getText());

                    String day = capitalize(dayInput.getText());
                    String month = capitalize(monthInput.getText());
                    String time = timeInput.getText() + " " + amPmChoiceBox.getValue(); // Append AM/PM

                    // check day of the week
                    // encapsulation
                    if (!isValidDay(day)) {
                        summaryLabel.setText("Invalid day entered. Please enter a valid day of the week.");
                        return;
                    }

                    // check  month
                    if (!isValidMonth(month)) {
                        summaryLabel.setText("Invalid month entered. Please enter a valid month.");
                        return;
                    }

                    // check time format (simple validation)
                    if (!isValidTimeFormat(time)) {
                        summaryLabel.setText("Invalid time entered. Please enter a valid time (e.g., 10:00 AM).");
                        return;
                    }
                    // ERROR HANDLING
                    addTemperature(temp);
                    summaryLabel.setText("Temperature recorded: " + temp + "°" + getTemperatureUnit() + " on "
                            + day + ", " + month + " at " + time + "\n"
                            + getRecommendation());
                } catch (NumberFormatException e) {
                    summaryLabel.setText("Invalid temperature input. Please enter a numeric value.");
                }
            }
        });
    }
    // checking if the day user enters exists
    private boolean isValidDay(String day) {
        String[] validDays = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        for (String validDay : validDays) {
            if (validDay.equalsIgnoreCase(day)) {
                return true;
            }
        }
        return false;
    }

    // checking if the month user enters exists
    private boolean isValidMonth(String month) {
        String[] validMonths = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        for (String validMonth : validMonths) {
            if (validMonth.equalsIgnoreCase(month)) {
                return true;
            }
        }
        return false;
    }
    // checking if the time user enters exists
    private boolean isValidTimeFormat(String time) {
        // validating time format (e.g., 10:00 AM or 10:00 PM)
        return time.matches("^(0?[1-9]|1[0-2]):[0-5][0-9] [APap][mM]$");
    }
    // whatever the user enters will be automatically capitalized for aesthetics
    private String capitalize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    // math conversions
    private double convertToCelsius(double temp) {
        switch (scale) {
            case "Fahrenheit":
                return (temp - 32) * 5 / 9;
            case "Kelvin":
                return temp - 273.15;
            default:
                return temp;
        }
    }

    private double convertFromCelsius(double tempInCelsius) {
        switch (scale) {
            case "Fahrenheit":
                return tempInCelsius * 9 / 5 + 32;
            case "Kelvin":
                return tempInCelsius + 273.15;
            default:
                return tempInCelsius;
        }
    }
    // for finding the highest, lowest temp
    private void addTemperature(double temp) {
        double tempInCelsius = convertToCelsius(temp);
        totalTemperature += tempInCelsius;
        count++;
        latestTemperature = tempInCelsius;

        if (tempInCelsius > highestTemperature) {
            highestTemperature = tempInCelsius;
        }
        if (tempInCelsius < lowestTemperature) {
            lowestTemperature = tempInCelsius;
        }
    }

    private double calculateAverage() {
        return convertFromCelsius(count > 0 ? totalTemperature / count : 0.0);
    }

    private String getRecommendation() {
        // no need to convert as latestTemperature is already stored in Celsius
        double latestTempDisplay = latestTemperature;
        // needed to ask playlab for writing the feedback
        // no overlap with numbers, using assignment operators with booleans
        if (latestTempDisplay <= 0) {
            return "It is freezing! Wear a heavy coat, scarf, gloves, and maybe even a hat!";
        } else if (latestTempDisplay <= 5) {
            return "It is very cold! Dress warmly with a winter coat and layers underneath.";
        } else if (latestTempDisplay <= 10) {
            return "It is chilly; bring a coat and consider a scarf.";
        } else if (latestTempDisplay <= 15) {
            return "It is cool weather; a jacket or sweater would be good.";
        } else if (latestTempDisplay <= 20) {
            return "It is mildly cool; a light jacket or sweater should be enough.";
        } else if (latestTempDisplay <= 25) {
            return "It is a bit warm; a long-sleeve shirt or light clothing is fine";
        } else if (latestTempDisplay <= 30) {
            return "It is warm weather; a t-shirt should be fine.";
        } else if (latestTempDisplay <= 35) {
            return "It is hot! Dress in light, breathable clothing to stay cool.";
        } else if (latestTempDisplay <= 40) {
            return "It is very hot! Stay hydrated and avoid too much sun; consider a hat and sunglasses.";
        } else {
            return "It is extremely hot! Limit outdoor activities, stay hydrated, and wear sun protection.";
        }
    }
}
