package org.elastos.hive.exception;

public class HiveRException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public HiveRException() {
		super();
	}

	public HiveRException(String message) {
		super(message);
	}

	public HiveRException(String message, Throwable cause) {
		super(message, cause);
	}

	public HiveRException(Throwable cause) {
		super(cause);
	}
}
