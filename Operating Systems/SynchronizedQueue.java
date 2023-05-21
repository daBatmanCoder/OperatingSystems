// Name- Jonathan Kandel ID- 206483604

public class SynchronizedQueue<T>
{
	private T[] bufferArray;
	private int producers;
	private int front;
	private int size;

	/**
	 * Constructor. Allocates a buffer (an array) with the given capacity and
	 * resets pointers and counters.
	 * @param capacity Buffer capacity
	 */
	@SuppressWarnings("unchecked")
	// Constructor
	public SynchronizedQueue(int capacity) {
		this.bufferArray = (T[])(new Object[capacity]);
		this.producers = 0;
		this.front = 0;
		this.size = 0;
	}

	/**
	 * Dequeues the first item from the queue and returns it.
	 * If the queue is empty but producers are still registered to this queue, 
	 * this method blocks until some item is available.
	 * If the queue is empty and no more items are planned to be added to this 
	 * queue (because no producers are registered), this method returns null.
	 *
	 * @return The first item, or null if there are no more items
	 * @throws InterruptedException
	 * @see #registerProducer()
	 * @see #unregisterProducer()
	 */
	public synchronized T dequeue() throws InterruptedException
	{
		while (this.producers > 0 && this.size == 0) // if the queue is empty and we have more producers then we wait.
		{
			this.wait();
		}
		if (this.size > 0) // if the queue has elements in it.
		{
			T theFirstItemInTheQueue = this.bufferArray[this.front];
			this.front = (this.front + 1) % this.bufferArray.length;
			this.size--;
			this.notifyAll(); // we want to notify the others that we finished so they can join the program.
			return theFirstItemInTheQueue; // return the first element we enqueued.
		}
		return null; // if the queue is empty and there is 0 producers then we'll return null
	}

	/**
	 * Enqueues an item to the end of this queue. If the queue is full, this 
	 * method blocks until some space becomes available.
	 *
	 * @param item Item to enqueue
	 * @throws InterruptedException
	 */
	public synchronized void enqueue(T anyItem) throws InterruptedException
	{
		while (this.size == this.bufferArray.length) // if the queue is full we wait until a thread finishes
		{
			this.wait();
		}

		this.bufferArray[(this.front + this.size) % this.bufferArray.length] = anyItem;
		this.size++;
		this.notifyAll();
	}

	/**
	 * Returns the capacity of this queue
	 * @return queue capacity
	 */
	public int getCapacity()
	{
		return this.bufferArray.length;
	}

	/**
	 * Returns the current size of the queue (number of elements in it)
	 * @return queue size
	 */
	public int getSize()
	{
		return this.size;
	}

	/**
	 * Registers a producer to this queue. This method actually increases the
	 * internal producers counter of this queue by 1. This counter is used to
	 * determine whether the queue is still active and to avoid blocking of
	 * consumer threads that try to dequeue elements from an empty queue, when
	 * no producer is expected to add any more items.
	 * Every producer of this queue must call this method before starting to 
	 * enqueue items, and must also call <see>{@link #unregisterProducer()}</see> when
	 * finishes to enqueue all items.
	 *
	 * @see #dequeue()
	 * @see #unregisterProducer()
	 */
	public synchronized void registerProducer()
	{
		this.producers++;
	}

	/**
	 * Unregisters a producer from this queue. See <see>{@link #registerProducer()}</see>.
	 *
	 * @see #dequeue()
	 * @see #registerProducer()
	 */
	public synchronized void unregisterProducer()
	{
			this.producers--;
			this.notifyAll();
	}
}