package parqueadero;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/** Registra los cobros realizados y permite consultar la recaudacion. */
public class Caja {
    private final Path archivo;
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public Caja(Path archivo) throws IOException {
        this.archivo = archivo;
        if (archivo.getParent() != null) Files.createDirectories(archivo.getParent());
        if (!Files.exists(archivo)) Files.createFile(archivo);
    }

    public void registrarCobro(Vehiculo v, LocalDateTime salida, long horas, double valor) throws IOException {
        String linea = String.format(Locale.US, "%s|%s|%s|%d|%.2f%n",
                v.getPlaca(), v.getFechaHoraIngreso(), salida, horas, valor);
        Files.writeString(archivo, linea, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
    }

    public double obtenerRecaudacionTotal() throws IOException {
        double total = 0;
        List<String> lineas = Files.readAllLines(archivo, StandardCharsets.UTF_8);
        for (String linea : lineas) {
            if (linea.isBlank()) continue;
            String[] p = linea.split("\\|", -1);
            if (p.length == 5) {
                try { total += Double.parseDouble(p[4]); }
                catch (NumberFormatException ignored) { /* omite registros danados */ }
            }
        }
        return total;
    }

    public int contarCobros() throws IOException {
        int total = 0;
        for (String linea : Files.readAllLines(archivo, StandardCharsets.UTF_8)) {
            if (!linea.isBlank()) total++;
        }
        return total;
    }

    public Path getArchivo() { return archivo; }
}
