package pl.edu.pwr.worker;

import com.dropbox.core.DbxException;
import com.dropbox.core.json.JsonReader;
import pl.edu.pwr.dropbox.Dropbox;
import pl.edu.pwr.service.FileListener;
import pl.edu.pwr.service.FileSender;
import pl.edu.pwr.service.StatsService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by 7lol on 2016-01-24.
 */
public class MainWorker implements Callable,Worker {
    Path path;
    ExecutorService pool;
    List<Future<?>> future = new ArrayList<>();
    ListenerWorker listenerWorker;
    StatsWorker statsWorker;
    boolean stop=false;

    public MainWorker(String path, int threads) {
        pool = Executors.newFixedThreadPool(threads + 2);// for stats service+listener+file uploading
        this.path = Paths.get(path);
    }

    @Override
    public Object call() throws Exception {
        this.stop=false;
        try {
            Dropbox drop = new Dropbox("sad");
            FileSender sender = new FileSender(drop);
            FileListener listener = new FileListener(sender, this.path, pool);
            listenerWorker = new ListenerWorker(listener);
            StatsService stats = new StatsService(sender);
            statsWorker = new StatsWorker(stats);
            future.add(pool.submit(statsWorker));
            future.add(pool.submit(listenerWorker));
            System.out.println("added "+this.path+" to listening");
        } catch (JsonReader.FileLoadException e) {
            File file = new File("key.json");
            System.out.println(file.getAbsolutePath());
            System.out.println("key.json is missing in "+file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Cannot save auth file");
        } catch (DbxException e) {
            System.out.println("blad");
        }
        return null;
    }
    @Override
    public void stop(){
        this.stop=true;
        listenerWorker.stop();
        statsWorker.stop();
        pool.shutdownNow();
    }

}
