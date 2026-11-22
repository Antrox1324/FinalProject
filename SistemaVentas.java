
import java.io.IOException; //Modificar este import raro que desconocemos
import java.nio.charset.StandardCharsets; //Modificar este import raro que desconocemos
import java.nio.file.*; //Modificar este import raro que desconocemos //Modificar este import raro que desconocemos
import java.time.LocalDateTime; //BORRAR TIEMPO
import java.time.format.DateTimeFormatter; //BORRAR TIEMPO
import java.util.ArrayList;
import usuario.Usuario;

public class SistemaVentas {

    private static final String ARCHIVO_USUARIOS = "usuarios.txt";
    private static final String ARCHIVO_PRODUCTOS = "productos.txt";
    private static final String ARCHIVO_VENTAS = "ventas.txt";
    private static final String ARCHIVO_DETALLES = "detalles_ventas.txt";

    // Cuenta del administrador por defecto. Se crea sola en usuarios.txt la primera vez
    // (o si el archivo no tiene ningún administrador) y luego inicia sesión como cualquier usuario.
    public static final String ADMIN_CORREO = "admin@tienda.com";
    public static final String ADMIN_PASSWORD = "Admin2026";
    public static final String ADMIN_ID = "0000000000";
    public static final String ADMIN_NOMBRE = "Administrador";
    public static final int MAX_PRECIO = 100000000;
    public static final int MAX_STOCK = 100000;

    private final ArrayList<Usuario> usuarios = new ArrayList<>();
    private final ArrayList<Producto> productos = new ArrayList<>();
    private final ArrayList<DetalleCompra> carrito = new ArrayList<>();
    private final ArrayList<Venta> ventas = new ArrayList<>();
    private Usuario usuarioActual;
    private boolean adminActivo = false;
    private int ultimoNumeroVenta = 0;

    public SistemaVentas() {
        cargarDatos();
    }

    // ===================== USUARIOS Y SESIÓN =====================
    public boolean existeCorreo(String correo) {
        return buscarUsuarioPorCorreo(correo) != null;
    }

    public boolean existeId(String id) {
        return buscarUsuarioPorId(id) != null;
    }

    public boolean registrarUsuario(String nombre, String correo, String password, String id) {
        if (existeCorreo(correo) || existeId(id)) {
            return false;
        }
        usuarios.add(new Usuario(nombre, correo, password, id));
        guardarUsuarios();
        return true;
    }

    public Usuario buscarUsuarioPorCorreo(String correo) {
        for (Usuario usuario : usuarios) {
            if (usuario.getCorreo().equalsIgnoreCase(correo)) {
                return usuario;
            }
        }
        return null;
    }

    public Usuario buscarUsuarioPorId(String id) {
        for (Usuario usuario : usuarios) {
            if (usuario.getId().equals(id)) {
                return usuario;
            }
        }
        return null;
    }

    public boolean iniciarSesion(String correo, String password) {
        Usuario usuario = buscarUsuarioPorCorreo(correo);
        if (usuario != null && usuario.getPassword().equals(password)) {
            usuarioActual = usuario;
            adminActivo = usuario.esAdministrador();
            carrito.clear();
            return true;
        }
        return false;
    }

    // Al cerrar sesión se vacía el carrito para que otro usuario no lo herede.
    public void cerrarSesion() {
        usuarioActual = null;
        adminActivo = false;
        carrito.clear();
    }

