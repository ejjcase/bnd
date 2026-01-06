package aQute.bnd.gradle;

import aQute.bnd.build.Workspace;
import aQute.bnd.osgi.Constants;

import java.io.File;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Properties;

/**
 * Configures a Bnd workspace.
 */
public class WorkspaceConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	private File rootDir;

	private String cnfDir = Workspace.CNFDIR;

	private boolean offline = false;

	private Properties bndProperties = new Properties();

	/**
	 * @see Serializable
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(rootDir);
		out.writeObject(cnfDir);
		out.writeObject(offline);
		out.writeObject(bndProperties);
	}

	/**
	 * @see Serializable
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		rootDir = (File) in.readObject();
		cnfDir = (String) in.readObject();
		offline = (boolean) in.readObject();
		bndProperties = (Properties) in.readObject();
	}

	/**
	 * @see Serializable
	 */
	private void readObjectNoData() throws ObjectStreamException {
		throw new InvalidObjectException("No data");
	}

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
	public Properties getBndProperties() {
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
