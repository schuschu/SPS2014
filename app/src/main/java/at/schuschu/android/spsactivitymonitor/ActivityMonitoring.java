package at.schuschu.android.spsactivitymonitor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;

import com.jjoe64.graphview.GraphView;

import org.jtransforms.fft.FloatFFT_1D;

import java.util.Collections;
import java.util.Vector;

/**
 * Created by shinji on 7/22/14.
 */
public class ActivityMonitoring implements SensorEventListener {

    public ACTIVITY getCur_activity() {
        return cur_activity;
    }

    public void setCur_activity(ACTIVITY cur_activity) {
        this.cur_activity = cur_activity;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public MODE getCur_mode() {
        return cur_mode;
    }

    public void setCur_mode(MODE cur_mode) {
        this.cur_mode = cur_mode;
    }

    public enum MODE {
        idle(0), training(1), testing(2);
        public final int value;

        private MODE(final int value) {
            this.value = value;
        }
    }

    public enum ACTIVITY {
        idle(0, "idle"), walking(1, "walking"), running(2, "running"), jumping(3, "jumping");
        public final int value;
        public static final int max = 4;
        public final String name;

        private ACTIVITY(final int value, final String name) {
            this.value = value;
            this.name = name;
        }
    }

    private MODE cur_mode;
    private ACTIVITY cur_activity;
    private final int AMP_THRESHOLD = 100;

    private float AxisX, AxisY, AxisZ;
    private final float ALPHA = 0.0f;
    private DataLinkedList acc_data;
    private long delta;
    int sample_size;
    private static final int window_size = 2000;
    private static final int training_ft_size = 20;
    private int cur_fts;
    private ActivityInterface observer;
    FloatFFT_1D fft_stuff;

    private Vector<Feature> training_set;

    public ActivityMonitoring(ActivityInterface observer) {

        AxisX = 0;
        AxisY = 0;
        AxisZ = 0;
        delta = 0;
        sample_size = 0;
        fft_stuff = null;
        this.observer = observer;

        setCur_mode(MODE.idle);
        acc_data = new DataLinkedList();
        training_set = new Vector<Feature>();

        cur_fts = 0;
    }

    public void onSensorChanged(SensorEvent event) {
        if (getCur_mode() == null || getCur_mode() == MODE.idle)
            return;

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        AxisX = (ALPHA * AxisX + (1 - ALPHA) * x) * 0.5f;
        AxisY = (ALPHA * AxisY + (1 - ALPHA) * y) * 0.5f;
        AxisZ = (ALPHA * AxisZ + (1 - ALPHA) * z) * 0.5f;

        acc_data.insert(new AccData(x, y, z, event.timestamp));
        float[][] data;
        if (sample_size == 0 && acc_data.getSize() == 10) {
            calcDelta(10);
            sample_size = ActivityMonitoring.window_size / (int) delta;
            fft_stuff = new FloatFFT_1D(sample_size * 2);
        }
        if (sample_size != 0 && acc_data.getSize() > sample_size) {
            data = getDataForFFT(sample_size);
            float[] variance = new float[3];
            float[] mean = new float[3];
            for (int i = 0; i < 3; i++) {
                mean[i] = calcMean(data[i], sample_size);
                variance[i] = calcVariance(data[i], sample_size, mean[i]);
            }
            fft_stuff.realForward((float[]) data[0]);
            fft_stuff.realForward((float[]) data[1]);
            fft_stuff.realForward((float[]) data[2]);
            int[] max_index = new int[3];
            float[] max_amp = new float[3];
            for (int i = 0; i < 3; i++) {
                max_index[i] = getMax(data[i], sample_size);
                max_amp[i] = data[i][max_index[i]];
//                min_amp[i] = data[i][getMin(data[i], sample_size)];
            }
            float[] freqs = new float[3];
            for (int i = 0; i < freqs.length; i++) {
                if (max_amp[i] > AMP_THRESHOLD) {
                    freqs[i] = max_index[i] * (1000.0f / delta) / (sample_size * 2);
                    if (freqs[i] > 20)
                        freqs[i] = -1;
                } else
                    freqs[i] = 0;
            }
            observer.onFrequencyChange(freqs);
            ACTIVITY act = null;
            if (getCur_mode() == MODE.training) {
                if (cur_fts < training_ft_size) {
                    Feature cur_feature = new Feature(max_index, max_amp, mean, variance, getCur_activity());
                    act = cur_feature.getActivity();
                    training_set.add(cur_feature);
                    cur_fts++;
                }
                if (cur_fts == training_ft_size) {
                    cur_fts = 0;
                    setCur_mode(MODE.idle);
                }
            } else if (getCur_mode() == MODE.testing) {
                Feature cur_feature = new Feature(max_index, max_amp, mean, variance, null);
                kNN(cur_feature, 3);
                setCur_activity(cur_feature.getActivity());
                act = cur_feature.getActivity();
            }
            observer.onActivityChange(act);
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

        t1 = acc_data.popFront().getTimestamp_();
        for (int i = 0; i < size - 1; i++) {
            t2 = acc_data.popFront().getTimestamp_();
            delta += (t2 - t1) / size;
            t1 = t2;
        }
        delta = delta / (1000000); // now we have ms...
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
            mean += data[i * 2];
        }
        return mean / (float) n;
    }


    public static float calcVariance(float[] data, int n) {
        float mean = calcMean(data, n);
        return calcVariance(data, n, mean);
    }

    public static float calcVariance(float[] data, int n, float mean) {
        float var = 0.0f;
        for (int i = 0; i < n; i++) {
            var += (data[2 * i] - mean) * (data[2 * i] - mean);
        }
        return var / (float) n;
    }

    private void kNN(Feature ft, int n) {
        int[] mode = new int[ACTIVITY.max];
        for (int i = 0; i < ACTIVITY.max; i++) {
            mode[i] = 0;
        }
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

    public void setTrainWalking() {
        setCur_mode(MODE.training);
        setCur_activity(ACTIVITY.walking);
        cur_fts = 0;
    }

    public void setTrainJumping() {
        setCur_mode(MODE.training);
        setCur_activity(ACTIVITY.jumping);
        cur_fts = 0;
    }

    public void setTrainStanding() {
        setCur_mode(MODE.training);
        setCur_activity(ACTIVITY.idle);
        cur_fts = 0;
    }

    public void setTrainRunning() {
        setCur_mode(MODE.training);
        setCur_activity(ACTIVITY.running);
        cur_fts = 0;
    }

    public void setTesting() {
        setCur_mode(MODE.testing);
    }

}
