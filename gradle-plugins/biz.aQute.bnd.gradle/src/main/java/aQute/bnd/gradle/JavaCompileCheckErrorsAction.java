package aQute.bnd.gradle;

import aQute.lib.strings.Strings;
import org.gradle.api.Action;
import org.gradle.api.Task;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.compile.JavaCompile;

import java.util.Objects;

import static aQute.bnd.gradle.BndUtils.unwrap;
import static aQute.bnd.gradle.BndUtils.unwrapFile;

/**
 * Task action to add to {@link JavaCompile} tasks.
 */
public abstract class JavaCompileCheckErrorsAction extends AbstractBndGradleObject implements Action<Task> {

	@Override
	public void execute(final Task task) {
		Logger logger = task.getLogger();
		JavaCompile t = (JavaCompile) task;
		checkErrors(logger);
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
