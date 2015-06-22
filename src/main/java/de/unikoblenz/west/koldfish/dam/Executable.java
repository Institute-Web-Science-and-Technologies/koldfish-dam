package de.unikoblenz.west.koldfish.dam;

public interface Executable <T extends Exception>{

	public void start() throws T;
	public void terminate() throws T;
}
