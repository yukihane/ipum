package yukihane.ipum;

import java.io.File;

public class Item {

    private final File input;
    private int progress = -1;

    public Item(File file) {
        this.input = file;
    }

    void setProgress(int i) {
        progress = i;
    }

    File getInput() {
        return input;
    }

    int getProgress() {
        return progress;
    }
}
