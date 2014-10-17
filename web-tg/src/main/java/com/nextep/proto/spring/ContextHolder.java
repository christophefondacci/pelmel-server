package com.nextep.proto.spring;

public final class ContextHolder {

	private static final ThreadLocal<Boolean> isReadOnly = new ThreadLocal<Boolean>();

	public static void toggleReadonly() {
		isReadOnly.set(true);
	}

	public static void toggleWrite() {
		isReadOnly.set(false);
	}

	public static boolean isWritable() {
		final Boolean b = isReadOnly.get();
		if (b == null) {
			return false;
		} else {
			return !b;
		}
	}
}
