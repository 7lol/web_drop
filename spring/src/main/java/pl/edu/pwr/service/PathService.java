package pl.edu.pwr.service;

import pl.edu.pwr.model.Path;

import java.util.List;



public interface PathService {
	
	Path findById(long id);
	
	Path findByName(String name);
	
	void savePath(Path path);
	
	void updatePath(Path path);

	void startPath(Path path);

	void stopPath(Path path);
	
	void deletePathById(long id);

	List<Path> findAllPaths();
	
	void deleteAllPaths();
	
	public boolean isPathExist(Path path);
	
}
