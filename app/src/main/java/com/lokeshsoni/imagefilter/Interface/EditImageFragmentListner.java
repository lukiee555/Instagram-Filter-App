package com.lokeshsoni.imagefilter.Interface;

public interface EditImageFragmentListner {

    void onBrightnessChanged (int brightness);
    void onSaturationChanged(float saturation);
    void onConstrantChanged(float constrant);
    void onEditStarted();
    void onEditComplete();

}
