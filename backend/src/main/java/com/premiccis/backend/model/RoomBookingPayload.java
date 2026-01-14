package com.premiccis.backend.model;

public class RoomBookingPayload {
    private String room;
    private String date;
    private String time;
    private int duration;
    private String role;
    private int userId;
    private int students;
    private int pcs;
    private String fullName;
    private String professorName;
    private String program;
    private String section;
    private int year;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }


    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public int getStudents() { return students; }
    public void setStudents(int students) { this.students = students; }

    public int getPcs() { return pcs; }
    public void setPcs(int pcs) { this.pcs = pcs; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getProfessorName() { return professorName; }
    public void setProfessorName(String professorName) { this.professorName = professorName; }

    public String getProgram() { return program; }
    public void setProgram(String program) { this.program = program; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
}
