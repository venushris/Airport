package airport.controller;

import airport.model.Flight;
import airport.model.Plane;
import airport.model.Location;
import airport.model.Passenger;
import airport.observer.Observer;
import airport.observer.Subject;
import airport.response.Response;
import airport.response.StatusCode;
import airport.storage.Repository;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FlightController implements Subject {
    private static final Pattern FLIGHT_CODE_REGEX = Pattern.compile("^[A-Z]{3}\\d{3}$");

    private final Repository<Flight, String> flightStorage;
    private final Repository<Plane, String> aircraftStorage;
    private final Repository<Location, String> placeStorage;
    private final Repository<Passenger, Long> travelerStorage;
    private final List<Observer> notificationReceivers;

    public FlightController(Repository<Flight, String> flightDb,
                           Repository<Plane, String> planeDb,
                           Repository<Location, String> locationDb,
                           Repository<Passenger, Long> passengerDb) {
        this.flightStorage = flightDb;
        this.aircraftStorage = planeDb;
        this.placeStorage = locationDb;
        this.travelerStorage = passengerDb;
        this.notificationReceivers = new ArrayList<>();
    }

    @Override
    public void registerObserver(Observer observer) {
        if (observer != null && !notificationReceivers.contains(observer)) {
            notificationReceivers.add(observer);
        }
    }

    @Override
    public void removeObserver(Observer observer) {
        notificationReceivers.remove(observer);
    }

    @Override
    public void notifyObservers(String updateType) {
        notificationReceivers.forEach(observer -> observer.update(updateType));
    }

    public Response<Flight> createNewFlight(String flightNumber,
                                          String aircraftId,
                                          String originId,
                                          String destinationId,
                                          String stopoverId,
                                          int year, int month, int day,
                                          int hour, int minute,
                                          int flightHours, int flightMins,
                                          int stopHours, int stopMins) {
        if (flightNumber == null || !FLIGHT_CODE_REGEX.matcher(flightNumber).matches())
            return Response.of(StatusCode.BAD_REQUEST,
                    "Flight ID must follow XXXYYY format");
        if (flightStorage.findById(flightNumber).isPresent())
            return Response.of(StatusCode.CONFLICT,
                    "Flight with ID=" + flightNumber + " already exists");

        Optional<Plane> planeOpt = aircraftStorage.findById(aircraftId);
        if (planeOpt.isEmpty())
            return Response.of(StatusCode.NOT_FOUND,
                    "Aircraft with ID=" + aircraftId + " not found");

        Optional<Location> originOpt = placeStorage.findById(originId);
        if (originOpt.isEmpty())
            return Response.of(StatusCode.NOT_FOUND,
                    "Origin location ID=" + originId + " not found");
        Optional<Location> destOpt = placeStorage.findById(destinationId);
        if (destOpt.isEmpty())
            return Response.of(StatusCode.NOT_FOUND,
                    "Destination location ID=" + destinationId + " not found");

        boolean hasStopover = stopoverId != null && !stopoverId.isBlank();
        Location stopover = null;
        if (hasStopover) {
            Optional<Location> stopOpt = placeStorage.findById(stopoverId);
            if (stopOpt.isEmpty())
                return Response.of(StatusCode.NOT_FOUND,
                        "Stopover location ID=" + stopoverId + " not found");
            stopover = stopOpt.get();
        }

        if (!hasStopover && (stopHours != 0 || stopMins != 0))
            return Response.of(StatusCode.BAD_REQUEST,
                    "Stop duration must be 00:00 when no stopover");
        if (flightHours < 0 || flightMins < 0
                || (flightHours == 0 && flightMins == 0)
                || flightMins > 59)
            return Response.of(StatusCode.BAD_REQUEST,
                    "Invalid flight duration (must be > 00:00 and minutes < 60)");
        if (stopHours < 0 || stopMins < 0 || stopMins > 59)
            return Response.of(StatusCode.BAD_REQUEST,
                    "Invalid stopover duration");

        LocalDateTime departureTime;
        try {
            departureTime = LocalDateTime.of(year, month, day, hour, minute);
        } catch (DateTimeException e) {
            return Response.of(StatusCode.BAD_REQUEST,
                    "Invalid departure date/time");
        }

        Flight newFlight = hasStopover
                ? new Flight(flightNumber, planeOpt.get(), originOpt.get(), 
                           stopover, destOpt.get(), departureTime, 
                           flightHours, flightMins, stopHours, stopMins)
                : new Flight(flightNumber, planeOpt.get(), originOpt.get(), 
                           destOpt.get(), departureTime, flightHours, flightMins);

        Flight storedFlight = flightStorage.save(newFlight);
        Flight flightCopy = createFlightCopy(storedFlight);
        
        notifyObservers("flight");
        return Response.of(StatusCode.CREATED, "Flight created successfully", flightCopy);
    }

    public Response<List<Flight>> retrieveAllFlights() {
        List<Flight> originalFlights = flightStorage.findAll();
        List<Flight> flightCopies = originalFlights.stream()
                .map(this::createFlightCopy)
                .collect(Collectors.toList());
        return Response.of(StatusCode.OK, "Flight list", flightCopies);
    }

    public Response<List<Flight>> getPassengerFlights(long travelerId) {
        Optional<Passenger> passengerOpt = travelerStorage.findById(travelerId);
        if (passengerOpt.isEmpty())
            return Response.of(StatusCode.NOT_FOUND,
                    "Passenger with ID=" + travelerId + " not found");
                    
        List<Flight> orderedFlights = passengerOpt.get().getFlights().stream()
                .sorted(Comparator.comparing(Flight::getDepartureDate))
                .collect(Collectors.toList());
                
        List<Flight> flightCopies = orderedFlights.stream()
                .map(this::createFlightCopy)
                .collect(Collectors.toList());
                
        return Response.of(StatusCode.OK, "Passenger's flights", flightCopies);
    }

    public Response<Flight> assignPassengerToFlight(String flightNum, long passengerId) {
        Optional<Flight> flightOpt = flightStorage.findById(flightNum);
        if (flightOpt.isEmpty())
            return Response.of(StatusCode.NOT_FOUND,
                    "Flight with ID=" + flightNum + " not found");
                    
        Optional<Passenger> passengerOpt = travelerStorage.findById(passengerId);
        if (passengerOpt.isEmpty())
            return Response.of(StatusCode.NOT_FOUND,
                    "Passenger with ID=" + passengerId + " not found");
                    
        Flight flight = flightOpt.get();
        Passenger traveler = passengerOpt.get();

        if (flight.getNumPassengers() >= flight.getPlane().getMaxCapacity()) {
            return Response.of(StatusCode.CONFLICT,
                    "Flight has reached maximum passenger capacity");
        }

        flight.addPassenger(traveler);
        traveler.addFlight(flight);
        flightStorage.update(flight);

        Flight flightCopy = createFlightCopy(flight);
       
        notifyObservers("flight");
        notifyObservers("passenger");
        return Response.of(StatusCode.OK, "Passenger added to flight", flightCopy);
    }

    public Response<Flight> postponeFlight(String flightNum,
                                         int hoursDelay,
                                         int minutesDelay) {
        Optional<Flight> flightOpt = flightStorage.findById(flightNum);
        if (flightOpt.isEmpty())
            return Response.of(StatusCode.NOT_FOUND,
                    "Flight with ID=" + flightNum + " not found");
                    
        if (hoursDelay < 0 || minutesDelay < 0
                || (hoursDelay == 0 && minutesDelay == 0)
                || minutesDelay > 59)
            return Response.of(StatusCode.BAD_REQUEST,
                    "Invalid delay time");
                    
        Flight flight = flightOpt.get();
        flight.delay(hoursDelay, minutesDelay);
        flightStorage.update(flight);
        
        Flight flightCopy = createFlightCopy(flight);
        notifyObservers("flight");
        return Response.of(StatusCode.OK, "Flight delayed successfully", flightCopy);
    }

    private Flight createFlightCopy(Flight original) {
        return original.getScaleLocation() == null
                ? new Flight(original.getId(), original.getPlane(),
                        original.getDepartureLocation(),
                        original.getArrivalLocation(),
                        original.getDepartureDate(),
                        original.getHoursDurationArrival(),
                        original.getMinutesDurationArrival())
                : new Flight(original.getId(), original.getPlane(),
                        original.getDepartureLocation(),
                        original.getScaleLocation(),
                        original.getArrivalLocation(),
                        original.getDepartureDate(),
                        original.getHoursDurationArrival(),
                        original.getMinutesDurationArrival(),
                        original.getHoursDurationScale(),
                        original.getMinutesDurationScale());
    }
}