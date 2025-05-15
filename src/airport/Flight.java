/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package airport;

import java.time.LocalDateTime;

/**
 *
 * @author edangulo
 */
public class Flight {
    
    private final String id;
    private Location departureLocation;
    private Location scaleLocation;
    private Location arrivalLocation;
    private LocalDateTime departureDate;
    private int hoursDurationScale1;
    private int minutesDurationScale1;
    private int hoursDurationScale2;
    private int minutesDurationScale2;

    public Flight(String id, Location departureLocation, Location arrivalLocation, LocalDateTime departureDate, int hoursDurationScale1, int minutesDurationScale1) {
        this.id = id;
        this.departureLocation = departureLocation;
        this.arrivalLocation = arrivalLocation;
        this.departureDate = departureDate;
        this.hoursDurationScale1 = hoursDurationScale1;
        this.minutesDurationScale1 = minutesDurationScale1;
    }

    public Flight(String id, Location departureLocation, Location scaleLocation, Location arrivalLocation, LocalDateTime departureDate, int hoursDurationScale1, int minutesDurationScale1, int hoursDurationScale2, int minutesDurationScale2) {
        this.id = id;
        this.departureLocation = departureLocation;
        this.scaleLocation = scaleLocation;
        this.arrivalLocation = arrivalLocation;
        this.departureDate = departureDate;
        this.hoursDurationScale1 = hoursDurationScale1;
        this.minutesDurationScale1 = minutesDurationScale1;
        this.hoursDurationScale2 = hoursDurationScale2;
        this.minutesDurationScale2 = minutesDurationScale2;
    }

    public String getId() {
        return id;
    }

    public Location getDepartureLocation() {
        return departureLocation;
    }

    public Location getScaleLocation() {
        return scaleLocation;
    }

    public Location getArrivalLocation() {
        return arrivalLocation;
    }

    public LocalDateTime getDepartureDate() {
        return departureDate;
    }

    public int getHoursDurationScale1() {
        return hoursDurationScale1;
    }

    public int getMinutesDurationScale1() {
        return minutesDurationScale1;
    }

    public int getHoursDurationScale2() {
        return hoursDurationScale2;
    }

    public int getMinutesDurationScale2() {
        return minutesDurationScale2;
    }
    
}
