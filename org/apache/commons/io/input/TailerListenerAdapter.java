package org.apache.commons.io.input;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;

public class TailerListenerAdapter
implements TailerListener {
    @Override
    public void init(Tailer tailer) {
    }

    @Override
    public void fileNotFound() {
    }

    @Override
    public void fileRotated() {
    }

    @Override
    public void handle(String line) {
    }

    @Override
    public void handle(Exception ex2) {
    }

    public void endOfFileReached() {
    }
}

