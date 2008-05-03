/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2008 Grigor Iliev <grigor@grigoriliev.com>
 *
 *   This file is part of JSampler.
 *
 *   JSampler is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License version 2
 *   as published by the Free Software Foundation.
 *
 *   JSampler is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with JSampler; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *   MA  02111-1307  USA
 */

package org.jsampler.view.fantasia;

import java.awt.Dimension;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.Timer;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sf.juife.Dial;
import net.sf.juife.JuifeUtils;

import org.jsampler.CC;
import org.jsampler.HF;
import org.jsampler.SamplerChannelModel;

import org.jvnet.substance.SubstanceImageCreator;

import org.linuxsampler.lscp.SamplerChannel;
import org.linuxsampler.lscp.SamplerEngine;

import static org.jsampler.view.fantasia.FantasiaI18n.i18n;
import static org.jsampler.view.fantasia.FantasiaPrefs.*;
import static org.jsampler.view.fantasia.FantasiaUtils.*;

/**
 *
 * @author Grigor Iliev
 */
public class NormalChannelView extends JPanel implements ChannelView {
	private final Channel channel;
	private final ChannelOptionsView channelOptionsView;
	
	private final EnhancedDial dialVolume = new EnhancedDial();
	private final ChannelScreen screen;
	
	private final Channel.PowerButton btnPower;
	private final MuteButton btnMute = new MuteButton();
	private final SoloButton btnSolo = new SoloButton();
	private final Channel.OptionsButton btnOptions;
	
	
	/** Creates a new instance of <code>NormalChannelView</code> */
	public
	NormalChannelView(Channel channel) {
		this(channel, new NormalChannelOptionsView(channel));
	}
	
	/** Creates a new instance of <code>NormalChannelView</code> */
	public
	NormalChannelView(Channel channel, ChannelOptionsView channelOptionsView) {
		this.channel = channel;
		this.channelOptionsView = channelOptionsView;
		
		addMouseListener(channel.getContextMenu());
		
		btnPower = new Channel.PowerButton(channel);
		btnOptions = new Channel.OptionsButton(channel);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		screen = new ChannelScreen(channel);
		ChannelPane p = new ChannelPane();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		
		//p.add(Box.createRigidArea(new Dimension(3, 0)));
		
		btnPower.setAlignmentY(JPanel.TOP_ALIGNMENT);
		
		JPanel tb = new JPanel();
		tb.setBorder(BorderFactory.createEmptyBorder(3, 3, 0, 4));
		tb.setLayout(new BoxLayout(tb, BoxLayout.X_AXIS));
		tb.setOpaque(false);
		tb.setAlignmentY(JPanel.TOP_ALIGNMENT);
		tb.add(btnPower);
		tb.setPreferredSize(new Dimension(tb.getPreferredSize().width, 58));
		tb.setMinimumSize(tb.getPreferredSize());
		tb.setMaximumSize(tb.getPreferredSize());
		p.add(tb);
		
		//p.add(Box.createRigidArea(new Dimension(4, 0)));
		
		p.add(createVSeparator());
		
		//p.add(Box.createRigidArea(new Dimension(3, 0)));
		
		JPanel p2 = new JPanel();
		p2.setOpaque(false);
		p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
		p2.setAlignmentY(JPanel.TOP_ALIGNMENT);
		p2.setBorder(BorderFactory.createEmptyBorder(5, 3, 0, 2));
		p2.add(screen);
		p.add(p2);
		
		p.add(createVSeparator());
		
		p2 = new JPanel();
		p2.setOpaque(false);
		p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
		p2.setAlignmentY(JPanel.TOP_ALIGNMENT);
		p2.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
		p2.add(new JLabel(Res.gfxMuteTitle));
		p2.add(btnMute);
		p2.add(new JLabel(Res.gfxSoloTitle));
		p2.add(btnSolo);
		
		p.add(p2);
		
		p.add(createVSeparator());
		
		p2 = new JPanel();
		p2.setOpaque(false);
		p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
		p2.setAlignmentY(JPanel.TOP_ALIGNMENT);
		p2.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
		JLabel l = new JLabel(Res.gfxVolumeTitle);
		l.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		l.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
		p2.add(l);
		dialVolume.setDialPixmap(Res.gfxVolumeDial, 30, 330);
		dialVolume.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		p2.add(dialVolume);
		p.add(p2);
		
		p.add(createVSeparator());
		
		p2 = new JPanel();
		p2.setOpaque(false);
		p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
		p2.setAlignmentY(JPanel.TOP_ALIGNMENT);
		p2.setBorder(BorderFactory.createEmptyBorder(27, 0, 0, 0));
		l = new JLabel(Res.gfxOptionsTitle);
		l.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		l.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
		p2.add(l);
		
		p2.add(Box.createRigidArea(new Dimension(0, 3)));
		
		btnOptions.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		p2.add(btnOptions);
		p.add(p2);
		
		
		p.setPreferredSize(new Dimension(420, 60));
		p.setMinimumSize(p.getPreferredSize());
		p.setMaximumSize(p.getPreferredSize());
		//p.setBorder(BorderFactory.createEmptyBorder(1, 0, 1, 0));

		p.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		
		add(p);
		
		installView();
	}
	
