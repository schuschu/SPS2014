package at.schuschu.android.spsactivitymonitor;

/**
 * Created by shinji on 7/14/2014.
 */
public class Feature implements Comparable {
    private int[] index;

    private float[] min_amp;
    private float[] max_amp;
    private float[] mean;
    private float[] variance;

    private float distance;

    private ActivityMonitoring.ACTIVITY activity;

    public Feature() {
        setIndex(null);
        setMin_amp(null);
        setMax_amp(null);
        setVariance(null);
        setMean(null);
    }

    public Feature(int[] index, float[] max_amp, float[] mean, float[] variance, ActivityMonitoring.ACTIVITY activity) {
        setIndex(index);
        setMax_amp(max_amp);
        setMean(mean);
        setVariance(variance);
        setMin_amp(null);
        setActivity(activity);
    }

    public int[] getIndex() {
        return index;
    }

    public void setIndex(int[] index) {
        this.index = index;
    }

    public float[] getMin_amp() {
        return min_amp;
    }

    public void setMin_amp(float[] min_amp) {
        this.min_amp = min_amp;
    }

    public float[] getMax_amp() {
        return max_amp;
    }

    public void setMax_amp(float[] max_amp) {
        this.max_amp = max_amp;
    }

    public float[] getMean() {
        return mean;
    }

    public void setMean(float[] mean) {
        this.mean = mean;
    }

    public void setVariance(float[] variance) {
        this.variance = variance;
    }

    public float[] getVariance() {
        return variance;
    }

    public ActivityMonitoring.ACTIVITY getActivity() {
        return activity;
    }

    public void setActivity(ActivityMonitoring.ACTIVITY activity) {
        this.activity = activity;
    }

    public float calcDistance(Feature other) {
        float ret_val = 0.0f;
        for (int i = 0; i < 3; i++) {
//            ret_val += Math.pow((double)(other.getIndex()[i]) - (double)(getIndex()[i]), 2.0);
//            ret_val += Math.pow((double)(other.getMax_amp()[i]) - (double)(getMax_amp()[i]), 2.0);
//            ret_val += Math.pow((double)(other.getMean()[i]) - (double)(getMean()[i]), 2.0);
            ret_val += Math.pow((double)(other.getVariance()[i]) - (double)(getVariance()[i]), 2.0);
//            ret_val += Math.pow((double)(other.getActivity()[i]) - (double)(getActivity()[i]), 2.0);
        }

        setDistance((float)Math.sqrt(ret_val));
        return getDistance();
    }

    @Override
    public int compareTo(Object another) {
        return (-1) * Float.compare(((Feature)another).getDistance(), getDistance());
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }
}
