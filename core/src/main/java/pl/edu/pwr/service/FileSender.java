package pl.edu.pwr.service;

import com.dropbox.core.DbxException;
import pl.edu.pwr.dropbox.Dropbox;

import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: pszczerbicki
 */
public class FileSender {

    AtomicInteger statsCreate = new AtomicInteger(0);
    AtomicInteger statsProcess = new AtomicInteger(0);
    Dropbox drop;

    public FileSender(Dropbox drop){
        this.drop=drop;
    }

    public void send(Path path,String mainPath, int event) throws IOException, DbxException, ParseException {
        statsProcess.incrementAndGet();
        sendFile(path,mainPath,event);
    }

    public int getStatsCreate() {
        return statsCreate.getAndSet(0);
    }

    public int getStatsProcess() {
        return statsProcess.getAndSet(0);
    }

    private void sendFile(Path f, String mainPath, int event) throws IOException, DbxException, ParseException {
        String path=f.toAbsolutePath().toString();
        path=path.substring(mainPath.length());
        if (event == 0){
            if (drop.uploadFile(f.toAbsolutePath().toString(),path)) statsCreate.incrementAndGet();
            return;
        }
        if (event == 1){
            if (drop.updateFile(f.toAbsolutePath().toString(),path)) statsCreate.incrementAndGet();
            return;
        }
        if (event == 2){
            if (drop.deleteFile(path));
            return;
        }
        if (event == 3){
            if (drop.createFolder(path));
            return;
        }
    }
}
