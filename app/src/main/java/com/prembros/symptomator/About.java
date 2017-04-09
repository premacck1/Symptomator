package com.prembros.symptomator;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

/*
* Created by Prem $ on 7/22/2016.
*/
public class About extends Fragment {

    public View rootView;
    private String previousTitle;
    private String previousSubTitle;
    private ActionBar actionBar;

    @SuppressWarnings("ConstantConditions")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.about, container, false);
        rootView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fragment_anim_in));

//        SET ACTION BAR
        actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            previousTitle = actionBar.getTitle().toString();
            previousSubTitle = actionBar.getSubtitle().toString();
            actionBar.setTitle(R.string.about);
            actionBar.setSubtitle(R.string.prembros);
        }

//        JUST ADD android:autoLink="web|email" IN CORRESPONDING XML LAYOUT FILE
//        AND YOU DON'T NEED THE FOLLOWING SNIPPET!
/*
          CustomTextViewSemiLight emailLink = (CustomTextViewSemiLight) rootView.findViewById(R.id.email_link);
        emailLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND, Uri.fromParts(
                        "mailto","itsprembros@gmail.com", null));
                emailIntent.setType("text/email");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, R.string.email);
                startActivity(Intent.createChooser(emailIntent, "Send email"));
            }
        });
*/

        TextView PremBrosLink = (TextView) rootView.findViewById(R.id.PremBros_link);
        PremBrosLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=Prem+Bros")));
                } catch (android.content.ActivityNotFoundException e){
                    e.printStackTrace();
                    Toast.makeText(getContext(), "No app found for this action!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onDestroy() {
        actionBar.setTitle(previousTitle);
        actionBar.setSubtitle(previousSubTitle);
        super.onDestroy();
    }
}
