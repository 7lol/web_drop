package pl.edu.pwr.service;

import pl.edu.pwr.worker.FileWorker;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * Created by Pawel on 2014-12-05.
 */
public class FileListener {

    private FileSender sender;

    private ExecutorService pool;

    private final WatchService watcher;

    private final Map<WatchKey, Path> keys;
    private String mainPath;

    public FileListener(FileSender sender, Path dir,ExecutorService pool) throws IOException {
        this.sender = sender;
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey, Path>();
        mainPath = dir.toAbsolutePath().toString();
        this.pool=pool;
        registerAll(dir);
    }

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
        keys.put(key, dir);
    }

    private void registerAll(final Path start) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException {
                register(dir);
                File file = new File(dir.toAbsolutePath().toString());
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    if (!files[i].isDirectory()) {
                        // adding files
                        String path = (files[i].getAbsolutePath().toString().substring(mainPath.length()));
                        pool.submit(new FileWorker(sender, Paths.get(files[i].getAbsolutePath()), mainPath, "update"));
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }


    public void listen() throws InterruptedException, IOException {
        // wait for key to be signalled
        WatchKey key;
        try {
            key = watcher.take();
        } catch (InterruptedException x) {
            return;
        }

        Path dir = keys.get(key);
        if (dir == null) {
            System.err.println("WatchKey not recognized!!");
            return;
        }
        for (WatchEvent<?> event : key.pollEvents()) {
            WatchEvent.Kind kind = event.kind();

            // TBD - provide example of how OVERFLOW event is handled
            if (kind == StandardWatchEventKinds.OVERFLOW) {
                continue;
            }

            // Context for directory entry event is the file name of entry
            WatchEvent<Path> ev = cast(event);
            Path name = ev.context();
            Path child = dir.resolve(name);
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) {
            }
            if (!Files.isDirectory(child)) {
                sendInThread(child, event.kind());
            }
            // if directory is created, and watching recursively, then
            // register it and its sub-directories
            if ((kind == StandardWatchEventKinds.ENTRY_CREATE)) {
                try {
                    if (Files.isDirectory(child)) {
                        sendInThread(child, event.kind());
                        registerAll(child);
                    }
                } catch (IOException ignored) {
                    // ignore to keep sample readbale
                }
            }
        }

        // reset key and remove from set if directory no longer accessible
        boolean valid = key.reset();
        if (!valid) {
            keys.remove(key);

            // all directories are inaccessible
            if (keys.isEmpty()) {
                return;
            }
        }
    }


    public void sendInThread(Path path, WatchEvent.Kind event) {
        if (event == StandardWatchEventKinds.ENTRY_CREATE) {
            pool.submit(new FileWorker(sender, path, mainPath, "create"));
        } else if (event == StandardWatchEventKinds.ENTRY_MODIFY) {
            pool.submit(new FileWorker(sender, path, mainPath, "update"));
        } else if (event == StandardWatchEventKinds.ENTRY_DELETE) {
            pool.submit(new FileWorker(sender, path, mainPath, "delete"));
        }
    }
}
