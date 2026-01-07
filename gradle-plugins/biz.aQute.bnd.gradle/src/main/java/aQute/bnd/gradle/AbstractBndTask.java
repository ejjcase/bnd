package aQute.bnd.gradle;

import aQute.bnd.build.Project;
import aQute.bnd.build.Workspace;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.services.ServiceReference;
import org.gradle.api.tasks.Internal;
import org.gradle.work.DisableCachingByDefault;

import java.io.File;
import java.util.Optional;

import static aQute.bnd.gradle.BndUtils.unwrapOptional;

@DisableCachingByDefault(because = "Abstract base class; not used directly")
public abstract class AbstractBndTask extends DefaultTask {

	private final String projectName;
	private final File projectDir;

	public AbstractBndTask() {
		org.gradle.api.Project gradleProject = getProject();
		projectName = gradleProject.getName();
		projectDir = gradleProject.getProjectDir();
	}

	@Internal
	public String getProjectName() {
		return projectName;
	}

	@ServiceReference
	@org.gradle.api.tasks.Optional
	public abstract Property<BndWorkspaceService> getBndWorkspaceService();

	@Internal
	public Optional<Workspace> getBndWorkspace() {
		return unwrapOptional(getBndWorkspaceService())
			.flatMap(service -> service.getAncestorWorkspace(projectDir));
	}

	@Internal
	public Optional<Project> getBndProject() {
		return getBndWorkspace().map(ws -> ws.getProject(getProjectName()));
	}
}
