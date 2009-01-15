package se.slide.maven;

import java.io.File;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

/**
 * 
 * @author www.slide.se
 * @goal velocity
 */
public class VelocityMojo extends AbstractMojo {
	/**
	 * Location of the file.
	 * 
	 * @parameter expression="${project.build.directory}"
	 * @required
	 */
	private File outputDirectory;

	/**
	 * Template file TODO Make this a fileset so that a collection of files
	 * could be processed
	 * 
	 * @parameter
	 */
	private File templateFile;

	/**
	 * Template values
	 * 
	 * @parameter
	 */
	private Properties templateValues;

	public void execute() throws MojoExecutionException {
		getLog().info("velocity....");
		try {
			Velocity.init();
			VelocityContext context = new VelocityContext();

			Enumeration e = templateValues.propertyNames();

			while (e.hasMoreElements()) {
				String key = (String)e.nextElement();
				String value = templateValues.getProperty(key);
				context.put(key, value);
			}

			//context.put("name", new String("Velocity"));

			Template template = null;

			//template = Velocity.getTemplate("mytemplate.vm");
			String absPath = templateFile.getAbsolutePath();
			String path = templateFile.getPath();
			template = Velocity.getTemplate("infra.txt");
			StringWriter sw = new StringWriter();
			template.merge(context, sw);

			getLog().info(sw.toString());
		} catch (ResourceNotFoundException rnfe) {
			getLog().error(rnfe.getMessage());
			// couldn't find the template
		} catch (ParseErrorException pee) {
			// syntax error: problem parsing the template
		} catch (MethodInvocationException mie) {
			// something invoked in the template
			// threw an exception
		} catch (Exception e) {

		}

	}
}
