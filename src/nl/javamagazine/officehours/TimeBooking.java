package nl.javamagazine.officehours;

import java.util.Date;

public class TimeBooking {

    private long id;
    private String project;
    private Date startDateTime;
    private int hours;
    private int minutes;
    
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getProject() {
        return project;
    }
    public void setProject(String project) {
        this.project = project;
    }
    public Date getStartDateTime() {
        return startDateTime;
    }
    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }
    public int getHours() {
        return hours;
    }
    public void setHours(int hours) {
        this.hours = hours;
    }
    public int getMinutes() {
        return minutes;
    }
    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

}