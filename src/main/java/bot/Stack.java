package bot;

import java.util.ArrayList;

// a stack class that is an improved version over Java's Stack class
public class Stack<E>
{	
	private ArrayList<E> stack; 
	
	public Stack()
	{
		stack = new ArrayList<>();
	}
	
	public void push(E element)
	{
		stack.add(element); 
	}
	
	public E pop()
	{
		if(size() == 0)
		{
			throw new IllegalStateException("Empty stack, cannot pop.");
		}
		return stack.removeLast();
	}
	
	public E peek()
	{
		if(size() == 0)
		{
			throw new IllegalStateException("Empty stack, cannot peek.");
		}
		return stack.getLast();
	}
	
	public boolean isEmpty()
	{
		return stack.isEmpty();
	}
	
	public int size()
	{
		return stack.size();
	}
	
	public void clear()
	{
		stack.clear();
	}
}
