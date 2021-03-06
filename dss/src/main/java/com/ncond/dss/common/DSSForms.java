/**
 * Copyright (C) 2008-2012 Electric Power Research Institute, Inc.
 * Copyright (C) 2009-2012 Richard Lincoln
 * All rights reserved.
 */
package com.ncond.dss.common;

import java.util.List;

public interface DSSForms {

	void createControlPanel();

	void exitControlPanel();

	void initProgressForm();

	void progressCaption(String s);

	void progressFormCaption(String s);

	void progressHide();

	void showControlPanel();

	void showHelpForm(String cmd);

	void showHelpForm();

	void showAboutBox();

	void showPropEditForm();

	void showPctProgress(int count);

	void showMessageForm(List<String> s);

	int messageDlg(String msg, boolean err);

	void infoMessageDlg(String msg);

	String getDSSExeFile();

	void closeDownForms();

	void showTreeView(String fname);

	boolean makeChannelSelection(int numFieldsToSkip, String filename);

	boolean isRebuildHelpForm();

	void setRebuildHelpForm(boolean rebuildHelpForm);

}
