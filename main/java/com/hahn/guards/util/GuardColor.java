package com.hahn.guards.util;

public enum GuardColor {
	BLUE(0),
	AQUA(1),
	GREEN(2),
	LIGHT_PURPLE(3),
	RED(4),
	YELLOW(5);
	
	private final byte code;
	
	private GuardColor(int code) {
		this.code = (byte) code;
	}
	
	public static GuardColor fromCode(int code) {
		for (GuardColor c : GuardColor.values()) {
			if (c.code == code) return c;
		}
		
		return null;
	}
	
	public byte toCode() {
		return code;
	}
}
