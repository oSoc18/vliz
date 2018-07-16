package exceptions;

public class FatalException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public FatalException() {
		super();
	}

	public FatalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public FatalException(String message, Throwable cause) {
		super(message, cause);
	}

	public FatalException(String message) {
		super(message);
	}

	public FatalException(Throwable cause) {
		super(cause);
	}
	
	

}
