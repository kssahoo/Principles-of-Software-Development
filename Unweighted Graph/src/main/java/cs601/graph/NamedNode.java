package cs601.graph;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** A node with a name and list of directed edges (edges do not have labels). */

public class NamedNode implements Node<String>
{
    String name;
    List<String> edgeList = new ArrayList<String>();;
    public NamedNode(String name)
    {
        this.name = name;
    }


    @Override
    public String getEdge(int i) throws IndexOutOfBoundsException
    {
        return edgeList.get(i);
    }

    @Override
    public int getEdgeCount()
    {
        return edgeList.size();
    }

    @Override
    public void addEdge(Node<String> target)
    {
        if(target != null)
        {
            if (!edgeList.contains(target.getName()))
            {
                edgeList.add(target.getName());
            }
        }
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public Iterable<String> edges()
    {
        if(edgeList.size() != 0)
        {
            return edgeList;
        }
        else
        {
            return null;
        }
    }

    @Override
    public int compareTo(Node<String> o)
    {
        return this.getName().compareTo(o.getName());
    }
    @Override
    public String toString()
    {
        return name.toString();
    }


}
