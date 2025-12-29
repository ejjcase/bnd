package aQute.bnd.gradle;

import aQute.bnd.build.Workspace;
import org.gradle.api.Action;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.services.BuildService;
import org.gradle.api.services.BuildServiceParameters;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Gradle {@link BuildService} that holds {@link Workspace} instances for
 * use by tasks, allowing tasks to use the configuration cache.
 */
public abstract class BndWorkspaceService implements BuildService<BndWorkspaceService.Params>, AutoCloseable {

	public record WorkspaceInitializationData(File rootDir, String cnf, boolean isOffline) implements Serializable {

		private Workspace createWorkspace() {
			try {
				Workspace workspace = new Workspace(rootDir, cnf);
				workspace.setOffline(isOffline);
				return workspace;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public interface Params extends BuildServiceParameters {

		ListProperty<WorkspaceInitializationData> getWorkspaceInitializationData();

		/**
		 * Register a {@link Workspace} to be created on demand.
		 * @param rootDir The workspace's root directory.
		 * @param cnf The path to the workspace's "cnf" project relative to {@code rootDir}.
		 * @param isOffline Whether the workspace should be offline.
		 */
		default void registerWorkspace(File rootDir, String cnf, boolean isOffline) {
			if (rootDir == null || !rootDir.isDirectory()) {
				throw new IllegalArgumentException("rootDir must be an existing directory");
			}
			if (cnf == null || !new File(rootDir, cnf).isDirectory()) {
				throw new IllegalArgumentException("cnf must be an existing directory relative to rootDir");
			}
			getWorkspaceInitializationData().get().forEach(existingData -> {
				if (existingData.rootDir().equals(rootDir)) {
					throw new IllegalStateException("A workspace at " + rootDir + " is already registered");
				}
			});
			getWorkspaceInitializationData().add(new WorkspaceInitializationData(rootDir, cnf, isOffline));
		}
	}

	private final Map<File, Workspace> workspaces = new LinkedHashMap<>(1);

	public Optional<Workspace> getWorkspace(File rootDir) {
		if (workspaces.containsKey(rootDir)) {
			return Optional.of(workspaces.get(rootDir));
		}
		Optional<WorkspaceInitializationData> data = getParameters().getWorkspaceInitializationData().get().stream()
			.filter(d -> rootDir.equals(d.rootDir()))
			.findFirst();
		if (data.isPresent()) {
			Workspace workspace = data.get().createWorkspace();
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
