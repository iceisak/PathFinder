import javafx.scene.paint.Color;

import javafx.scene.shape.Circle;

public class City extends Circle{
    
    private double x; 
    private double y; 
    private String name; 


    public City(String name, double x, double y){
        this.name = name; 
        this.x = x; 
        this.y = y;
        this.setRadius(10); 
        this.setCenterX(x);
        this.setCenterY(y);
        this.setFill(Color.BLUE);
        this.setId(this.getName());
    }

    public String getName(){
        return name; 
    }
    public double getX(){
        return x; 
    }
    public double getY(){
        return y; 
    }
}
