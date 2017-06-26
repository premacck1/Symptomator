package com.prembros.symptomator;

/*
 * Created by Prem $ on 4/7/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;


class DatabaseHolder {
    private static final String database_name = "Symptomator";
    private static final int database_version = 1;

    /*OLDER ATTRIBUTES*/
//    private static final String patient_aadhar = "aadharNumber";
//    private static final String patient_name = "Name";
//    private static final String patient_contact = "contact";
//    private static final String patient_email = "email";
//    private static final String patient_gender = "gender";
//    private static final String patient_age = "age";
//    private static final String patient_disease = "disease";
//    private static final String patient_doctorID = "doctorID";
//    private static final String patient_nextAppointment = "nextAppointment";
//    private static final String patient_hospitalID = "hospitalID";
//    private static final String patient_address = "address";

    private final String symptomList_tableName = "SymptomList";
    private final String selectedSymptoms_tableName = "SelectedSymptoms";
    private final String emergencyNumbers_tableName = "emergencyNumbers";

    /*OLDER TABLE NAMES*/
//    private  final String doctor_tableName = "Doctor";
//    private  final String nurse_tableName = "Nurse";
//    private final String hospital_tableName = "Hospital";
//    private final String ambulance_tableName = "Ambulance";
//    private final String patient_tableName = "Patient";

    private static final String create_table_symptom_list = "create table if not exists SymptomList (Symptom text not null, BodyPart text not null, Sex text not null);";

    private static final String create_table_selected_symptoms = "create table if not exists SelectedSymptoms (Symptom text not null);";

    private static final String create_table_emergency_numbers = "create table if not exists emergencyNumbers(Country text not null, Code text not null, Number text not null)";

    /*OLDER QUERIES*/
//    private static final String create_table_doctor = "create table if not exists Doctor (id text not null primary key, Name text not null, SpecialisedField text not null, contact int not null);";

//    private static final String create_table_hospital = "create table if not exists Hospital (id integer not null primary key , Name text not null, address text not null, ambulanceCount int not null, userRating int not null);";

//    private static final String create_table_ambulance = "create table if not exists Ambulance (id text not null primary key , stateIn text not null, hospital text not null, availability int not null, FOREIGN KEY (hospital) REFERENCES Hospital(id));";

//    private static final String create_table_nurse = "create table if not exists Nurse (id text not null primary key, Name text not null, contact int not null, available int not null, hospitalID text not null, FOREIGN KEY (hospitalID) REFERENCES Hospital(id));";

