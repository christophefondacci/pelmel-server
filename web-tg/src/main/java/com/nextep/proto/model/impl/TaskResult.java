package com.nextep.proto.model.impl;

public class TaskResult {

	private boolean success;
	private String message;
	private boolean finished;

	public TaskResult() {

	}

	/**
	 * Builds a finished task with the given success flag / message
	 * 
	 * @param success
	 *            success of the task
	 * @param message
	 *            message
	 */
	public TaskResult(boolean success, String message) {
		this.success = success;
		this.message = message;
		this.finished = true;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

}
