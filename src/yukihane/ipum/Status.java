/* $Id$ */
package yukihane.ipum;

/**
 *
 * @author yuki
 */
public class Status {

    private State state = State.NOT_STARTED;
    private int progress = -1;

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
        this.progress = progress;
        this.state = State.CONVERTING;
    }
}
