package com.picovr.picovrpowermanager.test;

public interface OnPackagedObserver {

	public void packageInstalled(String packageName, int returnCode);
	public void packageDeleted(String packageName, int returnCode);
	
}
