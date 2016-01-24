package pl.edu.pwr.worker;

import pl.edu.pwr.service.FileSender;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

/**
 * Created by Pawel on 2014-12-05.
 */
public class FileWorker implements Callable{

    private FileSender fileSender;
    Path path;
    String mainPath;
    int event; //0=create 1=update 2=delete 3=create dir
    public FileWorker(FileSender fileSender, Path path,String mainPath,String event) {
        this.fileSender = fileSender;
        this.mainPath=mainPath;
        this.path=path;
        switch (event.toUpperCase()){
            case "CREATE" :{if(Files.isDirectory(path)){
                this.event=3;break;
            }else
                this.event=0;break;}
            case "UPDATE" :{this.event=1;break;}
            case "DELETE" :{this.event=2;break;}
            default: this.event=0;
        }
    }

    @Override
    public Void call() throws Exception {
        fileSender.send(path,mainPath,event);
        return null;
    }
}
