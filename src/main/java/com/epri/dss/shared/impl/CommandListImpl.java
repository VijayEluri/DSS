package com.epri.dss.shared.impl;

import com.epri.dss.shared.CommandList;
import com.epri.dss.shared.HashList;

public class CommandListImpl implements CommandList {

	private HashList commandList;
	private boolean abbrevAllowed;

	public CommandListImpl(String[] commands) {
		super();
		this.commandList = new HashListImpl(commands.length);

		for (int i = 0; i < commands.length; i++)
			this.commandList.add(commands[i]);

		abbrevAllowed = true;
	}

	public void addCommand(String cmd) {
		commandList.add(cmd);
	}

	public int getCommand(String cmd) {
		int result = commandList.find(cmd);
		/* If no match found on whole command, check for abbrev */
		/* This routine will generally be faster if one enters the whole command */
		if (result == -1)
			if (abbrevAllowed)
				result = commandList.findAbbrev(cmd);

		return result;
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
