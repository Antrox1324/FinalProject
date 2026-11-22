package usuario;

public class Usuario {

    public static final String ROL_CLIENTE = "CLIENTE";
    public static final String ROL_ADMIN = "ADMIN";

    private final String id;
    private String nombre;
    private String correo;
    private String password;
    private final String rol;

    // Usuario normal: siempre queda como cliente.
    public Usuario(String nombre, String correo, String password, String id) {
        this(nombre, correo, password, id, ROL_CLIENTE);
    }

    public Usuario(String nombre, String correo, String password, String id, String rol) {
        this.nombre = nombre;
        this.correo = correo;
        this.password = password;
        this.id = id;
        this.rol = rol;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getPassword() {
        return password;
    }

    public String getRol() {
        return rol;
    }

    public boolean esAdministrador() {
        return ROL_ADMIN.equals(rol);
    }
}
