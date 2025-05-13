/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package airport;

/**
 *
 * @author edangulo
 */
public class Plane {
    
    private final String id;
    private final int maxCapacity;

    public Plane(String id, int maxCapacity) {
        this.id = id;
        this.maxCapacity = maxCapacity;
    }

    public String getId() {
        return id;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }
    
}
