/* $Id$ */
package yukihane.ipum;

import java.io.File;

public class Item {

    private final File input;
    private Status status = new Status();

    public Item(File file) {
        this.input = file;
    }

    File getInput() {
        return input;
    }

    public Status getStatus() {
        return (Status) status.clone();
    }

    public void setStatus(Status.State state) {
        status.setState(state);
    }

    public void setStatus(int state) {
        status.setProgress(state);
    }
}
