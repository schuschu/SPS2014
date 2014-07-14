package at.schuschu.android.spsactivitymonitor;

import android.app.Activity;
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

public class GraphActivity extends Activity implements SensorEventListener {
    public enum MODE {
        idle(0), training(1), testing(2);
        public final int value;
        private MODE(final int value) {
            this.value = value;
        }
    };
    public enum ACTIVITY {
        idle(0, "idle"), walking(1, "walking"), running(2, "running"), jumping(3, "jumping");
        public final int value;
        public static final int max = 4;
        public final String name;
        private ACTIVITY(final int value, final String name) {
            this.value = value;
            this.name = name;
        }
    };
    private MODE cur_mode;
    private ACTIVITY cur_activity;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private float AxisX,AxisY,AxisZ;
    private final float ALPHA=0.0f;
    private DataLinkedList acc_data;
    private long delta;
    int sample_size;
    private static final int window_size = 200;
    private static final int training_ft_size = 5;
    private int cur_fts;
    FloatFFT_1D fft_stuff;

    GraphView fft_result;

    private Vector<Feature> training_set;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        AxisX=0;
        AxisY=0;
        AxisZ=0;
        delta = 0;
        int sample_size = 0;
        fft_stuff = null;

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        acc_data = new DataLinkedList();
        training_set = new Vector<Feature>();
        cur_mode = MODE.idle;
        cur_fts = 0;

    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    }
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
// can be safely ignored for this demo
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

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (cur_mode == MODE.idle)
            return;

        TextView tvX= (TextView)findViewById(R.id.x_axis);
        TextView tvY= (TextView)findViewById(R.id.y_axis);
        TextView tvZ= (TextView)findViewById(R.id.z_axis);
      //  TextView tvO= (TextView)findViewById(R.id.overflowtext);


        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        AxisX=(ALPHA * AxisX + (1-ALPHA) * x)*0.5f;
        AxisY=(ALPHA * AxisY + (1-ALPHA) * y)*0.5f;
        AxisZ=(ALPHA * AxisZ + (1-ALPHA) * z)*0.5f;

        tvX.setText(Float.toString(AxisX));
        tvY.setText(Float.toString(AxisY));
        tvZ.setText(Float.toString(AxisZ));

