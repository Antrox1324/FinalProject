import java.util.ArrayList;
import usuario.Usuario;

public class Venta {

    private final int numero;
    private final Usuario usuario;
    private final ArrayList<DetalleCompra> detalles;
    private final String fechaCompra;
    private final double subtotal;
    private final int porcentajeDescuento;
    private final double valorDescuento;
    private final double total;

    // Venta nueva: calcula subtotal, descuento y total a partir de los detalles.
    public Venta(int numero, Usuario usuario, ArrayList<DetalleCompra> detalles, String fechaCompra) {
        this.numero = numero;
        this.usuario = usuario;
        this.detalles = new ArrayList<>(detalles);
        this.fechaCompra = fechaCompra;

        double suma = 0;
        for (DetalleCompra detalle : this.detalles) {
            suma += detalle.getSubtotal();
        }
        this.subtotal = suma;
        this.porcentajeDescuento = calcularPorcentajeDescuento(suma);
        this.valorDescuento = suma * porcentajeDescuento / 100;
        this.total = suma - valorDescuento;
    }

    // Venta recuperada del archivo: usa los valores que se guardaron.
    public Venta(int numero, Usuario usuario, ArrayList<DetalleCompra> detalles, String fechaCompra,
            double subtotal, int porcentajeDescuento, double valorDescuento, double total) {
        this.numero = numero;
        this.usuario = usuario;
        this.detalles = new ArrayList<>(detalles);
        this.fechaCompra = fechaCompra;
        this.subtotal = subtotal;
        this.porcentajeDescuento = porcentajeDescuento;
        this.valorDescuento = valorDescuento;
        this.total = total;
    }

    // Se evalúa desde el rango más alto hacia el más bajo, así los límites
    // exactos ($100.000, $300.000 y $600.000) quedan en el nivel correcto.
    public static int calcularPorcentajeDescuento(double subtotal) {
        if (subtotal >= 600000) {
            return 15;
        } else if (subtotal >= 300000) {
            return 10;
        } else if (subtotal >= 100000) {
            return 5;
        } else {
            return 0;
        }
    }

    public int getNumero() {
        return numero;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public ArrayList<DetalleCompra> getDetalles() {
        return detalles;
    }

    public String getFechaCompra() {
        return fechaCompra;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public int getPorcentajeDescuento() {
        return porcentajeDescuento;
    }

    public double getValorDescuento() {
        return valorDescuento;
    }

    public double getTotal() {
        return total;
    }
}
