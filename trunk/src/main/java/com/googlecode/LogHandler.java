package com.googlecode;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;

/**
 * @author last modified by: $Author$
 * @version $Revision$ $Date$
 */
public class LogHandler implements LogChute {
	
	private AbstractMojo mojo;
	
	public LogHandler( AbstractMojo velocityMojo ) {
		this.mojo = velocityMojo;
	}

	public boolean isLevelEnabled(int level) {
		boolean enabled = false;
		if (level == DEBUG_ID && mojo.getLog().isDebugEnabled())
			enabled = true;
		else if (level == INFO_ID && mojo.getLog().isInfoEnabled())
			enabled = true;
		else if (level == WARN_ID && mojo.getLog().isWarnEnabled())
			enabled = true;
		else if (level == ERROR_ID && mojo.getLog().isErrorEnabled())
			enabled = true;

		return enabled;
	}
	

	public void log(int level, String content) {
		if (isLevelEnabled(level))
			switch (level) {
			case DEBUG_ID : 
				mojo.getLog().debug(content);
				break;
			case INFO_ID :
				mojo.getLog().info(content);
				break;
			case WARN_ID :
				mojo.getLog().warn(content);
				break;
			case ERROR_ID :
				mojo.getLog().error(content);
				break;
			default:
			}
	}

	public void log(int level, String content, Throwable throwable) {
		if (isLevelEnabled(level))
			switch (level) {
			case DEBUG_ID : 
				mojo.getLog().debug(content, throwable);
				break;
			case INFO_ID :
				mojo.getLog().info(content, throwable);
				break;
			case WARN_ID :
				mojo.getLog().warn(content, throwable);
				break;
			case ERROR_ID :
				mojo.getLog().error(content, throwable);
				break;
			default:
			}
	}

	//LogChute implementation
	public void init(RuntimeServices runtimeServices) throws Exception {
		// Left empty
	}
}
