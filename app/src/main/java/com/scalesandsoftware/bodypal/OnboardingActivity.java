package com.scalesandsoftware.bodypal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    //VARIABLES
    ViewPager onboardingPager;
    OnboardingViewPager onboardingViewPager;
    Button nextButton;
    int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        //CHECK IF THIS ACTIVITY HAS BEEN OPENED BEFORE OR NOT
        if(restorePrefData()){
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
        }

        //SETTING UP ONBOARDING SCREENS
        final List<ScreenItem> mList = new ArrayList<>();
        mList.add(new ScreenItem("Exercises", "To Your Personalised Profile", R.drawable.undraw_healthy_habit_bh5w));
        mList.add(new ScreenItem("Keep Eye On Your Health", "Log Your Activities", R.drawable.undraw_fitness_tracker_3033));
        mList.add(new ScreenItem("Check Your Progress", "A Personalised Calendar", R.drawable.undraw_calendar_dutt));

        //INITIALIZE VIEWS
        onboardingPager = findViewById(R.id.onboardingPager);
        nextButton = findViewById(R.id.nextButton);

        //INITIALIZING VIEW PAGER ADAPTER
        onboardingViewPager = new OnboardingViewPager(this, mList);

        //SETTING UP ADAPTER
        onboardingPager.setAdapter(onboardingViewPager);

        final Intent intent = new Intent(this, LoginActivity.class);

        //CLICK FUNCTION FOR NEXT BUTTON
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = onboardingPager.getCurrentItem();
                if (position < mList.size()){
                    position++;
                    onboardingPager.setCurrentItem(position);
                }
                if (position == mList.size()-1){
                    nextButton.setText(R.string.onboarding_button_2);
                }
                if (position == mList.size()){
                    startActivity(intent);
                    savePrefData();
                    finish();
                }
            }
        });
    }

    private boolean restorePrefData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPreference", MODE_PRIVATE);
        return pref.getBoolean("isOnboardingScreenOpen", false);
    }

    private void savePrefData(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPreference", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isOnboardingScreenOpen",true);
        editor.apply();
    }

}
