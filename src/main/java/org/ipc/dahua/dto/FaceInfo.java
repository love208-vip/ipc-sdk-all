package org.ipc.dahua.dto;

/**
 * @Author 洋芋_Sir
 * @Date 2020/6/30
 * @description
 **/
public class FaceInfo {
    private String groupId;
    private String faceId;
    private String name;
    private int sex;
    private boolean isBirthday;
    private Birthday birthday;
    private int idType;
    private String idNumber;
    private byte[] image;
    private String similar=null;


    public String getSimilar() {
        return similar;
    }

    public void setSimilar(String similar) {
        this.similar = similar;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public FaceInfo(){

    }

    public FaceInfo(String groupId, String faceId, String similar) {
        this.groupId = groupId;
        this.faceId=faceId;
        this.name = null;
        this.image = null;
        this.isBirthday=false;
        this.idType=0;
        this.sex=0;
        this.similar=similar;
    }

    public FaceInfo(String groupId, String faceId) {
        this.groupId = groupId;
        this.faceId=faceId;
        this.name = null;
        this.image = null;
        this.isBirthday=false;
        this.idType=0;
        this.sex=0;
    }

    public FaceInfo(String groupId, String name, byte[] image) {
        this.groupId = groupId;
        this.name = name;
        this.image = image;
        this.isBirthday=false;
        this.idType=0;
        this.sex=0;
    }

    public FaceInfo(String groupId, String faceId, String name, boolean isBirthday, String birthday, int idType, String idNumber, byte[] image, int sex) {
        this.groupId = groupId;
        this.faceId = faceId;
        this.name = name;
        this.isBirthday = isBirthday;
        this.birthday = new Birthday(birthday);
        this.idType = idType;
        this.idNumber = idNumber;
        this.image = image;
        this.sex=sex;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getFaceId() {
        return faceId;
    }

    public void setFaceId(String faceId) {
        this.faceId = faceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isBirthday() {
        return isBirthday;
    }

    public void setBirthday(boolean birthday) {
        isBirthday = birthday;
    }

    public Birthday getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = new Birthday(birthday);
    }

    public int getIdType() {
        return idType;
    }

    public void setIdType(int idType) {
        this.idType = idType;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
