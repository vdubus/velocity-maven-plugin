package com.googlecode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.maven.model.FileSet;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.VelocityException;
import org.codehaus.plexus.util.FileUtils;

/**
 * @author www.slide.se
 * @author javamonkey79 - Shaun Elliott
 *
 * @goal velocity
 */
public class VelocityMojo extends AbstractMojo {
	
	/**
	 * The maven project.
	 *
	 * @parameter property="project"
	 * @readonly
	 */
	private MavenProject project;
	
	/**
	 * The character encoding scheme to be applied when filtering resources. Must not be null.
	 *
	 * @parameter property="encoding" default-value="${project.build.sourceEncoding}"
	 */
	private String encoding;
	
	/**
	 * Location of the file. Defaults to project.build.directory.
	 *
	 * @parameter property="project.build.directory"
	 * @required
	 */
	private File outputDirectory;
	
	/**
	 * Template files. The files to apply velocity on.
	 *
	 * @parameter
	 * @required
	 */
	private FileSet templateFiles;
	
	/**
	 * Template values
	 *
	 * @parameter
	 */
	private Properties templateValues;
	
	/**
	 * Set this parameter if you want the plugin to remove an unwanted extension when saving result.<br>
	 * For example {@code "foo.xml.vtl"} will become {@code "foo.xml"} if removeExtension = {@code '.vtl'}.<br>
	 * Null and empty means no substition.
	 *
	 * @parameter
	 */
	private String removeExtension;
	
	/**
	 * Velocity engine instance.
	 */
	private VelocityEngine velocity;
	
	public void execute() throws MojoExecutionException {
		
		getLog().info("velocity....");
		try {
			velocity = new VelocityEngine();
			velocity.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM, new LogHandler(this));
			velocity.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH, project.getBasedir().getAbsolutePath());
			
			velocity.init();
			VelocityContext context = new VelocityContext();
			
			addPropertiesToContext(context, templateValues);
			context.put("project", project);
			
			List<?> fileNames = expandFileSet();
			if (fileNames == null) {
				getLog().warn("Emtpy fileset");
			} else {
				getLog().debug("Translating files");
				final Iterator<?> i = fileNames.iterator();
				while (i.hasNext()) {
					final String templateFile = (String) i.next();
					getLog().debug("templateFile -> " + templateFile);
					translateFile(templateFiles.getDirectory(), templateFile, context);
				}
			}
		} catch (ResourceNotFoundException e) {
			throw new MojoExecutionException("Resource not found", e);
		} catch (VelocityException e) {
			getLog().info(e.getMessage());
		} catch (MojoExecutionException e) {
			throw e;
		} catch (IOException e) {
			throw new MojoExecutionException("Failed to save result", e);
		} catch (Exception e) {
			getLog().error(e);
			throw new MojoExecutionException("Unexpected", e);
		}
	}
	
	private void addPropertiesToContext(final VelocityContext context, final Properties prop) {
		getLog().debug("Exporting properties to context: " + prop);
		if (prop == null) {
			return;
		}
		final Enumeration<?> propEnumeration = prop.propertyNames();
		while (propEnumeration.hasMoreElements()) {
			final String key = (String) propEnumeration.nextElement();
			final String value = prop.getProperty(key);
			getLog().debug(key + "=" + value);
			context.put(key, value);
		}
	}
	
	private List<?> expandFileSet() throws IOException {
		final File baseDir = new File(project.getBasedir().getAbsolutePath() + File.separator + templateFiles.getDirectory());
		getLog().debug(baseDir.getAbsolutePath());
		final String includes = list2CvsString(templateFiles.getIncludes());
		getLog().debug("includes: " + includes);
		final String excludes = list2CvsString(templateFiles.getExcludes());
		getLog().debug("excludes: " + excludes);
		return FileUtils.getFileNames(baseDir, includes, excludes, false);
	}
	
	private String list2CvsString(final List<?> patterns) {
		String delim = "";
		final StringBuffer buf = new StringBuffer();
		if (patterns != null) {
			final Iterator<?> i = patterns.iterator();
			while (i.hasNext()) {
				buf.append(delim).append(i.next());
				delim = ", ";
			}
		}
		return buf.toString();
	}
	
	private void translateFile(final String basedir, final String templateFile, final VelocityContext context)
			throws ResourceNotFoundException, VelocityException, MojoExecutionException, IOException
	
	{
		Template template = null;
		String tmpTemplateFile = templateFile;
		final String inputFile = basedir + File.separator + tmpTemplateFile;
		getLog().debug("inputFile -> " + inputFile);
		try {
			template = velocity.getTemplate(inputFile, encoding == null ? "UTF-8" : encoding);
		} catch (Exception e) {
			getLog().info("Failed to load: " + inputFile);
			throw new MojoExecutionException("Get template failed: " + inputFile, e);
		}
		final StringWriter sw = new StringWriter();
		try {
			template.merge(context, sw);
		} catch (Exception e) {
			getLog().info("Failed to merge: " + inputFile + ":" + e.getMessage());
			throw new MojoExecutionException("Fail to merge template: " + inputFile, e);
			
		}
		
		if (removeExtension != null && !removeExtension.trim().equals("") && tmpTemplateFile.endsWith(removeExtension)) {
			final String tmp = tmpTemplateFile.substring(0, tmpTemplateFile.length() - removeExtension.length());
			
			if (tmp.endsWith("/")) {
				getLog().warn("removePrefix equals filename will not remove it. " + tmpTemplateFile);
			} else
				tmpTemplateFile = tmp;
		}
		final File result = new File(outputDirectory.getAbsoluteFile() + File.separator + tmpTemplateFile);
		final File dir = result.getParentFile();
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				throw new MojoExecutionException("Failed to create outputDirectory");
			}
		}
		
		FileOutputStream os = new FileOutputStream(result);
		try {
			os.write(sw.toString().getBytes(encoding == null ? "UTF-8" : encoding));
		} finally {
			os.close();
		}
	}
	
	void setProject(final MavenProject project) {
		this.project = project;
	}
	
	void setEncoding(final String encoding) {
		this.encoding = encoding;
	}
	
	void setOutputDirectory(final File outputDirectory) {
		this.outputDirectory = outputDirectory;
	}
	
	void setTemplateFiles(final FileSet templateFiles) {
		this.templateFiles = templateFiles;
	}
	
	void setTemplateValues(final Properties templateValues) {
		this.templateValues = templateValues;
	}
	
}
