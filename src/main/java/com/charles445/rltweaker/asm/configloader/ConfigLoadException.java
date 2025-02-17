package com.charles445.rltweaker.asm.configloader;

class ConfigLoadException extends RuntimeException {

	private static final long serialVersionUID = 6898289228917918430L;

	public ConfigLoadException() {
		super();
	}

	public ConfigLoadException(String message) {
		super(message);
	}

	public ConfigLoadException(Throwable cause) {
		super(cause);
	}

	public ConfigLoadException(String message, Throwable cause) {
		super(message, cause);
	}

}
