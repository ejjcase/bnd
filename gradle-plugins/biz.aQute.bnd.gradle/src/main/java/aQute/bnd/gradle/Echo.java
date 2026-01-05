package aQute.bnd.gradle;

import aQute.bnd.build.Project;
import aQute.bnd.build.Workspace;
import aQute.bnd.osgi.About;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Classpath;
import org.gradle.api.tasks.CompileClasspath;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.DisableCachingByDefault;

import java.util.Formatter;

import static aQute.bnd.gradle.BndUtils.unwrap;

/**
 * Print information about the project.
 */
@DisableCachingByDefault(because = "No output files")
public abstract class Echo extends AbstractBndWorkspaceTask {

	private ProjectLayout layout;

	public Echo() {
		layout = getProject().getLayout();
	}

	@InputFiles
	@PathSensitive(PathSensitivity.RELATIVE)
	public abstract ConfigurableFileCollection getSourceDirectories();

	@InputFiles
	@PathSensitive(PathSensitivity.RELATIVE)
	public abstract ConfigurableFileCollection getTestSourceDirectories();

	@InputDirectory
	@PathSensitive(PathSensitivity.RELATIVE)
	public abstract DirectoryProperty getCompileDestinationDirectory();

	@InputDirectory
	@PathSensitive(PathSensitivity.RELATIVE)
	public abstract DirectoryProperty getTestCompileDestinationDirectory();

	@InputFiles
	@CompileClasspath
	public abstract ConfigurableFileCollection getCompileClasspath();

	@InputFiles
	@Classpath
	public abstract ConfigurableFileCollection getTestClasspath();

	@InputFiles
	@PathSensitive(PathSensitivity.RELATIVE)
	public abstract ConfigurableFileCollection getAllSrcDirs();

	@InputFiles
	@Classpath
	public abstract ConfigurableFileCollection getBootstrapClasspath();

	@InputFiles
	@PathSensitive(PathSensitivity.RELATIVE)
	public abstract ConfigurableFileCollection getDeliverables();

	@Input
	public abstract Property<String> getExecutable();

	@Input
	public abstract Property<Integer> getRelease();

	@Input
	public abstract Property<String> getSourceCompatibility();

	@Input
	public abstract Property<String> getTargetCompatibility();

	@Input
	public abstract Property<String> getJavacProfile();

	@TaskAction
	public void execute() throws Exception {
		Workspace bndWorkspace = getBndWorkspace().get();
		Project project = getBndProject().get();

		try (Formatter f = new Formatter()) {
			f.format("------------------------------------------------------------%n");
			f.format("Project %s // Bnd version %s%n", project.getName(), About.getBndVersion());
			f.format("------------------------------------------------------------%n");
			f.format("%n");
			f.format("project.workspace:      %s%n", bndWorkspace.getBase());
			f.format("project.name:           %s%n", project.getName());
			f.format("project.dir:            %s%n", layout.getProjectDirectory());
			f.format("target:                 %s%n", unwrap(layout.getBuildDirectory()));
			f.format("project.dependson:      %s%n", project.getDependson());
			f.format("project.sourcepath:     %s%n", getSourceDirectories().getAsPath());
			f.format("project.output:         %s%n", unwrap(getCompileDestinationDirectory().getAsFile()));
			f.format("project.buildpath:      %s%n", getCompileClasspath()
				.getAsPath());
			f.format("project.allsourcepath:  %s%n", getAllSrcDirs().getAsPath());
			f.format("project.testsrc:        %s%n", getTestSourceDirectories().getAsPath());
			f.format("project.testoutput:     %s%n", unwrap(getTestCompileDestinationDirectory().getAsFile()));
			f.format("project.testpath:       %s%n", getTestClasspath().getAsPath());
			if (!getBootstrapClasspath().isEmpty()) {
				f.format("project.bootclasspath:  %s%n", getBootstrapClasspath().getAsPath());
			}
			f.format("project.deliverables:   %s%n", getDeliverables().getFiles());
			f.format("javac:                  %s%n", unwrap(getExecutable()));
			if (getRelease().isPresent()) {
				f.format("--release:              %s%n", unwrap(getRelease()));
			} else {
				f.format("-source:                %s%n", unwrap(getSourceCompatibility()));
				f.format("-target:                %s%n", unwrap(getTargetCompatibility()));
			}
			if (getJavacProfile().isPresent()) {
				f.format("-profile:               %s%n", unwrap(getJavacProfile()));
			}
			System.out.print(f);
		}
		checkErrors(true);
	}

}