//    private static final String create_table_patient = "create table if not exists Patient (aadharNumber bigint not null primary key, Name text not null, contact bigint not null, email text not null, gender text not null, age int not null, disease text not null, doctorID text not null, nextAppointment blob not null, hospitalID text not null, address blob not null, FOREIGN KEY (doctorID) REFERENCES Doctor(id), FOREIGN KEY (hospitalID) REFERENCES Hospital(id));";

    private static DatabaseHelper dbHelper;
    private final Context context;
    private SQLiteDatabase db;


    DatabaseHolder(Context context) {
        this.context = context;
        if (dbHelper == null) {
            dbHelper = new DatabaseHelper(context);
        }
    }

    void open() {
        db  = dbHelper.getWritableDatabase();
    }

    void close() {
        dbHelper.close();
    }

    /*
    *INSERTION / REMOVAL METHODS
     */
    void insertInSymptomListTable(String symptom, String bodyPart, String sex){
        ContentValues content = new ContentValues();
        content.put("Symptom", symptom);
        content.put("BodyPart", bodyPart);
        content.put("Sex", sex);
        db.insertOrThrow(symptomList_tableName, null, content);
    }

    void insertInEmergencyNumbersTable(String country, String code, String number){
        ContentValues content = new ContentValues();
        content.put("Country", country);
        content.put("Code", code);
        content.put("Number", number);
        db.insertOrThrow(emergencyNumbers_tableName, null, content);
    }

    void insertInSelectedSymptomsTable(String symptom) {
        removeFromSelectedSymptomsTable(symptom);

        ContentValues content = new ContentValues();
        content.put("Symptom", symptom);
        db.insertOrThrow(selectedSymptoms_tableName, null, content);
    }

    void removeFromSelectedSymptomsTable(String symptom){
        db.delete(selectedSymptoms_tableName, "Symptom='" + symptom + "'", null);
    }

    void resetSelectedSymptomsTable(){
        db.execSQL("DROP TABLE IF EXISTS " + selectedSymptoms_tableName);
        db.execSQL(create_table_selected_symptoms);
    }

    /*
    * RETRIEVAL METHODS
     */
    Cursor returnSymptoms(String sex, String bodyPart){
        Cursor cursor = null;
        if (sex.equalsIgnoreCase("male")){
            try{
                cursor = db.query(true, symptomList_tableName,
                        new String[]{"Symptom"},
                        "BodyPart = '" + bodyPart + "' AND (Sex = '"+ sex +"' OR sex = 'All')",
                        null, null, null, "Symptom ASC", null);
            }
            catch (SQLiteException e){
                if (e.getMessage().contains("no such table")){
                    Toast.makeText(context, "ERROR: Table doesn't exist!", Toast.LENGTH_SHORT).show();
                    // create table
                    // re-run query, etc.
                } else e.printStackTrace();
            }
        }
        else if (sex.equalsIgnoreCase("female")){
            try{
                cursor = db.query(true, symptomList_tableName,
                        new String[]{"Symptom"},
                        "BodyPart = '" + bodyPart + "' AND (Sex = '"+ sex +"' OR sex = 'All')",
                        null, null, null, "Symptom ASC", null);
            }
            catch (SQLiteException e){
                if (e.getMessage().contains("no such table")){
                    Toast.makeText(context, "ERROR: Table doesn't exist!", Toast.LENGTH_SHORT).show();
                    // create table
                    // re-run query, etc.
                } else e.printStackTrace();
            }
        } else cursor = null;

        return cursor;
    }

//    Cursor returnAllSymptoms(){
//        Cursor cursor = null;
//        try {
//            cursor = db.query(true, symptomList_tableName, new String[]{"Symptom"}, null, null, null, null, "Symptom ASC", null);
//        } catch (SQLiteException e){
//            if (e.getMessage().contains("no such table")){
//                Toast.makeText(context, "ERROR: Table doesn't exist", Toast.LENGTH_SHORT).show();
//            }
//        }
//        return cursor;
//    }

    Cursor returnEmergencyNumber(String countryCode){
        Cursor cursor = null;
        try{
            cursor = db.query(emergencyNumbers_tableName,
                    new String[]{"Country", "Number"},
                    "Code = '" + countryCode.toUpperCase() + "'",
                    null, null, null, null, null);
        }
        catch (SQLiteException e){
            if (e.getMessage().contains("no such table")){
                Toast.makeText(context, "ERROR: Table doesn't exist!", Toast.LENGTH_SHORT).show();
                // create table
                // re-run query, etc.
            } else e.printStackTrace();
        }
        return cursor;
    }

    Cursor returnSelectedSymptoms(){
        Cursor cursor = null;
        try {
            cursor  = db.query(true, selectedSymptoms_tableName, new String[]{"Symptom"}, null, null, null, null, "Symptom ASC", null);
        } catch (SQLiteException e) {
            if (e.getMessage().contains("no such table")) {
                Toast.makeText(context, "ERROR: Table doesn't exist", Toast.LENGTH_SHORT).show();
            }
        }
        return cursor;
    }

