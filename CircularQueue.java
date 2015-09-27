

public class CircularQueue {

	private int front;
	private int rear;
	private Object[] elements;
	public CircularQueue(int capacity)
	{
		front=0;
		rear=-1;
		elements=new Object[capacity];
	}
	public void enqueue(Object data){
		if(isFull())
		{
			System.out.println("Queue overflow");
		}
		else
		{	
			rear=(rear+1)%elements.length;
			elements[rear]=data;
		}
	}
	public Object dequeue()
	{
		if(isEmpty())
		{
			System.out.println("Queue is empty");
			return null;
		}
		else
		{
			Object retData=elements[front];
			elements[front]=null;
			front=(front+1)%elements.length;	
			return retData;
		}
	}
	public Object peek()
	{
		if(isEmpty())
		{
			System.out.println("Queue is empty");
			return null;
		}
		else
			return elements[front];
	}
	public boolean isFull()
	{
		return (front==(rear+1)%elements.length && elements[front]!=null && elements[rear]!=null);
	}
	public boolean isEmpty()
	{
		return elements[front] == null;
	}
	public int size()
	{
		if(rear>=front) return rear-front+1;
		else if(elements[front]!=null) return elements.length-(front-rear)+1;
		else return 0;
	}

}
