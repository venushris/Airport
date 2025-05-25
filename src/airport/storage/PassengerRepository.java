package airport.storage;

import airport.model.Passenger;
import java.util.*;
import java.util.stream.Collectors;


public class PassengerRepository implements Repository<Passenger, Long> {

    private final List<Passenger> data = new ArrayList<>();

    @Override
    public Passenger save(Passenger p) {
        data.add(p);
        data.sort(Comparator.comparingLong(Passenger::getId));
        return p;
    }

    @Override
    public Passenger update(Passenger p) {
       
        data.removeIf(x -> x.getId() == p.getId());
        data.add(p);
        data.sort(Comparator.comparingLong(Passenger::getId));
        return p;
    }

    @Override
    public Optional<Passenger> findById(Long id) {
        return data.stream()
                .filter(p -> p.getId() == id)
                .findFirst();
    }

    @Override
    public List<Passenger> findAll() {
        return data.stream()
                .collect(Collectors.toList());
    }
}
