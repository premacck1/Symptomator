package com.prembros.symptomator;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 *
 * Created by Prem $ on 5/5/2017.
 */

class ConditionsReferenceJSONParser extends ArrayList<ConditionReferenceBeans> {

    ArrayList<ConditionReferenceBeans> parse(JSONObject jsonObject, String bodyPart, String selectedSymptom) {

        JSONObject bodyPartObject;
        JSONObject finalObject = null;

        if (jsonObject != null) {
            try {
                if (bodyPart != null) {
                    bodyPart = bodyPart.replace(" ", "_");
                    bodyPartObject = jsonObject.getJSONObject(bodyPart);
                    if (selectedSymptom != null) {
                        selectedSymptom = selectedSymptom.replace(" ", "_");
                        finalObject = bodyPartObject.getJSONObject(selectedSymptom);
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

            if (finalObject != null) {
                ArrayList<ConditionReferenceBeans> bodyPartList = new ArrayList<>();

                bodyPartList.add(getWarningAndCondition(finalObject, selectedSymptom));

                return bodyPartList;
            } else {
                Log.d("JSON parse ERROR!: ", "finalObject is null.");
                return null;
            }
        } else {
            Log.d("JSON parse ERROR!: ", "JSONObject is null.");
            return null;
        }
    }

    ArrayList<ConditionReferenceBeans> parseAll(JSONObject jsonObject, String bodyPart, String[] selectedSymptoms) {

        JSONObject bodyPartObject;
        JSONObject[] finalObjects = new JSONObject[selectedSymptoms.length];
        ArrayList<ConditionReferenceBeans> bodyPartList = new ArrayList<>();

        if (jsonObject != null) {
            try {
                if (bodyPart != null) {
                    bodyPart = bodyPart.replace(" ", "_");
                    bodyPartObject = jsonObject.getJSONObject(bodyPart);
                    for (int i = 0; i < selectedSymptoms.length; i++) {
                        selectedSymptoms[i] = selectedSymptoms[i].replace(" ", "_");
                        finalObjects[i] = bodyPartObject.getJSONObject(selectedSymptoms[i]);

                        bodyPartList.add(getWarningAndCondition(finalObjects[i], selectedSymptoms[i]));
                    }
                } else {
                    Log.d("JSON parse ERROR!: ", "Body part is null.");
                    return null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return bodyPartList;
        } else {
            Log.d("JSON parse ERROR!: ", "JSONObject is null.");
            return null;
        }
    }

    private ConditionReferenceBeans getWarningAndCondition(JSONObject jsonObject, String selectedSymptom) {
        ConditionReferenceBeans conditionReferenceBeans = new ConditionReferenceBeans();
        String warning;
        String conditions;
        try {
            warning = jsonObject.getString("warning");
            conditions = jsonObject.getString("conditions");

            conditionReferenceBeans.setSymptom(selectedSymptom);
            conditionReferenceBeans.setWarning(warning);
            conditionReferenceBeans.setConditions(conditions);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return conditionReferenceBeans;
    }
}
