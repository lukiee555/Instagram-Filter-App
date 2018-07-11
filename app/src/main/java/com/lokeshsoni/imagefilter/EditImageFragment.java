package com.lokeshsoni.imagefilter;


import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.lokeshsoni.imagefilter.Interface.EditImageFragmentListner;


public class EditImageFragment extends BottomSheetDialogFragment implements SeekBar.OnSeekBarChangeListener {

    private EditImageFragmentListner listner;
    SeekBar seekbar_brightness, seekbar_constrant,seekbar_saturation;

    static EditImageFragment instance;

    public static EditImageFragment getInstance() {
        if(instance == null)
            instance = new EditImageFragment();
        return instance;
    }

    public void setListner(EditImageFragmentListner listner) {
        this.listner = listner;
    }

    public EditImageFragment() {
        // Required empty public constructor
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView =  inflater.inflate(R.layout.fragment_edit_image, container, false);

        seekbar_brightness = itemView.findViewById(R.id.seekbar_brightness);
        seekbar_constrant = itemView.findViewById(R.id.seekbar_constraint);
        seekbar_saturation = itemView.findViewById(R.id.seekbar_saturation);

        seekbar_brightness.setMax(200);
        seekbar_brightness.setProgress(100);

        seekbar_constrant.setMax(20);
        seekbar_constrant.setProgress(0);

        seekbar_saturation.setMax(30);
        seekbar_saturation.setProgress(10);

        seekbar_saturation.setOnSeekBarChangeListener(this);
        seekbar_constrant.setOnSeekBarChangeListener(this);
        seekbar_brightness.setOnSeekBarChangeListener(this);

        return itemView;

    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if(listner != null){
            if(seekBar.getId() == R.id.seekbar_brightness){
                listner.onBrightnessChanged(progress - 100);
            }else if(seekBar.getId() == R.id.seekbar_constraint){
                progress+=10;
                float value = .10f*progress;
                listner.onConstrantChanged(value);
            }else if(seekBar.getId() == R.id.seekbar_saturation){

                float value = .10f*progress;
                listner.onSaturationChanged(value);
            }
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

        if(listner != null)
            listner.onEditStarted();

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if(listner != null)
            listner.onEditComplete();

    }

    public void  resetControls(){
        seekbar_brightness.setProgress(100);
        seekbar_constrant.setProgress(0);
        seekbar_saturation.setProgress(10);
    }
}
