public class Producto {

    private final int codigo;
    private String nombre;
    private String categoria;
    private double precio;
    private int stock;

    public Producto(int codigo, String nombre, String categoria, double precio, int stock) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.stock = stock;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public double getPrecio() {
        return precio;
    }

    public int getStock() {
        return stock;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    // Suma unidades al inventario (reposición hecha por el administrador).
    public void aumentarStock(int cantidad) {
        stock += cantidad;
    }

    // Descuenta unidades del inventario. Nunca deja el stock en negativo:
    // si la cantidad no es válida o supera el stock, no cambia nada y devuelve false.
    public boolean reducirStock(int cantidad) {
        if (cantidad <= 0 || cantidad > stock) {
            return false;
        }
        stock -= cantidad;
        return true;
    }
}
