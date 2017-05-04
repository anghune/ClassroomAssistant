package com.lzp.classroomassistant.data;

import cn.bmob.v3.BmobObject;

/**
 * Created by WangChunhe on 2017/4/17.
 */

public class Seat extends BmobObject {
    private Integer row;
    private Integer column;
    private User student;
    private Course course;


    public Seat(Integer row, Integer column, User student,Course course) {
        this.row = row;
        this.column = column;
        this.student = student;
        this.course = course;
    }

    public Seat() {
    }
    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    @Override
    public String toString() {
        return "Seat{" +
                "row=" + row +
                ", column=" + column +
                ", student=" + student +
                '}';
    }
}
