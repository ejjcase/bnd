package aQute.bnd.gradle;

import aQute.bnd.build.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.logging.Logger;
import org.gradle.api.provider.Property;
import org.gradle.api.services.ServiceReference;
import org.gradle.api.tasks.Internal;

/**
 * Base class for Gradle objects that require access to a Bnd
 * project and workspace.
 */
public abstract class AbstractBndGradleObject {

	/**
	 * @return The name of the project to which the task belongs.
	 */
	@Internal
	public abstract Property<String> getProjectName();

	/**
	 * @return The project directory for the project to which the task belongs.
	 */
	@Internal
	public abstract DirectoryProperty getProjectDir();

	/**
	 * @return Gives access to the Bnd workspace and projects.
	 */
	@ServiceReference
	public abstract Property<BndWorkspaceService> getBndWorkspaceService();

	/**
	 * @return The Bnd project.
	 */
	protected Project getBndProject() {
		return BndUtils.unwrapOptional(getBndWorkspaceService())
			.flatMap(s -> s.getAncestorWorkspace(getProjectDir().get().getAsFile()))
			.map(ws -> ws.getProject(getProjectName().get()))
			.get();
	}

	protected void checkErrors(Logger logger) {
		checkErrors(logger, false);
	}

	protected void checkErrors(Logger logger, boolean failOnError) {
		BndUtils.checkProjectErrors(getBndProject(), logger, failOnError);
	}
}
