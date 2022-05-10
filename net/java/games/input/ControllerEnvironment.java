package net.java.games.input;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEvent;
import net.java.games.input.ControllerListener;
import net.java.games.input.DefaultControllerEnvironment;

public abstract class ControllerEnvironment {
    private static ControllerEnvironment defaultEnvironment;
    protected final ArrayList controllerListeners = new ArrayList();
    static final /* synthetic */ boolean $assertionsDisabled;

    static void logln(String msg) {
        ControllerEnvironment.log(msg + "\n");
    }

    static void log(String msg) {
        Logger.getLogger(ControllerEnvironment.class.getName()).info(msg);
    }

    protected ControllerEnvironment() {
    }

    public abstract Controller[] getControllers();

    public void addControllerListener(ControllerListener l2) {
        if (!$assertionsDisabled && l2 == null) {
            throw new AssertionError();
        }
        this.controllerListeners.add(l2);
    }

    public abstract boolean isSupported();

    public void removeControllerListener(ControllerListener l2) {
        if (!$assertionsDisabled && l2 == null) {
            throw new AssertionError();
        }
        this.controllerListeners.remove(l2);
    }

    protected void fireControllerAdded(Controller c2) {
        ControllerEvent ev2 = new ControllerEvent(c2);
        Iterator it2 = this.controllerListeners.iterator();
        while (it2.hasNext()) {
            ((ControllerListener)it2.next()).controllerAdded(ev2);
        }
    }

    protected void fireControllerRemoved(Controller c2) {
        ControllerEvent ev2 = new ControllerEvent(c2);
        Iterator it2 = this.controllerListeners.iterator();
        while (it2.hasNext()) {
            ((ControllerListener)it2.next()).controllerRemoved(ev2);
        }
    }

    public static ControllerEnvironment getDefaultEnvironment() {
        return defaultEnvironment;
    }

    static {
        $assertionsDisabled = !ControllerEnvironment.class.desiredAssertionStatus();
        defaultEnvironment = new DefaultControllerEnvironment();
    }
}

