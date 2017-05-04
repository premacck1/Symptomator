package com.prembros.symptomator;

import android.os.Parcel;
import android.os.Parcelable;

/*
 * Created by Prem $ on 4/13/2017.
 */

class FirstAidBeans implements Parcelable {

    private String field = "";
    private String topic = "";
    private String heading = "";
    private String content = "";

    FirstAidBeans(){
        //Empty constructor
    }

    private FirstAidBeans(Parcel parcel){
        field = parcel.readString();
        topic = parcel.readString();
        heading = parcel.readString();
        content = parcel.readString();
    }

//    void setField(String field){
//        this.field = field;
//    }

    void setTopic(String topic){
        this.topic = topic;
    }

    void setHeading(String heading){
        this.heading = heading;
    }

    void setContent(String content){
        this.content = content;
    }

//    String getField(){
//        return field;
//    }

//    String getTopic(){
//        return topic;
//    }

    String getHeading(){
        return heading;
    }

    String getContent(){
        return content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(field);
        parcel.writeString(topic);
        parcel.writeString(heading);
        parcel.writeString(content);
    }

    public static final Creator<FirstAidBeans> CREATOR = new Creator<FirstAidBeans>() {
        @Override
        public FirstAidBeans createFromParcel(Parcel parcel) {
            return new FirstAidBeans(parcel);
        }

        @Override
        public FirstAidBeans[] newArray(int size) {
            return new FirstAidBeans[size];
        }
    };
}
