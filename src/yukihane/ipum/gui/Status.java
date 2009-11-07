/* $Id$ */
package yukihane.ipum.gui;

/**
 *
 * @author yuki
 */
public class Status implements Cloneable {

    private State state = State.NOT_STARTED;
    private int progress = -1;

    public Status() {
    }

    private Status(State state, int progress) {
        this.state = state;
        this.progress = progress;
    }

    public enum State {

        NOT_STARTED, CONVERTING, DONE, FAIL;
    }

    public State getState() {
        return state;
    }

    public int getProgress() {
        return progress;
    }

    public void setState(State state) {
        this.state = state;
        if (state == State.CONVERTING && this.progress < 0) {
            this.progress = 0;
        }
    }

    public void setProgress(int progress) {
        if (progress < 0 || progress > 100) {
            throw new IllegalArgumentException();
        }

        if (progress == 100) {
            this.state = State.DONE;
        } else {
            this.progress = progress;
            this.state = State.CONVERTING;
        }
    }

    @Override
    public Object clone() {
        return new Status(this.state, this.progress);
    }
}
