package aQute.bnd.gradle;

import org.gradle.work.DisableCachingByDefault;

import static aQute.bnd.gradle.BndUtils.checkProjectErrors;

/**
 * {@link AbstractBndTask} that can only run within a project in a Bnd workspace.
 * <p>
 * Classes that extend this class may assume {@link #getBndProject()} and
 * {@link #getBndWorkspace()} never return empty optionals.
 * </p>
 */
@DisableCachingByDefault(because = "Abstract base class; not used directly")
public abstract class AbstractBndWorkspaceTask extends AbstractBndTask {

	protected void checkErrors() {
		checkErrors(false);
	}

	/**
	 * Log a status report and optionally fail if any errors have occurred.
	 * @param failOnError Whether to fail the task if there are errors.
	 */
	protected void checkErrors(boolean failOnError) {
		checkProjectErrors(getBndProject().get(), getLogger(), failOnError);
	}
}
