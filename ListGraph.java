import java.util.*;

public class ListGraph<T> implements Graph<T>{
private final Map<T, Set<Edge<T>>> nodesMap = new HashMap<>();
private int counter;

@Override
public void remove(T node){
    if(!nodesMap.containsKey(node)){
        throw new NoSuchElementException();
    }
    Set<Edge<T>> tempList = new HashSet<>(); 
    for(Edge<T> edge : getEdgesFrom(node)){
        tempList.add(edge);
    }
    for(Edge<T> edge : tempList){
        disconnect(edge.getDestination(), node);
    }

    nodesMap.remove(node);
}


private void checkForDestinationInSet(Set<Edge<T>> edges, T node){
    for(Edge<T> edge : edges){
        if(edge.getDestination() == node){
            throw new IllegalStateException();
        }
    }
}

@Override
public void connect(T one, T two, String name, int weight){
    if(!nodesMap.containsKey(one) || !nodesMap.containsKey(two)){
        throw new NoSuchElementException();
    }
    if(weight < 0){
        throw new IllegalArgumentException();
    }

    Set<Edge<T>> oneEdges = nodesMap.get(one);
    Set<Edge<T>> twoEdges = nodesMap.get(two);
    
    checkForDestinationInSet(oneEdges, two);
    checkForDestinationInSet(twoEdges, one);

    oneEdges.add(new Edge<T>(two, name, weight));
    twoEdges.add(new Edge<T>(one, name, weight));
    
}


@Override
public void disconnect(T one, T two){
    if(!nodesMap.containsKey(one) || !nodesMap.containsKey(two)){
        throw new NoSuchElementException();
    }
    Edge<?> temp = getEdgeBetween(one, two);
    if(temp == null){
        throw new IllegalStateException();
    }
    nodesMap.get(one).remove(temp);

    temp = getEdgeBetween(two, one);
    if(temp == null){
        throw new IllegalStateException();
    }
    nodesMap.get(two).remove(temp);
}

@Override
public void setConnectionWeight(T one, T two, int weight){
    if(weight < 0){
        throw new IllegalArgumentException();
    }
    if(!nodesMap.containsKey(one) || !nodesMap.containsKey(two)){
        throw new NoSuchElementException();
    }
    Edge<T> firstEdge = getEdgeBetween(one, two); 
    Edge<T> secondEdge = getEdgeBetween(two, one);
    if(firstEdge == null || secondEdge == null){
        throw new NoSuchElementException();
        
    }
    else{
        firstEdge.setWeight(weight);
        secondEdge.setWeight(weight);
    }
}

@Override
public Set<T> getNodes(){
    return nodesMap.keySet(); 
}

@Override
public Edge<T> getEdgeBetween(T one, T two){ 
    if(!nodesMap.containsKey(one) || !nodesMap.containsKey(two)){
        throw new NoSuchElementException();
    }
    for(Edge<T> edge : nodesMap.get(one)){
        if(edge.getDestination().equals(two)){
            return edge;  
        }
    }
    return null; 
}

@Override
public Set<Edge<T>> getEdgesFrom(T node){ //returns a list of some kind
    if(!nodesMap.containsKey(node)){
        throw new NoSuchElementException();
    }
    return nodesMap.get(node);
}

@Override
public boolean pathExists(T one, T two){
    if(!nodesMap.containsKey(one) || !nodesMap.containsKey(two)){
        return false; 
    }
    Set<T> connection = new HashSet<>();

    depthFireVisitAll(one, connection);
    if(connection.contains(two)){
        return true; 
    }
    return false; 
}

@Override
public List<Edge<T>> getPath(T from, T to){
    if(!pathExists(from, to)){
        return null;
    }
    Map<T, T> connection = new HashMap<>();

    depthFirstConnection(from, null, connection);

    if(!connection.containsKey(to)){
        return Collections.emptyList();
    }

    //List<Edge<T>> path = new ArrayList<>();

    return gatherPath(from, to, connection); 
}

private List<Edge<T>> gatherPath(T from, T to, Map<T, T> connection){
    LinkedList<Edge<T>> path = new LinkedList<>();
    T current = to; 
    while (!current.equals(from)){
        T next = connection.get(current);
        Edge<T> edge = getEdgeBetween(next, current);
        path.addFirst(edge);
        current = next; 
    }
    return Collections.unmodifiableList(path); 
}

private void depthFirstConnection(T to, T from, Map<T, T> connection){
    connection.put(to, from);
    for(Edge<T> edge : nodesMap.get(to)){
        if( !connection.containsKey(edge.getDestination())){
            depthFirstConnection(edge.getDestination(), to, connection);
        }
    }
}

private void depthFireVisitAll(T current, Set<T> visited){
    visited.add(current);
    for(Edge<T> edge : nodesMap.get(current)){
        if(!visited.contains(edge.getDestination())) {
            depthFireVisitAll(edge.getDestination(), visited);
        }
    }
}


@Override
public String toString(){
    StringBuilder sb = new StringBuilder();
    for(T node : nodesMap.keySet()){
        sb.append(node).append(": ").append(nodesMap.get(node)).append("\n");
    }
    return sb.toString();
}


@Override
public void add(T node) {
    nodesMap.putIfAbsent(node, new HashSet<>());
}




}

