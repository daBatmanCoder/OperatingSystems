// Name- Jonathan Kandel ID- 206483604

import java.io.File;

public class DiskSearcher
{
    public static final int AUDITING_QUEUE_CAPACITY = 50;
    public static final int DIRECTORY_QUEUE_CAPACITY = 50;
    public static final int RESULTS_QUEUE_CAPACITY = 50;

    public static void main (String[] args) throws InterruptedException
    {
        long programStart = System.nanoTime(); // starts the clock.

        // We'll assume that our input parameters are ok.

        boolean auditFlag = Boolean.parseBoolean(args[0]); // 0: <boolean of milestoneQueueFlag>
        String prefix = args[1]; // 1:<file-prefix>
        File rootDirectory = new File(args[2]); // 2: <root directory>
        File destDir = new File(args[3]); // 3: <destination directory>
        int numOfSearchers = Integer.parseInt(args[4]); // 4: <(num) of searchers>
        int numOfCopiers = Integer.parseInt(args[5]); // 5: <(num) of copiers>

        int id = 0;
        String theStringFromTheQueue;

        // initializing the Queue's ( directory, result and audit )
        SynchronizedQueue<File> directoryQueue = new SynchronizedQueue<File>(DIRECTORY_QUEUE_CAPACITY);
        SynchronizedQueue<File> resultQueue = new SynchronizedQueue<File>(RESULTS_QUEUE_CAPACITY);
        SynchronizedQueue<String> auditQueue = null; // only if the audit flag is true then we'll initialize the queue

        if(auditFlag) // if the flag is true we initialize the queue
        {
            auditQueue = new SynchronizedQueue<String>(AUDITING_QUEUE_CAPACITY);
        }

        Scouter scouter = new Scouter(id, directoryQueue, rootDirectory, auditQueue, auditFlag); // we create the Scouter instance
        id++; // advanced the id.

        Thread scouterThread = new Thread(scouter); // initialize the Thread for the scouter
        scouterThread.start(); // started the runnable. ( run function from the class we created )

        Thread[] searchersThreadArray = new Thread[numOfSearchers]; // initializing the threads for the searchers.
        for(int i = 0; i < numOfSearchers; i++)
        {
            // we want to save the location each thread as started for multi-thread work ( join )
            searchersThreadArray[i] = new Thread(new Searcher(id, prefix, directoryQueue, resultQueue, auditQueue, auditFlag));
            searchersThreadArray[i].start(); // started the run
            id++;
        }

        Thread[] copiersThreadArray = new Thread[numOfCopiers]; // initializing the threads for the copiers
        for(int i = 0; i < numOfCopiers; i++)
        {
            copiersThreadArray[i] = new Thread(new Copier(id, destDir, resultQueue, auditQueue,auditFlag));
            copiersThreadArray[i].start();
            id++;
        }
        // now we make all the threads join together for all threads, scouters, copiers and searchers.
        scouterThread.join();

        for (int i = 0; i < numOfSearchers; i++)
        {
            try
            {
                searchersThreadArray[i].join();
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < numOfCopiers; i++)
        {
            try
            {
                copiersThreadArray[i].join();
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        //if our printing flag is true then we want to print all the operations we did.
        if(auditFlag)
        {
            while((theStringFromTheQueue = auditQueue.dequeue()) != null)
            {
                System.out.println(theStringFromTheQueue);
            }
        }

        long programEnd = System.nanoTime();// stopper stop.
        //Now lets print:  milisecond to second --> 1*e^-9 that means:
        System.out.println("The total elapsed time of the program is " + ((programEnd - programStart) * 0.000000001) + " seconds");
    }
}