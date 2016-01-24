package pl.edu.pwr.worker;

import pl.edu.pwr.service.StatsService;

import java.util.concurrent.Callable;

/**
 * Created by Pawel on 2014-12-05.
 */
public class StatsWorker implements Callable,Worker {

    private StatsService statsService;
    private double processed,created;
    private long lasttime;
    private boolean stop=false;


    public StatsWorker(StatsService statsService) {
        this.statsService = statsService;
        lasttime=System.currentTimeMillis();
    }

    public void generateStats(){
        created=statsService.getStatsCreate();
        processed=statsService.getStatsProcess();
    }
    @Override
    public void stop(){
        this.stop=true;
    }

    @Override
    public Object call() throws Exception {
        this.stop=false;
        while(true){
            long x=System.currentTimeMillis();
            if (lasttime+10000>x){
                Thread.sleep(lasttime-x+10000);}
            generateStats();
            System.out.println("Files processed in last 10secs: "+processed);
            System.out.println("Files uploaded in last 10secs: "+created);
            Thread.sleep(10);
            lasttime=System.currentTimeMillis();        //stats produced in every 10 seconds
            if (stop) break;
        }
        return null;
    }
}
