// Name- Jonathan Kandel ID- 206483604

import java.io.File;

public class Searcher implements Runnable
{
    private int id;
    private String prefix;
    private SynchronizedQueue<File> dirQueue;
    private SynchronizedQueue<File> resultsQueue;
    private SynchronizedQueue<String> auditingQueue;
    private boolean isAudit;

    public Searcher(int id, String prefix,  SynchronizedQueue<File> dirQueue,
                    SynchronizedQueue<File> resultsQueue,  SynchronizedQueue<String> auditingQueue, boolean isAudit)
    {
        this.id = id;
        this.prefix = prefix;
        this.dirQueue = dirQueue;
        this.resultsQueue = resultsQueue;
        this.auditingQueue = auditingQueue;
        this.isAudit = isAudit;
    }
    @Override
    public void run()
    {
        File currnentDirInQueue;

        try
        {
            resultsQueue.registerProducer(); // before we enqueue to the results queue we need to assign the queue a
                                                                                                            // producer
            while ((currnentDirInQueue = dirQueue.dequeue()) != null)
            {
                // for the directory we found we want to least all the files that inside this directory
                File [] fileArray = currnentDirInQueue.listFiles();
                if(fileArray == null) // if the directory is empty ( none of the files were listed )
                {
                    continue; // if none we want to continue to the next directory
                }
                for(File fFile : fileArray) // for every file inside this directory we check the prefix and if file.
                {
                    String currnentFileInQueueStr = fFile.getName(); // gets the file name
                    // checks the relevant condition ( file and prefix )
                    if (fFile.isFile() && prefix.equals(currnentFileInQueueStr.substring(0, prefix.length())))
                    {
                        // if its a file and the prefix matches then we want to insert the file to the results queue.
                        resultsQueue.enqueue(fFile);
                        // and if that happens and the audit flag is true we want to write to the String command queue.
                        if (this.isAudit)
                        {
                            auditingQueue.registerProducer();
                            auditingQueue.enqueue("Searcher on thread id " + this.id + ": file named " +
                                    currnentFileInQueueStr + " was found");
                            auditingQueue.unregisterProducer();
                        }
                    }
                }
            }
            resultsQueue.unregisterProducer();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

}