package parqueadero;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Representa un vehiculo que se encuentra dentro del parqueadero. */
public class Vehiculo {
    private final String placa;
    private final String marca;
    private final LocalDateTime fechaHoraIngreso;

    public Vehiculo(String placa, String marca, LocalDateTime fechaHoraIngreso) {
        this.placa = placa.trim().toUpperCase();
        this.marca = marca.trim();
        this.fechaHoraIngreso = fechaHoraIngreso;
    }

    public String getPlaca() { return placa; }
    public String getMarca() { return marca; }
    public LocalDateTime getFechaHoraIngreso() { return fechaHoraIngreso; }

    public String toLineaArchivo() {
        return placa + "|" + marca.replace("|", "/") + "|" + fechaHoraIngreso;
    }

    public static Vehiculo desdeLineaArchivo(String linea) {
        String[] p = linea.split("\\|", -1);
        if (p.length != 3) throw new IllegalArgumentException("Registro de vehiculo invalido.");
        return new Vehiculo(p[0], p[1], LocalDateTime.parse(p[2]));
    }

    public String ingresoFormateado() {
        return fechaHoraIngreso.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }
}
