package com.prembros.symptomator;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 *
 * Created by Prem $ on 5/5/2017.
 */

public class ConditionsReferenceJSONParser extends ArrayList<ConditionReferenceBeans> {

    ArrayList<ConditionReferenceBeans> parse(JSONObject jsonObject, String bodyArea, String bodyPart) {

        JSONObject bodyAreaObject;
        JSONObject finalObject = null;

        if (jsonObject != null) {
            try {
                if (bodyArea != null) {
                    bodyAreaObject = jsonObject.getJSONObject(bodyArea);
                    JSONObject bodyPartObject = bodyAreaObject.getJSONObject(bodyPart);
                    if (bodyPart != null) {
                        String bodyPartText = bodyPart.replace(" ", "_");
                        finalObject = bodyPartObject.getJSONObject(bodyPartText);
                    } else {
                        Log.d("JSON parse ERROR!: ", "Body area is null.");
                        return null;
                    }
                } else {
                    Log.d("JSON parse ERROR!: ", "Body part is null.");
                    return null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return getAllBodyParts(finalObject, bodyArea);
        } else {
            Log.d("JSON parse ERROR!: ", "JSONObject is null.");
            return null;
        }
    }

    private ArrayList<ConditionReferenceBeans> getAllBodyParts(JSONObject jsonObject, String bodyArea) {
        int bodyPartCount;
        try {
            bodyPartCount = jsonObject.length();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }

        ArrayList<ConditionReferenceBeans> bodyPartList = new ArrayList<>();

        for (int i = 0; i < bodyPartCount; i++) {
            try {
                bodyPartList.add(getWarningAndCondition((JSONObject) jsonObject.get(bodyArea), bodyArea));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return bodyPartList;
    }

    private ConditionReferenceBeans getWarningAndCondition(JSONObject jsonObject, String bodyPart) {
        ConditionReferenceBeans conditionReferenceBeans = new ConditionReferenceBeans();
        String warning;
        String conditions;
        try {
            warning = jsonObject.getString("warning");
            conditions = jsonObject.getString("conditions");

            conditionReferenceBeans.setBodyPart(bodyPart);
            conditionReferenceBeans.setWarning(warning);
            conditionReferenceBeans.setConditions(conditions);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return conditionReferenceBeans;
    }
}
