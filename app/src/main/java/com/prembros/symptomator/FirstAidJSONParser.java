package com.prembros.symptomator;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/*
 * Created by Prem $ on 4/13/2017.
 */

class FirstAidJSONParser extends ArrayList<FirstAidBeans> {

    ArrayList<FirstAidBeans> parseFirstAidJSON(JSONObject jsonObject, String field, String topic){

//        String t = topic.substring(0, 4) + "the personf NOW if";
        JSONArray jFieldArray;
        JSONArray jFinalArray = null;

        if (jsonObject != null) {
            try {
                if (field != null) {
                    jFieldArray = jsonObject.getJSONArray(field);
                    JSONObject jTopicObject = jFieldArray.getJSONObject(0);
                    if (topic != null) {
                        String t = topic.replace(" ", "_");
                        jFinalArray = jTopicObject.getJSONArray(t);
                    } else {
                        Log.d("JSON parse ERROR!: ", "Topic is null.");
                        return null;
                    }
                } else {
                    Log.d("JSON parse ERROR!: ", "Field is null.");
                    return null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return getAllParagraphs(jFinalArray, topic);
        }  else {
            Log.d("JSON parse ERROR!: ", "JSONObject is null.");
            return null;
        }
    }

    private ArrayList<FirstAidBeans> getAllParagraphs(JSONArray jsonArray, String topic){
        int paraCount;
        try {
            paraCount = jsonArray.length();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
        ArrayList<FirstAidBeans> topicList = new ArrayList<>();
        FirstAidBeans firstAidBeans;

        for (int i = 0; i < paraCount; i++){
            try {
                firstAidBeans = getParagraph((JSONObject) jsonArray.get(i), topic);
                topicList.add(firstAidBeans);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return topicList;
    }

    private FirstAidBeans getParagraph(JSONObject jsonObject, String topic){
        FirstAidBeans firstAidBeans = new FirstAidBeans();
//        String para;
        String heading;
        String content;
        try {
//            para = jsonObject.getString("topic");
            heading = jsonObject.getString("heading");
            content = jsonObject.getString("content");

            firstAidBeans.setTopic(topic);
//            firstAidBeans.setPara(para);
            firstAidBeans.setHeading(heading);
            firstAidBeans.setContent(content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return firstAidBeans;
    }

//    ArrayList<FirstAidBeans> parseSymptomJSON(JSONObject jsonObject, String field, String topic){
//
//    }
}