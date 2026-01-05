package aQute.bnd.gradle;

import aQute.bnd.build.Project;
import org.gradle.api.GradleException;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.provider.Property;
import org.gradle.api.services.ServiceReference;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.DisableCachingByDefault;

/**
 * Release a Bnd project.
 */
@DisableCachingByDefault(because = "Not confirmed to be safely cacheable")
public abstract class Release extends AbstractBndWorkspaceTask {

	/**
	 * @return All outputs of the "jar" task.
	 */
	@InputFiles
	@PathSensitive(PathSensitivity.RELATIVE)
	public abstract ConfigurableFileCollection getJar();

	@ServiceReference
	public abstract Property<ReleaseCounterService> getReleaseCounterService();

	@TaskAction
	public void execute() {
		final Project bndProject = getBndProject().get();
		try {
			ReleaseCounterService releaseCounter = getReleaseCounterService().get();
			int count = releaseCounter.getRemaining();
			boolean isLastBundle = releaseCounter.isLastReleaseTask();

			if (!isLastBundle) {
				getLogger()
					.lifecycle("bnd: Release bundle ({}) {}", count, bndProject.getName());
				bndProject.release();
			} else {
				// releasing last bundle in workspace (special
				// case for sonatype release)
				getLogger()
					.lifecycle("bnd: Last release bundle ({}) {}", count, bndProject.getName());
				bndProject.release(new Project.ReleaseParameter(null, false, true));
			}
		} catch (Exception e) {
			throw new GradleException(
				String.format("Project %s failed to release", bndProject.getName()), e);
		}
		checkErrors();
	}
}
