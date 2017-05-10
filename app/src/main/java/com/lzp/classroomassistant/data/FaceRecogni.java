package com.lzp.classroomassistant.data;

import cn.bmob.v3.BmobObject;

/**
 * Created by WangChunhe on 2017/5/8.
 */

public class FaceRecogni extends BmobObject {
    private User teacher;
    private Course course;
    private User student;
    private Integer result;

    public FaceRecogni(User teacher, Course course, User student, Integer result) {
        this.teacher = teacher;
        this.course = course;
        this.student = student;
        this.result = result;
    }

    public User getTeacher() {
        return teacher;
    }

    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }
}
