package at.schuschu.android.spsactivitymonitor;

/**
 * Created by shinji on 7/22/14.
 */
public class Particle {
    private Particle parent;
    private float x;
    private float y;

    public Particle(float x, float y) {
        setX(x);
        setY(y);
        parent = null;
    }

    public Particle(float x, float y, Particle parent) {
        setX(x);
        setY(y);
        setParent(parent);
    }

    public Particle getParent() {
        return parent;
    }

    public void setParent(Particle parent) {
        this.parent = parent;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
