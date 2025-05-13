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
    private Location arrivalLocation;
    private LocalDateTime departureDate;
    private int hoursDuration;
    private int minutesDuration;

    public Flight(String id, Location departureLocation, Location arrivalLocation, LocalDateTime departureDate, int hoursDuration, int minutesDuration) {
        this.id = id;
        this.departureLocation = departureLocation;
        this.arrivalLocation = arrivalLocation;
        this.departureDate = departureDate;
        this.hoursDuration = hoursDuration;
        this.minutesDuration = minutesDuration;
    }

    public String getId() {
        return id;
    }

    public Location getDepartureLocation() {
        return departureLocation;
    }

    public Location getArrivalLocation() {
        return arrivalLocation;
    }

    public LocalDateTime getDepartureDate() {
        return departureDate;
    }

    public int getHoursDuration() {
        return hoursDuration;
    }

    public int getMinutesDuration() {
        return minutesDuration;
    }
    
}
