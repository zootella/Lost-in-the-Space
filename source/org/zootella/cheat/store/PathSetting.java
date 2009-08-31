package org.zootella.cheat.store;

import org.zootella.cheat.data.Outline;
import org.zootella.cheat.exception.DataException;
import org.zootella.cheat.file.Path;


public class PathSetting {
	
	public PathSetting(Outline outline, String name, Path program) {
		setting = new StringSetting(outline, name, program.toString());
		this.program = program;
	}
	private final StringSetting setting;
	private final Path program;
	
	public void set(Path value) { setting.set(value.toString()); }
	public Path value() {
		try {
			return new Path(setting.value());
		} catch (DataException e) { return program; }
	}
}
