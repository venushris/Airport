package airport.storage;

import airport.controller.*;
import airport.model.*;
import airport.response.Response;
import org.json.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class JsonDataLoader {

    public static void cargarTodo(
            PassengerController passengerCtrl,
            PlaneController planeCtrl,
            LocationController locationCtrl,
            FlightController flightCtrl) throws Exception {

        cargarPasajeros(passengerCtrl);
        cargarAviones(planeCtrl);
        cargarUbicaciones(locationCtrl);
        cargarVuelos(flightCtrl);
    }

    private static void cargarPasajeros(PassengerController pc) throws Exception {
        try (InputStream input = abrirJson("passengers")) {
            JSONArray listaPasajeros = new JSONArray(new JSONTokener(input));
            for (int i = 0; i < listaPasajeros.length(); i++) {
                JSONObject obj = listaPasajeros.getJSONObject(i);
                long documento = obj.getLong("id");
                String nombre = obj.getString("firstname");
                String apellido = obj.getString("lastname");
                LocalDate nacimiento = LocalDate.parse(obj.getString("birthDate"));
                int codigoPais = obj.getInt("countryPhoneCode");
                long telefono = obj.getLong("phone");
                String pais = obj.getString("country");

                Response<Passenger> resultado = pc.registerPassenger(
                        documento, nombre, apellido,
                        nacimiento.getYear(), nacimiento.getMonthValue(), nacimiento.getDayOfMonth(),
                        codigoPais, telefono, pais
                );

                if (!resultado.isSuccess()) {
                    System.err.println("❌ Falló el pasajero " + documento + ": " + resultado.getMessage());
                }
            }
        }
    }

    private static void cargarAviones(PlaneController plc) throws Exception {
        try (InputStream input = abrirJson("planes")) {
            JSONArray listaAviones = new JSONArray(new JSONTokener(input));
            for (int i = 0; i < listaAviones.length(); i++) {
                JSONObject obj = listaAviones.getJSONObject(i);
                String codigo = obj.getString("id");
                String marca = obj.getString("brand");
                String modelo = obj.getString("model");
                int capacidad = obj.getInt("maxCapacity");
                String aerolinea = obj.getString("airline");

                Response<Plane> resultado = plc.createPlane(codigo, marca, modelo, capacidad, aerolinea);
                if (!resultado.isSuccess()) {
                    System.err.println("❌ Falló el avión " + codigo + ": " + resultado.getMessage());
                }
            }
        }
    }

    private static void cargarUbicaciones(LocationController lc) throws Exception {
        try (InputStream input = abrirJson("locations")) {
            JSONArray listaLugares = new JSONArray(new JSONTokener(input));
            for (int i = 0; i < listaLugares.length(); i++) {
                JSONObject obj = listaLugares.getJSONObject(i);
                String idAeropuerto = obj.getString("airportId");
                String nombreAeropuerto = obj.getString("airportName");
                String ciudad = obj.getString("airportCity");
                String pais = obj.getString("airportCountry");
                double latitud = obj.getDouble("airportLatitude");
                double longitud = obj.getDouble("airportLongitude");

                Response<Location> resultado = lc.createLocation(idAeropuerto, nombreAeropuerto, ciudad, pais, latitud, longitud);
                if (!resultado.isSuccess()) {
                    System.err.println("❌ Falló la ubicación " + idAeropuerto + ": " + resultado.getMessage());
                }
            }
        }
    }

    private static void cargarVuelos(FlightController fc) throws Exception {
        try (InputStream input = abrirJson("flights")) {
            JSONArray vuelos = new JSONArray(new JSONTokener(input));
            for (int i = 0; i < vuelos.length(); i++) {
                JSONObject obj = vuelos.getJSONObject(i);

                String vueloId = obj.getString("id");
                String avionId = obj.getString("plane");
                String origen = obj.getString("departureLocation");
                String destino = obj.getString("arrivalLocation");
                String escala = obj.optString("scaleLocation", "").trim();
                LocalDateTime salida = LocalDateTime.parse(obj.getString("departureDate"));
                int horasLlegada = obj.getInt("hoursDurationArrival");
                int minsLlegada = obj.getInt("minutesDurationArrival");
                int horasEscala = obj.getInt("hoursDurationScale");
                int minsEscala = obj.getInt("minutesDurationScale");

                Response<Flight> resultado = fc.createFlight(
                        vueloId, avionId, origen, destino, escala,
                        salida.getYear(), salida.getMonthValue(), salida.getDayOfMonth(),
                        salida.getHour(), salida.getMinute(),
                        horasLlegada, minsLlegada, horasEscala, minsEscala
                );

                if (!resultado.isSuccess()) {
                    System.err.println("❌ Falló el vuelo " + vueloId + ": " + resultado.getMessage());
                    continue;
                }

                if (obj.has("passengers")) {
                    JSONArray pasajeros = obj.getJSONArray("passengers");
                    for (int j = 0; j < pasajeros.length(); j++) {
                        long pasajeroId = pasajeros.getLong(j);
                        fc.addPassengerToFlight(vueloId, pasajeroId);
                    }
                }
            }
        }
    }

    private static InputStream abrirJson(String nombreArchivo) throws Exception {
        return new FileInputStream("json/" + nombreArchivo + ".json");
    }
}
