package com.oolcay.magicDecide;

import android.app.ListActivity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends ListActivity  {

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private ShakeListener mShakeListener;
    private ArrayList<String> mChoiceList = new ArrayList<String>();
    private EditText mEditText;
    private ArrayAdapter<String> arrayAdapter;
    private Vibrator mVibrator;

    public static final int VIBRATE_LENGTH = 500; //milli

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if(mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            setupSensor();
        }else{
            displaySensorNotFound();
        }

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mChoiceList);
        setListAdapter(arrayAdapter);

        setupEditTextListener();
    }

    @Override
    public void onResume(){
        super.onResume();
        mSensorManager.registerListener(mShakeListener, mSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause(){
        super.onPause();
        mSensorManager.unregisterListener(mShakeListener);
    }

    private void handleEvent(){

        int numberChoices = mChoiceList.size();

        if(numberChoices > 1){
            int min = 0;
            int max = numberChoices;

            Random r = new Random();
            int i1 = r.nextInt(max - min) + min;

            mChoiceList.remove(i1);
            arrayAdapter.notifyDataSetChanged();

            if (mChoiceList.size() == 1)    //vibrate when done
                vibrate();
        }
    }

    private void setupSensor(){
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeListener = new ShakeListener(new ShakeInterface() {
            @Override
            public void shakeDetected() {
                handleEvent();
            }
        });
    }

    private void displaySensorNotFound(){
        Context context = getApplicationContext();
        Toast.makeText(context, getString(R.string.shake_sensor_error), Toast.LENGTH_LONG);
    }

    private void addChoice(){
        mChoiceList.add(mEditText.getText().toString());
        mEditText.setText("");
    }

    private void vibrate(){
        if (mVibrator.hasVibrator())
            mVibrator.vibrate(VIBRATE_LENGTH);
    }

    private void setupEditTextListener(){
        mEditText = (EditText) findViewById(R.id.choiceEditText);

        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == KeyEvent.KEYCODE_ENTER) {
                    InputMethodManager inputMethodManager = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    addChoice();
                    return true;
                }
                return false;
            }
        });
    }
}