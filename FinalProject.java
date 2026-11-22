
import java.util.Locale;
import java.util.Scanner;
import usuario.Usuario;

public class FinalProject {

    private static final Scanner leer = new Scanner(System.in);
    private static final SistemaVentas sistema = new SistemaVentas();

    public static void main(String[] args) {
        int opcion;

        do {
            System.out.println("\n====================================");
            System.out.println("       SISTEMA DE VENTAS");
            System.out.println("====================================");
            System.out.println("1. Registrar usuario");
            System.out.println("2. Iniciar sesión");
            System.out.println("3. Salir");
            opcion = leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1:
                    registrarUsuario();
                    break;
                case 2:
                    if (iniciarSesion()) {
                        if (sistema.esAdministrador()) {
                            menuAdmin();
                        } else {
                            menuUsuario();
                        }
                    }
                    break;
                case 3:
                    System.out.println("Hasta pronto.");
                    break;
                default:
                    System.out.println("Opción no válida.");
                    break;
            }
        } while (opcion != 3);
    }

    // Lee un entero sin que el programa se caiga si escriben letras.
    // Devuelve -1 cuando el texto no es un número válido.
    public static int leerEntero(String mensaje) {
        System.out.print(mensaje);
        try {
            return Integer.parseInt(leer.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // ===================== REGISTRO E INICIO DE SESIÓN =====================
    public static void registrarUsuario() {
        System.out.println("\n--- REGISTRO DE USUARIO ---");

        System.out.print("Identificación (6 a 10 dígitos): ");
        String id = leer.nextLine().trim();

        if (id.length() < 6 || id.length() > 10) {
            System.out.println("La identificación debe tener entre 6 y 10 dígitos.");
            return;
        }
        for (int i = 0; i < id.length(); i++) {
            if (!Character.isDigit(id.charAt(i))) {
                System.out.println("La identificación solo puede tener números.");
                return;
            }
        }
        if (sistema.existeId(id)) {
            System.out.println("Ya existe un usuario con esa identificación.");
            return;
        }

        System.out.print("Nombre: ");
        String nombre = leer.nextLine().trim();
        if (nombre.length() < 3 || nombre.length() > 50) {
            System.out.println("El nombre debe tener entre 3 y 50 caracteres.");
            return;
        }
        if (!nombre.matches("[\\p{L} ]+")) {
            System.out.println("El nombre solo puede tener letras y espacios.");
            return;
        }

        System.out.print("Correo electrónico: ");
        String correo = leer.nextLine().trim();
        if (!correoValido(correo)) {
            System.out.println("El correo no es válido (ejemplo: nombre@dominio.com).");
            return;
        }
        if (sistema.existeCorreo(correo)) {
            System.out.println("Ya existe un usuario con ese correo.");
            return;
        }

        System.out.print("Contraseña (mínimo 4 caracteres): ");
        String password = leer.nextLine().trim();
        if (password.length() < 4) {
            System.out.println("La contraseña debe tener al menos 4 caracteres.");
            return;
        }
        if (password.contains(";")) {
            System.out.println("La contraseña no puede contener el símbolo ;");
            return;
        }

        if (sistema.registrarUsuario(nombre, correo, password, id)) {
            System.out.println("Usuario registrado correctamente.");
        } else {
            System.out.println("El correo o la identificación ya existen.");
        }
    }

    // Debe tener un solo @, texto antes de él, y un punto con texto después del @.
    public static boolean correoValido(String correo) {
        int arroba = correo.indexOf('@');
        int punto = correo.lastIndexOf('.');

        if (correo.contains(" ") || correo.contains(";")) {
            return false;
        }
        if (arroba < 1 || arroba != correo.lastIndexOf('@')) {
            return false;
        }
        return punto > arroba + 1 && punto < correo.length() - 1;
    }

    // Devuelve true si el inicio de sesión fue exitoso.
    public static boolean iniciarSesion() {
        System.out.println("\n--- INICIAR SESIÓN ---");

        System.out.print("Correo: ");
        String correo = leer.nextLine().trim();
        System.out.print("Contraseña: ");
        String password = leer.nextLine().trim();

        if (correo.isEmpty() || password.isEmpty()) {
            System.out.println("El correo y la contraseña no pueden estar vacíos.");
            return false;
        }

        if (sistema.iniciarSesion(correo, password)) {
            if (sistema.esAdministrador()) {
                System.out.println("Bienvenido, Administrador.");
            } else {
                System.out.println("Bienvenido, " + sistema.getUsuarioActual().getNombre() + ".");
            }
            return true;
        }

        System.out.println("Credenciales inválidas.");
        return false;
    }

    // ===================== MENÚ DEL USUARIO =====================
    public static void menuUsuario() {
        int opcion;

        do {
            System.out.println("\n====================================");
            System.out.println("     BIENVENIDO AL SISTEMA");
            System.out.println("====================================");
            System.out.println("1. Ver catálogo de productos");
            System.out.println("2. Agregar producto al carrito");
            System.out.println("3. Ver carrito de compras");
            System.out.println("4. Modificar cantidad del carrito");
            System.out.println("5. Retirar producto del carrito");
            System.out.println("6. Confirmar compra");
            System.out.println("7. Cancelar compra");
            System.out.println("8. Consultar historial de compras");
            System.out.println("9. Cerrar sesión");
            opcion = leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1:
                    mostrarCatalogo();
                    break;
                case 2:
                    agregarProducto();
                    break;
                case 3:
                    mostrarCarrito();
                    break;
                case 4:
                    modificarCantidad();
                    break;
                case 5:
                    retirarProducto();
                    break;
                case 6:
                    confirmarCompra();
                    break;
                case 7:
                    cancelarCompra();
                    break;
                case 8:
                    mostrarHistorial();
                    break;
                case 9:
                    sistema.cerrarSesion();
                    System.out.println("Sesión cerrada.");
                    break;
                default:
                    System.out.println("Opción no válida.");
                    break;
            }
        } while (opcion != 9);
    }

    public static void mostrarCatalogo() {
        System.out.println("\n--- CATÁLOGO DE PRODUCTOS ---");
        for (Producto producto : sistema.getProductos()) {
            String stock = producto.getStock() > 0
                    ? "Stock: " + producto.getStock()
                    : "AGOTADO";
            System.out.println("Código: " + producto.getCodigo()
                    + " | " + producto.getNombre()
                    + " | " + producto.getCategoria()
                    + " | " + formatoMoneda(producto.getPrecio())
                    + " | " + stock);
        }
    }

    public static void agregarProducto() {
        int codigo = leerEntero("Código del producto: ");
        Producto producto = sistema.buscarProducto(codigo);
        if (producto == null) {
            System.out.println("No existe un producto con ese código.");
            return;
        }

        int cantidad = leerEntero("Cantidad: ");
        if (cantidad <= 0) {
            System.out.println("La cantidad debe ser un número mayor que cero.");
            return;
        }

        if (sistema.agregarAlCarrito(codigo, cantidad)) {
            System.out.println("Producto agregado al carrito.");
        } else {
            System.out.println("No hay inventario suficiente. Stock disponible: " + producto.getStock());
        }
    }

    public static void mostrarCarrito() {
        System.out.println("\n--- CARRITO DE COMPRAS ---");

        if (sistema.getCarrito().isEmpty()) {
            System.out.println("El carrito está vacío.");
            return;
        }

        for (DetalleCompra detalle : sistema.getCarrito()) {
            System.out.println("Código: " + detalle.getProducto().getCodigo()
                    + " | " + detalle.getProducto().getNombre()
                    + " | Cantidad: " + detalle.getCantidad()
                    + " | Precio unitario: " + formatoMoneda(detalle.getPrecioUnitario())
                    + " | Subtotal: " + formatoMoneda(detalle.getSubtotal()));
        }
        System.out.println("Subtotal acumulado: " + formatoMoneda(sistema.calcularSubtotal()));
    }

    public static void modificarCantidad() {
        if (sistema.getCarrito().isEmpty()) {
            System.out.println("El carrito está vacío.");
            return;
        }

        int codigo = leerEntero("Código del producto a modificar: ");
        int cantidad = leerEntero("Nueva cantidad: ");
        if (cantidad <= 0) {
            System.out.println("La cantidad debe ser un número mayor que cero.");
            return;
        }

        if (sistema.modificarCantidad(codigo, cantidad)) {
            System.out.println("Cantidad modificada.");
        } else {
            System.out.println("No se pudo modificar: el producto no está en el carrito o no hay stock suficiente.");
        }
    }

    public static void retirarProducto() {
        if (sistema.getCarrito().isEmpty()) {
            System.out.println("El carrito está vacío.");
            return;
        }

        int codigo = leerEntero("Código del producto a retirar: ");
        if (sistema.retirarDelCarrito(codigo)) {
            System.out.println("Producto retirado del carrito.");
        } else {
            System.out.println("Ese producto no está en el carrito.");
        }
    }

    public static void confirmarCompra() {
        if (sistema.getCarrito().isEmpty()) {
            System.out.println("No se puede confirmar: el carrito está vacío.");
            return;
        }

        Venta venta = sistema.confirmarCompra();
        if (venta == null) {
            System.out.println("No se pudo confirmar: el inventario de algún producto ya no alcanza.");
            return;
        }

        System.out.println("\n--- COMPRA CONFIRMADA ---");
        System.out.println("Número de venta: " + venta.getNumero());
        System.out.println("Subtotal: " + formatoMoneda(venta.getSubtotal()));
        System.out.println("Descuento aplicado: " + venta.getPorcentajeDescuento() + " %");
        System.out.println("Valor del descuento: " + formatoMoneda(venta.getValorDescuento()));
        System.out.println("Total a pagar: " + formatoMoneda(venta.getTotal()));
    }

    public static void cancelarCompra() {
        if (sistema.getCarrito().isEmpty()) {
            System.out.println("No hay una compra en curso.");
            return;
        }
        sistema.cancelarCompra();
        System.out.println("Compra cancelada. No se registró ninguna venta.");
    }

    public static void mostrarHistorial() {
        System.out.println("\n--- HISTORIAL DE COMPRAS ---");

        Usuario usuario = sistema.getUsuarioActual();
        boolean hayVentas = false;

        for (Venta venta : sistema.getVentasDeUsuario(usuario)) {
            hayVentas = true;
            System.out.println("\nVenta número: " + venta.getNumero());
            System.out.println("Fecha: " + venta.getFechaCompra());
            for (DetalleCompra detalle : venta.getDetalles()) {
                System.out.println("  " + detalle.getProducto().getNombre()
                        + " x" + detalle.getCantidad()
                        + " (" + formatoMoneda(detalle.getPrecioUnitario()) + " c/u)");
            }
            System.out.println("Subtotal: " + formatoMoneda(venta.getSubtotal()));
            System.out.println("Descuento: " + venta.getPorcentajeDescuento() + " % ("
                    + formatoMoneda(venta.getValorDescuento()) + ")");
            System.out.println("Total pagado: " + formatoMoneda(venta.getTotal()));
        }

        if (!hayVentas) {
            System.out.println("Todavía no tienes compras registradas.");
        }
    }

    // ===================== MENÚ DEL ADMINISTRADOR =====================
    public static void menuAdmin() {
        int opcion;

        do {
            System.out.println("\n====================================");
            System.out.println("     PANEL DE ADMINISTRADOR");
            System.out.println("====================================");
            System.out.println("1. Ver catálogo de productos");
            System.out.println("2. Registrar producto nuevo");
            System.out.println("3. Modificar precio de un producto");
            System.out.println("4. Reponer stock");
            System.out.println("5. Ver todas las ventas");
            System.out.println("6. Cerrar sesión");
            opcion = leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1:
                    mostrarCatalogo();
                    break;
                case 2:
                    registrarProductoNuevo();
                    break;
                case 3:
                    modificarPrecio();
                    break;
                case 4:
                    reponerStock();
                    break;
                case 5:
                    mostrarTodasLasVentas();
                    break;
                case 6:
                    sistema.cerrarSesion();
                    System.out.println("Sesión cerrada.");
                    break;
                default:
                    System.out.println("Opción no válida.");
                    break;
            }
        } while (opcion != 6);
    }

    public static void registrarProductoNuevo() {
        System.out.println("\n--- REGISTRAR PRODUCTO ---");

        System.out.print("Nombre del producto (3 a 40 caracteres): ");
        String nombre = leer.nextLine().trim();
        System.out.print("Categoría (3 a 40 caracteres): ");
        String categoria = leer.nextLine().trim();
        if (nombre.length() < 3 || nombre.length() > 40 || nombre.contains(";")
                || categoria.length() < 3 || categoria.length() > 40 || categoria.contains(";")) {
            System.out.println("El nombre y la categoría deben tener entre 3 y 40 caracteres y no pueden llevar ;");
            return;
        }

        int precio = leerEntero("Precio en pesos (entero): ");
        if (precio <= 0 || precio > SistemaVentas.MAX_PRECIO) {
            System.out.println("El precio debe ser un entero entre 1 y " + SistemaVentas.MAX_PRECIO + ".");
            return;
        }

        int stock = leerEntero("Stock inicial: ");
        if (stock < 0 || stock > SistemaVentas.MAX_STOCK) {
            System.out.println("El stock debe ser un entero entre 0 y " + SistemaVentas.MAX_STOCK + ".");
            return;
        }

        Producto nuevo = sistema.registrarProducto(nombre, categoria, precio, stock);
        if (nuevo == null) {
            System.out.println("No se pudo registrar: ya existe un producto con ese nombre.");
        } else {
            System.out.println("Producto registrado con el código " + nuevo.getCodigo() + ".");
        }
    }

    public static void modificarPrecio() {
        int codigo = leerEntero("Código del producto: ");
        Producto producto = sistema.buscarProducto(codigo);
        if (producto == null) {
            System.out.println("No existe un producto con ese código.");
            return;
        }

        System.out.println("Precio actual de " + producto.getNombre() + ": " + formatoMoneda(producto.getPrecio()));
        int precio = leerEntero("Nuevo precio en pesos (entero): ");
        if (sistema.cambiarPrecio(codigo, precio)) {
            System.out.println("Precio actualizado. Las ventas anteriores conservan su precio original.");
        } else {
            System.out.println("El precio debe ser un entero entre 1 y " + SistemaVentas.MAX_PRECIO + ".");
        }
    }

    public static void reponerStock() {
        int codigo = leerEntero("Código del producto: ");
        Producto producto = sistema.buscarProducto(codigo);
        if (producto == null) {
            System.out.println("No existe un producto con ese código.");
            return;
        }

        System.out.println("Stock actual de " + producto.getNombre() + ": " + producto.getStock());
        int cantidad = leerEntero("Unidades a agregar: ");
        if (sistema.reponerStock(codigo, cantidad)) {
            System.out.println("Stock actualizado. Ahora hay " + producto.getStock() + " unidades.");
        } else {
            System.out.println("Cantidad inválida. Debe ser mayor que cero y el stock total no puede pasar de "
                    + SistemaVentas.MAX_STOCK + ".");
        }
    }

    public static void mostrarTodasLasVentas() {
        System.out.println("\n--- TODAS LAS VENTAS ---");

        if (sistema.getVentas().isEmpty()) {
            System.out.println("Todavía no hay ventas registradas.");
            return;
        }

        double totalVendido = 0;
        for (Venta venta : sistema.getVentas()) {
            totalVendido += venta.getTotal();
            System.out.println("Venta " + venta.getNumero() + " | " + venta.getFechaCompra()
                    + " | Cliente: " + venta.getUsuario().getNombre()
                    + " (" + venta.getUsuario().getId() + ")"
                    + " | Descuento: " + venta.getPorcentajeDescuento() + " %"
                    + " | Total: " + formatoMoneda(venta.getTotal()));
        }
        System.out.println("Total vendido: " + formatoMoneda(totalVendido));
    }

    // Pesos colombianos con punto como separador de miles: $310.000
    public static String formatoMoneda(double valor) {
        return "$" + String.format(Locale.US, "%,.0f", valor).replace(',', '.');
    }
}
