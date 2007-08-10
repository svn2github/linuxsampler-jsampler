/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2007 Grigor Iliev <grigor@grigoriliev.com>
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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jsampler.AudioDeviceModel;
import org.jsampler.CC;

import org.jsampler.event.AudioDeviceEvent;
import org.jsampler.event.AudioDeviceListener;
import org.jsampler.event.ParameterEvent;
import org.jsampler.event.ParameterListener;

import org.jsampler.view.ParameterTable;

import org.linuxsampler.lscp.AudioOutputChannel;
import org.linuxsampler.lscp.AudioOutputDevice;
import org.linuxsampler.lscp.Parameter;

import static org.jsampler.view.fantasia.FantasiaI18n.i18n;

/**
 *
 * @author Grigor Iliev
 */
public class AudioDevicePane extends DevicePane {
	private final OptionsPane optionsPane;
	private final ParameterTable channelParamTable = new ParameterTable();
	
	private final AudioDeviceModel audioDeviceModel;
	
	
	/** Creates a new instance of <code>AudioDevicePane</code> */
	public
	AudioDevicePane(AudioDeviceModel model) {
		audioDeviceModel = model;
		
		optionsPane = new OptionsPane();
		setOptionsPane(optionsPane);
		
		int id = model.getDeviceId();
		setDeviceName(i18n.getLabel("AudioDevicePane.lDevName", id));
	}
	
	protected void
	destroyDevice() {
		CC.getSamplerModel().removeBackendAudioDevice(getDeviceId());
	}
	
	public int
	getDeviceId() { return audioDeviceModel.getDeviceId(); }
	
	class OptionsPane extends PixmapPane implements ActionListener, ItemListener,
				ChangeListener, AudioDeviceListener, ParameterListener {
		
		private final JCheckBox checkActive =
			new JCheckBox(i18n.getLabel("AudioDevicePane.checkActive"));
		
		private final JLabel lChannels
			= new JLabel(i18n.getLabel("AudioDevicePane.lChannels"));
		
		private final JSpinner spinnerChannels
			= new JSpinner(new SpinnerNumberModel(1, 1, 50, 1));
		
		private final JLabel lChannel =
			new JLabel(i18n.getLabel("AudioDevicePane.lChannel"));
		
		private final JComboBox cbChannel = new JComboBox();
		
		OptionsPane() {
			super(Res.gfxChannelOptions);
			
			setAlignmentX(LEFT_ALIGNMENT);
			
			setPixmapInsets(new Insets(1, 1, 1, 1));
			setLayout(new java.awt.BorderLayout());
			setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			setOpaque(false);
			
			PixmapPane mainPane = new PixmapPane(Res.gfxRoundBg7);
			mainPane.setPixmapInsets(new Insets(3, 3, 3, 3));
			mainPane.setOpaque(false);
			
			mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
			mainPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			mainPane.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
			
			JPanel p = new JPanel();
			p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
			
			p.add(checkActive);
			p.add(Box.createRigidArea(new Dimension(12, 0)));
			p.add(lChannels);
			p.add(Box.createRigidArea(new Dimension(5, 0)));
			p.add(spinnerChannels);
			p.setOpaque(false);
			
			mainPane.add(p);
			mainPane.add(Box.createRigidArea(new Dimension(0, 5)));
			
			mainPane.add(createHSeparator());
			mainPane.add(Box.createRigidArea(new Dimension(0, 5)));
			
			p = new JPanel();
			p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
			p.setOpaque(false);
			
			JPanel p2 = new JPanel();
			p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
			p2.add(lChannel);
			p2.add(Box.createRigidArea(new Dimension(5, 0)));
			p2.add(cbChannel);
			p2.setOpaque(false);
			p.add(p2);
			
			p.add(Box.createRigidArea(new Dimension(0, 5)));
			
			JScrollPane sp = new JScrollPane(channelParamTable);
			sp.setPreferredSize(new Dimension(77, 90));
			p.add(sp);
			
			mainPane.add(p);
			add(mainPane);
			
			checkActive.setSelected(audioDeviceModel.isActive());
			spinnerChannels.setValue(audioDeviceModel.getDeviceInfo().getChannelCount());
			
			cbChannel.addActionListener(this);
			checkActive.addItemListener(this);
			spinnerChannels.addChangeListener(this);
			audioDeviceModel.addAudioDeviceListener(this);
			channelParamTable.getModel().addParameterListener(this);
			
			AudioDeviceModel m = audioDeviceModel;
			for(AudioOutputChannel chn : m.getDeviceInfo().getAudioChannels()) {
				cbChannel.addItem(chn);
			}
		}
		
		public void
		actionPerformed(ActionEvent e) {
			Object obj = cbChannel.getSelectedItem();
			if(obj == null) {
				channelParamTable.getModel().setParameters(new Parameter[0]);
				return;
			}
			
			AudioOutputChannel chn = (AudioOutputChannel)obj;
			
			channelParamTable.getModel().setParameters(chn.getAllParameters());
		}
		
		public void
		itemStateChanged(ItemEvent e) {
			boolean a = checkActive.isSelected();
			if(a != audioDeviceModel.isActive()) audioDeviceModel.setBackendActive(a);
		}
		
		public void
		stateChanged(ChangeEvent e) {
			int c = (Integer)spinnerChannels.getValue();
			if(c != audioDeviceModel.getDeviceInfo().getAudioChannelCount()) {
				audioDeviceModel.setBackendChannelCount(c);
			}
		}
		
		public void
		settingsChanged(AudioDeviceEvent e) {
			int c = (Integer)spinnerChannels.getValue();
			int nc = audioDeviceModel.getDeviceInfo().getAudioChannelCount();
			if(c != nc) spinnerChannels.setValue(nc);
			
			boolean a = checkActive.isSelected();
			boolean na = audioDeviceModel.isActive();
			if(a != na) checkActive.setSelected(na);
			
			AudioOutputDevice d = e.getAudioDeviceModel().getDeviceInfo();
			
			int idx = cbChannel.getSelectedIndex();
			cbChannel.removeAllItems();
			for(AudioOutputChannel chn : d.getAudioChannels()) cbChannel.addItem(chn);
			
			if(idx >= cbChannel.getModel().getSize()) idx = 0;
			
			if(cbChannel.getModel().getSize() > 0) cbChannel.setSelectedIndex(idx);
		}
		
		public void
		parameterChanged(ParameterEvent e) {
			int c = cbChannel.getSelectedIndex();
			if(c == -1) {
				CC.getLogger().warning("There is no audio channel selected!");
				return;
			}
			
			audioDeviceModel.setBackendChannelParameter(c, e.getParameter());
		}
	}
	
}