package aQute.bnd.gradle;

import aQute.bnd.build.Project;
import org.gradle.api.GradleException;
import org.gradle.work.DisableCachingByDefault;

import java.util.Optional;

import static aQute.bnd.gradle.BndUtils.logReport;

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
	 * To be called at the end of the task action.
	 * @param failOnError Whether to fail the task if there are errors.
	 */
	protected void checkErrors(boolean failOnError) {
		Project p = getBndProject().get();
		p.getInfo(p.getWorkspace(), p.getWorkspace()
			.getBase()
			.getName()
			.concat(" :"));
		boolean failed = !failOnError && !p.isOk();
		int errorCount = p.getErrors()
			.size();
		logReport(p, getLogger());
		p.clear();
		if (failed) {
			String str;
			if (errorCount == 1) {
				str = "%s has errors, one error was reported";
			} else if (errorCount > 1) {
				str = "%s has errors, %s errors were reported";
			} else {
				str = "%s has errors even though no errors were reported";
			}
			throw new GradleException(String.format(str, p.getName(), errorCount));
		}
	}
}
