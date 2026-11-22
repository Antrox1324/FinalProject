public class DetalleCompra {

    private final Producto producto;
    private int cantidad;
    private final double precioUnitario;

    // Compra nueva: el precio se toma del producto en este momento.
    public DetalleCompra(Producto producto, int cantidad) {
        this(producto, cantidad, producto.getPrecio());
    }

    // Se usa al recuperar ventas del archivo, conservando el precio que se pagó.
    public DetalleCompra(Producto producto, int cantidad, double precioUnitario) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public Producto getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public double getSubtotal() {
        return cantidad * precioUnitario;
    }
}
