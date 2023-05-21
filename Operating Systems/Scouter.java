// Name- Jonathan Kandel ID- 206483604

import java.io.File;

public class Scouter implements Runnable
{
    private int id;
    private SynchronizedQueue<File> dirQueue;
    private File rootDir;
    private SynchronizedQueue<String> auditingQueue;
    private boolean isAudit;

    public Scouter(int id, SynchronizedQueue<File> dirQueue, File rootDir,
                   SynchronizedQueue<String> auditingQueue, boolean isAudit)
    {
        this.id = id;
        this.dirQueue = dirQueue;
        this.rootDir = rootDir;
        this.auditingQueue = auditingQueue;
        this.isAudit = isAudit;
    }
    @Override
    public void run()
    {
        try {
                this.dirQueue.registerProducer();
                // directoryQueue.enqueue(root);//check how to enter the directory of the root to the queue???
                if (this.isAudit)
                {
                    auditingQueue.registerProducer();
                    auditingQueue.enqueue("General, program has started the search");
                    auditingQueue.unregisterProducer();
                }
                pathToDirectoryQueue(rootDir); // sends the root directory to the recursive function to
                dirQueue.unregisterProducer();                             // list all directories inside it
            }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void pathToDirectoryQueue(File maybeADir) throws InterruptedException
    {
        if(maybeADir.isDirectory())
        {
            dirQueue.enqueue(maybeADir); // we know that the File is a directory then we want to enqueue it.
            File[] dirArray = maybeADir.listFiles(); // lists all the files to File array to check every file
            if (dirArray == null)                                                   // to see if it is a directory.
            {
                return;
            }
            for(File someFile : dirArray) // for each file inside the directory we'll check if is it a directory.
            {
                if(someFile.isDirectory())
                {
                    //is need to add registproducer for dirQue and auditque?
                    dirQueue.enqueue(someFile);
                    // if that happens and the audit flag is true we want to write to the String command queue.
                    if (this.isAudit)
                    {
                        auditingQueue.registerProducer();
                        auditingQueue.enqueue("Scouter on thread id " + this.id + ": directory named " +
                                someFile.getName() + " was scouted");
                        auditingQueue.unregisterProducer();
                    }
                    pathToDirectoryQueue(someFile); // recurisve call for the directory to check if more directories
                }                                                                   // exists inside it
            }
        }
    }
}