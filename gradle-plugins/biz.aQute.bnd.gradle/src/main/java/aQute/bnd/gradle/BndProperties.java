package aQute.bnd.gradle;

import aQute.bnd.build.Project;
import aQute.bnd.osgi.About;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.DisableCachingByDefault;

import java.util.Formatter;

/**
 * Print the Bnd project properties.
 */
@DisableCachingByDefault(because = "No output files")
public abstract class BndProperties extends AbstractBndWorkspaceTask {

	@TaskAction
	public void execute() {
		Project bndProject = getBndProject().get();
		try (Formatter f = new Formatter()) {
			f.format("------------------------------------------------------------%n");
			f.format("Project %s // Bnd version %s%n", getProjectName(), About.getBndVersion());
			f.format("------------------------------------------------------------%n");
			f.format("%n");
			bndProject.getPropertyKeys(true)
				.stream()
				.sorted()
				.forEachOrdered(key -> f.format("%s: %s%n", key, bndProject.getProperty(key, "")));
			f.format("%n");
			System.out.print(f.toString());
		}
		checkErrors(true);
	}
}
