package com.example.taskmanager;

public class TaskData {
    private int id;
    private String title;
    private String content;
    private String date;
    private int userID;

    public TaskData(int taskID, String taskTitle, String taskContent, String taskDate, int taskUserID) {
        super();
        this.id = taskID;
        this.title = taskTitle;
        this.content = taskContent;
        this.date = taskDate;
        this.userID = taskUserID;
    }

    // overriding the toString function to our decide
    // string output on how we want to format our TaskData
    @Override
    public String toString() {
        String result = "";
        result += "Title: " +title+ "\n";
        result += "Content:\n" +content+ "\n";
        result += "Date: " +date+ "\n";
        result += "User: " +userID+ "";
        return result;
    }
}
