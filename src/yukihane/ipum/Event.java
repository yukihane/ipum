/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package yukihane.ipum;

import java.io.File;
import yukihane.ipum.gui.Status;

/**
 *
 * @author yuki
 */
public class Event {

    private final File file;
    private final Status status;

    public File getFile() {
        return file;
    }

    public Status getStatus() {
        return status;
    }

    Event(File file, Status status) {
        this.file = file;
        this.status = status;
    }
}
