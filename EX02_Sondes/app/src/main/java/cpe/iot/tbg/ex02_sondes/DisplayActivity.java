package cpe.iot.tbg.ex02_sondes;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.Console;
import java.util.Date;

public class DisplayActivity extends AppCompatActivity implements SensorEventListener {

    SensorManager sm;
    TextView tv_values;
    TextView tv_datetime;
    TextView tv_prox_values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        tv_values = findViewById(R.id.tv_values);
        tv_datetime = findViewById(R.id.tv_datetime);
        tv_prox_values = findViewById(R.id.tv_prox_values);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);
        //sm = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        sm.registerListener(
                this,
                sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI
        );
        sm.registerListener(
                this,
                sm.getDefaultSensor(Sensor.TYPE_PROXIMITY),
                SensorManager.SENSOR_DELAY_UI
        );

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        switch(event.sensor.getType())
        {
            case Sensor.TYPE_ACCELEROMETER:
                refreshAccelerometerValues(event.values);
                break;

            case Sensor.TYPE_PROXIMITY:
                refreshProximityValues(event.values);
                break;

            default:
                break;
        }

        onAccuracyChanged(event.sensor, event.accuracy);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        Date dt = new Date();
        tv_datetime.setText(getString(R.string.txt_datetime, dt));
    }

    private void refreshAccelerometerValues(float values[]) {

        // alpha is calculated as t / (t + dT)
        // with t, the low-pass filter's time-constant
        // and dT, the event delivery rate

        final double alpha = 0.8;
        double gravity[] = {0,0,0};
        double linear_acceleration[] = {0,0,0};

        gravity[0] = alpha * gravity[0] + (1 - alpha) * values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * values[2];

        linear_acceleration[0] = values[0] - gravity[0];
        linear_acceleration[1] = values[1] - gravity[1];
        linear_acceleration[2] = values[2] - gravity[2];

        Log.d(String.valueOf(linear_acceleration[0]), "onSensorChanged V1 : ");
        Log.d(String.valueOf(linear_acceleration[1]), "onSensorChanged V2 : ");
        Log.d(String.valueOf(linear_acceleration[2]), "onSensorChanged V3 : ");

        tv_values.setText(getString(
                R.string.txt_values,
                linear_acceleration[0],
                linear_acceleration[1],
                linear_acceleration[2])
        );
    }

    private void refreshProximityValues(float values[])
    {
        tv_prox_values.setText(getString(R.string.txt_prox_values, values[0]));
    }

}
