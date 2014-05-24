/*
 * Copyright (C) 2012  Armin Häberling
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

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
