package aQute.bnd.gradle;

import aQute.bnd.build.Project;
import aQute.lib.strings.Strings;
import org.gradle.api.Action;
import org.gradle.api.Task;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.logging.Logger;
import org.gradle.api.provider.Property;
import org.gradle.api.services.ServiceReference;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.compile.JavaCompile;

import java.util.Objects;

import static aQute.bnd.gradle.BndUtils.checkProjectErrors;
import static aQute.bnd.gradle.BndUtils.unwrap;
import static aQute.bnd.gradle.BndUtils.unwrapFile;

/**
 * Task action to add to {@link JavaCompile} tasks.
 */
public abstract class JavaCompileCheckErrorsAction implements Action<Task> {

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

	@Override
	public void execute(final Task task) {
		Project project = BndUtils.unwrapOptional(getBndWorkspaceService())
			.flatMap(s -> s.getAncestorWorkspace(getProjectDir().get().getAsFile()))
			.map(ws -> ws.getProject(getProjectName().get()))
			.get();
		Logger logger = task.getLogger();
		JavaCompile t = (JavaCompile) task;
		checkProjectErrors(project, logger, false);
		if (logger.isInfoEnabled()) {
			logger.info("Compile to {}", unwrapFile(t.getDestinationDirectory()));
			if (t.getOptions()
				.getRelease()
				.isPresent()) {
				logger.info("--release {} {}", unwrap(t.getOptions()
					.getRelease()), Strings.join(" ",
					t.getOptions()
						.getAllCompilerArgs()));
			} else {
				logger.info("-source {} -target {} {}", t.getSourceCompatibility(),
					t.getTargetCompatibility(), Strings.join(" ", t.getOptions()
						.getAllCompilerArgs()));
			}
			logger.info("-classpath {}", t.getClasspath()
				.getAsPath());
			if (Objects.nonNull(t.getOptions()
				.getBootstrapClasspath())) {
				logger.info("-bootclasspath {}", t.getOptions()
					.getBootstrapClasspath()
					.getAsPath());
			}
		}
	}
}
