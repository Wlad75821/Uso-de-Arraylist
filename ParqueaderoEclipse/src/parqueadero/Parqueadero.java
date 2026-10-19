package parqueadero;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/** Gestiona el ingreso, consulta y salida de los vehiculos. */
public class Parqueadero {
    public static final double TARIFA_POR_HORA = 0.50;
    private final Path archivoVehiculos;
    private final Caja caja;
    private final List<Vehiculo> vehiculos = new ArrayList<>();

    public Parqueadero(Path archivoVehiculos, Caja caja) throws IOException {
        this.archivoVehiculos = archivoVehiculos;
        this.caja = caja;
        if (archivoVehiculos.getParent() != null) Files.createDirectories(archivoVehiculos.getParent());
        if (!Files.exists(archivoVehiculos)) Files.createFile(archivoVehiculos);
        cargarVehiculos();
    }

    private void cargarVehiculos() throws IOException {
        vehiculos.clear();
        for (String linea : Files.readAllLines(archivoVehiculos, StandardCharsets.UTF_8)) {
            if (linea.isBlank()) continue;
            try { vehiculos.add(Vehiculo.desdeLineaArchivo(linea)); }
            catch (RuntimeException e) {
                System.err.println("Aviso: se omitio un registro invalido de vehiculos.txt.");
            }
        }
    }

    private void guardarVehiculos() throws IOException {
        List<String> lineas = new ArrayList<>();
        for (Vehiculo v : vehiculos) lineas.add(v.toLineaArchivo());
        Files.write(archivoVehiculos, lineas, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public boolean registrarIngreso(String placa, String marca) throws IOException {
        String placaNormalizada = placa.trim().toUpperCase();
        if (placaNormalizada.isEmpty() || marca.trim().isEmpty())
            throw new IllegalArgumentException("La placa y la marca son obligatorias.");
        if (buscarVehiculo(placaNormalizada).isPresent()) return false;
        vehiculos.add(new Vehiculo(placaNormalizada, marca, LocalDateTime.now()));
        guardarVehiculos();
        return true;
    }

    public List<Vehiculo> listarVehiculos() {
        List<Vehiculo> copia = new ArrayList<>(vehiculos);
        copia.sort(Comparator.comparing(Vehiculo::getFechaHoraIngreso));
        return copia;
    }

    public Optional<Vehiculo> buscarVehiculo(String placa) {
        String buscada = placa.trim().toUpperCase();
        return vehiculos.stream().filter(v -> v.getPlaca().equals(buscada)).findFirst();
    }

    public Optional<ResumenSalida> registrarSalida(String placa) throws IOException {
        Optional<Vehiculo> encontrado = buscarVehiculo(placa);
        if (encontrado.isEmpty()) return Optional.empty();

        Vehiculo v = encontrado.get();
        LocalDateTime salida = LocalDateTime.now();
        long minutos = Math.max(0, Duration.between(v.getFechaHoraIngreso(), salida).toMinutes());
        // Se cobra cada hora iniciada o fraccion; incluso una permanencia menor a una hora cobra una hora.
        long horasCobradas = Math.max(1, (minutos + 59) / 60);
        double valor = horasCobradas * TARIFA_POR_HORA;

        caja.registrarCobro(v, salida, horasCobradas, valor);
        vehiculos.remove(v);
        guardarVehiculos();
        return Optional.of(new ResumenSalida(v, salida, horasCobradas, valor, minutos));
    }

    public Path getArchivoVehiculos() { return archivoVehiculos; }

    public static class ResumenSalida {
        private final Vehiculo vehiculo;
        private final LocalDateTime salida;
        private final long horasCobradas;
        private final double valor;
        private final long minutos;

        public ResumenSalida(Vehiculo vehiculo, LocalDateTime salida, long horasCobradas, double valor, long minutos) {
            this.vehiculo = vehiculo;
            this.salida = salida;
            this.horasCobradas = horasCobradas;
            this.valor = valor;
            this.minutos = minutos;
        }
        public Vehiculo getVehiculo() { return vehiculo; }
        public LocalDateTime getSalida() { return salida; }
        public long getHorasCobradas() { return horasCobradas; }
        public double getValor() { return valor; }
        public long getMinutos() { return minutos; }
    }
}
