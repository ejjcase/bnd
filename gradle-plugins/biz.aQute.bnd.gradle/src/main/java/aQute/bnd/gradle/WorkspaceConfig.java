package aQute.bnd.gradle;

import aQute.bnd.build.Project;
import aQute.bnd.build.Workspace;
import aQute.bnd.osgi.Constants;
import aQute.lib.utf8properties.UTF8Properties;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;

import java.io.File;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Configures a Bnd workspace.
 */
public class WorkspaceConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	private final File rootDir;

	private String cnfDir = Workspace.CNFDIR;

	private boolean offline = false;

	private final UTF8Properties bndProperties = new UTF8Properties();

	/**
	 * @return The workspace's root directory.
	 */
	public File getRootDir() {
		return rootDir;
	}

	/**
	 * @return The name of the cnf directory relative to the root directory.
	 */
	public String getCnfDir() {
		return cnfDir;
	}

	/**
	 * @param cnfDir The name of the cnf directory relative to the root directory.
	 */
	public void setCnfDir(final String cnfDir) {
		this.cnfDir = cnfDir;
	}

	/**
	 * @return Whether to initialize the workspace in offline mode.
	 */
	public boolean getOffline() {
		return offline;
	}

	/**
	 * @param offline Whether to initialize the workspace should be in offline mode.
	 */
	public void setOffline(final boolean offline) {
		this.offline = offline;
	}

	/**
	 * @return Properties to set on the workspace when it is created.
	 */
	public UTF8Properties getBndProperties() {
		return bndProperties;
	}

	public WorkspaceConfig(File rootDir) {
		this.rootDir = rootDir;
	}

	/**
	 * Set a Bnd property on the workspace when it is created.
	 * <p>
	 * For use by closures or actions assigned to the {@code bndWorkspaceConfigure} extra property.
	 * Since these were formerly applied directly to a {@link Workspace}, this method has the same
	 * API as a {@link Workspace} method for partial backward compatibility.
	 * </p>
	 * @see Workspace#setProperty(String, String)
	 * @param key The property name.
	 * @param value The property value.
	 */
	public void setProperty(String key, String value) {
		getBndProperties().setProperty(key, value);
	}

	public Workspace createWorkspace() {
		try {
			Workspace.setDriver(Constants.BNDDRIVER_GRADLE);
			Workspace.addGestalt(Constants.GESTALT_BATCH, null);
			Workspace workspace = new Workspace(rootDir, cnfDir);
			workspace.setOffline(offline);
			workspace.addProperties(bndProperties);
			return workspace;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
