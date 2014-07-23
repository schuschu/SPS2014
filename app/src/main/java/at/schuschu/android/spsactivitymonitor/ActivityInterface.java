package at.schuschu.android.spsactivitymonitor;

/**
 * Created by shinji on 7/22/14.
 */
public interface ActivityInterface {
    public void onActivityChange(ActivityMonitoring.ACTIVITY activity);
    public void onFrequencyChange(float[] frequency);
}