	//////////////////////////////////////////////
	// Implementation of the ChannelView interface
	//////////////////////////////////////////////
	
	public JComponent
	getComponent() { return this; }
	
	public void
	installView() {
		String vmud = VOL_MEASUREMENT_UNIT_DECIBEL;
		preferences().addPropertyChangeListener(vmud, new PropertyChangeListener() {
			public void
			propertyChange(PropertyChangeEvent e) {
				boolean b;
				b = preferences().getBoolProperty(VOL_MEASUREMENT_UNIT_DECIBEL);
				screen.updateVolumeInfo(dialVolume.getValue());
			}
		});
	}
	
	public void
	uninstallView() {
		screen.onDestroy();
	}
	
	public ChannelOptionsView
	getChannelOptionsView() { return channelOptionsView; }
	
	public void
	updateChannelInfo() {
		SamplerChannel sc = channel.getChannelInfo();
		
		screen.updateScreenInfo(sc);
		float f = sc.getVolume() * 100.0f;
		screen.updateVolumeInfo((int)f);
		updateMuteIcon(sc);
		
		if(sc.isSoloChannel()) btnSolo.setIcon(Res.gfxSoloOn);
		else btnSolo.setIcon(Res.gfxSoloOff);
		dialVolume.setValue((int)(sc.getVolume() * 100));
		
		boolean b = sc.getEngine() != null;
		dialVolume.setEnabled(b);
		btnSolo.setEnabled(b);
		btnMute.setEnabled(b);
	}
	
	public void
	updateStreamCount(int count) { screen.updateStreamCount(count); }
	
	public void
	updateVoiceCount(int count) { screen.updateVoiceCount(count); }
	
	public void
	expandChannel() {
		if(btnOptions.isSelected()) return;
		btnOptions.doClick();
	}
	
	//////////////////////////////////////////////
	
	
	/**
	 * Updates the mute button with the proper icon regarding to information obtained
	 * from <code>channel</code>.
	 * @param channel A <code>SamplerChannel</code> instance containing the new settings
	 * for this channel.
	 */
	private void
	updateMuteIcon(SamplerChannel channel) {
		if(channel.isMutedBySolo()) btnMute.setIcon(Res.gfxMutedBySolo);
		else if(channel.isMuted()) btnMute.setIcon(Res.gfxMuteOn);
		else btnMute.setIcon(Res.gfxMuteOff);
	}
	
	/** Invoked when the user changes the volume */
	private void
	setVolume() {
		screen.updateVolumeInfo(dialVolume.getValue());
		
		if(dialVolume.getValueIsAdjusting()) return;
		
		int vol = (int)(channel.getChannelInfo().getVolume() * 100);
		
		if(vol == dialVolume.getValue()) return;
		
		/*
		 * If the model's volume is not equal to the dial knob
		 * value we assume that the change is due to user input.
		 * So we must update the volume at the backend too.
		 */
		float volume = dialVolume.getValue();
		volume /= 100;
		channel.getModel().setBackendVolume(volume);
	}
	
	private JPanel
	createVSeparator() {
		PixmapPane p = new PixmapPane(Res.gfxVLine);
		p.setAlignmentY(JPanel.TOP_ALIGNMENT);
		p.setPreferredSize(new Dimension(2, 60));
		p.setMinimumSize(p.getPreferredSize());
		p.setMaximumSize(p.getPreferredSize());
		return p;
	}
	
