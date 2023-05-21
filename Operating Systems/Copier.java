// Name- Jonathan Kandel ID- 206483604

import java.io.*;
import java.nio.*;

public class Copier implements Runnable
{
    public static final int COPY_BUFFER_SIZE  = 4096;
    private int id;
    private File destDir;
    private SynchronizedQueue<File> resultsQueue;
    private SynchronizedQueue<String> auditingQueue;
    private boolean isAudit;

    // Constructor
    public Copier(int id, File destDir,  SynchronizedQueue<File> resultsQueue,
                  SynchronizedQueue<String> auditingQueue,  boolean isAudit)
    {
        this.id = id;
        this.destDir = destDir;
        this.resultsQueue = resultsQueue;
        this.auditingQueue = auditingQueue;
        this.isAudit = isAudit;
    }

    @Override // run function from the interface runnable
    public void run()
    {
        File source;
        InputStream fileToReadFrom = null;
        OutputStream fileToWriteTo = null;
        byte[] byteArray = new byte[COPY_BUFFER_SIZE];
        int byteToWrite;

        try
        {
            while ((source = resultsQueue.dequeue()) != null)
            {
                fileToReadFrom = new FileInputStream(source);
                fileToWriteTo = new FileOutputStream(new File(this.destDir,source.getName()));
                while((byteToWrite = fileToReadFrom.read(byteArray)) > 0)
                {
                    fileToWriteTo.write(byteArray, 0, byteToWrite); // writes the Bytes to fileToWrite. ( output stream )
                }
                fileToReadFrom.close();
                fileToWriteTo.close();
                // if audit flag is true we want to write to the string queue.
                if (this.isAudit)
                {
                    auditingQueue.registerProducer();
                    auditingQueue.enqueue("Copier from thread id " + this.id + ": file named " +
                            source.getName() + " was copied");
                    auditingQueue.unregisterProducer();
                }
            }
        }
        catch (InterruptedException | IOException e)
        {
            e.printStackTrace();
        }
    }

}