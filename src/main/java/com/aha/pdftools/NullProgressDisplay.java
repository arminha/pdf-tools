package com.aha.pdftools;

public class NullProgressDisplay implements ProgressDisplay {

    @Override
    public void startTask(String message, int max, boolean cancelable) {
    }

    @Override
    public void setProgress(int nv) {
    }

    @Override
    public void setNote(String note) {
    }

    @Override
    public boolean isCanceled() {
        return false;
    }

    @Override
    public void endTask() {
    }

}