    public boolean esAdministrador() {
        return adminActivo;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    // ===================== CATÁLOGO =====================
    public ArrayList<Producto> getProductos() {
        return productos;
    }

    public Producto buscarProducto(int codigo) {
        for (Producto producto : productos) {
            if (producto.getCodigo() == codigo) {
                return producto;
            }
        }
        return null;
    }

    // ===================== ADMINISTRACIÓN DE PRODUCTOS =====================
    private boolean textoProductoValido(String texto) {
        return texto != null && texto.trim().length() >= 3 && texto.trim().length() <= 40
                && !texto.contains(";");
    }

    // Crea un producto con el siguiente código libre. Devuelve null si los datos
    // no son válidos o si ya existe un producto con ese nombre.
    public Producto registrarProducto(String nombre, String categoria, int precio, int stock) {
        if (!adminActivo || !textoProductoValido(nombre) || !textoProductoValido(categoria)
                || precio <= 0 || precio > MAX_PRECIO || stock < 0 || stock > MAX_STOCK) {
            return null;
        }
        int codigoMayor = 0;
        for (Producto p : productos) {
            if (p.getNombre().equalsIgnoreCase(nombre.trim())) {
                return null;
            }
            if (p.getCodigo() > codigoMayor) {
                codigoMayor = p.getCodigo();
            }
        }
        Producto nuevo = new Producto(codigoMayor + 1, nombre.trim(), categoria.trim(), precio, stock);
        productos.add(nuevo);
        guardarProductos();
        return nuevo;
    }

    // El cambio de precio solo afecta compras futuras: las ventas ya hechas
    // conservan el precio unitario que se pagó en su DetalleCompra.
    public boolean cambiarPrecio(int codigo, int nuevoPrecio) {
        Producto producto = buscarProducto(codigo);
        if (!adminActivo || producto == null || nuevoPrecio <= 0 || nuevoPrecio > MAX_PRECIO) {
            return false;
        }
        producto.setPrecio(nuevoPrecio);
        guardarProductos();
        return true;
    }

    public boolean reponerStock(int codigo, int cantidad) {
        Producto producto = buscarProducto(codigo);
        if (!adminActivo || producto == null || cantidad <= 0
                || cantidad > MAX_STOCK - producto.getStock()) {
            return false;
        }
        producto.aumentarStock(cantidad);
        guardarProductos();
        return true;
    }

    // ===================== CARRITO =====================
    public ArrayList<DetalleCompra> getCarrito() {
        return carrito;
    }

    private DetalleCompra buscarEnCarrito(int codigo) {
        for (DetalleCompra detalle : carrito) {
            if (detalle.getProducto().getCodigo() == codigo) {
                return detalle;
            }
        }
        return null;
    }

    // Si el producto ya está en el carrito, suma la cantidad. En ambos casos
    // valida que lo pedido no supere el inventario disponible.
    public boolean agregarAlCarrito(int codigo, int cantidad) {
        Producto producto = buscarProducto(codigo);
        if (producto == null || cantidad <= 0) {
            return false;
        }

        DetalleCompra existente = buscarEnCarrito(codigo);
        int cantidadTotal = cantidad;
        if (existente != null) {
            cantidadTotal += existente.getCantidad();
        }

        if (cantidadTotal > producto.getStock()) {
            return false;
        }

        if (existente != null) {
            existente.setCantidad(cantidadTotal);
        } else {
            carrito.add(new DetalleCompra(producto, cantidad));
        }
        return true;
    }

    public boolean modificarCantidad(int codigo, int nuevaCantidad) {
        DetalleCompra detalle = buscarEnCarrito(codigo);
        if (detalle == null || nuevaCantidad <= 0
                || nuevaCantidad > detalle.getProducto().getStock()) {
            return false;
        }
        detalle.setCantidad(nuevaCantidad);
        return true;
    }

    public boolean retirarDelCarrito(int codigo) {
        DetalleCompra detalle = buscarEnCarrito(codigo);
        if (detalle == null) {
            return false;
        }
        carrito.remove(detalle);
        return true;
    }

    public double calcularSubtotal() {
        double subtotal = 0;
        for (DetalleCompra detalle : carrito) {
            subtotal += detalle.getSubtotal();
        }
        return subtotal;
    }

    public void cancelarCompra() {
        carrito.clear();
    }

    // ===================== VENTAS =====================
    // Devuelve la venta registrada, o null si el carrito está vacío o el stock
    // ya no alcanza. Solo cuando todo está bien se descuenta inventario.
    public Venta confirmarCompra() {
        if (usuarioActual == null || adminActivo || carrito.isEmpty()) {
            return null;
        }

        for (DetalleCompra detalle : carrito) {
            if (detalle.getCantidad() > detalle.getProducto().getStock()) {
                return null;
            }
        }

        for (DetalleCompra detalle : carrito) {
            detalle.getProducto().reducirStock(detalle.getCantidad());
        }

        ultimoNumeroVenta++;
        //LO MODIFICAMOS PARA QUE NO SALGA MÁS LA FECHA NI HORA.
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        Venta venta = new Venta(ultimoNumeroVenta, usuarioActual, carrito, fecha);
        ventas.add(venta);
        carrito.clear();

        guardarProductos();
        guardarVentas();
        return venta;
    }

    public ArrayList<Venta> getVentas() {
        return ventas;
    }

    // Solo las ventas del usuario indicado: nadie ve el historial de otro.
    public ArrayList<Venta> getVentasDeUsuario(Usuario usuario) {
        ArrayList<Venta> resultado = new ArrayList<>();
        for (Venta venta : ventas) {
            if (venta.getUsuario().getId().equals(usuario.getId())) {
                resultado.add(venta);
            }
        }
        return resultado;
    }

    // ===================== PERSISTENCIA (archivos de texto plano) =====================
    private void cargarDatos() {
        cargarUsuarios();
        asegurarAdministrador();
        if (Files.exists(Paths.get(ARCHIVO_PRODUCTOS))) {
            cargarProductos();
        } else {
            precargarProductos();
            guardarProductos();
        }
        cargarVentas();
    }

    private void precargarProductos() {
        productos.add(new Producto(1, "Mouse inalámbrico", "Accesorios", 45000, 20));
        productos.add(new Producto(2, "Teclado mecánico", "Accesorios", 120000, 15));
        productos.add(new Producto(3, "Audífonos", "Audio", 100000, 12));
        productos.add(new Producto(4, "Monitor 24 pulgadas", "Monitores", 600000, 8));
        productos.add(new Producto(5, "Memoria USB 64GB", "Almacenamiento", 30000, 50));
        productos.add(new Producto(6, "Webcam HD", "Accesorios", 85000, 10));
    }

    private ArrayList<String> leerLineas(String archivo) {
        ArrayList<String> lineas = new ArrayList<>();
        if (!Files.exists(Paths.get(archivo))) {
            return lineas;
        }
        try {
            lineas.addAll(Files.readAllLines(Paths.get(archivo), StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.println("Error al leer " + archivo + ": " + e.getMessage());
        }
        lineas.removeIf(linea -> linea.trim().isEmpty());
        return lineas;
    }

    private void escribirLineas(String archivo, ArrayList<String> lineas) {
        try {
            Files.write(Paths.get(archivo), lineas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Error al guardar " + archivo + ": " + e.getMessage());
        }
    }

    // usuarios.txt -> id;nombre;correo;password;rol   (rol = CLIENTE o ADMIN)
    // Las líneas viejas de 4 campos se leen como CLIENTE.
    private void guardarUsuarios() {
        ArrayList<String> lineas = new ArrayList<>();
        for (Usuario u : usuarios) {
            lineas.add(u.getId() + ";" + u.getNombre() + ";" + u.getCorreo()
                    + ";" + u.getPassword() + ";" + u.getRol());
        }
        escribirLineas(ARCHIVO_USUARIOS, lineas);
    }

    private void cargarUsuarios() {
        for (String linea : leerLineas(ARCHIVO_USUARIOS)) {
            String[] p = linea.split(";", -1);
            String rol = p.length == 5 ? p[4].trim() : Usuario.ROL_CLIENTE;
            boolean rolValido = rol.equals(Usuario.ROL_CLIENTE) || rol.equals(Usuario.ROL_ADMIN);
            boolean valida = (p.length == 4 || p.length == 5) && rolValido
                    && !p[0].trim().isEmpty() && !p[1].trim().isEmpty()
                    && !p[2].trim().isEmpty() && !p[3].isEmpty()
                    && !existeId(p[0]) && !existeCorreo(p[2]);
            if (valida) {
                usuarios.add(new Usuario(p[1], p[2], p[3], p[0], rol));
            } else {
                System.out.println("Línea ignorada en " + ARCHIVO_USUARIOS + ": " + linea);
            }
        }
    }

    // Si no hay ningún administrador registrado, crea el de por defecto y lo guarda,
    // así el admin ya queda registrado y solo tiene que iniciar sesión.
    private void asegurarAdministrador() {
        for (Usuario u : usuarios) {
            if (u.esAdministrador()) {
                return;
            }
        }
        if (existeId(ADMIN_ID) || existeCorreo(ADMIN_CORREO)) {
            System.out.println("No se pudo crear el administrador: el correo o la id ya están en uso.");
            return;
        }
        usuarios.add(new Usuario(ADMIN_NOMBRE, ADMIN_CORREO, ADMIN_PASSWORD, ADMIN_ID, Usuario.ROL_ADMIN));
        guardarUsuarios();
    }

    // productos.txt -> codigo;nombre;categoria;precio;stock
    private void guardarProductos() {
        ArrayList<String> lineas = new ArrayList<>();
        for (Producto p : productos) {
            lineas.add(p.getCodigo() + ";" + p.getNombre() + ";" + p.getCategoria()
                    + ";" + p.getPrecio() + ";" + p.getStock());
        }
        escribirLineas(ARCHIVO_PRODUCTOS, lineas);
    }

    private void cargarProductos() {
        for (String linea : leerLineas(ARCHIVO_PRODUCTOS)) {
            String[] p = linea.split(";", -1);
            try {
                if (p.length != 5) {
                    throw new NumberFormatException();
                }
                productos.add(new Producto(Integer.parseInt(p[0]), p[1], p[2],
                        Double.parseDouble(p[3]), Integer.parseInt(p[4])));
            } catch (NumberFormatException e) {
                System.out.println("Línea ignorada en " + ARCHIVO_PRODUCTOS + ": " + linea);
            }
        }
    }

    // ventas.txt          -> numero;idUsuario;fecha;subtotal;porcentaje;valorDescuento;total
    // detalles_ventas.txt -> numeroVenta;codigoProducto;cantidad;precioUnitario
    // El número de venta une cada venta con sus detalles.
    private void guardarVentas() {
        ArrayList<String> lineasVentas = new ArrayList<>();
        ArrayList<String> lineasDetalles = new ArrayList<>();

        for (Venta v : ventas) {
            lineasVentas.add(v.getNumero() + ";" + v.getUsuario().getId() + ";" + v.getFechaCompra()
                    + ";" + v.getSubtotal() + ";" + v.getPorcentajeDescuento()
                    + ";" + v.getValorDescuento() + ";" + v.getTotal());
            for (DetalleCompra d : v.getDetalles()) {
                lineasDetalles.add(v.getNumero() + ";" + d.getProducto().getCodigo()
                        + ";" + d.getCantidad() + ";" + d.getPrecioUnitario());
            }
        }

        escribirLineas(ARCHIVO_VENTAS, lineasVentas);
        escribirLineas(ARCHIVO_DETALLES, lineasDetalles);
    }

    private void cargarVentas() {
        ArrayList<String> lineasVentas = leerLineas(ARCHIVO_VENTAS);
        ArrayList<String> lineasDetalles = leerLineas(ARCHIVO_DETALLES);

        for (String linea : lineasVentas) {
            String[] p = linea.split(";", -1);
            try {
                if (p.length != 7) {
                    throw new NumberFormatException();
                }
                int numero = Integer.parseInt(p[0]);
                Usuario usuario = buscarUsuarioPorId(p[1]);
                if (usuario == null) {
                    continue;
                }

                ArrayList<DetalleCompra> detalles = new ArrayList<>();
                for (String lineaDetalle : lineasDetalles) {
                    String[] d = lineaDetalle.split(";", -1);
                    if (d.length == 4 && Integer.parseInt(d[0]) == numero) {
                        Producto producto = buscarProducto(Integer.parseInt(d[1]));
                        if (producto != null) {
                            detalles.add(new DetalleCompra(producto,
                                    Integer.parseInt(d[2]), Double.parseDouble(d[3])));
                        }
                    }
                }

                ventas.add(new Venta(numero, usuario, detalles, p[2], Double.parseDouble(p[3]),
                        Integer.parseInt(p[4]), Double.parseDouble(p[5]), Double.parseDouble(p[6])));
                if (numero > ultimoNumeroVenta) {
                    ultimoNumeroVenta = numero;
                }
            } catch (NumberFormatException e) {
                System.out.println("Línea ignorada en " + ARCHIVO_VENTAS + ": " + linea);
            }
        }
    }
}
