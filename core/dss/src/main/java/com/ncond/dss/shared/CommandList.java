/**
 * Copyright (C) 2008-2012 Electric Power Research Institute, Inc.
 * Copyright (C) 2009-2012 Richard Lincoln
 * All rights reserved.
 */
package com.ncond.dss.shared;


public class CommandList {

	private HashList commandList;
	private boolean abbrevAllowed;

	public CommandList(String[] commands) {
		this(commands, true);
	}

	public CommandList(String[] commands, boolean abbrevAllowed) {
		super();
		commandList = new HashList(commands.length);

		for (int i = 0; i < commands.length; i++)
			commandList.add(commands[i]);

		this.abbrevAllowed = abbrevAllowed;
	}

	public void addCommand(String cmd) {
		commandList.add(cmd);
	}

	public int getCommand(String cmd) {
		int idx = commandList.find(cmd);

		/* If no match found on whole command, check for abbrev */
		/* This routine will generally be faster if one enters the whole command */
		if (idx == -1)
			if (abbrevAllowed)
				idx = commandList.findAbbrev(cmd);

		return idx;
	}

	public String get(int i) {
		return commandList.get(i);
	}

	public int getNumCommands() {
		return commandList.listSize();
	}

	public boolean isAbbrevAllowed() {
		return abbrevAllowed;
	}

	public void setAbbrevAllowed(boolean allowed) {
		abbrevAllowed = allowed;
	}

}
