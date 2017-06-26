package com.prembros.symptomator;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntro2Fragment;
import com.github.paolorotolo.appintro.AppIntroFragment;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.INTERNET;
import static android.graphics.Color.rgb;

/*
 * Created by Prem $ on 4/8/2017.
 */

public class Introduction extends AppIntro2 {

    private DatabaseHolder db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new DatabaseHolder(this);

        new HttpAsyncTask().execute();

        addSlide(AppIntroFragment.newInstance("Hey there!", "Welcome to Symptomator!\nCheck for possible diseases by choosing symptoms.",
                R.drawable.ic_app_logo_png, rgb(26,188,156)));

        addSlide(AppIntroFragment.newInstance("First Aid Info", "Because you never know, it may come in handy at any time.",
                R.drawable.ic_first_aid_intro, rgb(52,73,94)));

//        addSlide(AppIntroFragment.newInstance("Ambulance Services", "To facilitate you with ambulance whenever and wherever you need them.",
//                R.drawable.ic_ambulance_intro, rgb(52,73,94)));

        addSlide(AppIntroFragment.newInstance("Doctor Services", "So you never have to get in long lines to see one.",
                R.drawable.ic_doctor_intro, rgb(39,174,96)));

        addSlide(AppIntroFragment.newInstance("Hospital Services", "It's sometimes useful to know where the nearest hospital is.",
                R.drawable.ic_hospital_intro, rgb(44,62,80)));

        addSlide(AppIntro2Fragment.newInstance("You're all set!", "Now that you're familiar with the features, let's get started!",
                R.drawable.ic_tick, rgb(12, 152, 149)));

        setDepthAnimation();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!checkPermission()) {
                    requestPermission();
                }
            }
        }, 600);

