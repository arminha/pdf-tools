package com.aha.pdftools.gui;

import java.awt.Component;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aha.pdftools.Messages;

public abstract class ReportingWorker<T, V> extends SwingWorker<T, V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportingWorker.class);

    private final Component parentComponent;

    public ReportingWorker(Component parentComponent) {
        this.parentComponent = parentComponent;
    }

    @Override
    protected void done() {
        try {
            get();
        } catch (InterruptedException e) {
            LOGGER.warn(e.getMessage(), e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            LOGGER.warn(cause.getMessage(), cause);
            showErrorMessage(cause);
        }
    }

    protected void showErrorMessage(Throwable throwable) {
        String message = throwable.getMessage();
        JOptionPane.showMessageDialog(parentComponent, message,
                Messages.getString("PermissionManager.Error"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
    }
}
