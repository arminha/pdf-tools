package com.aha.pdftools.gui;

import java.awt.Component;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import com.aha.pdftools.Messages;

public abstract class ReportingWorker<T, V> extends SwingWorker<T, V> {
	private final Component parentComponent;
	public ReportingWorker(Component parentComponent) {
		this.parentComponent = parentComponent;
	}

	@Override
	protected void done() {
		try {
			get();
		} catch (InterruptedException e) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, null, e);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();
			Logger.getLogger(getClass().getName()).log(Level.WARNING, null, cause);
			showErrorMessage(cause);
		}
	}

	protected void showErrorMessage(Throwable throwable) {
		String message = throwable.getMessage();
		JOptionPane.showMessageDialog(parentComponent, message, Messages.getString("PermissionManager.Error"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
	}
}
