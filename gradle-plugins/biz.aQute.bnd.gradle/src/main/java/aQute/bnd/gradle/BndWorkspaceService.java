package aQute.bnd.gradle;

import aQute.bnd.build.Workspace;
import org.gradle.api.Action;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.services.BuildService;
import org.gradle.api.services.BuildServiceParameters;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Gradle {@link BuildService} that holds {@link Workspace} instances for
 * use by tasks, allowing tasks to use the configuration cache.
 */
public abstract class BndWorkspaceService implements BuildService<BndWorkspaceService.Params>, AutoCloseable {

	/**
	 * Parameters for configuring {@link BndWorkspaceService}.
	 */
	public static abstract class Params implements BuildServiceParameters {

		public abstract ListProperty<WorkspaceConfig> getWorkspaceConfigs();

		/**
		 * Register a {@link Workspace} to be created on demand.
		 * @param rootDir The workspace root directory.
		 * @param configAction Configures the workspace.
		 */
		public void registerWorkspace(File rootDir, Action<WorkspaceConfig> configAction) {
			getWorkspaceConfigs().get().forEach(existingConfig -> {
				if (Objects.equals(rootDir, existingConfig.getRootDir())) {
					throw new IllegalStateException("A workspace at " + rootDir + " is already registered");
				}
			});
			WorkspaceConfig workspaceConfig = new WorkspaceConfig(rootDir);
			configAction.execute(workspaceConfig);
			getWorkspaceConfigs().add(workspaceConfig);
		}
	}

	private final Map<File, Workspace> workspaces = new LinkedHashMap<>(1);

	public Optional<Workspace> getWorkspace(File rootDir) {
		if (workspaces.containsKey(rootDir)) {
			return Optional.of(workspaces.get(rootDir));
		}
		Optional<WorkspaceConfig> config = getParameters().getWorkspaceConfigs().get().stream()
			.filter(d -> rootDir.equals(d.getRootDir()))
			.findFirst();
		if (config.isPresent()) {
			Workspace workspace = config.get().createWorkspace();
			workspaces.put(rootDir, workspace);
			return Optional.of(workspace);
		}
		return Optional.empty();
	}

	@Override
	public void close() {
		workspaces.values().forEach(Workspace::close);
		workspaces.clear();
	}
}
