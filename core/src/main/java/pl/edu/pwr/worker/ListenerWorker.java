package pl.edu.pwr.worker;

import pl.edu.pwr.service.FileListener;

import java.util.concurrent.Callable;

/**
 *
 */
public class ListenerWorker implements Callable,Worker {

    FileListener listener;
    private boolean stop=false;
    public ListenerWorker(FileListener listener) {
        this.listener = listener;
    }

    @Override
    public void stop(){
        this.stop=true;
    }

    @Override
    public Object call() throws Exception {
        this.stop=false;
        while (true) {
            listener.listen();
            if (stop) break;
        }
        return null;
    }
}
