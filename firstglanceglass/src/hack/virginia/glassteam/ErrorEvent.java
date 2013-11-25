package hack.virginia.glassteam;

public class ErrorEvent {
	private final String error;

	public ErrorEvent(String error) {
		this.error = error;
	}

	public String getError() {
		return error;
	}
}