//        askForPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, 5);
    }

    private void loadMainActivity(){
        startActivity(new Intent(this, MainActivity.class));
        this.finish();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        loadMainActivity();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        loadMainActivity();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean checkPermission() {
        int fineLocationResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int coarseLocationResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_COARSE_LOCATION);
        int internetResult = ContextCompat.checkSelfPermission(getApplicationContext(), INTERNET);
        int phoneResult = ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE);

        return fineLocationResult == PackageManager.PERMISSION_GRANTED &&
                coarseLocationResult == PackageManager.PERMISSION_GRANTED &&
                internetResult == PackageManager.PERMISSION_GRANTED &&
                phoneResult == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, INTERNET, CALL_PHONE}, 100);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0) {

                    boolean fineLocationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean coarseLocationAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean internetAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean callsAccepted = grantResults[3] == PackageManager.PERMISSION_GRANTED;

                    if (fineLocationAccepted && coarseLocationAccepted && internetAccepted && callsAccepted)
                        Snackbar.make(getWindow().getDecorView(),
                                R.string.permission_granted,
                                Snackbar.LENGTH_LONG).show();
                    else {
                        Snackbar.make(getWindow().getDecorView(),
                                R.string.permission_denied,
                                Snackbar.LENGTH_LONG).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel(
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(
                                                            new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, INTERNET, CALL_PHONE},
                                                            100);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }


                break;
        }
    }


    private void showMessageOKCancel(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage("You need to allow access to both the permissions")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void loadTables(){
        db.open();
        insertSymptomListData();
        insertFemaleData();
        insertMaleData();
        insertRemainingData();
        insertEmergencyNumbers();
        db.close();
    }

    private void insertSymptomListData(){
        db.insertInSymptomListTable("Agitation", "Head", "All");
        db.insertInSymptomListTable("Anxiety", "Head", "All");
        db.insertInSymptomListTable("Apathy", "Head", "All");
        db.insertInSymptomListTable("Bald spots", "Head", "All");
        db.insertInSymptomListTable("Blackouts (memory time loss)", "Head", "All");
        db.insertInSymptomListTable("Bleeding", "Head", "All");
        db.insertInSymptomListTable("Brittle hair", "Head", "All");
        db.insertInSymptomListTable("Broken bone (single fracture)", "Head", "All");
        db.insertInSymptomListTable("Broken bones (multiple fractures)", "Head", "All");
        db.insertInSymptomListTable("Bruising or discoloration", "Head", "All");
        db.insertInSymptomListTable("Coma", "Head", "All");
        db.insertInSymptomListTable("Compulsive behavior", "Head", "All");
        db.insertInSymptomListTable("Confusion", "Head", "All");
        db.insertInSymptomListTable("Craving alcohol", "Head", "All");
        db.insertInSymptomListTable("Craving to eat ice, dirt or paper", "Head", "All");
        db.insertInSymptomListTable("Crawling sensation", "Head", "All");
        db.insertInSymptomListTable("Delusions", "Head", "All");
        db.insertInSymptomListTable("Depressed mood", "Head", "All");
        db.insertInSymptomListTable("Difficult to wake from sleep", "Head", "All");
        db.insertInSymptomListTable("Difficulty concentrating", "Head", "All");
        db.insertInSymptomListTable("Difficulty falling asleep", "Head", "All");
        db.insertInSymptomListTable("Difficulty finding words", "Head", "All");
        db.insertInSymptomListTable("Difficulty learning new things", "Head", "All");
        db.insertInSymptomListTable("Difficulty sleeping", "Head", "All");
        db.insertInSymptomListTable("Difficulty solving problems", "Head", "All");
        db.insertInSymptomListTable("Difficulty staying asleep", "Head", "All");
        db.insertInSymptomListTable("Difficulty staying awake during day", "Head", "All");
        db.insertInSymptomListTable("Disorientation", "Head", "All");
        db.insertInSymptomListTable("Dizziness", "Head", "All");
        db.insertInSymptomListTable("Drainage or pus", "Head", "All");
        db.insertInSymptomListTable("Drowsiness", "Head", "All");
        db.insertInSymptomListTable("Early morning waking", "Head", "All");
        db.insertInSymptomListTable("Easily distracted", "Head", "All");
        db.insertInSymptomListTable("Emotional detachment", "Head", "All");
        db.insertInSymptomListTable("Fainting", "Head", "All");
        db.insertInSymptomListTable("Fear of air", "Head", "All");
        db.insertInSymptomListTable("Fear of gaining weight", "Head", "All");
        db.insertInSymptomListTable("Fear of water", "Head", "All");
        db.insertInSymptomListTable("Fear of being smothered", "Head", "All");
        db.insertInSymptomListTable("Feeling of water", "Head", "All");
        db.insertInSymptomListTable("Feeling of being detached from reality", "Head", "All");
        db.insertInSymptomListTable("Feeling smothered", "Head", "All");
        db.insertInSymptomListTable("Feeling something moving on scalp", "Head", "All");
        db.insertInSymptomListTable("Fever", "Head", "All");
        db.insertInSymptomListTable("Fits of rage", "Head", "All");
        db.insertInSymptomListTable("Flashbacks", "Head", "All");
        db.insertInSymptomListTable("Food cravings", "Head", "All");
        db.insertInSymptomListTable("Forgetfulness", "Head", "All");
        db.insertInSymptomListTable("Frightening dreams", "Head", "All");
        db.insertInSymptomListTable("Frightening thoughts", "Head", "All");
        db.insertInSymptomListTable("Hair loss", "Head", "All");
        db.insertInSymptomListTable("Hallucinations", "Head", "All");
        db.insertInSymptomListTable("Headache", "Head", "All");
        db.insertInSymptomListTable("Headache (worst ever)", "Head", "All");
        db.insertInSymptomListTable("Hearing voices", "Head", "All");
        db.insertInSymptomListTable("Impaired judgement", "Head", "All");
        db.insertInSymptomListTable("Impaired social skills", "Head", "All");
        db.insertInSymptomListTable("Impulsive behavior", "Head", "All");
        db.insertInSymptomListTable("Itching or burning", "Head", "All");
        db.insertInSymptomListTable("Lack of emotion", "Head", "All");
        db.insertInSymptomListTable("Lack of motivation", "Head", "All");
        db.insertInSymptomListTable("Lack of pleasure", "Head", "All");
        db.insertInSymptomListTable("Lightheadedness", "Head", "All");
        db.insertInSymptomListTable("Loss of consciousness", "Head", "All");
        db.insertInSymptomListTable("Lump or bulge", "Head", "All");
        db.insertInSymptomListTable("Memory problems", "Head", "All");
        db.insertInSymptomListTable("Mood swings", "Head", "All");
        db.insertInSymptomListTable("Multiple bruises of different ages", "Head", "All");
        db.insertInSymptomListTable("Numbness or tingling", "Head", "All");
        db.insertInSymptomListTable("Paranoid behavior", "Head", "All");
        db.insertInSymptomListTable("Personality changes", "Head", "All");
        db.insertInSymptomListTable("Poor concentration", "Head", "All");
        db.insertInSymptomListTable("Pulling out hair", "Head", "All");
        db.insertInSymptomListTable("Punching or kicking in sleep", "Head", "All");
        db.insertInSymptomListTable("Repeats phrases", "Head", "All");
        db.insertInSymptomListTable("Repetitive behaviors", "Head", "All");
        db.insertInSymptomListTable("Restless (tossing and turning) sleep", "Head", "All");
        db.insertInSymptomListTable("Sadness", "Head", "All");
        db.insertInSymptomListTable("Scratching", "Head", "All");
        db.insertInSymptomListTable("Seizures (uncontrollable jerking of limbs)", "Head", "All");
        db.insertInSymptomListTable("Sense of impending doom", "Head", "All");
        db.insertInSymptomListTable("Skin irritation", "Head", "All");
        db.insertInSymptomListTable("Slow thinking", "Head", "All");
        db.insertInSymptomListTable("Spinning sensation", "Head", "All");
        db.insertInSymptomListTable("Swelling", "Head", "All");
        db.insertInSymptomListTable("Tenderness to touch", "Head", "All");
        db.insertInSymptomListTable("Unusual behavior", "Head", "All");
        db.insertInSymptomListTable("Visible bugs or parasites", "Head", "All");
        db.insertInSymptomListTable("Visible deformity", "Head", "All");
        db.insertInSymptomListTable("Warm to touch", "Head", "All");
        db.insertInSymptomListTable("White specks on scalps or hair", "Head", "All");
        db.insertInSymptomListTable("Blank stare", "Eyes", "All");
        db.insertInSymptomListTable("Bleeding", "Eyes", "All");
        db.insertInSymptomListTable("Bleeding in eye", "Eyes", "All");
        db.insertInSymptomListTable("Blind spot in vision", "Eyes", "All");
        db.insertInSymptomListTable("Blindness", "Eyes", "All");
        db.insertInSymptomListTable("Blinking eyes", "Eyes", "All");
        db.insertInSymptomListTable("Blurred vision", "Eyes", "All");
        db.insertInSymptomListTable("Broken eye socket", "Eyes", "All");
        db.insertInSymptomListTable("Brownish-yellow ring around the color of eye", "Eyes", "All");
        db.insertInSymptomListTable("Bruising or discoloration", "Eyes", "All");
        db.insertInSymptomListTable("Bulging eyes", "Eyes", "All");
        db.insertInSymptomListTable("Change in vision", "Eyes", "All");
        db.insertInSymptomListTable("Cloudy vision", "Eyes", "All");
        db.insertInSymptomListTable("Decreased night vision", "Eyes", "All");
        db.insertInSymptomListTable("Discharge or mucus in eyes", "Eyes", "All");
        db.insertInSymptomListTable("Distortion of part of visual field", "Eyes", "All");
        db.insertInSymptomListTable("Double vision (with one eye covered)", "Eyes", "All");
        db.insertInSymptomListTable("Double vision (without one eye covered)", "Eyes", "All");
        db.insertInSymptomListTable("Drainage or pus", "Eyes", "All");
        db.insertInSymptomListTable("Dry eyes", "Eyes", "All");
        db.insertInSymptomListTable("Enlarged (dilated pupils)", "Eyes", "All");
        db.insertInSymptomListTable("Eye crusting with sleep", "Eyes", "All");
        db.insertInSymptomListTable("Eye irritation", "Eyes", "All");
        db.insertInSymptomListTable("Eyelid redness", "Eyes", "All");
        db.insertInSymptomListTable("Eyes do not track together", "Eyes", "All");
        db.insertInSymptomListTable("Eyes rolling back", "Eyes", "All");
        db.insertInSymptomListTable("Flickering lights in vision", "Eyes", "All");
        db.insertInSymptomListTable("Flickering uncolored zig-zag line in vision", "Eyes", "All");
        db.insertInSymptomListTable("Floating spots or strings in vision", "Eyes", "All");
        db.insertInSymptomListTable("Frequent changes in eye glass prescription", "Eyes", "All");
        db.insertInSymptomListTable("Frequent squinting", "Eyes", "All");
        db.insertInSymptomListTable("Gritty or scratchy eyes", "Eyes", "All");
        db.insertInSymptomListTable("Holding objects closer to read (Short-sightedness/Nearsightedness/Myopia)", "Eyes", "All");
        db.insertInSymptomListTable("Holding objects further away to read (Farsightedness/Hyperopia/Hypermetropia)", "Eyes", "All");
        db.insertInSymptomListTable("Itching or burning", "Eyes", "All");
        db.insertInSymptomListTable("Jerking eye movements", "Eyes", "All");
        db.insertInSymptomListTable("Loss of outside 1/3 of eyebrow (unintentional)", "Eyes", "All");
        db.insertInSymptomListTable("Loss of side vision", "Eyes", "All");
        db.insertInSymptomListTable("Lump or bulge", "Eyes", "All");
        db.insertInSymptomListTable("Multiple bruises of different ages", "Eyes", "All");
        db.insertInSymptomListTable("Nasal symptoms and one red eye", "Eyes", "All");
        db.insertInSymptomListTable("Need brighter light to read", "Eyes", "All");
        db.insertInSymptomListTable("Numbness or tingling", "Eyes", "All");
        db.insertInSymptomListTable("Pain or discomfort", "Eyes", "All");
        db.insertInSymptomListTable("Pain when moving eyes", "Eyes", "All");
        db.insertInSymptomListTable("Painful red lump on eyelids", "Eyes", "All");
        db.insertInSymptomListTable("Partial vision loss", "Eyes", "All");
        db.insertInSymptomListTable("Puffy eyelids", "Eyes", "All");
        db.insertInSymptomListTable("Pulling out eyelashes", "Eyes", "All");
        db.insertInSymptomListTable("Red (bloodshot) eyes", "Eyes", "All");
        db.insertInSymptomListTable("Red eye (single)", "Eyes", "All");
        db.insertInSymptomListTable("Red spots inside lower eyelid", "Eyes", "All");
        db.insertInSymptomListTable("Scaly skin on eyelids", "Eyes", "All");
        db.insertInSymptomListTable("See letters, numbers or musical notes as colors", "Eyes", "All");
        db.insertInSymptomListTable("Sensation of something in eye", "Eyes", "All");
        db.insertInSymptomListTable("Sensitive to light", "Eyes", "All");
        db.insertInSymptomListTable("Shadow over part of vision", "Eyes", "All");
        db.insertInSymptomListTable("Small (constricted) pupils", "Eyes", "All");
        db.insertInSymptomListTable("Sore or burning eyes", "Eyes", "All");
        db.insertInSymptomListTable("Squinting eyes", "Eyes", "All");
        db.insertInSymptomListTable("Sudden flash of lights", "Eyes", "All");
        db.insertInSymptomListTable("Sunken eyes", "Eyes", "All");
        db.insertInSymptomListTable("Swelling", "Eyes", "All");
        db.insertInSymptomListTable("Tears in one eye", "Eyes", "All");
        db.insertInSymptomListTable("Tenderness to touch", "Eyes", "All");
        db.insertInSymptomListTable("Tilts head to look at something", "Eyes", "All");
        db.insertInSymptomListTable("Trouble distinguishing color shades", "Eyes", "All");
        db.insertInSymptomListTable("Unable to blink or close eyelid", "Eyes", "All");
        db.insertInSymptomListTable("Unequal pupils (size)", "Eyes", "All");
        db.insertInSymptomListTable("Visible deformity", "Eyes", "All");
        db.insertInSymptomListTable("Vision fading of colors", "Eyes", "All");
        db.insertInSymptomListTable("Visual halos around lights", "Eyes", "All");
        db.insertInSymptomListTable("Warm to touch", "Eyes", "All");
        db.insertInSymptomListTable("Watery eyes", "Eyes", "All");
        db.insertInSymptomListTable("Yellow eyes", "Eyes", "All");
        db.insertInSymptomListTable("Bruising or discoloration", "Nose", "All");
        db.insertInSymptomListTable("Decreased smell", "Nose", "All");
        db.insertInSymptomListTable("Difficulty breathing through nose", "Nose", "All");
        db.insertInSymptomListTable("Drainage or pus", "Nose", "All");
        db.insertInSymptomListTable("Episodes of not breathing during sleep", "Nose", "All");
        db.insertInSymptomListTable("Itching or burning", "Nose", "All");
        db.insertInSymptomListTable("Lump or bulge", "Nose", "All");
        db.insertInSymptomListTable("Multiple bruises of different ages", "Nose", "All");
        db.insertInSymptomListTable("Nasal congestion", "Nose", "All");
        db.insertInSymptomListTable("Nasal symptoms  and one red eye", "Nose", "All");
        db.insertInSymptomListTable("Noisy breathing", "Nose", "All");
        db.insertInSymptomListTable("Nosebleed", "Nose", "All");
        db.insertInSymptomListTable("Numbness or tingling", "Nose", "All");
        db.insertInSymptomListTable("Pain or discomfort", "Nose", "All");
        db.insertInSymptomListTable("Runny nose", "Nose", "All");
        db.insertInSymptomListTable("Sneezing", "Nose", "All");
        db.insertInSymptomListTable("Snoring", "Nose", "All");
        db.insertInSymptomListTable("Strange smell or taste", "Nose", "All");
        db.insertInSymptomListTable("Swelling", "Nose", "All");
        db.insertInSymptomListTable("Tenderness to touch", "Nose", "All");
        db.insertInSymptomListTable("Visible deformity", "Nose", "All");
        db.insertInSymptomListTable("Warm to touch", "Nose", "All");
        db.insertInSymptomListTable("Abnormally round face", "Face", "All");
        db.insertInSymptomListTable("Blank stare", "Face", "All");
        db.insertInSymptomListTable("Bleeding", "Face", "All");
        db.insertInSymptomListTable("Broken bone (single fracture)", "Face", "All");
        db.insertInSymptomListTable("Broken bones (multiple fractures)", "Face", "All");
        db.insertInSymptomListTable("Bruising or discoloration", "Face", "All");
        db.insertInSymptomListTable("Drainage or pus", "Face", "All");
        db.insertInSymptomListTable("Drooping of one side of face", "Face", "All");
        db.insertInSymptomListTable("Enlarged/swollen glands", "Face", "All");
        db.insertInSymptomListTable("Lump or bulge", "Face", "All");
        db.insertInSymptomListTable("Multiple bruises of different ages", "Face", "All");
        db.insertInSymptomListTable("Muscle cramps or spasms (painful)", "Face", "All");
        db.insertInSymptomListTable("Muscle twitching (painless)", "Face", "All");
        db.insertInSymptomListTable("Numbness or tingling", "Face", "All");
        db.insertInSymptomListTable("Pain or discomfort", "Face", "All");
        db.insertInSymptomListTable("Pulling out beard", "Face", "All");
        db.insertInSymptomListTable("Swelling", "Face", "All");
        db.insertInSymptomListTable("Tenderness to touch", "Face", "All");
        db.insertInSymptomListTable("Unusual facial expression", "Face", "All");
        db.insertInSymptomListTable("Visible deformity", "Face", "All");
        db.insertInSymptomListTable("Warm to touch", "Face", "All");
        db.insertInSymptomListTable("Bleeding", "Ears", "All");
        db.insertInSymptomListTable("Bruising or discoloration", "Ears", "All");
        db.insertInSymptomListTable("Drainage or pus", "Ears", "All");
        db.insertInSymptomListTable("Ear ache", "Ears", "All");
        db.insertInSymptomListTable("Hearing loss", "Ears", "All");
        db.insertInSymptomListTable("Itching or burning", "Ears", "All");
        db.insertInSymptomListTable("Lump or bulge", "Ears", "All");
        db.insertInSymptomListTable("Multiple bruises of different ages", "Ears", "All");
        db.insertInSymptomListTable("Numbness or tingling", "Ears", "All");
        db.insertInSymptomListTable("Pain or discomfort", "Ears", "All");
        db.insertInSymptomListTable("Ringing in ears", "Ears", "All");
        db.insertInSymptomListTable("Sensitive to noise", "Ears", "All");
        db.insertInSymptomListTable("Swelling", "Ears", "All");
        db.insertInSymptomListTable("Taste words when they are heard", "Ears", "All");
        db.insertInSymptomListTable("Tenderness to touch", "Ears", "All");
        db.insertInSymptomListTable("Warm to touch", "Ears", "All");
        db.insertInSymptomListTable("Bad breath", "Mouth", "All");
        db.insertInSymptomListTable("Bad taste in mouth", "Mouth", "All");
        db.insertInSymptomListTable("Belching", "Mouth", "All");
        db.insertInSymptomListTable("Bitter almond odor in breath", "Mouth", "All");
        db.insertInSymptomListTable("Bleeding", "Mouth", "All");
        db.insertInSymptomListTable("Bleeding gums", "Mouth", "All");
        db.insertInSymptomListTable("Bruising or discoloration", "Mouth", "All");
        db.insertInSymptomListTable("Coated or furry tongue", "Mouth", "All");
        db.insertInSymptomListTable("Cough", "Mouth", "All");
        db.insertInSymptomListTable("Cracks at corner of the mouth", "Mouth", "All");
        db.insertInSymptomListTable("Damaged teeth enamel", "Mouth", "All");
        db.insertInSymptomListTable("Decreased appetite", "Mouth", "All");
        db.insertInSymptomListTable("Decreased taste", "Mouth", "All");
        db.insertInSymptomListTable("Difficulty opening mouth", "Mouth", "All");
        db.insertInSymptomListTable("Difficulty swallowing", "Mouth", "All");
        db.insertInSymptomListTable("Difficulty talking", "Mouth", "All");
        db.insertInSymptomListTable("Drainage or pus", "Mouth", "All");
        db.insertInSymptomListTable("Drinking excessive fluids", "Mouth", "All");
        db.insertInSymptomListTable("Drooling", "Mouth", "All");
        db.insertInSymptomListTable("Dry mouth", "Mouth", "All");
        db.insertInSymptomListTable("Episodes of not breathing during sleep", "Mouth", "All");
        db.insertInSymptomListTable("Excessive mouth watering", "Mouth", "All");
        db.insertInSymptomListTable("Fruity odor in breath", "Mouth", "All");
        db.insertInSymptomListTable("Gagging", "Mouth", "All");
        db.insertInSymptomListTable("Grinding teeth", "Mouth", "All");
        db.insertInSymptomListTable("Grooved tongue", "Mouth", "All");
        db.insertInSymptomListTable("Gum sores", "Mouth", "All");
        db.insertInSymptomListTable("Hoarse voice", "Mouth", "All");
        db.insertInSymptomListTable("Increased speech volume", "Mouth", "All");
        db.insertInSymptomListTable("Increased talkativeness", "Mouth", "All");
        db.insertInSymptomListTable("Increased thirst", "Mouth", "All");
        db.insertInSymptomListTable("Involuntary movements (picking, lip smacking etc.)", "Mouth", "All");
        db.insertInSymptomListTable("Itching or burning", "Mouth", "All");
        db.insertInSymptomListTable("Jaw locking", "Mouth", "All");
        db.insertInSymptomListTable("Loss of voice", "Mouth", "All");
        db.insertInSymptomListTable("Lump or bulge", "Mouth", "All");
        db.insertInSymptomListTable("Metallic taste in mouth", "Mouth", "All");
        db.insertInSymptomListTable("Mouth sores", "Mouth", "All");
        db.insertInSymptomListTable("Muffled voice", "Mouth", "All");
        db.insertInSymptomListTable("Multiple bruises of different ages", "Mouth", "All");
        db.insertInSymptomListTable("Noisy breathing", "Mouth", "All");
        db.insertInSymptomListTable("Numbness or tingling", "Mouth", "All");
        db.insertInSymptomListTable("Pain or discomfort", "Mouth", "All");
        db.insertInSymptomListTable("Rapid speech", "Mouth", "All");
        db.insertInSymptomListTable("Red (strawberry) tongue", "Mouth", "All");
        db.insertInSymptomListTable("Red gums", "Mouth", "All");
        db.insertInSymptomListTable("Red spots", "Mouth", "All");
        db.insertInSymptomListTable("Regurgitation of food or liquid", "Mouth", "All");
        db.insertInSymptomListTable("Shortness of breath", "Mouth", "All");
        db.insertInSymptomListTable("Slurred speech", "Mouth", "All");
        db.insertInSymptomListTable("Smooth tongue", "Mouth", "All");
        db.insertInSymptomListTable("Snoring", "Mouth", "All");
        db.insertInSymptomListTable("Sore tongue", "Mouth", "All");
        db.insertInSymptomListTable("Soreness or burning inside mouth", "Mouth", "All");
        db.insertInSymptomListTable("Spots on throat", "Mouth", "All");
        db.insertInSymptomListTable("Spots in tonsils", "Mouth", "All");
        db.insertInSymptomListTable("Strange smell or taste", "Mouth", "All");
        db.insertInSymptomListTable("Swelling", "Mouth", "All");
        db.insertInSymptomListTable("Swollen gums", "Mouth", "All");
        db.insertInSymptomListTable("Swollen tongue", "Mouth", "All");
        db.insertInSymptomListTable("Swollen tonsils", "Mouth", "All");
        db.insertInSymptomListTable("Taste of acid in mouth", "Mouth", "All");
        db.insertInSymptomListTable("Taste words when they are heard", "Mouth", "All");
        db.insertInSymptomListTable("Teeth do not fit like they used to", "Mouth", "All");
        db.insertInSymptomListTable("Tenderness to touch", "Mouth", "All");
        db.insertInSymptomListTable("Thick saliva or mucus", "Mouth", "All");
        db.insertInSymptomListTable("Unable to open mouth/jaw", "Mouth", "All");
        db.insertInSymptomListTable("Uncontrollable verbal outbursts", "Mouth", "All");
        db.insertInSymptomListTable("Unusual taste in mouth", "Mouth", "All");
        db.insertInSymptomListTable("Upset stomach", "Mouth", "All");
        db.insertInSymptomListTable("Warm to touch", "Mouth", "All");
        db.insertInSymptomListTable("White patches inside mouth", "Mouth", "All");
        db.insertInSymptomListTable("White patches on tongue", "Mouth", "All");
        db.insertInSymptomListTable("Bleeding", "Jaw", "All");
        db.insertInSymptomListTable("Broken bone (single fracture)", "Jaw", "All");
        db.insertInSymptomListTable("Broken bones (multiple fractures)", "Jaw", "All");
        db.insertInSymptomListTable("Bruising or discoloration", "Jaw", "All");
        db.insertInSymptomListTable("Clicking or popping sound from jaw", "Jaw", "All");
        db.insertInSymptomListTable("Difficulty opening mouth", "Jaw", "All");
        db.insertInSymptomListTable("Drainage or pus", "Jaw", "All");
        db.insertInSymptomListTable("Enlarged/swollen glands", "Jaw", "All");
        db.insertInSymptomListTable("Jaw locking", "Jaw", "All");
        db.insertInSymptomListTable("Lump or bulge", "Jaw", "All");
        db.insertInSymptomListTable("Multiple bruises of different ages", "Jaw", "All");
        db.insertInSymptomListTable("Muscle cramps/spasms (painful)", "Jaw", "All");
        db.insertInSymptomListTable("Numbness or tingling", "Jaw", "All");
        db.insertInSymptomListTable("Pain or discomfort", "Jaw", "All");
        db.insertInSymptomListTable("Pulling out beard", "Jaw", "All");
        db.insertInSymptomListTable("Stiffness or decreased movement", "Jaw", "All");
        db.insertInSymptomListTable("Swelling", "Jaw", "All");
        db.insertInSymptomListTable("Tender glands", "Jaw", "All");
        db.insertInSymptomListTable("Tenderness to touch", "Jaw", "All");
        db.insertInSymptomListTable("Unable to open mouth (jaw)", "Jaw", "All");
        db.insertInSymptomListTable("Visible deformity", "Jaw", "All");
        db.insertInSymptomListTable("Warm to touch", "Jaw", "All");
        db.insertInSymptomListTable("Bleeding", "Neck", "All");
        db.insertInSymptomListTable("Broken bone (single fracture)", "Neck", "All");
        db.insertInSymptomListTable("Broken bones (multiple fractures)", "Neck", "All");
        db.insertInSymptomListTable("Bruising or discoloration", "Neck", "All");
        db.insertInSymptomListTable("Drainage or pus", "Neck", "All");
        db.insertInSymptomListTable("Enlarged/swollen glands", "Neck", "All");
        db.insertInSymptomListTable("Joint aches", "Neck", "All");
        db.insertInSymptomListTable("Joint pain", "Neck", "All");
        db.insertInSymptomListTable("Lump or bulge", "Neck", "All");
        db.insertInSymptomListTable("Morning joint stiffness", "Neck", "All");
        db.insertInSymptomListTable("Multiple bruises of different ages", "Neck", "All");
        db.insertInSymptomListTable("Muscle cramps/spasms (painful)", "Neck", "All");
        db.insertInSymptomListTable("Muscle twitching (painless)", "Neck", "All");
        db.insertInSymptomListTable("Numbness or tingling", "Neck", "All");
        db.insertInSymptomListTable("Pain or discomfort", "Neck", "All");
        db.insertInSymptomListTable("Short, wide neck", "Neck", "All");
        db.insertInSymptomListTable("Stiff neck", "Neck", "All");
        db.insertInSymptomListTable("Stiffness or decreased movement", "Neck", "All");
        db.insertInSymptomListTable("Swelling", "Neck", "All");
        db.insertInSymptomListTable("Tender glands", "Neck", "All");
        db.insertInSymptomListTable("Tenderness to touch", "Neck", "All");
        db.insertInSymptomListTable("Tilts head to look at something", "Neck", "All");
        db.insertInSymptomListTable("Visible deformity", "Neck", "All");
        db.insertInSymptomListTable("Warm to touch", "Neck", "All");
        db.insertInSymptomListTable("Bleeding", "Chest", "All");
        db.insertInSymptomListTable("Bleeding from nipple", "Chest", "All");
        db.insertInSymptomListTable("Broken bone (single fracture)", "Chest", "All");
        db.insertInSymptomListTable("Broken bones (multiple fractures)", "Chest", "All");
        db.insertInSymptomListTable("Bruising or discoloration", "Chest", "All");
        db.insertInSymptomListTable("Cough", "Chest", "All");
        db.insertInSymptomListTable("Difficulty talking", "Chest", "All");
        db.insertInSymptomListTable("Discharge from nipple", "Chest", "All");
        db.insertInSymptomListTable("Drainage or pus", "Chest", "All");
        db.insertInSymptomListTable("Episodes of not breathing during sleep", "Chest", "All");
        db.insertInSymptomListTable("Feeling of not being able to get enough air", "Chest", "All");
        db.insertInSymptomListTable("Food getting stuck (swallowing)", "Chest", "All");
        db.insertInSymptomListTable("Heartburn", "Chest", "All");
        db.insertInSymptomListTable("Hyperventilating (rapid/deep/heavy breathing)", "Chest", "All");
        db.insertInSymptomListTable("Irregular heartbeat", "Chest", "All");
        db.insertInSymptomListTable("Labored breathing", "Chest", "All");
        db.insertInSymptomListTable("Lump or bulge", "Chest", "All");
        db.insertInSymptomListTable("Multiple bruises of different ages", "Chest", "All");
        db.insertInSymptomListTable("Muscle cramps or spasms (painful)", "Chest", "All");
        db.insertInSymptomListTable("New onset asthma", "Chest", "All");
        db.insertInSymptomListTable("Nighttime wheezing", "Chest", "All");
        db.insertInSymptomListTable("Noisy breathing", "Chest", "All");
        db.insertInSymptomListTable("Numbness or tingling", "Chest", "All");
        db.insertInSymptomListTable("Pain or discomfort", "Chest", "All");
        db.insertInSymptomListTable("Palpitations (fluttering in chest)", "Chest", "All");
        db.insertInSymptomListTable("Pounding heart (pulse)", "Chest", "All");
        db.insertInSymptomListTable("Pressure or heaviness", "Chest", "All");
        db.insertInSymptomListTable("Prolonged breathing pauses", "Chest", "All");
        db.insertInSymptomListTable("Rapid breathing", "Chest", "All");
        db.insertInSymptomListTable("Rapid heart rate (pulse)", "Chest", "All");
        db.insertInSymptomListTable("Shortness of breath", "Chest", "All");
        db.insertInSymptomListTable("Slow heart rate (pulse)", "Chest", "All");
        db.insertInSymptomListTable("Slow or irregular breathing", "Chest", "All");
        db.insertInSymptomListTable("Stiffness or decreased movement", "Chest", "All");
        db.insertInSymptomListTable("Swelling", "Chest", "All");
        db.insertInSymptomListTable("Tenderness to touch", "Chest", "All");
        db.insertInSymptomListTable("Tightness", "Chest", "All");
        db.insertInSymptomListTable("Visible deformity", "Chest", "All");
        db.insertInSymptomListTable("Warm to touch", "Chest", "All");
        db.insertInSymptomListTable("Wheezing", "Chest", "All");
        db.insertInSymptomListTable("Bleeding", "Side of chest", "All");
        db.insertInSymptomListTable("Broken bone (single fracture)", "Side of chest", "All");
        db.insertInSymptomListTable("Broken bones (multiple fractures)", "Side of chest", "All");
        db.insertInSymptomListTable("Bruising or discoloration", "Side of chest", "All");
        db.insertInSymptomListTable("Drainage or pus", "Side of chest", "All");
        db.insertInSymptomListTable("Lump or bulge", "Side of chest", "All");
        db.insertInSymptomListTable("Multiple bruises of different ages", "Side of chest", "All");
        db.insertInSymptomListTable("Muscle cramps or spasms (painful)", "Side of chest", "All");
        db.insertInSymptomListTable("Numbness or tingling", "Side of chest", "All");
        db.insertInSymptomListTable("Pain or discomfort", "Side of chest", "All");
        db.insertInSymptomListTable("Swelling", "Side of chest", "All");
        db.insertInSymptomListTable("Tenderness to touch", "Side of chest", "All");
        db.insertInSymptomListTable("Visible deformity", "Side of chest", "All");
        db.insertInSymptomListTable("Warm to touch", "Side of chest", "All");
        db.insertInSymptomListTable("Bleeding", "Sternum", "All");
        db.insertInSymptomListTable("Broken bone (single fracture)", "Sternum", "All");
        db.insertInSymptomListTable("Broken bones (multiple fractures)", "Sternum", "All");
        db.insertInSymptomListTable("Bruising or discoloration", "Sternum", "All");
        db.insertInSymptomListTable("Cough", "Sternum", "All");
        db.insertInSymptomListTable("Difficulty swallowing", "Sternum", "All");
        db.insertInSymptomListTable("Drainage or pus", "Sternum", "All");
        db.insertInSymptomListTable("Food getting stuck (swallowing)", "Sternum", "All");
        db.insertInSymptomListTable("Heartburn", "Sternum", "All");
        db.insertInSymptomListTable("Irregular heartbeat", "Sternum", "All");
        db.insertInSymptomListTable("Lump or bulge", "Sternum", "All");
        db.insertInSymptomListTable("Multiple bruises of different ages", "Sternum", "All");
        db.insertInSymptomListTable("Numbness or tingling", "Sternum", "All");
        db.insertInSymptomListTable("Pain or discomfort", "Sternum", "All");
        db.insertInSymptomListTable("Pain while swallowing", "Sternum", "All");
        db.insertInSymptomListTable("Palpitations (fluttering in chest)", "Sternum", "All");
        db.insertInSymptomListTable("Pressure or heaviness", "Sternum", "All");
        db.insertInSymptomListTable("Swelling", "Sternum", "All");
        db.insertInSymptomListTable("Tenderness to touch", "Sternum", "All");
        db.insertInSymptomListTable("Visible deformity", "Sternum", "All");
        db.insertInSymptomListTable("Warm to touch", "Sternum", "All");
        db.insertInSymptomListTable("Bleeding", "Upper abdomen", "All");
        db.insertInSymptomListTable("Bloating or fullness", "Upper abdomen", "All");
        db.insertInSymptomListTable("Bloody or red colored vomit", "Upper abdomen", "All");
        db.insertInSymptomListTable("Bruising or discoloration", "Upper abdomen", "All");
        db.insertInSymptomListTable("Bulging veins", "Upper abdomen", "All");
        db.insertInSymptomListTable("Change in bowel habits", "Upper abdomen", "All");
        db.insertInSymptomListTable("Coffee grounds colored vomit", "Upper abdomen", "All");
        db.insertInSymptomListTable("Constipation", "Upper abdomen", "All");
        db.insertInSymptomListTable("Distended stomach", "Upper abdomen", "All");
        db.insertInSymptomListTable("Drainage or pus", "Upper abdomen", "All");
        db.insertInSymptomListTable("Intentional vomiting (purging)", "Upper abdomen", "All");
        db.insertInSymptomListTable("Lump or bulge", "Upper abdomen", "All");
        db.insertInSymptomListTable("Multiple bruises of different ages", "Upper abdomen", "All");
        db.insertInSymptomListTable("Muscle cramps or spasms (painful)", "Upper abdomen", "All");
        db.insertInSymptomListTable("Nausea or vomiting", "Upper abdomen", "All");
        db.insertInSymptomListTable("Numbness or tingling", "Upper abdomen", "All");
        db.insertInSymptomListTable("Pain or discomfort", "Upper abdomen", "All");
        db.insertInSymptomListTable("Painful bowel movements", "Upper abdomen", "All");
        db.insertInSymptomListTable("Pressure or fullness", "Upper abdomen", "All");
        db.insertInSymptomListTable("Pulsating sensation", "Upper abdomen", "All");
        db.insertInSymptomListTable("Stomach cramps", "Upper abdomen", "All");
        db.insertInSymptomListTable("Swelling", "Upper abdomen", "All");
        db.insertInSymptomListTable("Tenderness to touch", "Upper abdomen", "All");
        db.insertInSymptomListTable("Upset stomach", "Upper abdomen", "All");
        db.insertInSymptomListTable("Visible deformity", "Upper abdomen", "All");
        db.insertInSymptomListTable("Warm to touch", "Upper abdomen", "All");
        db.insertInSymptomListTable("Bleeding", "Lower abdomen", "All");
        db.insertInSymptomListTable("Bloating or fullness", "Lower abdomen", "All");
        db.insertInSymptomListTable("Bruising or discoloration", "Lower abdomen", "All");
        db.insertInSymptomListTable("Bulging veins", "Lower abdomen", "All");
        db.insertInSymptomListTable("Change in bowel habits", "Lower abdomen", "All");
        db.insertInSymptomListTable("Constipation", "Lower abdomen", "All");
        db.insertInSymptomListTable("Diarrhea", "Lower abdomen", "All");
        db.insertInSymptomListTable("Distended stomach", "Lower abdomen", "All");
        db.insertInSymptomListTable("Drainage or pus", "Lower abdomen", "All");
        db.insertInSymptomListTable("Foul smelling stools", "Lower abdomen", "All");
        db.insertInSymptomListTable("Frequent bowel movements", "Lower abdomen", "All");
        db.insertInSymptomListTable("Frequent urges to have bowel movement", "Lower abdomen", "All");
        db.insertInSymptomListTable("Increased passing gas", "Lower abdomen", "All");
        db.insertInSymptomListTable("Lump or bulge", "Lower abdomen", "All");
        db.insertInSymptomListTable("Multiple bruises of different ages", "Lower abdomen", "All");
        db.insertInSymptomListTable("Muscle cramps or spasms (painful)", "Lower abdomen", "All");
        db.insertInSymptomListTable("Numbness or tingling", "Lower abdomen", "All");
        db.insertInSymptomListTable("Pain or discomfort", "Lower abdomen", "All");
        db.insertInSymptomListTable("Painful bowel movements", "Lower abdomen", "All");
        db.insertInSymptomListTable("Pressure or fullness", "Lower abdomen", "All");
        db.insertInSymptomListTable("Pulsating sensation", "Lower abdomen", "All");
        db.insertInSymptomListTable("Stomach cramps", "Lower abdomen", "All");
        db.insertInSymptomListTable("Straining with bowel movements", "Lower abdomen", "All");
        db.insertInSymptomListTable("Swelling", "Lower abdomen", "All");
        db.insertInSymptomListTable("Tenderness to touch", "Lower abdomen", "All");
        db.insertInSymptomListTable("Upset stomach", "Lower abdomen", "All");
        db.insertInSymptomListTable("Visible deformity", "Lower abdomen", "All");
        db.insertInSymptomListTable("Warm to touch", "Lower abdomen", "All");
        db.insertInSymptomListTable("Worms in stool", "Lower abdomen", "All");
        db.insertInSymptomListTable("Bleeding", "Back", "All");
        db.insertInSymptomListTable("Broken bone (single fracture)", "Back", "All");
        db.insertInSymptomListTable("Broken bones (multiple fractures)", "Back", "All");
        db.insertInSymptomListTable("Bruising or discoloration", "Back", "All");
        db.insertInSymptomListTable("Curved spine", "Back", "All");
        db.insertInSymptomListTable("Difficulty walking", "Back", "All");
        db.insertInSymptomListTable("Drainage or pus", "Back", "All");
        db.insertInSymptomListTable("Lump or bulge", "Back", "All");
        db.insertInSymptomListTable("Multiple bruises of different ages", "Back", "All");
        db.insertInSymptomListTable("Muscle cramps or spasms (painful)", "Back", "All");
        db.insertInSymptomListTable("Numbness or tingling", "Back", "All");
        db.insertInSymptomListTable("Pain or discomfort", "Back", "All");
        db.insertInSymptomListTable("Stiffness or decreased movement", "Back", "All");
        db.insertInSymptomListTable("Swelling", "Back", "All");
        db.insertInSymptomListTable("Tenderness to touch", "Back", "All");
        db.insertInSymptomListTable("Visible deformity", "Back", "All");
        db.insertInSymptomListTable("Warm to touch", "Back", "All");
        db.insertInSymptomListTable("Bleeding", "Upper spine", "All");
        db.insertInSymptomListTable("Broken bone (single fracture)", "Upper spine", "All");
        db.insertInSymptomListTable("Broken bones (multiple fractures)", "Upper spine", "All");
        db.insertInSymptomListTable("Bruising or discoloration", "Upper spine", "All");
        db.insertInSymptomListTable("Curved spine", "Upper spine", "All");
        db.insertInSymptomListTable("Drainage or pus", "Upper spine", "All");
        db.insertInSymptomListTable("Lump or bulge", "Upper spine", "All");
        db.insertInSymptomListTable("Multiple bruises of different ages", "Upper spine", "All");
        db.insertInSymptomListTable("Muscle cramps or spasms (painful)", "Upper spine", "All");
        db.insertInSymptomListTable("Numbness or tingling", "Upper spine", "All");
        db.insertInSymptomListTable("Pain or discomfort", "Upper spine", "All");
        db.insertInSymptomListTable("Stiffness or decreased movement", "Upper spine", "All");
        db.insertInSymptomListTable("Swelling", "Upper spine", "All");
        db.insertInSymptomListTable("Tenderness to touch", "Upper spine", "All");
        db.insertInSymptomListTable("Visible deformity", "Upper spine", "All");
        db.insertInSymptomListTable("Warm to touch", "Upper spine", "All");
        db.insertInSymptomListTable("Bleeding", "Lower spine", "All");
        db.insertInSymptomListTable("Broken bone (single fracture)", "Lower spine", "All");
        db.insertInSymptomListTable("Broken bones (multiple fractures)", "Lower spine", "All");
        db.insertInSymptomListTable("Bruising or discoloration", "Lower spine", "All");
        db.insertInSymptomListTable("Curved spine", "Lower spine", "All");
        db.insertInSymptomListTable("Difficulty walking", "Lower spine", "All");
        db.insertInSymptomListTable("Drainage or pus", "Lower spine", "All");
        db.insertInSymptomListTable("Joint pain", "Lower spine", "All");
        db.insertInSymptomListTable("Lump or bulge", "Lower spine", "All");
        db.insertInSymptomListTable("Morning joint stiffness", "Lower spine", "All");
        db.insertInSymptomListTable("Multiple bruises of different ages", "Lower spine", "All");
        db.insertInSymptomListTable("Muscle cramps or spasms (painful)", "Lower spine", "All");
        db.insertInSymptomListTable("Numbness or tingling", "Lower spine", "All");
        db.insertInSymptomListTable("Pain or discomfort", "Lower spine", "All");
        db.insertInSymptomListTable("Stiffness or decreased movement", "Lower spine", "All");
        db.insertInSymptomListTable("Swelling", "Lower spine", "All");
        db.insertInSymptomListTable("Tenderness to touch", "Lower spine", "All");
        db.insertInSymptomListTable("Visible deformity", "Lower spine", "All");
        db.insertInSymptomListTable("Warm to touch", "Lower spine", "All");
        db.insertInSymptomListTable("Weakness", "Lower spine", "All");
        db.insertInSymptomListTable("Bleeding", "Pelvis", "All");
        db.insertInSymptomListTable("Bloating or fullness", "Pelvis", "All");
        db.insertInSymptomListTable("Broken bone (single fracture)", "Pelvis", "All");
        db.insertInSymptomListTable("Broken bones (multiple fractures)", "Pelvis", "All");
        db.insertInSymptomListTable("Bruising or discoloration", "Pelvis", "All");
        db.insertInSymptomListTable("Cloudy urine with strong odor", "Pelvis", "All");
        db.insertInSymptomListTable("Dark colored (brown) urine", "Pelvis", "All");
        db.insertInSymptomListTable("Difficulty climbing stairs", "Pelvis", "All");
        db.insertInSymptomListTable("Difficulty urinating", "Pelvis", "All");
        db.insertInSymptomListTable("Difficulty walking", "Pelvis", "All");
        db.insertInSymptomListTable("Drainage or pus", "Pelvis", "All");
        db.insertInSymptomListTable("Frequent nighttime urination", "Pelvis", "All");
        db.insertInSymptomListTable("Frequent urge to urinate", "Pelvis", "All");
        db.insertInSymptomListTable("Itching or burning", "Pelvis", "All");
        db.insertInSymptomListTable("Lump or bulge", "Pelvis", "All");
        db.insertInSymptomListTable("Multiple bruises of different ages", "Pelvis", "All");
        db.insertInSymptomListTable("Muscle cramps or spasms (painful)", "Pelvis", "All");
        db.insertInSymptomListTable("Numbness or tingling", "Pelvis", "All");
        db.insertInSymptomListTable("Pain or discomfort", "Pelvis", "All");
        db.insertInSymptomListTable("Pressure or fullness", "Pelvis", "All");
        db.insertInSymptomListTable("Sudden urge to urinate", "Pelvis", "All");
        db.insertInSymptomListTable("Swelling", "Pelvis", "All");
        db.insertInSymptomListTable("Tenderness to touch", "Pelvis", "All");
        db.insertInSymptomListTable("Urine leaking", "Pelvis", "All");
        db.insertInSymptomListTable("Visible bugs or parasites", "Pelvis", "All");
        db.insertInSymptomListTable("Warm to touch", "Pelvis", "All");
        db.insertInSymptomListTable("Bleeding", "Genitals", "All");
        db.insertInSymptomListTable("Blood or red colored urine", "Genitals", "All");
        db.insertInSymptomListTable("Bruising or discoloration", "Genitals", "All");
        db.insertInSymptomListTable("Cloudy urine with string odor", "Genitals", "All");
        db.insertInSymptomListTable("Dark colored (brown) urine", "Genitals", "All");
        db.insertInSymptomListTable("Decreased urination", "Genitals", "All");
        db.insertInSymptomListTable("Difficulty starting urine stream", "Genitals", "All");
        db.insertInSymptomListTable("Difficulty urinating", "Genitals", "All");
        db.insertInSymptomListTable("Discharge from penis", "Genitals", "All");
        db.insertInSymptomListTable("Drainage or pus", "Genitals", "All");
        db.insertInSymptomListTable("Frequent nighttime urination", "Genitals", "All");
        db.insertInSymptomListTable("Frequent urge to urinate", "Genitals", "All");
        db.insertInSymptomListTable("Frequent urination", "Genitals", "All");
        db.insertInSymptomListTable("Itching or burning", "Genitals", "All");
        db.insertInSymptomListTable("Lump or bulge", "Genitals", "All");
        db.insertInSymptomListTable("Multiple bruises of different ages", "Genitals", "All");
        db.insertInSymptomListTable("Numbness or tingling", "Genitals", "All");
        db.insertInSymptomListTable("Pain during erection", "Genitals", "All");
        db.insertInSymptomListTable("Pain or discomfort", "Genitals", "All");
        db.insertInSymptomListTable("Sudden urge to urinate", "Genitals", "All");
        db.insertInSymptomListTable("Swelling", "Genitals", "All");
        db.insertInSymptomListTable("Tenderness to touch", "Genitals", "All");
        db.insertInSymptomListTable("Unable to obtain or maintain erection", "Genitals", "All");
        db.insertInSymptomListTable("Urine leaking (incontinence)", "Genitals", "All");
        db.insertInSymptomListTable("Visible bugs or parasites", "Genitals", "All");
        db.insertInSymptomListTable("Warm to touch", "Genitals", "All");
    }

    private void insertFemaleData(){
        db.insertInSymptomListTable("Heavy menstrual bleeding", "Pelvis", "Female");
        db.insertInSymptomListTable("Irregular menstrual periods", "Pelvis", "Female");
        db.insertInSymptomListTable("Missed/late menstrual period", "Pelvis", "Female");
        db.insertInSymptomListTable("Pain with sexual intercourse (female)", "Pelvis", "Female");
        db.insertInSymptomListTable("Pain with urination (female)", "Pelvis", "Female");
        db.insertInSymptomListTable("Vaginal bleeding", "Pelvis", "Female");
        db.insertInSymptomListTable("Vaginal bleeding after menopause", "Pelvis", "Female");
        db.insertInSymptomListTable("Vaginal bleeding between periods", "Pelvis", "Female");
        db.insertInSymptomListTable("Vaginal discharge", "Pelvis", "Female");
        db.insertInSymptomListTable("Heavy menstrual bleeding", "Genitals", "Female");
        db.insertInSymptomListTable("Irregular menstrual periods", "Genitals", "Female");
        db.insertInSymptomListTable("Pain with sexual intercourse (female)", "Genitals", "Female");
        db.insertInSymptomListTable("Vaginal bleeding between periods", "Genitals", "Female");
        db.insertInSymptomListTable("Vaginal discharge", "Genitals", "Female");
        db.insertInSymptomListTable("Vaginal odor", "Genitals", "Female");
    }

    private void insertMaleData(){
        db.insertInSymptomListTable("Curved or bent penis during erection", "Genitals", "Male");
        db.insertInSymptomListTable("Discharge from penis", "Genitals", "Male");
        db.insertInSymptomListTable("Erectile dysfunction", "Genitals", "Male");
        db.insertInSymptomListTable("Pain with sexual intercourse (male)", "Genitals", "Male");
        db.insertInSymptomListTable("Pain with urination (male)", "Genitals", "Male");
        db.insertInSymptomListTable("Painful ejaculation", "Genitals", "Male");
        db.insertInSymptomListTable("Premature ejaculation", "Genitals", "Male");
        db.insertInSymptomListTable("Testicles shrinkage", "Genitals", "Male");
        db.insertInSymptomListTable("Testicular pain", "Genitals", "Male");
        db.insertInSymptomListTable("Unable to obtain or maintain erection", "Genitals", "Male");
    }

    private void insertRemainingData(){
        db.insertInSymptomListTable("Black (tar) colored stools", "Buttock", "All");
        db.insertInSymptomListTable("Bleeding", "Buttock", "All");
        db.insertInSymptomListTable("Blood in toilet", "Buttock", "All");
        db.insertInSymptomListTable("Blood on stool surface", "Buttock", "All");
        db.insertInSymptomListTable("Blood in toilet tissue", "Buttock", "All");
        db.insertInSymptomListTable("Blood or red colored stool", "Buttock", "All");
        db.insertInSymptomListTable("Broken bone (single fracture)", "Buttock", "All");
        db.insertInSymptomListTable("Broken bones (multiple fractures)", "Buttock", "All");
        db.insertInSymptomListTable("Bruising or discoloration", "Buttock", "All");
        db.insertInSymptomListTable("Bulging veins", "Buttock", "All");
        db.insertInSymptomListTable("Change in stools", "Buttock", "All");
        db.insertInSymptomListTable("Drainage or pus", "Buttock", "All");
        db.insertInSymptomListTable("Lump or bulge", "Buttock", "All");
        db.insertInSymptomListTable("Multiple bruises of different ages", "Buttock", "All");
        db.insertInSymptomListTable("Muscle cramps or spasms (painful)", "Buttock", "All");
        db.insertInSymptomListTable("Numbness or tingling", "Buttock", "All");
        db.insertInSymptomListTable("Pain or discomfort", "Buttock", "All");
        db.insertInSymptomListTable("Painful bowel movements", "Buttock", "All");
        db.insertInSymptomListTable("Protruding rectal material", "Buttock", "All");
        db.insertInSymptomListTable("Stool leaking (incontinence)", "Buttock", "All");
        db.insertInSymptomListTable("Swelling", "Buttock", "All");
        db.insertInSymptomListTable("Tenderness to touch", "Buttock", "All");
        db.insertInSymptomListTable("Warm to touch", "Buttock", "All");
        db.insertInSymptomListTable("Worms in stool", "Buttock", "All");
        db.insertInSymptomListTable("Bleeding", "Hip", "All");
        db.insertInSymptomListTable("Broken bone (single fracture)", "Hip", "All");
        db.insertInSymptomListTable("Broken bones (multiple fractures)", "Hip", "All");
        db.insertInSymptomListTable("Bruising or discoloration", "Hip", "All");
        db.insertInSymptomListTable("Difficulty climbing stairs", "Hip", "All");
        db.insertInSymptomListTable("Difficulty getting up from a chair", "Hip", "All");
        db.insertInSymptomListTable("Difficulty walking", "Hip", "All");
        db.insertInSymptomListTable("Drainage or pus", "Hip", "All");
        db.insertInSymptomListTable("Joint aches", "Hip", "All");
        db.insertInSymptomListTable("Joint instability", "Hip", "All");
        db.insertInSymptomListTable("Joint locking or catching", "Hip", "All");
        db.insertInSymptomListTable("Joint pain", "Hip", "All");
        db.insertInSymptomListTable("Lump or bulge", "Hip", "All");
        db.insertInSymptomListTable("Multiple bruises of different ages", "Hip", "All");
        db.insertInSymptomListTable("Numbness or tingling", "Hip", "All");
        db.insertInSymptomListTable("Stiffness or decreased movement", "Hip", "All");
        db.insertInSymptomListTable("Swelling", "Hip", "All");
        db.insertInSymptomListTable("Tenderness to touch", "Hip", "All");
        db.insertInSymptomListTable("Unable to bear weight", "Hip", "All");
        db.insertInSymptomListTable("Unable to move joint", "Hip", "All");
        db.insertInSymptomListTable("Visible deformity", "Hip", "All");
        db.insertInSymptomListTable("Warm to touch", "Hip", "All");
        db.insertInSymptomListTable("Weakness", "Hip", "All");
        db.insertInSymptomListTable("Bleeding", "Groin", "All");
        db.insertInSymptomListTable("Bruising or discoloration", "Groin", "All");
        db.insertInSymptomListTable("Drainage or pus", "Groin", "All");
        db.insertInSymptomListTable("Enlarged or swollen glands", "Groin", "All");
        db.insertInSymptomListTable("Lump or bulge", "Groin", "All");
        db.insertInSymptomListTable("Multiple bruises of different ages", "Groin", "All");
        db.insertInSymptomListTable("Muscle cramps or spasms (painful)", "Groin", "All");
        db.insertInSymptomListTable("Numbness or tingling", "Groin", "All");
        db.insertInSymptomListTable("Pain or discomfort", "Groin", "All");
        db.insertInSymptomListTable("Pressure or fullness", "Groin", "All");
        db.insertInSymptomListTable("Stiffness or decreased movement", "Groin", "All");
        db.insertInSymptomListTable("Swelling", "Groin", "All");
        db.insertInSymptomListTable("Tender glands", "Groin", "All");
        db.insertInSymptomListTable("Tenderness to touch", "Groin", "All");
        db.insertInSymptomListTable("Warm to touch", "Groin", "All");
        db.insertInSymptomListTable("Bleeding", "Shoulder", "All");
        db.insertInSymptomListTable("Bruising or discoloration", "Shoulder", "All");
        db.insertInSymptomListTable("Drainage or pus", "Shoulder", "All");
        db.insertInSymptomListTable("Guarding or favoring joint", "Shoulder", "All");
        db.insertInSymptomListTable("Inability to move", "Shoulder", "All");
        db.insertInSymptomListTable("Joint aches", "Shoulder", "All");
        db.insertInSymptomListTable("Joint pain", "Shoulder", "All");
        db.insertInSymptomListTable("Lump or bulge", "Shoulder", "All");
        db.insertInSymptomListTable("Morning joint stiffness", "Shoulder", "All");
        db.insertInSymptomListTable("Multiple bruises of different ages", "Shoulder", "All");
        db.insertInSymptomListTable("Muscle cramps or spasms (painful)", "Shoulder", "All");
        db.insertInSymptomListTable("Numbness or tingling", "Shoulder", "All");
        db.insertInSymptomListTable("Popping or snapping sound from joint", "Shoulder", "All");
        db.insertInSymptomListTable("Stiffness or decreased movement", "Shoulder", "All");
        db.insertInSymptomListTable("Swelling", "Shoulder", "All");
        db.insertInSymptomListTable("Visible deformity", "Shoulder", "All");
        db.insertInSymptomListTable("Warm to touch", "Shoulder", "All");
        db.insertInSymptomListTable("Weakness", "Shoulder", "All");
        db.insertInSymptomListTable("Bleeding", "Armpit", "All");
        db.insertInSymptomListTable("Bruising or discoloration", "Armpit", "All");
        db.insertInSymptomListTable("Drainage or pus", "Armpit", "All");
        db.insertInSymptomListTable("Enlarged/swollen glands", "Armpit", "All");
        db.insertInSymptomListTable("Excessive sweating", "Armpit", "All");
        db.insertInSymptomListTable("Lump or bulge", "Armpit", "All");
        db.insertInSymptomListTable("Multiple bruises of different ages", "Armpit", "All");
        db.insertInSymptomListTable("Numbness or tingling", "Armpit", "All");
        db.insertInSymptomListTable("Pain or discomfort", "Armpit", "All");
        db.insertInSymptomListTable("Swelling", "Armpit", "All");
        db.insertInSymptomListTable("Tender glands", "Armpit", "All");
        db.insertInSymptomListTable("Tenderness to touch", "Armpit", "All");
        db.insertInSymptomListTable("Warm to touch", "Armpit", "All");
        db.insertInSymptomListTable("Bleeding", "Upper arm", "All");
        db.insertInSymptomListTable("Broken bone (single fracture)", "Upper arm", "All");
        db.insertInSymptomListTable("Broken bones (multiple fractures)", "Upper arm", "All");
        db.insertInSymptomListTable("Bruising or discoloration", "Upper arm", "All");
        db.insertInSymptomListTable("Bulging veins", "Upper arm", "All");
        db.insertInSymptomListTable("Drainage or pus", "Upper arm", "All");
        db.insertInSymptomListTable("Inability to move", "Upper arm", "All");
        db.insertInSymptomListTable("Lump or bulge", "Upper arm", "All");
        db.insertInSymptomListTable("Multiple bruises of different ages", "Upper arm", "All");
        db.insertInSymptomListTable("Muscle cramps or spasms (painful)", "Upper arm", "All");
        db.insertInSymptomListTable("Numbness or tingling", "Upper arm", "All");
        db.insertInSymptomListTable("Pain or discomfort", "Upper arm", "All");
        db.insertInSymptomListTable("Stiffness or decreased movement", "Upper arm", "All");
        db.insertInSymptomListTable("Swelling", "Upper arm", "All");
        db.insertInSymptomListTable("Tenderness to touch", "Upper arm", "All");
        db.insertInSymptomListTable("Visible deformity", "Upper arm", "All");
        db.insertInSymptomListTable("Warm to touch", "Upper arm", "All");
        db.insertInSymptomListTable("Weakness", "Upper arm", "All");
        db.insertInSymptomListTable("Bleeding", "Elbow", "All");
        db.insertInSymptomListTable("Broken bone (single fracture)", "Elbow", "All");
        db.insertInSymptomListTable("Bruising or discoloration", "Elbow", "All");
        db.insertInSymptomListTable("Drainage or pus", "Elbow", "All");
        db.insertInSymptomListTable("Enlarged/swollen glands", "Elbow", "All");
        db.insertInSymptomListTable("Guarding or favoring joint", "Elbow", "All");
        db.insertInSymptomListTable("Inability to move", "Elbow", "All");
        db.insertInSymptomListTable("Joint aches", "Elbow", "All");
        db.insertInSymptomListTable("Joint instability", "Elbow", "All");
        db.insertInSymptomListTable("Joint pain", "Elbow", "All");
        db.insertInSymptomListTable("Lump or bulge", "Elbow", "All");
        db.insertInSymptomListTable("Morning joint stiffness", "Elbow", "All");
        db.insertInSymptomListTable("Multiple bruises of different ages", "Elbow", "All");
        db.insertInSymptomListTable("Numbness or tingling", "Elbow", "All");
        db.insertInSymptomListTable("Stiffness or decreased movement", "Elbow", "All");
        db.insertInSymptomListTable("Swelling", "Elbow", "All");
        db.insertInSymptomListTable("Tender glands", "Elbow", "All");
        db.insertInSymptomListTable("Tenderness to touch", "Elbow", "All");
        db.insertInSymptomListTable("Unable to move joint", "Elbow", "All");
        db.insertInSymptomListTable("Visible deformity", "Elbow", "All");
        db.insertInSymptomListTable("Warm to touch", "Elbow", "All");
        db.insertInSymptomListTable("Weakness", "Elbow", "All");
        db.insertInSymptomListTable("Bleeding", "Forearm", "All");
        db.insertInSymptomListTable("Broken bone (single fracture)", "Forearm", "All");
        db.insertInSymptomListTable("Broken bones (multiple fractures)", "Forearm", "All");
        db.insertInSymptomListTable("Bruising or discoloration", "Forearm", "All");
        db.insertInSymptomListTable("Bulging veins", "Forearm", "All");
        db.insertInSymptomListTable("Drainage or pus", "Forearm", "All");
        db.insertInSymptomListTable("Lump or bulge", "Forearm", "All");
        db.insertInSymptomListTable("Multiple bruises of different ages", "Forearm", "All");
        db.insertInSymptomListTable("Numbness or tingling", "Forearm", "All");
        db.insertInSymptomListTable("Pain or discomfort", "Forearm", "All");
        db.insertInSymptomListTable("Swelling", "Forearm", "All");
        db.insertInSymptomListTable("Tenderness to touch", "Forearm", "All");
        db.insertInSymptomListTable("Unable to move arm", "Forearm", "All");
        db.insertInSymptomListTable("Visible deformity", "Forearm", "All");
        db.insertInSymptomListTable("Warm to touch", "Forearm", "All");
        db.insertInSymptomListTable("Weakness", "Forearm", "All");
        db.insertInSymptomListTable("Bleeding", "Wrist", "All");
        db.insertInSymptomListTable("Broken bone (single fracture)", "Wrist", "All");
        db.insertInSymptomListTable("Bruising or discoloration", "Wrist", "All");
        db.insertInSymptomListTable("Drainage or pus", "Wrist", "All");
        db.insertInSymptomListTable("Joint aches", "Wrist", "All");
        db.insertInSymptomListTable("Joint pain", "Wrist", "All");
        db.insertInSymptomListTable("Lump or bulge", "Wrist", "All");
        db.insertInSymptomListTable("Morning joint stiffness", "Wrist", "All");
        db.insertInSymptomListTable("Multiple bruises of different ages", "Wrist", "All");
        db.insertInSymptomListTable("Numbness or tingling", "Wrist", "All");
        db.insertInSymptomListTable("Stiffness or decreased movement", "Wrist", "All");
        db.insertInSymptomListTable("Swelling", "Wrist", "All");
        db.insertInSymptomListTable("Tenderness to touch", "Wrist", "All");
        db.insertInSymptomListTable("Visible deformity", "Wrist", "All");
        db.insertInSymptomListTable("Warm to touch", "Wrist", "All");
        db.insertInSymptomListTable("Weakness", "Wrist", "All");
        db.insertInSymptomListTable("Bleeding", "Hand", "All");
        db.insertInSymptomListTable("Blue colored skin", "Hand", "All");
        db.insertInSymptomListTable("Broken bone (single fracture)", "Hand", "All");
        db.insertInSymptomListTable("Broken bones (multiple fractures)", "Hand", "All");
        db.insertInSymptomListTable("Bruising or discoloration", "Hand", "All");
        db.insertInSymptomListTable("Cold hands", "Hand", "All");
        db.insertInSymptomListTable("Color change", "Hand", "All");
        db.insertInSymptomListTable("Drainage or pus", "Hand", "All");
        db.insertInSymptomListTable("Excessive sweating", "Hand", "All");
        db.insertInSymptomListTable("Involuntary movements (picking, lip smacking etc.", "Hand", "All");
        db.insertInSymptomListTable("Lump or bulge", "Hand", "All");
        db.insertInSymptomListTable("Multiple bruises of different ages", "Hand", "All");
        db.insertInSymptomListTable("Muscle twitching (painless)", "Hand", "All");
        db.insertInSymptomListTable("Numbness or tingling", "Hand", "All");
        db.insertInSymptomListTable("Pain or discomfort", "Hand", "All");
        db.insertInSymptomListTable("Shaking hands or tremor", "Hand", "All");
        db.insertInSymptomListTable("Single palm crease", "Hand", "All");
        db.insertInSymptomListTable("Swelling", "Hand", "All");
        db.insertInSymptomListTable("Tenderness to touch", "Hand", "All");
        db.insertInSymptomListTable("Unable to grip (hands)", "Hand", "All");
        db.insertInSymptomListTable("Visible deformity", "Hand", "All");
        db.insertInSymptomListTable("Warm to touch", "Hand", "All");
        db.insertInSymptomListTable("Weakness", "Hand", "All");
        db.insertInSymptomListTable("Black colored skin", "Fingers", "All");
        db.insertInSymptomListTable("Bleeding", "Fingers", "All");
        db.insertInSymptomListTable("Blue colored skin", "Fingers", "All");
        db.insertInSymptomListTable("Broken bone (single fracture)", "Fingers", "All");
        db.insertInSymptomListTable("Broken bones (multiple fractures)", "Fingers", "All");
        db.insertInSymptomListTable("Bruising or discoloration", "Fingers", "All");
        db.insertInSymptomListTable("Cold hands", "Fingers", "All");
        db.insertInSymptomListTable("Color change", "Fingers", "All");
        db.insertInSymptomListTable("Curved fingernails", "Fingers", "All");
        db.insertInSymptomListTable("Drainage or pus", "Fingers", "All");
        db.insertInSymptomListTable("Enlarged fingertips", "Fingers", "All");
        db.insertInSymptomListTable("Inability to move", "Fingers", "All");
        db.insertInSymptomListTable("Involuntary movements (picking, lip smacking etc.", "Fingers", "All");
        db.insertInSymptomListTable("Joint pain", "Fingers", "All");
        db.insertInSymptomListTable("Lump or bulge", "Fingers", "All");
        db.insertInSymptomListTable("Morning joint stiffness", "Fingers", "All");
        db.insertInSymptomListTable("Multiple bruises of different ages", "Fingers", "All");
        db.insertInSymptomListTable("Muscle cramps or spasms (painful)", "Fingers", "All");
        db.insertInSymptomListTable("Muscle twitching (painless)", "Fingers", "All");
        db.insertInSymptomListTable("Numbness or tingling", "Fingers", "All");
        db.insertInSymptomListTable("Pain or discomfort", "Fingers", "All");
        db.insertInSymptomListTable("Red or black spots in fingernails", "Fingers", "All");
        db.insertInSymptomListTable("Shaking hands or tremor", "Fingers", "All");
        db.insertInSymptomListTable("Stiffness or decreased movement", "Fingers", "All");
        db.insertInSymptomListTable("Swelling", "Fingers", "All");
        db.insertInSymptomListTable("Tenderness to touch", "Fingers", "All");
        db.insertInSymptomListTable("Unable to grip (hands)", "Fingers", "All");
        db.insertInSymptomListTable("Usually short fourth fingers", "Fingers", "All");
        db.insertInSymptomListTable("Upward curving (spooning) of nails", "Fingers", "All");
        db.insertInSymptomListTable("Visible deformity", "Fingers", "All");
        db.insertInSymptomListTable("Warm to touch", "Fingers", "All");
        db.insertInSymptomListTable("Weakness", "Fingers", "All");
        db.insertInSymptomListTable("Bleeding", "Thigh", "All");
        db.insertInSymptomListTable("Broken bone (single fracture)", "Thigh", "All");
        db.insertInSymptomListTable("Broken bones (multiple fractures)", "Thigh", "All");
        db.insertInSymptomListTable("Bruising or discoloration", "Thigh", "All");
        db.insertInSymptomListTable("Bulging veins", "Thigh", "All");
        db.insertInSymptomListTable("Drainage or pus", "Thigh", "All");
        db.insertInSymptomListTable("Lump or bulge", "Thigh", "All");
        db.insertInSymptomListTable("Multiple bruises of different ages", "Thigh", "All");
        db.insertInSymptomListTable("Muscle cramps or spasms (painful)", "Thigh", "All");
        db.insertInSymptomListTable("Numbness or tingling", "Thigh", "All");
        db.insertInSymptomListTable("Pain or discomfort", "Thigh", "All");
        db.insertInSymptomListTable("Stiffness or decreased movement", "Thigh", "All");
        db.insertInSymptomListTable("Swelling", "Thigh", "All");
        db.insertInSymptomListTable("Tenderness to touch", "Thigh", "All");
        db.insertInSymptomListTable("Unable to bear weight", "Thigh", "All");
        db.insertInSymptomListTable("Unable to move leg", "Thigh", "All");
        db.insertInSymptomListTable("Visible deformity", "Thigh", "All");
        db.insertInSymptomListTable("Warm to touch", "Thigh", "All");
        db.insertInSymptomListTable("Weakness", "Thigh", "All");
        db.insertInSymptomListTable("Bleeding", "Hamstring", "All");
        db.insertInSymptomListTable("Broken bone (single fracture)", "Hamstring", "All");
        db.insertInSymptomListTable("Broken bones (multiple fractures)", "Hamstring", "All");
        db.insertInSymptomListTable("Bruising or discoloration", "Hamstring", "All");
        db.insertInSymptomListTable("Bulging veins", "Hamstring", "All");
        db.insertInSymptomListTable("Drainage or pus", "Hamstring", "All");
        db.insertInSymptomListTable("Lump or bulge", "Hamstring", "All");
        db.insertInSymptomListTable("Multiple bruises of different ages", "Hamstring", "All");
        db.insertInSymptomListTable("Muscle cramps or spasms (painful)", "Hamstring", "All");
        db.insertInSymptomListTable("Numbness or tingling", "Hamstring", "All");
        db.insertInSymptomListTable("Pain or discomfort", "Hamstring", "All");
        db.insertInSymptomListTable("Stiffness or decreased movement", "Hamstring", "All");
        db.insertInSymptomListTable("Swelling", "Hamstring", "All");
        db.insertInSymptomListTable("Tenderness to touch", "Hamstring", "All");
        db.insertInSymptomListTable("Unable to bear weight", "Hamstring", "All");
        db.insertInSymptomListTable("Unable to move leg", "Hamstring", "All");
        db.insertInSymptomListTable("Visible deformity", "Hamstring", "All");
        db.insertInSymptomListTable("Warm to touch", "Hamstring", "All");
        db.insertInSymptomListTable("Weakness", "Hamstring", "All");
        db.insertInSymptomListTable("Bleeding", "Knee", "All");
        db.insertInSymptomListTable("Bruising or discoloration", "Knee", "All");
        db.insertInSymptomListTable("Broken bone (single fracture)", "Knee", "All");
        db.insertInSymptomListTable("Broken bones (multiple fractures)", "Knee", "All");
        db.insertInSymptomListTable("Bruising or discoloration", "Knee", "All");
        db.insertInSymptomListTable("Difficulty walking", "Knee", "All");
        db.insertInSymptomListTable("Drainage or pus", "Knee", "All");
        db.insertInSymptomListTable("Guarding or favoring joint", "Knee", "All");
        db.insertInSymptomListTable("Joint aches", "Knee", "All");
        db.insertInSymptomListTable("Joint instability", "Knee", "All");
        db.insertInSymptomListTable("Joint pain", "Knee", "All");
        db.insertInSymptomListTable("Lump or bulge", "Knee", "All");
        db.insertInSymptomListTable("Multiple bruises of different ages", "Knee", "All");
        db.insertInSymptomListTable("Numbness or tingling", "Knee", "All");
        db.insertInSymptomListTable("Popping or snapping sound from joint", "Knee", "All");
        db.insertInSymptomListTable("Stiffness or decreased movement", "Knee", "All");
        db.insertInSymptomListTable("Swelling", "Knee", "All");
        db.insertInSymptomListTable("Tenderness to touch", "Knee", "All");
        db.insertInSymptomListTable("Unable to bear weight", "Knee", "All");
        db.insertInSymptomListTable("Visible deformity", "Knee", "All");
        db.insertInSymptomListTable("Warm to touch", "Knee", "All");
        db.insertInSymptomListTable("Weakness", "Knee", "All");
        db.insertInSymptomListTable("Bleeding", "Back of knee", "All");
        db.insertInSymptomListTable("Bruising or discoloration", "Back of knee", "All");
        db.insertInSymptomListTable("Drainage or pus", "Back of knee", "All");
        db.insertInSymptomListTable("Guarding or favoring joint", "Back of knee", "All");
        db.insertInSymptomListTable("Joint instability", "Back of knee", "All");
        db.insertInSymptomListTable("Joint pain", "Back of knee", "All");
        db.insertInSymptomListTable("Lump or bulge", "Back of knee", "All");
        db.insertInSymptomListTable("Morning joint stiffness", "Back of knee", "All");
        db.insertInSymptomListTable("Multiple bruises of different ages", "Back of knee", "All");
        db.insertInSymptomListTable("Numbness or tingling", "Back of knee", "All");
        db.insertInSymptomListTable("Stiffness or decreased movement", "Back of knee", "All");
        db.insertInSymptomListTable("Swelling", "Back of knee", "All");
        db.insertInSymptomListTable("Tenderness to touch", "Back of knee", "All");
        db.insertInSymptomListTable("Warm to touch", "Back of knee", "All");
        db.insertInSymptomListTable("Bleeding", "Shin", "All");
        db.insertInSymptomListTable("Broken bone (single fracture)", "Shin", "All");
        db.insertInSymptomListTable("Broken bones (multiple fractures)", "Shin", "All");
        db.insertInSymptomListTable("Bruising or discoloration", "Shin", "All");
        db.insertInSymptomListTable("Bulging veins", "Shin", "All");
        db.insertInSymptomListTable("Drainage or pus", "Shin", "All");
        db.insertInSymptomListTable("Lump or bulge", "Shin", "All");
        db.insertInSymptomListTable("Multiple bruises of different ages", "Shin", "All");
        db.insertInSymptomListTable("Muscle cramps or spasms (painful)", "Shin", "All");
        db.insertInSymptomListTable("Numbness or tingling", "Shin", "All");
        db.insertInSymptomListTable("Pain or discomfort", "Shin", "All");
        db.insertInSymptomListTable("Swelling", "Shin", "All");
        db.insertInSymptomListTable("Tenderness to touch", "Shin", "All");
        db.insertInSymptomListTable("Unable to bear weight", "Shin", "All");
        db.insertInSymptomListTable("Visible deformity", "Shin", "All");
        db.insertInSymptomListTable("Warm to touch", "Shin", "All");
        db.insertInSymptomListTable("Bleeding", "Calf", "All");
        db.insertInSymptomListTable("Broken bone (single fracture)", "Calf", "All");
        db.insertInSymptomListTable("Broken bones (multiple fractures)", "Calf", "All");
        db.insertInSymptomListTable("Bruising or discoloration", "Calf", "All");
        db.insertInSymptomListTable("Bulging veins", "Calf", "All");
        db.insertInSymptomListTable("Drainage or pus", "Calf", "All");
        db.insertInSymptomListTable("Lump or bulge", "Calf", "All");
        db.insertInSymptomListTable("Multiple bruises of different ages", "Calf", "All");
        db.insertInSymptomListTable("Muscle cramps or spasms (painful)", "Calf", "All");
        db.insertInSymptomListTable("Numbness or tingling", "Calf", "All");
        db.insertInSymptomListTable("Pain or discomfort", "Calf", "All");
        db.insertInSymptomListTable("Stiffness or decreased movement", "Calf", "All");
        db.insertInSymptomListTable("Swelling", "Calf", "All");
        db.insertInSymptomListTable("Unable to bear weight", "Calf", "All");
        db.insertInSymptomListTable("Visible deformity", "Calf", "All");
        db.insertInSymptomListTable("Warm to touch", "Calf", "All");
        db.insertInSymptomListTable("Bleeding", "Ankle", "All");
        db.insertInSymptomListTable("Broken bone (single fracture)", "Ankle", "All");
        db.insertInSymptomListTable("Bruising or discoloration", "Ankle", "All");
        db.insertInSymptomListTable("Difficulty walking", "Ankle", "All");
        db.insertInSymptomListTable("Drainage or pus", "Ankle", "All");
        db.insertInSymptomListTable("Guarding or favoring joint", "Ankle", "All");
        db.insertInSymptomListTable("Joint aches", "Ankle", "All");
        db.insertInSymptomListTable("Joint pain", "Ankle", "All");
        db.insertInSymptomListTable("Lump or bulge", "Ankle", "All");
        db.insertInSymptomListTable("Morning joint stiffness", "Ankle", "All");
        db.insertInSymptomListTable("Multiple bruises of different ages", "Ankle", "All");
        db.insertInSymptomListTable("Stiffness or decreased movement", "Ankle", "All");
        db.insertInSymptomListTable("Swelling", "Ankle", "All");
        db.insertInSymptomListTable("Tenderness to touch", "Ankle", "All");
        db.insertInSymptomListTable("Unable to bear weight", "Ankle", "All");
        db.insertInSymptomListTable("Unable to bend foot down", "Ankle", "All");
        db.insertInSymptomListTable("Unable to move joint", "Ankle", "All");
        db.insertInSymptomListTable("Visible deformity", "Ankle", "All");
        db.insertInSymptomListTable("Warm to touch", "Ankle", "All");
        db.insertInSymptomListTable("Weakness", "Ankle", "All");
        db.insertInSymptomListTable("Bleeding", "Foot", "All");
        db.insertInSymptomListTable("Blue colored skin", "Foot", "All");
        db.insertInSymptomListTable("Broken bone (single fracture)", "Foot", "All");
        db.insertInSymptomListTable("Broken bones (multiple fractures)", "Foot", "All");
        db.insertInSymptomListTable("Bruising or discoloration", "Foot", "All");
        db.insertInSymptomListTable("Cold feet", "Foot", "All");
        db.insertInSymptomListTable("Color change", "Foot", "All");
        db.insertInSymptomListTable("Difficulty walking", "Foot", "All");
        db.insertInSymptomListTable("Drainage or pus", "Foot", "All");
        db.insertInSymptomListTable("Lump or bulge", "Foot", "All");
        db.insertInSymptomListTable("Multiple bruises of different ages", "Foot", "All");
        db.insertInSymptomListTable("Muscle cramps or spasms (painful)", "Foot", "All");
        db.insertInSymptomListTable("Numbness or tingling", "Foot", "All");
        db.insertInSymptomListTable("Pain or discomfort", "Foot", "All");
        db.insertInSymptomListTable("Stiffness or decreased movement", "Foot", "All");
        db.insertInSymptomListTable("Swelling", "Foot", "All");
        db.insertInSymptomListTable("Tenderness to touch", "Foot", "All");
        db.insertInSymptomListTable("Unable to bear weight", "Foot", "All");
        db.insertInSymptomListTable("Visible deformity", "Foot", "All");
        db.insertInSymptomListTable("Warm to touch", "Foot", "All");
        db.insertInSymptomListTable("Weakness", "Foot", "All");
        db.insertInSymptomListTable("Black colored skin", "Toes", "All");
        db.insertInSymptomListTable("Bleeding", "Toes", "All");
        db.insertInSymptomListTable("Blue colored skin", "Toes", "All");
        db.insertInSymptomListTable("Broken bone (single fracture)", "Toes", "All");
        db.insertInSymptomListTable("Broken bones (multiple fractures)", "Toes", "All");
        db.insertInSymptomListTable("Bruising or discoloration", "Toes", "All");
        db.insertInSymptomListTable("Cold feet", "Toes", "All");
        db.insertInSymptomListTable("Color change", "Toes", "All");
        db.insertInSymptomListTable("Difficulty walking", "Toes", "All");
        db.insertInSymptomListTable("Drainage or pus", "Toes", "All");
        db.insertInSymptomListTable("Joint pain", "Toes", "All");
        db.insertInSymptomListTable("Lump or bulge", "Toes", "All");
        db.insertInSymptomListTable("Morning joint stiffness", "Toes", "All");
        db.insertInSymptomListTable("Multiple bruises of different ages", "Toes", "All");
        db.insertInSymptomListTable("Muscle cramps or spasms (painful)", "Toes", "All");
        db.insertInSymptomListTable("Numbness or tingling", "Toes", "All");
        db.insertInSymptomListTable("Pain or discomfort", "Toes", "All");
        db.insertInSymptomListTable("Stiffness or decreased movement", "Toes", "All");
        db.insertInSymptomListTable("Swelling", "Toes", "All");
        db.insertInSymptomListTable("Tenderness to touch", "Toes", "All");
        db.insertInSymptomListTable("Unable to bear weight", "Toes", "All");
        db.insertInSymptomListTable("Visible deformity", "Toes", "All");
        db.insertInSymptomListTable("Warm to touch", "Toes", "All");
        db.insertInSymptomListTable("Bleeding", "Sole", "All");
        db.insertInSymptomListTable("Blue colored skin", "Sole", "All");
        db.insertInSymptomListTable("Broken bone (single fracture)", "Sole", "All");
        db.insertInSymptomListTable("Broken bones (multiple fractures)", "Sole", "All");
        db.insertInSymptomListTable("Bruising or discoloration", "Sole", "All");
        db.insertInSymptomListTable("Cold feet", "Sole", "All");
        db.insertInSymptomListTable("Color change", "Sole", "All");
        db.insertInSymptomListTable("Difficulty walking", "Sole", "All");
        db.insertInSymptomListTable("Drainage or pus", "Sole", "All");
        db.insertInSymptomListTable("Excessive sweating", "Sole", "All");
        db.insertInSymptomListTable("Lump or bulge", "Sole", "All");
        db.insertInSymptomListTable("Multiple bruises of different ages", "Sole", "All");
        db.insertInSymptomListTable("Muscle cramps or spasms (painful)", "Sole", "All");
        db.insertInSymptomListTable("Numbness or tingling", "Sole", "All");
        db.insertInSymptomListTable("Pain or discomfort", "Sole", "All");
        db.insertInSymptomListTable("Swelling", "Sole", "All");
        db.insertInSymptomListTable("Tenderness to touch", "Sole", "All");
        db.insertInSymptomListTable("Unable to bear weight", "Sole", "All");
        db.insertInSymptomListTable("Visible deformity", "Sole", "All");
        db.insertInSymptomListTable("Warm to touch", "Sole", "All");
        db.insertInSymptomListTable("Weakness", "Sole", "All");
    }

    private void insertEmergencyNumbers(){
        db.insertInEmergencyNumbersTable("Afghanistan","AF","102");
        db.insertInEmergencyNumbersTable("Aland","AX","112");
        db.insertInEmergencyNumbersTable("Albania","AL","127");
        db.insertInEmergencyNumbersTable("Algeria","DZ","14");
        db.insertInEmergencyNumbersTable("American Samoa","AS","911");
        db.insertInEmergencyNumbersTable("Andorra","AD","112");
        db.insertInEmergencyNumbersTable("Angola","AO","115");
        db.insertInEmergencyNumbersTable("Antigua and Barbuda","AG","911");
        db.insertInEmergencyNumbersTable("Argentina","AR","107");
        db.insertInEmergencyNumbersTable("Armenia","AM","103");
        db.insertInEmergencyNumbersTable("Australia","AU","0");
        db.insertInEmergencyNumbersTable("Austria","AT","112");
        db.insertInEmergencyNumbersTable("Azerbaijan","AZ","103");
        db.insertInEmergencyNumbersTable("Bahamas, The","BS","911");
        db.insertInEmergencyNumbersTable("Bahrain","BH","999");
        db.insertInEmergencyNumbersTable("Bangladesh","BD","199");
        db.insertInEmergencyNumbersTable("Barbados","BB","511");
        db.insertInEmergencyNumbersTable("Belarus","BY","103");
        db.insertInEmergencyNumbersTable("Belgium","BE","112");
        db.insertInEmergencyNumbersTable("Belize","BZ","911");
        db.insertInEmergencyNumbersTable("Benin","BJ","118");
        db.insertInEmergencyNumbersTable("Bermuda","BM","911");
        db.insertInEmergencyNumbersTable("Bhutan","BT","112");
        db.insertInEmergencyNumbersTable("Bolivia","BO","118");
        db.insertInEmergencyNumbersTable("Bosnia and Herzegovina","BA","124");
        db.insertInEmergencyNumbersTable("Botswana","BW","997");
        db.insertInEmergencyNumbersTable("Brazil","BR","192");
        db.insertInEmergencyNumbersTable("British Indian Ocean Territory","IO","112");
        db.insertInEmergencyNumbersTable("Brunei","BN","991");
        db.insertInEmergencyNumbersTable("Bulgaria","BG","112");
        db.insertInEmergencyNumbersTable("Burkina Faso","BF","18");
        db.insertInEmergencyNumbersTable("Burundi","BI","118");
        db.insertInEmergencyNumbersTable("Cambodia","KH","119");
        db.insertInEmergencyNumbersTable("Cameroon","CM","112");
        db.insertInEmergencyNumbersTable("Canada","CA","911");
        db.insertInEmergencyNumbersTable("Cape Verde","CV","130");
        db.insertInEmergencyNumbersTable("Cayman Islands","KY","911");
        db.insertInEmergencyNumbersTable("Central African Republic","CF","118");
        db.insertInEmergencyNumbersTable("Chad","TD","18");
        db.insertInEmergencyNumbersTable("Chile","CL","131");
        db.insertInEmergencyNumbersTable("China, People's Republic of","CN","120");
        db.insertInEmergencyNumbersTable("Christmas Island","CX","0");
        db.insertInEmergencyNumbersTable("Cocos (Keeling) Islands","CC","0");
        db.insertInEmergencyNumbersTable("Colombia","CO","123");
        db.insertInEmergencyNumbersTable("Comoros","KM","18");
        db.insertInEmergencyNumbersTable("Congo, (Congo Â– Brazzaville)","CG","066654804");
        db.insertInEmergencyNumbersTable("Congo, (Congo Â– Kinshasa)","CD","066654804");
        db.insertInEmergencyNumbersTable("Cook Islands","CK","998");
        db.insertInEmergencyNumbersTable("Costa Rica","CR","911");
        db.insertInEmergencyNumbersTable("Cote d'Ivoire (Ivory Coast)","CI","111");
        db.insertInEmergencyNumbersTable("Croatia","HR","112");
        db.insertInEmergencyNumbersTable("Cuba","CU","105");
        db.insertInEmergencyNumbersTable("Cyprus","CY","112");
        db.insertInEmergencyNumbersTable("Czech Republic","CZ","112");
        db.insertInEmergencyNumbersTable("Denmark","DK","112");
        db.insertInEmergencyNumbersTable("Djibouti","DJ","351");
        db.insertInEmergencyNumbersTable("Dominica","DM","911");
        db.insertInEmergencyNumbersTable("Dominican Republic","DO","911");
        db.insertInEmergencyNumbersTable("Ecuador","EC","101");
        db.insertInEmergencyNumbersTable("Egypt","EG","122");
        db.insertInEmergencyNumbersTable("El Salvador","SV","911");
        db.insertInEmergencyNumbersTable("Equatorial Guinea","GQ","115");
        db.insertInEmergencyNumbersTable("Eritrea","ER","116");
        db.insertInEmergencyNumbersTable("Estonia","EE","112");
        db.insertInEmergencyNumbersTable("Ethiopia","ET","997");
        db.insertInEmergencyNumbersTable("Falkland Islands (Islas Malvinas)","FK","999");
        db.insertInEmergencyNumbersTable("Faroe Islands","FO","112");
        db.insertInEmergencyNumbersTable("Fiji","FJ","911");
        db.insertInEmergencyNumbersTable("Finland","FI","112");
        db.insertInEmergencyNumbersTable("France","FR","112");
        db.insertInEmergencyNumbersTable("French Guiana","GF","112");
        db.insertInEmergencyNumbersTable("French Polynesia","PF","15");
        db.insertInEmergencyNumbersTable("Gabon","GA","300");
        db.insertInEmergencyNumbersTable("Gambia, The","GM","116");
        db.insertInEmergencyNumbersTable("Georgia","GE","22");
        db.insertInEmergencyNumbersTable("Germany","DE","112");
        db.insertInEmergencyNumbersTable("Ghana","GH","193");
        db.insertInEmergencyNumbersTable("Gibraltar","GI","190");
        db.insertInEmergencyNumbersTable("Greece","GR","112");
        db.insertInEmergencyNumbersTable("Greenland","GL","112");
        db.insertInEmergencyNumbersTable("Grenada","GD","911");
        db.insertInEmergencyNumbersTable("Guadeloupe","GP","112");
        db.insertInEmergencyNumbersTable("Guam","GU","911");
        db.insertInEmergencyNumbersTable("Guatemala","GT","125");
        db.insertInEmergencyNumbersTable("Guernsey","GG","999");
        db.insertInEmergencyNumbersTable("Guinea","GN","1717");
        db.insertInEmergencyNumbersTable("Guinea-Bissau","GW","112");
        db.insertInEmergencyNumbersTable("Guyana","GY","913");
        db.insertInEmergencyNumbersTable("Haiti","HT","114");
        db.insertInEmergencyNumbersTable("Honduras","HN","199");
        db.insertInEmergencyNumbersTable("Hong Kong","HK","999");
        db.insertInEmergencyNumbersTable("Hungary","HU","112");
        db.insertInEmergencyNumbersTable("Iceland","IS","112");
        db.insertInEmergencyNumbersTable("India","IN","112");
        db.insertInEmergencyNumbersTable("Indonesia","ID","118");
        db.insertInEmergencyNumbersTable("Iran","IR","115");
        db.insertInEmergencyNumbersTable("Iraq","IQ","122");
        db.insertInEmergencyNumbersTable("Ireland","IE","112");
        db.insertInEmergencyNumbersTable("Isle of Man","IM","999");
        db.insertInEmergencyNumbersTable("Israel","IL","112");
        db.insertInEmergencyNumbersTable("Italy","IT","112");
        db.insertInEmergencyNumbersTable("Jamaica","JM","911");
        db.insertInEmergencyNumbersTable("Japan","JP","119");
        db.insertInEmergencyNumbersTable("Jersey","JE","112");
        db.insertInEmergencyNumbersTable("Jordan","JO","191");
        db.insertInEmergencyNumbersTable("Kazakhstan","KZ","103");
        db.insertInEmergencyNumbersTable("Kenya","KE","999");
        db.insertInEmergencyNumbersTable("Kiribati","KI","999");
        db.insertInEmergencyNumbersTable("Korea, North","KP","112");
        db.insertInEmergencyNumbersTable("Korea, South","KR","119");
        db.insertInEmergencyNumbersTable("Kuwait","KW","112");
        db.insertInEmergencyNumbersTable("Kyrgyzstan","KG","103");
        db.insertInEmergencyNumbersTable("Laos","LA","195");
        db.insertInEmergencyNumbersTable("Latvia","LV","112");
        db.insertInEmergencyNumbersTable("Lebanon","LB","112");
        db.insertInEmergencyNumbersTable("Lesotho","LS","121");
        db.insertInEmergencyNumbersTable("Liberia","LR","911");
        db.insertInEmergencyNumbersTable("Libya","LY","193");
        db.insertInEmergencyNumbersTable("Liechtenstein","LI","112");
        db.insertInEmergencyNumbersTable("Lithuania","LT","112");
        db.insertInEmergencyNumbersTable("Luxembourg","LU","112");
        db.insertInEmergencyNumbersTable("Macau","MO","999");
        db.insertInEmergencyNumbersTable("Macedonia","MK","112");
        db.insertInEmergencyNumbersTable("Madagascar","MG","117");
        db.insertInEmergencyNumbersTable("Malawi","MW","998");
        db.insertInEmergencyNumbersTable("Malaysia","MY","999");
        db.insertInEmergencyNumbersTable("Maldives","MV","102");
        db.insertInEmergencyNumbersTable("Mali","ML","15");
        db.insertInEmergencyNumbersTable("Malta","MT","112");
        db.insertInEmergencyNumbersTable("Marshall Islands","MH","911");
        db.insertInEmergencyNumbersTable("Martinique","MQ","15");
        db.insertInEmergencyNumbersTable("Mauritania","MR","118");
        db.insertInEmergencyNumbersTable("Mauritius","MU","114");
        db.insertInEmergencyNumbersTable("Mayotte","YT","15");
        db.insertInEmergencyNumbersTable("Mexico","MX","66");
        db.insertInEmergencyNumbersTable("Micronesia","FM","911");
        db.insertInEmergencyNumbersTable("Moldova","MD","903");
        db.insertInEmergencyNumbersTable("Monaco","MC","112");
        db.insertInEmergencyNumbersTable("Mongolia","MN","103");
        db.insertInEmergencyNumbersTable("Montenegro","ME","112");
        db.insertInEmergencyNumbersTable("Montserrat","MS","999");
        db.insertInEmergencyNumbersTable("Morocco","MA","190");
        db.insertInEmergencyNumbersTable("Mozambique","MZ","117");
        db.insertInEmergencyNumbersTable("Myanmar","MM","999");
        db.insertInEmergencyNumbersTable("Namibia","NA","026461230505");
        db.insertInEmergencyNumbersTable("Nauru","NR","111");
        db.insertInEmergencyNumbersTable("Nepal","NP","100");
        db.insertInEmergencyNumbersTable("Netherlands","NL","112");
        db.insertInEmergencyNumbersTable("New Caledonia","NC","15");
        db.insertInEmergencyNumbersTable("New Zealand","NZ","111");
        db.insertInEmergencyNumbersTable("Nicaragua","NI","118");
        db.insertInEmergencyNumbersTable("Niger","NE","18");
        db.insertInEmergencyNumbersTable("Nigeria","NG","199");
        db.insertInEmergencyNumbersTable("Niue","NU","999");
        db.insertInEmergencyNumbersTable("Norfolk Island","NF","0");
        db.insertInEmergencyNumbersTable("Norway","NO","112");
        db.insertInEmergencyNumbersTable("Oman","OM","9999");
        db.insertInEmergencyNumbersTable("Pakistan","PK","15");
        db.insertInEmergencyNumbersTable("Palau","PW","911");
        db.insertInEmergencyNumbersTable("Panama","PA","911");
        db.insertInEmergencyNumbersTable("Papua New Guinea","PG","0");
        db.insertInEmergencyNumbersTable("Paraguay","PY","911");
        db.insertInEmergencyNumbersTable("Peru","PE","117");
        db.insertInEmergencyNumbersTable("Philippines","PH","117");
        db.insertInEmergencyNumbersTable("Poland","PL","112");
        db.insertInEmergencyNumbersTable("Portugal","PT","112");
        db.insertInEmergencyNumbersTable("Puerto Rico","PR","911");
        db.insertInEmergencyNumbersTable("Qatar","QA","999");
        db.insertInEmergencyNumbersTable("Reunion","RE","112");
        db.insertInEmergencyNumbersTable("Romania","RO","112");
        db.insertInEmergencyNumbersTable("Russia","RU","3");
        db.insertInEmergencyNumbersTable("Rwanda","RW","112");
        db.insertInEmergencyNumbersTable("Saint Barthelemy","GP","911");
        db.insertInEmergencyNumbersTable("Saint Helena","SH","911");
        db.insertInEmergencyNumbersTable("Saint Kitts and Nevis","KN","911");
        db.insertInEmergencyNumbersTable("Saint Lucia","LC","911");
        db.insertInEmergencyNumbersTable("Saint Martin","GP","911");
        db.insertInEmergencyNumbersTable("Saint Pierre and Miquelon","PM","15");
        db.insertInEmergencyNumbersTable("Saint Vincent and the Grenadines","VC","911");
        db.insertInEmergencyNumbersTable("Samoa","WS","996");
        db.insertInEmergencyNumbersTable("San Marino","SM","118");
        db.insertInEmergencyNumbersTable("Sao Tome and Principe","ST","222");
        db.insertInEmergencyNumbersTable("Saudi Arabia","SA","997");
        db.insertInEmergencyNumbersTable("Serbia","RS","112");
        db.insertInEmergencyNumbersTable("Seychelles","SC","999");
        db.insertInEmergencyNumbersTable("Sierra Leone","SL","999");
        db.insertInEmergencyNumbersTable("Singapore","SG","995");
        db.insertInEmergencyNumbersTable("Slovakia","SK","112");
        db.insertInEmergencyNumbersTable("Slovenia","SI","112");
        db.insertInEmergencyNumbersTable("Solomon Islands","SB","999");
        db.insertInEmergencyNumbersTable("Somalia","SO","555");
        db.insertInEmergencyNumbersTable("South Africa","ZA","10177");
        db.insertInEmergencyNumbersTable("South Georgia & South Sandwich Islands","GS","999");
        db.insertInEmergencyNumbersTable("Spain","ES","112");
        db.insertInEmergencyNumbersTable("Sri Lanka","LK","110");
        db.insertInEmergencyNumbersTable("Sudan","SD","999");
        db.insertInEmergencyNumbersTable("Suriname","SR","115");
        db.insertInEmergencyNumbersTable("Svalbard","SJ","112");
        db.insertInEmergencyNumbersTable("Swaziland","SZ","999");
        db.insertInEmergencyNumbersTable("Sweden","SE","112");
        db.insertInEmergencyNumbersTable("Switzerland","CH","112");
        db.insertInEmergencyNumbersTable("Syria","SY","110");
        db.insertInEmergencyNumbersTable("Taiwan","TW","119");
        db.insertInEmergencyNumbersTable("Tajikistan","TJ","3");
        db.insertInEmergencyNumbersTable("Tanzania","TZ","111");
        db.insertInEmergencyNumbersTable("Thailand","TH","1554");
        db.insertInEmergencyNumbersTable("Timor-Leste (East Timor)","TL","112");
        db.insertInEmergencyNumbersTable("Togo","TG","242");
        db.insertInEmergencyNumbersTable("Tonga","TO","242");
        db.insertInEmergencyNumbersTable("Trinidad and Tobago","TT","990");
        db.insertInEmergencyNumbersTable("Tunisia","TN","197");
        db.insertInEmergencyNumbersTable("Turkey","TR","112");
        db.insertInEmergencyNumbersTable("Turkmenistan","TM","3");
        db.insertInEmergencyNumbersTable("Turks and Caicos Islands","TC","911");
        db.insertInEmergencyNumbersTable("Tuvalu","TV","911");
        db.insertInEmergencyNumbersTable("Uganda","UG","999");
        db.insertInEmergencyNumbersTable("Ukraine","UA","112");
        db.insertInEmergencyNumbersTable("United Arab Emirates","AE","998");
        db.insertInEmergencyNumbersTable("United Kingdom","GB","999");
        db.insertInEmergencyNumbersTable("United States","US","911");
        db.insertInEmergencyNumbersTable("Uruguay","UY","911");
        db.insertInEmergencyNumbersTable("Uzbekistan","UZ","3");
        db.insertInEmergencyNumbersTable("Vanuatu","VU","112");
        db.insertInEmergencyNumbersTable("Vatican City","VA","112");
        db.insertInEmergencyNumbersTable("Venezuela","VE","171");
        db.insertInEmergencyNumbersTable("Vietnam","VN","115");
        db.insertInEmergencyNumbersTable("Virgin Islands","VG","911");
        db.insertInEmergencyNumbersTable("Virgin Islands","VI","911");
        db.insertInEmergencyNumbersTable("Western Sahara","EH","150");
        db.insertInEmergencyNumbersTable("Yemen","YE","199");
        db.insertInEmergencyNumbersTable("Zambia","ZM","991");
        db.insertInEmergencyNumbersTable("Zimbabwe","ZW","994");
    }

    private class HttpAsyncTask extends AsyncTask<Void, Void, Void> {

//        int progress = 0;
//        ProgressBar progressBar;
//        Context context;

//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            context = getApplicationContext();

//            RelativeLayout layout = new RelativeLayout(context);
//            progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleLarge);
//            progressBar.setVisibility(View.VISIBLE);
//            progressBar.setIndeterminate(false);
//            progressBar.setMax(1054);
//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
//            );
//            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//            layout.addView(progressBar, params);

//            setContentView(layout);
//        }

        @Override
        protected Void doInBackground(Void... voids) {
            db.open();
            loadTables();
            db.close();
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
//            progress += Integer.valueOf(values[0]);
//            progressBar.setProgress(progress);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}