package com.prembros.symptomator;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/*
 * Created by Prem $ on 4/13/2017.
 */

class JSONParser extends ArrayList<Beans> {

    ArrayList<Beans> parseFirstAidJSON(JSONObject jsonObject, String field, String topic){

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

    private ArrayList<Beans> getAllParagraphs(JSONArray jsonArray, String topic){
        int paraCount;
        try {
            paraCount = jsonArray.length();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
        ArrayList<Beans> topicList = new ArrayList<>();
        Beans beans;

        for (int i = 0; i < paraCount; i++){
            try {
                beans = getParagraph((JSONObject) jsonArray.get(i), topic);
                topicList.add(beans);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return topicList;
    }

    private Beans getParagraph(JSONObject jsonObject, String topic){
        Beans beans = new Beans();
//        String para;
        String heading;
        String content;
        try {
//            para = jsonObject.getString("topic");
            heading = jsonObject.getString("heading");
            content = jsonObject.getString("content");

            beans.setTopic(topic);
//            beans.setPara(para);
            beans.setHeading(heading);
            beans.setContent(content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return beans;
    }

//    ArrayList<Beans> parseSymptomJSON(JSONObject jsonObject, String field, String topic){
//
//    }
}