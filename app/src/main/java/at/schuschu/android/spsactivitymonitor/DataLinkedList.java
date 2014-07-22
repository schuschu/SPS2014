package at.schuschu.android.spsactivitymonitor;


class DataLinkedList {
    private AccData begin_;
    private AccData end_;

    private int size;

    public DataLinkedList(AccData begin) {
        setBegin_(begin);
        setEnd_(begin);
        begin.setNext_(null);
        setSize(1);
    }

    public DataLinkedList() {
        setBegin_(null);
        setEnd_(null);
        setSize(0);
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
        setSize(getSize() + 1);
    }

    public AccData getEnd_() {
        return end_;
    }

    public void setEnd_(AccData end_) {
        this.end_ = end_;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public AccData popFront() {
        if (getBegin_() == null)
            return null;
        AccData ret_val = getBegin_();
        setBegin_(getBegin_().getNext_());
        if (getBegin_() == null)
            setEnd_(null);
        setSize(getSize() - 1);
        return ret_val;
    }

    public float[][] getDataForFFT(int len) {
        int cur_size = size;
        if (cur_size > len)
            cur_size = len;

        float data_array[][] = new float[3][cur_size * 3];
        AccData tmp;
        for (int i = 0; i < cur_size; i++) {
            tmp = popFront();
            if (tmp == null)
                break;
            data_array[0][2 * i] = tmp.getX_axis_();
            data_array[1][2 * i] = tmp.getY_axis_();
            data_array[2][2 * i] = tmp.getZ_axis_();
        }

        return data_array;
    }


}