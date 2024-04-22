package my.edu.utar.task_andassignment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataModel {

    private int id;
    private String title;
    private String description;
    private String date;

    // Default constructor
    public DataModel() {
    }

    // Constructor with parameters
    public DataModel(String title, String description, String date) {
        this.title = title;
        this.description = description;
        this.date = date;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        // Define the date format to match the format of your date strings
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            // Parse the date string if it's not null
            if (date != null) {
                return dateFormat.parse(date);
            } else {
                // Return a default date or null if the date string is null
                return null;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            // Handle the parsing exception appropriately, maybe return null or a default value
            return null;
        }
    }

    public void setDate(String date) {
        this.date = date;
    }
}