// --Commented out by Inspection START (6/26/2017 1:16 AM):
//    Cursor isStringAvailableInTable(String item) {
//        Cursor cursor = null;
//        try {
//            cursor = db.query(true, selectedSymptoms_tableName, new String[]{"Symptom"}, "Symptom='" + item + "'", null, null, null, null, null);
//        } catch (SQLiteException e) {
//            if (e.getMessage().contains("no such table")) {
//                Toast.makeText(context, "ERROR: Table doesn't exist", Toast.LENGTH_SHORT).show();
//            }
//        }
//        return cursor;
//    }
// --Commented out by Inspection STOP (6/26/2017 1:16 AM)

    /*OLDER METHODS*/
//    public long insertPatientData(String aadhar, String name, String contact, String email,
//                                  String gender, String age, String disease, String doctorID,
//                                  String nextAppointment, String hospitalID, String address) {
//        ContentValues content = new ContentValues();
//        content.put(patient_aadhar, aadhar);
//        content.put(patient_name, name);
//        content.put(patient_contact, contact);
//        content.put(patient_email, email);
//        content.put(patient_gender, gender);
//        content.put(patient_age, age);
//        content.put(patient_disease, disease);
//        content.put(patient_doctorID, doctorID);
//        content.put(patient_nextAppointment, nextAppointment);
//        content.put(patient_hospitalID, hospitalID);
//        content.put(patient_address, address);
//        return db.insertOrThrow(patient_tableName, null, content);
//    }
//
//    public long insertHospitalData(String id, String name, String address, String ambulanceCount, String userRating){
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("id", id);
//        contentValues.put("Name", name);
//        contentValues.put("address", address);
//        contentValues.put("ambulanceCount", ambulanceCount);
//        contentValues.put("userRating", userRating);
//        return db.insertOrThrow(hospital_tableName, null, contentValues);
//    }
//
//    public long insertAmbulanceData(String state, String hospital){
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("stateIn", state);
//        contentValues.put("hospital", hospital);
//        contentValues.put("availability", "1");
//        return db.insertOrThrow(ambulance_tableName, null, contentValues);
//    }
//
//    public long deletePatientData(String aadhar) {
//        return db.delete(patient_tableName, "aadharNumber = '"+aadhar+"'", null);
//    }
//
//    public long deleteAllPatientData(){
//        return db.delete(patient_tableName, "1", null);
//    }
//
//    public Cursor returnPatientData() {
//        Cursor cursor = null;
//        try{
//            cursor = db.query(patient_tableName, new String[] {
//                    patient_aadhar, patient_name, patient_contact,
//                    patient_email, patient_gender, patient_age, patient_disease,
//                    patient_doctorID, patient_nextAppointment, patient_hospitalID, patient_address
//            }, null, null, null, null, null);
//        }
//        catch (SQLiteException e){
//            if (e.getMessage().contains("no such table")){
//                Toast.makeText(context, "ERROR: Table doesn't exist!", Toast.LENGTH_SHORT).show();
//                // create table
//                // re-run query, etc.
//            } else e.printStackTrace();
//        }
//        return cursor;
//    }
//
//    public Cursor returnDoctorData(){
//        Cursor cursor = null;
//        try{
//            cursor = db.query("Doctor", new String[]{"Name", "SpecialisedField", "contact"}, null, null, null, null, null);
//        }
//        catch (SQLiteException e){
//            if (e.getMessage().contains("no such table")){
//                Toast.makeText(context, "ERROR: Table doesn't exist!", Toast.LENGTH_SHORT).show();
//                // create table
//                // re-run query, etc.
//            } else e.printStackTrace();
//        }
//        return cursor;
//    }
//
//    public Cursor returnNurseData() {
//        Cursor cursor = null;
//        try{
//            cursor = db.query("Nurse", new String[]{"Name", "available", "contact"}, null, null, null, null, null);
//        }
//        catch (SQLiteException e){
//            if (e.getMessage().contains("no such table")){
//                Toast.makeText(context, "ERROR: Table doesn't exist!", Toast.LENGTH_SHORT).show();
//                // create table
//                // re-run query, etc.
//            } else e.printStackTrace();
//        }
//        return cursor;
//    }
//
//    public Cursor returnHospitalData() {
//        Cursor cursor = null;
//        try{
//            cursor = db.query(hospital_tableName, new String[]{"Name", "address", "ambulanceCount", "userRating"}, null, null, null, null, null);
//        }
//        catch (SQLiteException e){
//            if (e.getMessage().contains("no such table")){
//                Toast.makeText(context, "ERROR: Table doesn't exist!", Toast.LENGTH_SHORT).show();
//                // create table
//                // re-run query, etc.
//            } else e.printStackTrace();
//        }
//        return cursor;
//    }
//
//    public Cursor returnAmbulanceData() {
//        Cursor cursor = null;
//        try{
//            cursor = db.query(ambulance_tableName, new String[]{"id", "stateIn", "hospital", "availability"}, null, null, null, null, null);
//        }
//        catch (SQLiteException e){
//            if (e.getMessage().contains("no such table")){
//                Toast.makeText(context, "ERROR: Table doesn't exist!", Toast.LENGTH_SHORT).show();
//                // create table
//                // re-run query, etc.
//            } else e.printStackTrace();
//        }
//        return cursor;
//    }
//
//    public Cursor returnSelectedStateAmbulanceAvailability(String state){
//        Cursor cursor = null;
//        try{
//            cursor = db.query(true, ambulance_tableName, new String[]{"id", "hospital", "availability"}, "stateIn = '"+ state +"'", null, null, null, null, null);
//        }
//        catch (SQLiteException e){
//            if (e.getMessage().contains("no such table")){
//                Toast.makeText(context, "ERROR: Table doesn't exist!", Toast.LENGTH_SHORT).show();
//                // create table
//                // re-run query, etc.
//            } else e.printStackTrace();
//        }
//        return cursor;
//    }

    /*RESET TABLES METHOD*/
