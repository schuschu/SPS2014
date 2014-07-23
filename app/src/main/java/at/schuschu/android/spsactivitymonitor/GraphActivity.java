package at.schuschu.android.spsactivitymonitor;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.content.Context;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;

import org.jtransforms.fft.FloatFFT_1D;

import java.util.Collections;
import java.util.Vector;

import static com.jjoe64.graphview.GraphView.GraphViewData;

public class GraphActivity extends Activity implements ActivityInterface {
    @Override
    public void onActivityChange(ActivityMonitoring.ACTIVITY activity) {
        //TODO present our shit
        TextView test_result = (TextView) findViewById(R.id.testingResult);
        if (monitoring.getCur_mode() == ActivityMonitoring.MODE.testing) {

            test_result.setText("you are currently " + activity.name);
        } else if (monitoring.getCur_mode() == ActivityMonitoring.MODE.training) {
            test_result.setText("you are currently training " + activity.name);
        } else {
            test_result.setText("You are currently doing nothing, neither training nor testing");
        }
    }

    @Override
    public void onFrequencyChange(float frequency) {
        TextView textView = (TextView) findViewById(R.id.frequResult);
        textView.setText("Frequency bin: "+frequency);

    }



    private SensorManager mSensorManager;
    private Sensor mAccelerometer;



    ActivityMonitoring monitoring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        monitoring = new ActivityMonitoring(this);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(monitoring, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);



    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(monitoring, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    }
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(monitoring);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.graph, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    public void setTrainWalking(View view) {
        monitoring.setTrainWalking();
    }
    public void setTrainJumping(View view) {
        monitoring.setTrainJumping();
    }
    public void setTrainStanding(View view) {
        monitoring.setTrainStanding();
    }
    public void setTrainRunning(View view) {
        monitoring.setTrainRunning();
    }


    public void launchParticlePower(View view) {
        Intent intent = new Intent(this, ParticlePower.class);
        startActivity(intent);
    }
    public void setTesting(View view) {
        monitoring.setTesting();
    }
}
