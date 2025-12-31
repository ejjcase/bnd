package aQute.bnd.gradle;

import aQute.bnd.build.Project;
import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.Task;

import java.io.File;
import java.util.Objects;

/**
 * Action that builds a Bnd project's bundles, replacing the
 * default action of a Gradle project's "jar" task.
 */
public abstract class BndBuildAction extends AbstractBndGradleObject implements Action<Task> {

	@Override
	public void execute(final Task task) {
		Project bndProject = getBndProject();
		File[] built;
		try {
			built = bndProject.build();
			if (Objects.nonNull(built)) {
				long now = System.currentTimeMillis();
				for (File f : built) {
					f.setLastModified(now);
				}
			}
		} catch (Exception e) {
			throw new GradleException(
				String.format("Project %s failed to build", bndProject.getName()), e);
		}
		checkErrors(task.getLogger());
		if (Objects.nonNull(built)) {
			task.getLogger()
				.info("Generated bundles: {}", (Object) built);
		}
	}
}