//        AccData last = acc_data.getEnd_();
//        if (last != null) {
//            long delta = event.timestamp - last.getTimestamp_();
//            tvO.setText("Delta: " + delta);
//        }
        acc_data.insert(new AccData(x,y,z,event.timestamp));
        float[][] data;
        if (sample_size == 0 && acc_data.getSize() == 10) {
            calcDelta(10);
            sample_size = GraphActivity.window_size/(int)delta;
            fft_stuff = new FloatFFT_1D(sample_size);
        }
        if (sample_size != 0 && acc_data.getSize() > sample_size) {
            data = getDataForFFT(sample_size);
            float[] variance = new float[3];
            float[] mean = new float[3];
            for (int i = 0; i < 3; i++) {
                mean[i] = calcMean(data[i], sample_size);
                variance[i] = calcVariance(data[i], sample_size, mean[i]);
            }
            fft_stuff.realForward((float[])data[0]);
            fft_stuff.realForward((float[])data[1]);
            fft_stuff.realForward((float[])data[2]);
            int[] max_index = new int[3];
            float[] max_amp = new float[3];
            for (int i = 0; i < 3; i++) {
                max_index[i] = getMax(data[i], sample_size);
                max_amp[i] = data[i][max_index[i]];
 //               min_amp[i] = data[i][getMin(data[i], sample_size)];
            }
            if (cur_mode == MODE.training) {
                if (cur_fts < training_ft_size) {
                    training_set.add(new Feature(max_index, max_amp, mean, variance, cur_activity));
                    cur_fts++;
                }
                if (cur_fts == training_ft_size) {
                    cur_fts = 0;
                    cur_mode = MODE.idle;
                }
                //TODO we need buttons, BUTTONS
            } else if(cur_mode == MODE.testing) {
                Feature cur_feature = new Feature(max_index, max_amp, mean, variance, null);
                kNN(cur_feature, 3);
                TextView test_result = (TextView) findViewById(R.id.testingResult);
                test_result.setText("you are currently " + cur_feature.getActivity().name);
                //TODO we need buttons + output for classification

            }
   //         GraphViewData g_data[][] = new GraphViewData[3][sample_size/2];


//            for (int i = 0; i < sample_size/2; i++) {
//                g_data[0][i] = new GraphViewData((float)i,(float)data[0][i]);
//                g_data[1][i] = new GraphViewData((float)i,(float)data[1][i]);
//                g_data[2][i] = new GraphViewData((float)i,(float)data[2][i]);
//            }
//            GraphViewSeries seriesx = new GraphViewSeries(g_data[0]);
//            GraphViewSeries seriesy = new GraphViewSeries(g_data[1]);
//            GraphViewSeries seriesz = new GraphViewSeries(g_data[2]);


//            if (fft_result == null) {
//                fft_result = new BarGraphView(this, "x-axis of awesome");
//                RelativeLayout rel_layout = (RelativeLayout) findViewById(R.id.RelLayourt);
//                rel_layout.addView(fft_result);
//
//            }
//            fft_result.removeAllSeries();
//            fft_result.addSeries(seriesx);
//            fft_result.addSeries(seriesy);
//            fft_result.addSeries(seriesz);

        }




    }

    public float[][] getDataForFFT(int len) {

        int size = acc_data.getSize();
        if (size > len)
            size = len;

        float data_array[][] = new float[3][size * 3];
        AccData tmp;
        for (int i = 0; i < size; i++) {
            tmp = acc_data.popFront();
            if (tmp == null)
                break;
            data_array[0][2 * i] = tmp.getX_axis_();
            data_array[1][2 * i] = tmp.getY_axis_();
            data_array[2][2 * i] = tmp.getZ_axis_();
        }

        return data_array;
    }

    private void calcDelta(int size) {
        long t1 = 0;
        long t2 = 0;

        AccData tmp;
        t1 = acc_data.popFront().getTimestamp_();
        for (int i = 0; i < size - 1; i++) {
             t2 = acc_data.popFront().getTimestamp_();
            delta = (t2 - t1)/size;
            t1 = t2;
        }
        delta = delta/(1000000); // now we have ms...
    }

    public static int getMax(float[] data, int n) {
        int max_index = -1;
        float max = 0.0f;
        for (int i = 0; i < n; i++) {
            if (max_index == -1) {
                max = data[i];
                max_index = i;
            }
            if (data[i] > max) {
                max = data[i];
                max_index = i;
            }
        }
        return max_index;
    }

    public static int getMin(float[] data, int n) {
        int max_index = -1;
        float max = 0.0f;
        for (int i = 0; i < n; i++) {
            if (max_index == -1) {
                max = data[i];
                max_index = i;
            }
            if (data[i] < max) {
                max = data[i];
                max_index = i;
            }
        }
        return max_index;
    }

    public static float calcMean(float[] data, int n) {
        float mean = 0.0f;
        for (int i = 0; i < n; i++) {
            mean += data[i];
        }
        return mean/(float)n;
    }


    public static float calcVariance(float[]data, int n) {
        float mean = calcMean(data, n);
        return calcVariance(data, n, mean);
    }
    public static float calcVariance(float[] data, int n, float mean) {
        float var = 0.0f;
        for (int i = 0; i < n; i++) {
            var += (data[i] - mean) * (data[i] - mean);
        }
        return var/(float)n;
    }

    private void kNN(Feature ft, int n) {
        int[] mode = new int[ACTIVITY.max];
        int max = -1;
        ACTIVITY max_act = ACTIVITY.idle;

        for (int i = 0; i < training_set.size(); i++) {
            training_set.elementAt(i).calcDistance(ft);
        }
        Collections.sort(training_set);
        for (int i = 0; i < n; i++) {
            if (max < ++(mode[training_set.elementAt(i).getActivity().value])) {
                max = mode[training_set.elementAt(i).getActivity().value];
                max_act = training_set.elementAt(i).getActivity();
            }
        }
        ft.setActivity(max_act);

    }

    public void setTrainWalking(View view) {
        cur_mode = MODE.training;
        cur_activity = ACTIVITY.walking;
        cur_fts = 0;
    }
    public void setTrainJumping(View view) {
        cur_mode = MODE.training;
        cur_activity = ACTIVITY.jumping;
        cur_fts = 0;
    }
    public void setTrainStanding(View view) {
        cur_mode = MODE.training;
        cur_activity = ACTIVITY.idle;
        cur_fts = 0;
    }
    public void setTrainRunning(View view) {
        cur_mode = MODE.training;
        cur_activity = ACTIVITY.running;
        cur_fts = 0;
    }

    public void setTesting(View view) {
        cur_mode = MODE.testing;
    }
}
