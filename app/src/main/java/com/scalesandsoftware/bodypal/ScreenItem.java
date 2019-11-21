package com.scalesandsoftware.bodypal;

class ScreenItem {

    private String onboardingTitle, onboardingDescription;
    private int onboardingImage;

    ScreenItem(String title, String description, int image){
        onboardingTitle = title;
        onboardingDescription = description;
        onboardingImage = image;
    }


    String getOnboardingTitle() {
        return onboardingTitle;
    }

    String getOnboardingDescription() {
        return onboardingDescription;
    }

    int getOnboardingImage() {
        return onboardingImage;
    }
}
