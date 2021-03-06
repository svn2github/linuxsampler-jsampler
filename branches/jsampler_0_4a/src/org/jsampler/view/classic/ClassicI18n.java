/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2006 Grigor Iliev <grigor@grigoriliev.com>
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

package org.jsampler.view.classic;

/**
 * This class manages the locale-specific data of JS Classic.
 * @author Grigor Iliev
 */
public class ClassicI18n extends net.sf.juife.I18n {
	/** Provides the locale-specific data of JS Classic. */
	public static ClassicI18n i18n = new ClassicI18n();
	
	private
	ClassicI18n() {
		setButtonsBundle("org.jsampler.view.classic.langprops.ButtonsLabelsBundle");
		setErrorsBundle("org.jsampler.view.classic.langprops.ErrorsBundle");
		setLabelsBundle("org.jsampler.view.classic.langprops.LabelsBundle");
		setMenusBundle("org.jsampler.view.classic.langprops.MenuLabelsBundle");
		setMessagesBundle("org.jsampler.view.classic.langprops.MessagesBundle");
	}
}
