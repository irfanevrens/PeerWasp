package org.peerbox.view.tray;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.io.IOException;

import net.engio.mbassy.listener.Handler;

import org.peerbox.notifications.AggregatedFileEventStatus;
import org.peerbox.notifications.ITrayNotifications;
import org.peerbox.notifications.InformationNotification;
import org.peerbox.presenter.tray.TrayActionHandler;
import org.peerbox.presenter.tray.TrayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class JSystemTray extends AbstractSystemTray implements ITrayNotifications {

	private final static Logger logger = LoggerFactory.getLogger(JSystemTray.class);

	private String tooltip;
	private java.awt.TrayIcon trayIcon;
	private JTrayIcons iconProvider;
	private JTrayMenu menu;

	@Inject
	public JSystemTray(TrayActionHandler actionHandler) {
		super(actionHandler);
		this.iconProvider = new JTrayIcons();
		this.menu = new JTrayMenu(trayActionHandler);
		setTooltip("");
	}

	private TrayIcon create(Image image) throws IOException {
		TrayIcon trayIcon = new java.awt.TrayIcon(image);
		trayIcon.setImageAutoSize(true);
		trayIcon.setToolTip(tooltip);
		trayIcon.setPopupMenu(menu.create());
		return trayIcon;
	}

	@Override
	public void show() throws TrayException {
		try {
			trayIcon = create(iconProvider.getDefaultIcon());
			java.awt.SystemTray sysTray = java.awt.SystemTray.getSystemTray();
			sysTray.add(trayIcon);
		} catch (AWTException e) {
			logger.debug("SysTray AWTException.", e);
			logger.error("Could not initialize systray (tray may not be supported?)");
			throw new TrayException(e);
		} catch (IOException e) {
			logger.debug("SysTray.show IOException.", e);
			logger.error("Could not initialize systray (image not found?)");
			throw new TrayException(e);
		}
	}

	@Override
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
		if(trayIcon != null) {
			trayIcon.setToolTip(this.tooltip);
		}
	}

	@Override
	public void showDefaultIcon() throws TrayException {
		try {
			trayIcon.setImage(iconProvider.getDefaultIcon());
		} catch (IOException e) {
			logger.debug("SysTray.show IOException.", e);
			logger.error("Could not change icon (image not found?)");
			throw new TrayException(e);
		}
	}

	@Override
	public void showSyncingIcon() throws TrayException {
		try {
			trayIcon.setImage(iconProvider.getSyncingIcon());
		} catch (IOException e) {
			logger.debug("SysTray.show IOException.", e);
			logger.error("Could not change icon (image not found?)");
			throw new TrayException(e);
		}
	}

	@Override
	public void showInformationMessage(String title, String message) {
		System.out.println(title);
		if(trayIcon != null) {
			trayIcon.displayMessage(title, message, MessageType.INFO);
		}
	}

	/**
	 * NOTIFICATIONS - implementation of the ITrayNotifications interface
	 */

	@Override
	@Handler
	public void showInformation(InformationNotification in) {
		logger.debug("information message: [{}] - [{}]", in.getTitle(), in.getMessage());
		if(trayIcon != null) {
			trayIcon.displayMessage(in.getTitle(), in.getMessage(), MessageType.INFO);
		}
	}

	@Override
	@Handler
	public void showFileEvents(AggregatedFileEventStatus event) {
		String msg = generateAggregatedFileEventStatusMessage(event);
		logger.debug("Message received: \n[{}]", msg);
		trayIcon.displayMessage("File Synchronization", msg, MessageType.INFO);
	}

	private String generateAggregatedFileEventStatusMessage(AggregatedFileEventStatus e) {
		StringBuilder sb = new StringBuilder();
		if(e.getNumFilesAdded() > 0) {
			sb.append("Files added: ").append(e.getNumFilesAdded()).append("\n");
		}
		if(e.getNumFilesModified() > 0) {
			sb.append("Files modified: ").append(e.getNumFilesModified()).append("\n");
		}
		if(e.getNumFilesDeleted() > 0) {
			sb.append("Files deleted: ").append(e.getNumFilesDeleted());
		}
		return sb.toString();
	}
}