	private class EnhancedDial extends Dial {
		EnhancedDial() {
			super(0, 100, 0);
			
			setMouseHandlerMode(MouseHandlerMode.LEFT_TO_RIGHT_AND_DOWN_TO_UP);
			
			int i = preferences().getIntProperty(MAXIMUM_CHANNEL_VOLUME);
			setMaximum(i);
			String mcv = MAXIMUM_CHANNEL_VOLUME;
			preferences().addPropertyChangeListener(mcv, new PropertyChangeListener() {
				public void
				propertyChange(PropertyChangeEvent e) {
					int j = preferences().getIntProperty(MAXIMUM_CHANNEL_VOLUME);
					setMaximum(j);
				}
			});
			
			addMouseListener(new MouseAdapter() {
				public void
				mouseClicked(MouseEvent e) {
					if(e.getButton() == e.BUTTON3) {
						setValue(getMaximum() / 2);
						return;
					}
					
					if(e.getButton() != e.BUTTON1) return;
					
					if(e.getClickCount() < 2) return;
					setValue(getValueByPoint(e.getPoint()));
				}
			});
			
			addChangeListener(new ChangeListener() {
				public void
				stateChanged(ChangeEvent e) { setVolume(); }
			});
		}
	}
	
	
	private class MuteButton extends PixmapButton implements ActionListener {
		MuteButton() {
			super(Res.gfxMuteOff);
			//setDisabledIcon(Res.gfxMuteSoloDisabled);
			setDisabledIcon (
				SubstanceImageCreator.makeTransparent(this, Res.gfxMuteOff, 0.4)
			);
			addActionListener(this);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			SamplerChannel sc = channel.getChannelInfo();
			boolean b = true;
		
			/*
			 * Changing the mute button icon now instead of
			 * leaving the work to the notification mechanism of the LinuxSampler.
			 */
			if(sc.isMuted() && !sc.isMutedBySolo()) {
				b = false;
				boolean hasSolo = CC.getSamplerModel().hasSoloChannel();
			
				if(sc.isSoloChannel() || !hasSolo) setIcon(Res.gfxMuteOff);
				else setIcon(Res.gfxMutedBySolo);
			} else setIcon(Res.gfxMuteOn);
			
			channel.getModel().setBackendMute(b);
		}
		
		public boolean
		contains(int x, int y) { return (x > 5 && x < 23) && (y > 5 && y < 16); }
	}
	
	private class SoloButton extends PixmapButton implements ActionListener {
		SoloButton() {
			super(Res.gfxSoloOff);
			//setDisabledIcon(Res.gfxMuteSoloDisabled);
			setDisabledIcon (
				SubstanceImageCreator.makeTransparent(this, Res.gfxSoloOff, 0.4)
			);
			addActionListener(this);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			SamplerChannel sc = channel.getChannelInfo();
			boolean b = !sc.isSoloChannel();
		
			/*
			 * Changing the solo button icon (and related) now instead of
			 * leaving the work to the notification mechanism of the LinuxSampler.
			 */
			if(b) {
				setIcon(Res.gfxSoloOn);
				if(sc.isMutedBySolo()) btnMute.setIcon(Res.gfxMuteOff);
			} else {
				setIcon(Res.gfxSoloOff);
				if(!sc.isMuted() && CC.getSamplerModel().getSoloChannelCount() > 1)
					btnMute.setIcon(Res.gfxMutedBySolo);
			}
		
			channel.getModel().setBackendSolo(b);
		}
		
		public boolean
		contains(int x, int y) { return (x > 5 && x < 23) && (y > 5 && y < 16); }
	}
}



class ChannelPane extends PixmapPane {
	ChannelPane() {
		super(Res.gfxChannel);
		setPixmapInsets(new Insets(3, 3, 3, 3));
	}
}

class ChannelScreen extends PixmapPane {
	private final Channel channel;
	
	private final InstrumentPane instrumentPane;
	
	private final Channel.StreamVoiceCountPane streamVoiceCountPane;
	
	private final Channel.VolumePane volumePane;
	
	private JButton btnInstr =
		createScreenButton(i18n.getButtonLabel("ChannelScreen.btnInstr"));
	
	private final JButton btnEditInstr =
		createScreenButton(i18n.getButtonLabel("ChannelScreen.btnEditInstr"));
	private final ScreenButtonBg sbbEditInstr = new ScreenButtonBg(btnEditInstr);
	
