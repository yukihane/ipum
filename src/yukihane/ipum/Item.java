package yukihane.ipum;

import java.io.File;

public class Item {

    private final File input;

    public Item(File file) {
        this.input = file;
    }

    File getInput() {
        return this.input;
    }
}
