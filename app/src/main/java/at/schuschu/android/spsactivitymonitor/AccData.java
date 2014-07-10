package at.schuschu.android.spsactivitymonitor;



class AccData {
    private float x_axis_;
    private float y_axis_;
    private float z_axis_;

    private long timestamp_;

    private AccData next_;

    public AccData(float x_axis, float y_axis, float z_axis, long timestamp) {
        setX_axis_(x_axis);
        setY_axis_(y_axis);
        setZ_axis_(z_axis);
        setTimestamp_(timestamp);
        setNext_(null);
    }

    public float getX_axis_() {
        return x_axis_;
    }

    public void setX_axis_(float x_axis_) {
        this.x_axis_ = x_axis_;
    }

    public float getY_axis_() {
        return y_axis_;
    }

    public void setY_axis_(float y_axis_) {
        this.y_axis_ = y_axis_;
    }

    public float getZ_axis_() {
        return z_axis_;
    }

    public void setZ_axis_(float z_axis_) {
        this.z_axis_ = z_axis_;
    }

    public long getTimestamp_() {
        return timestamp_;
    }

    public void setTimestamp_(long timestamp_) {
        this.timestamp_ = timestamp_;
    }

    public AccData getNext_() {
        return next_;
    }

    public void setNext_(AccData next_) {
        this.next_ = next_;
    }
}