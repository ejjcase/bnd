package aQute.bnd.gradle;

import org.gradle.api.Action;
import org.gradle.api.Task;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Internal;

/**
 * Task action that checks for Bnd errors and optionally fails if errors are found.
 */
public abstract class CheckErrorsTaskAction extends AbstractBndGradleObject implements Action<Task> {

	@Override
	public void execute(final Task task) {
		checkErrors(task.getLogger());
	}
}