	private final JButton btnFxSends =
		createScreenButton(i18n.getButtonLabel("ChannelScreen.btnFxSends"));
	
	private final JButton btnEngine
		= createScreenButton(i18n.getButtonLabel("ChannelScreen.btnEngine"));
	
	private final JPopupMenu menuEngines = new JPopupMenu();
	
	private Timer timer;
	
	ChannelScreen(final Channel channel) {
		super(Res.gfxChannelScreen);
		setPixmapInsets(new Insets(6, 6, 6, 6));
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		this.channel = channel;
		addMouseListener(channel.getContextMenu());
		streamVoiceCountPane = new Channel.StreamVoiceCountPane(channel);
		volumePane = new Channel.VolumePane(channel);
		
		setOpaque(false);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		btnInstr.setAlignmentX(CENTER_ALIGNMENT);
		btnInstr.setRolloverEnabled(false);
		btnInstr.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
		btnInstr.addMouseListener(channel.getContextMenu());
		
		instrumentPane = new InstrumentPane();
		add(instrumentPane);
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.setAlignmentX(CENTER_ALIGNMENT);
		p.setBorder(BorderFactory.createEmptyBorder(5, 2, 0, 0));
		
		btnFxSends.setToolTipText(i18n.getButtonLabel("ChannelScreen.btnFxSends.tt"));
		btnFxSends.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				channel.showFxSendsDialog();
			}
		});
		
		btnFxSends.addMouseListener(channel.getContextMenu());
		
		p.add(btnFxSends);
		
		//p.add(Box.createRigidArea(new Dimension(6, 0)));
		p.add(Box.createGlue());
		
		btnEngine.setIcon(Res.iconEngine12);
		btnEngine.setIconTextGap(1);
		btnEngine.addMouseListener(channel.getContextMenu());
		p.add(btnEngine);
		//p.add(new Label("|"));
		
		//p.add(Box.createRigidArea(new Dimension(6, 0)));
		
		//p.add(btnReset);
		
		p.add(Box.createGlue());
		
		p.add(streamVoiceCountPane);
		p.add(volumePane);
		
		p.setPreferredSize(new Dimension(260, p.getPreferredSize().height));
		p.setMinimumSize(p.getPreferredSize());
		p.setMaximumSize(p.getPreferredSize());
		
		//btnInstr.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		p.setOpaque(false);
		add(p);
		
		
		setPreferredSize(new Dimension(270, 48));
		setMinimumSize(getPreferredSize());
		setMaximumSize(getPreferredSize());
		
		createEngineMenu();
		installListeners();
	}
	
	protected void
	onDestroy() { timer.stop(); }
	
	private void
	createEngineMenu() {
		for(final SamplerEngine engine : CC.getSamplerModel().getEngines()) {
			JMenuItem mi = new JMenuItem(engine.getName() + " engine");
			mi.setToolTipText(engine.getDescription());
			
			mi.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					channel.getModel().setBackendEngineType(engine.getName());
				}
			});
			
			menuEngines.add(mi);
		}
	}
	
	private void
	installListeners() {
		btnInstr.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { channel.loadInstrument(); }
		});
		
		btnEditInstr.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				CC.getSamplerModel().editBackendInstrument(channel.getChannelId());
			}
		});
		
		btnEngine.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				int y = btnEngine.getHeight() + 1;
				menuEngines.show(btnEngine, 0, y);
			}
		});
		
		addMouseListener(getHandler());
		addHierarchyListener(getHandler());
		
		ActionListener l = new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				if(getMousePosition(true) != null) {
					getHandler().mouseEntered(null);
				} else {
					getHandler().mouseExited(null);
				}
			}
		};
		timer = new Timer(1000, l);
		timer.start();
	}
	
	protected void
	updateScreenInfo(SamplerChannel sc) {
		int status = sc.getInstrumentStatus();
		if(status >= 0 && status < 100) {
			btnInstr.setText(i18n.getLabel("ChannelScreen.loadingInstrument", status));
		} else if(status == -1) {
			btnInstr.setText(i18n.getButtonLabel("ChannelScreen.btnInstr"));
		} else if(status < -1) {
			 btnInstr.setText(i18n.getLabel("ChannelScreen.errorLoadingInstrument"));
		} else {
			if(sc.getInstrumentName() != null) btnInstr.setText(sc.getInstrumentName());
			else btnInstr.setText(i18n.getButtonLabel("ChannelScreen.btnInstr"));
		}
		
		instrumentPane.update();
	
		if(sc.getEngine() != null) {
			String s = sc.getEngine().getName();
			s += " engine";
			if(!s.equals(btnEngine.getText())) {
				btnEngine.setText(s);
				btnEngine.setToolTipText(sc.getEngine().getDescription());
			}
		}
		
	}
	
	protected void
	updateVolumeInfo(int volume) {
		volumePane.updateVolumeInfo(volume);
	}
	
	/**
	 * Updates the number of active disk streams.
	 * @param count The new number of active disk streams.
	 */
	protected void
	updateStreamCount(int count) {
		streamVoiceCountPane.updateStreamCount(count);
	}
	
	/**
	 * Updates the number of active voices.
	 * @param count The new number of active voices.
	 */
	protected void
	updateVoiceCount(int count) {
		streamVoiceCountPane.updateVoiceCount(count);
	}
	
	class InstrumentPane extends JPanel {
		private final JPanel leftPane = new JPanel();
		private final JPanel rightPane = new JPanel();
		
		InstrumentPane() {
			setOpaque(false);
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			add(leftPane);
			add(btnInstr);
			add(rightPane);
			add(sbbEditInstr);
			btnEditInstr.setToolTipText(i18n.getLabel("ChannelScreen.btnEditInstr.tt"));
			sbbEditInstr.setVisible(false);
			setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 6));
			
			update();
		}
		
		public void
		update() {
			int a = btnInstr.getMinimumSize().width;
			int b = 0;
			if(sbbEditInstr.isVisible()) b = sbbEditInstr.getPreferredSize().width;
			
			int max = 254 - b;
			if(a > max) a = max;
			
			int h = btnInstr.getPreferredSize().height;
			btnInstr.setPreferredSize(new Dimension(a, h));
			h = btnInstr.getMaximumSize().height;
			btnInstr.setMaximumSize(new Dimension(a, h));
			
			
			int i = (254 - btnInstr.getPreferredSize().width) / 2;
			
			int j = i;
			if(sbbEditInstr.isVisible()) j -= sbbEditInstr.getPreferredSize().width;
			if(i < 0 || j < 0) i = j = 0;
			
			Dimension d = new Dimension(i, 1);
			leftPane.setMinimumSize(d);
			leftPane.setPreferredSize(d);
			leftPane.setMaximumSize(d);
			
			d = new Dimension(j, 1);
			rightPane.setMinimumSize(d);
			rightPane.setPreferredSize(d);
			rightPane.setMaximumSize(d);
			
			validate();
		}
	}
	
	static class ScreenButtonBg extends PixmapPane {
		ScreenButtonBg(JButton btn) {
			super(Res.gfxScreenBtnBg);
			setPixmapInsets(new Insets(4, 4, 4, 4));
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			setBorder(BorderFactory.createEmptyBorder(0, 7, 0, 7));
			add(btn);
			setPreferredSize(new Dimension(getPreferredSize().width, 13));
		}
		
		public Dimension
		getPreferredSize() {
			return new Dimension(super.getPreferredSize().width, 13);
		}
	}
	
	
	private final EventHandler eventHandler = new EventHandler();
	
	private EventHandler
	getHandler() { return eventHandler; }
	
	private class EventHandler extends MouseAdapter implements HierarchyListener {
		public void
		mouseEntered(MouseEvent e)  {
			if(channel.getChannelInfo().getInstrumentStatus() != 100) return;
			
			if(!sbbEditInstr.isVisible()) {
				sbbEditInstr.setVisible(true);
				instrumentPane.update();
			}
		}
		
		public void
		mouseExited(MouseEvent e)  {
			if(getMousePosition(true) != null) return;
			if(sbbEditInstr.isVisible()) {
				sbbEditInstr.setVisible(false);
				instrumentPane.update();
			}
		}
		
		/** Called when the hierarchy has been changed. */
		public void
		hierarchyChanged(HierarchyEvent e) {
			if((e.getChangeFlags() & e.SHOWING_CHANGED) == e.SHOWING_CHANGED) {
				if(getMousePosition() == null) mouseExited(null);
				else mouseEntered(null);
			}
		}
	}
}