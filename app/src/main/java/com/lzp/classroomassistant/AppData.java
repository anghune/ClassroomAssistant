package com.lzp.classroomassistant;

import com.lzp.classroomassistant.data.College;
import com.lzp.classroomassistant.data.Course;
import com.lzp.classroomassistant.data.FaceRecogni;
import com.lzp.classroomassistant.data.Organization;
import com.lzp.classroomassistant.data.School;
import com.lzp.classroomassistant.data.Seat;
import com.lzp.classroomassistant.data.User;
import com.lzp.classroomassistant.data.UserOrgan;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WangChunhe on 2017/4/12.
 */

public class AppData {
    private AppData(){};
    private static class SingletonHolder {
        private static AppData instance = new AppData();
    }
    public static AppData getInstance(){
        return SingletonHolder.instance;
    }

    private List<Course> mCourseList;
    private List<Course> mAllCourseList;
    private List<School> mSchoolList;
    private List<College> mCollegeList;
    private List<Seat> mSeatList;
    private List<UserOrgan> mUserOrganList;
    private List<Organization> mOrganizationList;
    private ArrayList<ArrayList<User>> mMemberList;
    private FaceRecogni mFaceRecogni;


    public FaceRecogni getFaceRecogni() {
        return mFaceRecogni;
    }

    public void setFaceRecogni(FaceRecogni faceRecogni) {
        mFaceRecogni = faceRecogni;
    }

    public ArrayList<ArrayList<User>> getMemberList() {
        return mMemberList;
    }

    public void setMemberList(ArrayList<ArrayList<User>> memberList) {
        mMemberList = memberList;
    }

    public List<Organization> getOrganizationList() {
        return mOrganizationList;
    }

    public void setOrganizationList(List<Organization> organizationList) {
        mOrganizationList = organizationList;
    }

    public List<UserOrgan> getUserOrganList() {
        return mUserOrganList;
    }

    public void setUserOrganList(List<UserOrgan> userOrganList) {
        mUserOrganList = userOrganList;
    }

    public List<Seat> getSeatList() {
        return mSeatList;
    }

    public void setSeatList(List<Seat> seatList) {
        mSeatList = seatList;
    }

    public List<Course> getCourseList() {
        return mCourseList;
    }

    public void setCourseList(List<Course> courseList) {
        mCourseList = courseList;
    }

    public List<School> getSchoolList() {
        return mSchoolList;
    }

    public void setSchoolList(List<School> schoolList) {
        mSchoolList = schoolList;
    }

    public List<College> getCollegeList() {
        return mCollegeList;
    }

    public void setCollegeList(List<College> collegeList) {
        mCollegeList = collegeList;
    }

    public List<Course> getAllCourseList() {
        return mAllCourseList;
    }

    public void setAllCourseList(List<Course> allCourseList) {
        mAllCourseList = allCourseList;
    }
}
