package at.schuschu.android.spsactivitymonitor;
import at.schuschu.android.spsactivitymonitor.AccData;


class DataLinkedList {
    private AccData begin_;
    private AccData end_;

    private int size;

    public DataLinkedList(AccData begin) {
        setBegin_(begin);
        setEnd_(begin);
        begin.setNext_(null);
        size = 1;
    }

    public DataLinkedList() {
        setBegin_(null);
        setEnd_(null);
        size = 0;
    }

    public AccData getBegin_() {
        return begin_;
    }

    public void setBegin_(AccData begin_) {
        this.begin_ = begin_;
    }

    public void insert(AccData next) {
        if (getEnd_() != null) {
            getEnd_().setNext_(next);
            setEnd_(next);
            getEnd_().setNext_(null);
        } else {
            setBegin_(next);
            setEnd_(next);
        }
        size++;
    }

    public AccData getEnd_() {
        return end_;
    }

    public void setEnd_(AccData end_) {
        this.end_ = end_;
    }
}