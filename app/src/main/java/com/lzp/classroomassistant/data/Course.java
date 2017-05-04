package com.lzp.classroomassistant.data;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by WangChunhe on 2017/4/12.
 */

public class Course extends BmobObject implements Serializable {
    private Integer id;
    private String courseName;
    private Integer section;
    private Integer sectionSpan;
    private Integer week;
    private String classRoom;
    private User teacher;
    private Integer courseFlag;
    private BmobRelation students;


    public BmobRelation getStudents() {
        return students;
    }

    public void setStudents(BmobRelation students) {
        this.students = students;
    }

    public Course(){
        this.courseFlag = (int) (Math.random() * 10);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Integer getSection() {
        return section;
    }

    public void setSection(Integer section) {
        this.section = section;
    }

    public Integer getSectionSpan() {
        return sectionSpan;
    }

    public void setSectionSpan(Integer sectionSpan) {
        this.sectionSpan = sectionSpan;
    }

    public Integer getWeek() {
        return week;
    }

    public void setWeek(Integer week) {
        this.week = week;
    }

    public String getClassRoom() {
        return classRoom;
    }

    public void setClassRoom(String classRoom) {
        this.classRoom = classRoom;
    }


    public User getTeacher() {
        return teacher;
    }

    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }

    public Integer getCourseFlag() {
        return courseFlag;
    }

    public void setCourseFlag(Integer courseFlag) {
        this.courseFlag = courseFlag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return com.google.common.base.Objects.equal(id, course.getId())
                && com.google.common.base.Objects.equal(courseName,course.getCourseName());
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(id,courseName);
    }

}
