package pl.edu.pwr.service;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class StatsService {

    private List<FileSender> senders=new ArrayList<>();

    public StatsService(FileSender sender) {
        this.senders.clear();
        this.senders.add(sender);
    }

    public void addSender(FileSender sender)
    {
        senders.add(sender);
    }

    public void deleteSender(FileSender sender){
        senders.remove(sender);
    }

    public double getStatsCreate()
    {
        int x=0;
        for (FileSender sender : senders) {
            x+=sender.getStatsCreate();
        }
        return x;
    }

    public double getStatsProcess()
    {
        int x=0;
        for (FileSender sender : senders) {
            x+=sender.getStatsProcess();
        }
        return x;
    }
}
