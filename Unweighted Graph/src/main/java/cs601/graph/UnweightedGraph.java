package cs601.graph;
import java.util.*;
public class UnweightedGraph<ID extends Comparable, N extends Node<ID>> implements Graph<ID,N>
{
    List<N> list = new ArrayList<>();
    LinkedHashMap<ID, N> map = new LinkedHashMap<>();
    List<N> reachableList = new ArrayList<>();
    List<N> allNodesList = new ArrayList<>();
    List<N> pathList = new ArrayList<>();
    public N addNode(N node)
    {
        if(node != null)
        {
            if(!map.containsKey(node.getName()))
            {
                map.put(node.getName(),node);
            }
        }
        return map.get(node.getName());
    }
    @Override
    public List<N> getAllNodes(ID start, ID stop)
    {
        if(start == null || stop == null)
        {
            return Collections.emptyList();
        }
        if(allNodesList.size() == 0)
        {
            allNodesList.add(map.get(start));
        }
        while(start != null && map.get(start).getEdgeCount() != 0)
        {
            N startObj = map.get(start);
            for (int i = 0; i < startObj.getEdgeCount(); i++)
            {
                start = startObj.getEdge(i);
                N nodeObject = map.get(start);
                try
                {
                    if (nodeObject.edges().toString().contains(stop.toString()))
                    {
                        if (!allNodesList.contains(nodeObject) && nodeObject != map.get(stop))
                        {
                            allNodesList.add(nodeObject);
                            getAllNodes(start, stop);
                        }
                    }
                }
                catch(NullPointerException e)
                {
                }
            }
        }
        if(!allNodesList.contains(map.get(stop)))
        {
            allNodesList.add(map.get(stop));
        }
        return allNodesList;
    }
    @Override
    public int getMinPathLength(ID start, ID stop)
    {
        if(start == stop)
        {
            return 0;
        }
        if(start == null || stop == null)
        {
            return -1;
        }
        int exitStatus =0;
        while(start != null && map.get(start).getEdgeCount() != 0)
        {
            N startObj = map.get(start);
            for (int i = 0; i < startObj.getEdgeCount(); i++)
            {
                start = startObj.getEdge(i);
                N nodeObject = map.get(start);
                try
                {
                    if (nodeObject.edges().toString().contains(stop.toString()))
                    {
                        if (!pathList.contains(nodeObject))
                        {
                            pathList.add(nodeObject);
                            pathList.add(map.get(stop));
                            exitStatus = 1;
                            break;
                        }
                    }
                }
                catch(NullPointerException e)
                {
                }
            }
            if(exitStatus ==1 )
            {
                break;
            }
        }
        if(pathList.size() == 0)
        {
            return -1;
        }
        else
        {
            return pathList.size();
        }
    }
    @Override
    public List<N> getAllReachableNodes(ID start)
    {
        if(start == null)
        {
            return Collections.emptyList();
        }
        while(start != null)
        {
            N startObj = map.get(start);
            reachableList.add(startObj);
            for(int i=0;i < startObj.getEdgeCount();i++)
            {
                start = startObj.getEdge(i);
                getAllReachableNodes(start);
            }
            break;
        }
        return reachableList;
    }
    @Override
    public List<ID> getRootNames()
    {
        List<ID> rootList = new ArrayList<>();
        HashMap<ID,Integer> rootMap = new HashMap<>();
        for(ID key : map.keySet()) {
            if (!rootMap.containsKey(key)) {
                rootMap.put(key, 1);
            } else {
                int value = rootMap.get(key);
                rootMap.put(key, value + 1);
            }
            int noOfEdges = map.get(key).getEdgeCount();
            for (int j = 0; j < noOfEdges; j++) {
                if (!rootMap.containsKey(map.get(key).getEdge(j))) {
                    rootMap.put(map.get(key).getEdge(j), 1);
                } else {
                    int value = rootMap.get(map.get(key).getEdge(j));
                    rootMap.put(map.get(key).getEdge(j), value + 1);
                }
            }
        }
        for(ID key: rootMap.keySet())
        {
            if(rootMap.get(key) == 1)
            {
                rootList.add(key);
            }
        }
        return rootList;
    }
    @Override
    public String toString()
    {
        String finalString = "";
        for(ID key : map.keySet())
        {
            N nodeObject = map.get(key);
            if(nodeObject.getEdgeCount() !=0)
            {
                for(int i=0; i < nodeObject.getEdgeCount();i++)
                {
                    String source = key.toString();
                    String target = nodeObject.getEdge(i).toString();
                    finalString += source+" -> "+target+"\n";
                }
            }
        }
        return finalString;
    }
}