//    public void resetTables(){
//        db.execSQL("DROP TABLE IF EXISTS SymptomList");
//        db.execSQL("DROP TABLE IF EXISTS SelectedSymptoms");
//        db.execSQL("DROP TABLE IF EXISTS emergencyNumbers");
////        db.execSQL("DROP TABLE IF EXISTS Nurse");
////        db.execSQL("DROP TABLE IF EXISTS Hospital");
////        db.execSQL("DROP TABLE IF EXISTS Patient");
////        db.execSQL("DROP TABLE IF EXISTS Ambulance");
//        try{
//            db.execSQL(create_table_symptom_list);
//            db.execSQL(create_table_selected_symptoms);
//            db.execSQL(create_table_emergency_numbers);
////            db.execSQL(create_table_hospital);
////            db.execSQL(create_table_ambulance);
////            db.execSQL(create_table_nurse);
////            db.execSQL(create_table_patient);
//        } catch(SQLException e) {
//            e.printStackTrace();
//        }
//    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

//        private static DatabaseHelper mInstance = null;

        DatabaseHelper(Context context) {
            super(context, database_name, null, database_version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try{
                db.execSQL(create_table_symptom_list);
                db.execSQL(create_table_selected_symptoms);
                db.execSQL(create_table_emergency_numbers);
//                db.execSQL(create_table_hospital);
//                db.execSQL(create_table_ambulance);
//                db.execSQL(create_table_nurse);
//                db.execSQL(create_table_patient);
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS SymptomList");
            db.execSQL("DROP TABLE IF EXISTS SelectedSymptoms");
            db.execSQL("DROP TABLE IF EXISTS emergencyNumbers");
//            db.execSQL("DROP TABLE IF EXISTS Hospital");
//            db.execSQL("DROP TABLE IF EXISTS Ambulance");
//            db.execSQL("DROP TABLE IF EXISTS Nurse");
//            db.execSQL("DROP TABLE IF EXISTS Patient");
            onCreate(db);
        }
    }
}