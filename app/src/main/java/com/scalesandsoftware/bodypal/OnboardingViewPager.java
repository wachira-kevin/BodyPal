package com.scalesandsoftware.bodypal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class OnboardingViewPager extends PagerAdapter {

    private Context mContext;
    private List<ScreenItem> mListScreen;

    OnboardingViewPager(Context mContext, List<ScreenItem> mListScreen) {
        this.mContext = mContext;
        this.mListScreen = mListScreen;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("InflateParams") View layoutScreen = inflater.inflate(R.layout.layout_screen, null);

        ImageView onboardingImage = layoutScreen.findViewById(R.id.onboardingImage);
        TextView onboardingDescription = layoutScreen.findViewById(R.id.descriptionText);
        TextView onboardingTitle = layoutScreen.findViewById(R.id.headText);

        onboardingTitle.setText(mListScreen.get(position).getOnboardingTitle());
        onboardingDescription.setText(mListScreen.get(position).getOnboardingDescription());
        onboardingImage.setImageResource(mListScreen.get(position).getOnboardingImage());

        container.addView(layoutScreen);
        return layoutScreen;
    }

    @Override
    public int getCount() {
        return mListScreen.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
