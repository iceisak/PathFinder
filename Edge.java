
import java.util.Objects;


public class Edge<T>{
    


    // basically är one-way streck som pekar åt destinationer
    private T destination;
    private String name; 
    private int weight; 
    private Edge<T> soulmate;

    public Edge(T destination, String name, int weight){
        this.destination = destination;
        this.name = name; 

        if(Double.isNaN(weight)){
            throw new IllegalArgumentException();
        }
        this.weight = weight; 
    }

    public void setSoulmate(Edge<T>  a){
        soulmate = a; 
    }

    public void setWeight(int weight){
        this.weight = weight; 
    }

    public Edge<T>  getSoulmate(){
        return soulmate;
    }

    public T getDestination(){
        return destination;
    }



    public String getName(){
        return name;
    }

    public int getWeight(){
        return weight; 
    }

    public boolean equals(Object other){
        if (other instanceof Edge<?>  edge){
            return Objects.equals(name, edge.name) && Objects.equals(destination, edge.destination);
        }
        return false;
    }

    public int hashCode(){
        return Objects.hash(name, destination);
    }

    public String toString(){
        return "till " + destination + " med " + getName()  + " tar " + getWeight();
        //till G med A -] G tar 3
    }

}
