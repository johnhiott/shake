package com.oolcay.magicDecide;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class ShakeListener implements SensorEventListener {

    private float[] mGravity = { 0.0f, 0.0f, 0.0f };
    private float[] mLinearAcceleration = { 0.0f, 0.0f, 0.0f };
    private int mNumShakes = 0;
    private long mFirstShake = 0;


    private ShakeInterface mShakeInterface;

    public static final int X = 0;
    public static final int Y = 1;
    public static final int Z = 2;
    public static final int MIN_ACCELERATION = 4;
    public static final long MAX_SHAKE_SEPARATION = 500; //milli
    public static final int NUM_OF_SHAKES = 2;

    public ShakeListener(ShakeInterface shakeInterface){
        mShakeInterface = shakeInterface;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        removeGravity(event);

        //detect if the shake was hard enough
        if (mLinearAcceleration[Y] > MIN_ACCELERATION){

            long now = System.currentTimeMillis();

            if (mFirstShake == 0){
                mFirstShake = now;
            }

            long timeBetweenShakes = now - mFirstShake;

            if (timeBetweenShakes > MAX_SHAKE_SEPARATION){
                reset();
            }else{
                mNumShakes++;

                if (mNumShakes > NUM_OF_SHAKES){
                    mShakeInterface.shakeDetected();
                    reset();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //left blank
    }

    private void reset(){
        mFirstShake = 0;
        mNumShakes = 0;
    }

    private void removeGravity(SensorEvent event){

        //Code below found at
        //http://developer.android.com/guide/topics/sensors/sensors_motion.html

        final float alpha = 0.8f;

        // Isolate the force of gravity with the low-pass filter.
        mGravity[0] = alpha * mGravity[0] + (1 - alpha) * event.values[0];
        mGravity[1] = alpha * mGravity[1] + (1 - alpha) * event.values[1];
        mGravity[2] = alpha * mGravity[2] + (1 - alpha) * event.values[2];

        // Remove the gravity contribution with the high-pass filter.
        mLinearAcceleration[0] = event.values[0] - mGravity[0];
        mLinearAcceleration[1] = event.values[1] - mGravity[1];
        mLinearAcceleration[2] = event.values[2] - mGravity[2];

    }

}