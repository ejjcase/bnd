package aQute.bnd.gradle;

import aQute.bnd.build.Project;
import org.gradle.api.GradleException;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.Classpath;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectories;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.DisableCachingByDefault;
import org.gradle.work.NormalizeLineEndings;

import java.io.File;

/**
 * Generate source code.
 * @see aQute.bnd.build.ProjectGenerate
 */
@DisableCachingByDefault(because = "Not confirmed to be safely cacheable")
public abstract class Generate extends AbstractBndWorkspaceTask{

	@InputFiles
	@PathSensitive(PathSensitivity.RELATIVE)
	public abstract ConfigurableFileCollection getGenerateInputs();

	@InputFiles
	@Classpath
	public abstract ConfigurableFileCollection getBuildDependencies();

	@InputFiles
	@PathSensitive(PathSensitivity.RELATIVE)
	@NormalizeLineEndings
	public abstract ConfigurableFileCollection getBndConfiguration();

	@OutputDirectories
	public abstract SetProperty<File> getGenerateOutputs();

	@TaskAction
	public void generate() {
		Project bndProject = getBndProject().get();
		try {
			bndProject.getGenerate()
				.generate(false);
		} catch (Exception e) {
			throw new GradleException(String.format("Project %s failed to generate", bndProject.getName()), e);
		}
		checkErrors();
	}
}
