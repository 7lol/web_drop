package pl.edu.pwr.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pwr.model.Path;
import pl.edu.pwr.worker.MainWorker;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

@Service("userService")
@Transactional
public class PathServiceImpl implements PathService {

    private static final AtomicLong counter = new AtomicLong();

    private static List<Path> paths;

    HashMap<Integer,MainWorker> workers = new HashMap<Integer,MainWorker>();

    ExecutorService pool = Executors.newFixedThreadPool(10);

    static {
        paths = populateDummyUsers();
    }

    public List<Path> findAllPaths() {
        return paths;
    }

    public Path findById(long id) {
        for (Path path : paths) {
            if (path.getId() == id) {
                return path;
            }
        }
        return null;
    }

    public Path findByName(String name) {
        for (Path path : paths) {
            if (path.getName().equalsIgnoreCase(name)) {
                return path;
            }
        }
        return null;
    }

    public void savePath(Path path) {
        path.setId((int) counter.incrementAndGet());
        paths.add(path);
    }

    public void updatePath(Path path) {
        if (Files.isDirectory(Paths.get(path.getPath()))) {
            int index = paths.indexOf(path);
            paths.set(index, path);
        }
    }

    public void deletePathById(long id) {

        for (Iterator<Path> iterator = paths.iterator(); iterator.hasNext(); ) {
            Path path = iterator.next();
            if (path.getId() == id) {
                iterator.remove();
            }
        }
    }

    public boolean isPathExist(Path path) {
        return findByPath(path.getPath()) != null;
    }

    private Path findByPath(String path) {
        for (Path temp : paths) {
            if (temp.getPath().equalsIgnoreCase(path)) {
                return temp;
            }
        }
        return null;
    }

    public void deleteAllPaths() {
        paths.clear();
    }

    private static List<Path> populateDummyUsers() {
        List<Path> paths = new ArrayList<Path>();
        return paths;
    }

    public void startPath(Path path) {
        for (Iterator<Path> iterator = paths.iterator(); iterator.hasNext(); ) {
            Path temp = iterator.next();
            if (temp.getId() == path.getId()) {
                temp.setStarted(true);
                MainWorker worker = new MainWorker(path.getPath(),path.getThreads());
                workers.put((int)temp.getId(),worker);
                pool.submit(worker);
            }
        }
    }

    public void stopPath(Path path) {
        for (Iterator<Path> iterator = paths.iterator(); iterator.hasNext(); ) {
            Path temp = iterator.next();
            if (temp.getId() == path.getId()) {
                temp.setStarted(false);
                workers.get((int)temp.getId()).stop();
            }
        }
    }
}
