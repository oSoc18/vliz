package exceptions;

public class BizzException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BizzException() {
		super();
	}

	public BizzException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public BizzException(String message, Throwable cause) {
		super(message, cause);
	}

	public BizzException(String message) {
		super(message);
	}

	public BizzException(Throwable cause) {
		super(cause);
	}
	
}
