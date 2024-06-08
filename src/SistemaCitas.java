import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SistemaCitas { //PRINCIPAÑ ARRAY
    private List<Doctor> doctores;
    private List<Paciente> pacientes;
    private List<Citas> citas;
    private List<Admin> admins;

    public SistemaCitas() {
        this.doctores = new ArrayList<>();
        this.pacientes = new ArrayList<>();
        this.citas = new ArrayList<>();
        this.admins = new ArrayList<>();
        cargarDatos();
    }

    public void darDeAltaDoctor(String id, String nombreCompleto, String especialidad) {
        Doctor doctor = new Doctor(id, nombreCompleto, especialidad);
        doctores.add(doctor);
        guardarDatos();
    }

    public void darDeAltaPaciente(String id, String nombreCompleto) {
        Paciente paciente = new Paciente(id, nombreCompleto);
        pacientes.add(paciente);
        guardarDatos();
    }

    public void crearCita(String id, LocalDateTime fechaHora, String motivo, Doctor doctor, Paciente paciente) {
        Citas cita = new Citas(id, fechaHora, motivo, doctor, paciente);
        citas.add(cita);
        guardarDatos();
    }

    public boolean verificarAdmin(String id, String password) {
        for (Admin admin : admins) {
            if (admin.getId().equals(id) && admin.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    private void cargarDatos() {
        try {
            // Cargar Doctores
            Path pathDoctores = Paths.get("doctores.csv");
            if (Files.exists(pathDoctores)) {
                List<String> lines = Files.readAllLines(pathDoctores);
                for (String line : lines) {
                    String[] data = line.split(",");
                    darDeAltaDoctor(data[0], data[1], data[2]);
                }
            } else {
                Files.createFile(pathDoctores);
            }

            // Cargar Pacienteee
            Path pathPacientes = Paths.get("pacientes.csv");
            if (Files.exists(pathPacientes)) {
                List<String> lines = Files.readAllLines(pathPacientes);
                for (String line : lines) {
                    String[] data = line.split(",");
                    darDeAltaPaciente(data[0], data[1]);
                }
            } else {
                Files.createFile(pathPacientes);
            }

            // Cargar Citass
            Path pathCitas = Paths.get("citas.csv");
            if (Files.exists(pathCitas)) {
                List<String> lines = Files.readAllLines(pathCitas);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                for (String line : lines) {
                    String[] data = line.split(",");
                    Doctor doctor = buscarDoctorPorId(data[3]);
                    Paciente paciente = buscarPacientePorId(data[4]);
                    crearCita(data[0], LocalDateTime.parse(data[1], formatter), data[2], doctor, paciente);
                }
            } else {
                Files.createFile(pathCitas);
            }

            // LOADING Admins
            Path pathAdmins = Paths.get("admins.csv");
            if (Files.exists(pathAdmins)) {
                List<String> lines = Files.readAllLines(pathAdmins);
                if (lines.isEmpty()) {
                    // Si el archivo está vacío, agregar un administrador predeterminado
                    admins.add(new Admin("admin", "admin123"));
                } else {
                    for (String line : lines) {
                        String[] data = line.split(",");
                        admins.add(new Admin(data[0], data[1]));
                    }
                }
            } else {
                Files.createFile(pathAdmins);
                // Agregar un administrador predeterminado
                admins.add(new Admin("admin", "admin123"));
            }
            // revision para verificar el guardadod el admin
            guardarDatos();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//GUARDADO DE TODOS LOS DATOS EN CSV
private void guardarDatos() {
    try {
        // Verificar si la carpeta DB existe, y si no, crearla
        Path dbFolder = Paths.get("DB");
        if (!Files.exists(dbFolder)) {
            Files.createDirectory(dbFolder);
        }

        // Guardar Doctores
        List<String> lines = new ArrayList<>();
        for (Doctor doctor : doctores) {
            lines.add(doctor.getId() + "," + doctor.getNombreCompleto() + "," + doctor.getEspecialidad());
        }
        Files.write(Paths.get("DB/doctores.csv"), lines);

        // Guardar Pacient
        lines = new ArrayList<>();
        for (Paciente paciente : pacientes) {
            lines.add(paciente.getId() + "," + paciente.getNombreCompleto());
        }
        Files.write(Paths.get("DB/pacientes.csv"), lines);

        // Guardar Cita
        lines = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (Citas cita : citas) {
            lines.add(cita.getId() + "," + cita.getFechaHora().format(formatter) + "," + cita.getMotivo() + "," + cita.getDoctor().getId() + "," + cita.getPaciente().getId());
        }
        Files.write(Paths.get("DB/citas.csv"), lines);

        // Guardar superusuarios
        lines = new ArrayList<>();
        for (Admin admin : admins) {
            lines.add(admin.getId() + "," + admin.getPassword());
        }
        Files.write(Paths.get("DB/admins.csv"), lines);
    } catch (IOException e) {
        e.printStackTrace();
    }
}

    private Doctor buscarDoctorPorId(String id) {
        for (Doctor doctor : doctores) {
            if (doctor.getId().equals(id)) {
                return doctor;
            }
        }
        return null;
    }

    private Paciente buscarPacientePorId(String id) {
        for (Paciente paciente : pacientes) {
            if (paciente.getId().equals(id)) {
                return paciente;
            }
        }
        return null;
    }

    public void mostrarDoctores() {
        if (doctores.isEmpty()) {
            System.out.println("No hay doctores registrados.");
        } else {
            System.out.println("Doctores registrados:");
            for (Doctor doctor : doctores) {
                System.out.println("ID: " + doctor.getId() + ", Nombre: " + doctor.getNombreCompleto() + ", Especialidad: " + doctor.getEspecialidad());
            }
        }
    }

    public void mostrarPacientes() {
        if (pacientes.isEmpty()) {
            System.out.println("No hay pacientes registrados.");
        } else {
            System.out.println("Pacientes registrados:");
            for (Paciente paciente : pacientes) {
                System.out.println("ID: " + paciente.getId() + ", Nombre: " + paciente.getNombreCompleto());
            }
        }
    }

    public void mostrarCitas() {
        if (citas.isEmpty()) {
            System.out.println("No hay citas registradas.");
        } else {
            System.out.println("Citas registradas:");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            for (Citas cita : citas) {
                System.out.println("ID: " + cita.getId() + ", Fecha y Hora: " + cita.getFechaHora().format(formatter) + ", Motivo: " + cita.getMotivo() + ", Doctor: " + cita.getDoctor().getNombreCompleto() + ", Paciente: " + cita.getPaciente().getNombreCompleto());
            }
        }
    }

    public static void main(String[] args) { //MENU VISUAL PRINCIPAL
        SistemaCitas sistema = new SistemaCitas();
        Scanner scanner = new Scanner(System.in);

        System.out.print("Ingrese ID de Admin: ");
        String adminId = scanner.nextLine();
        System.out.print("Ingrese Contraseña de Admin: ");
        String adminPassword = scanner.nextLine();

        if (!sistema.verificarAdmin(adminId, adminPassword)) {
            System.out.println("Credenciales incorrectas. Acceso denegado.");
            return;
        }

        while (true) {
            System.out.println("Menú del Sistema de Citas");
            System.out.println("1. Dar de alta Doctor");
            System.out.println("2. Dar de alta Paciente");
            System.out.println("3. Crear Cita");
            System.out.println("4. Mostrar Doctores");
            System.out.println("5. Mostrar Pacientes");
            System.out.println("6. Mostrar Citas");
            System.out.println("7. Salir");
            System.out.print("Seleccione una opción: ");
            int opcion = scanner.nextInt();
            scanner.nextLine();  // Consume the newline

            switch (opcion) {
                case 1:
                    System.out.print("Ingrese ID del Doctor: ");
                    String idDoctor = scanner.nextLine();
                    System.out.print("Ingrese Nombre Completo del Doctor:");
                    String nombreDoctor = scanner.nextLine();
                    System.out.print("Ingrese Especialidad del Doctor: ");
                    String especialidadDoctor = scanner.nextLine();
                    sistema.darDeAltaDoctor(idDoctor, nombreDoctor, especialidadDoctor);
                    System.out.println("Doctor dado de alta exitosamentw");
                    break;
                case 2:
                    System.out.print("Ingrese ID del Paciente: ");
                    String idPaciente = scanner.nextLine();
                    System.out.print("Ingrese Nombre Completo del Paciente: ");
                    String nombrePaciente = scanner.nextLine();
                    sistema.darDeAltaPaciente(idPaciente, nombrePaciente);
                    System.out.println("Paciente dado de alta exitosamentw.");
                    break;
                case 3:
                    System.out.print("Ingrese ID de la Cita: ");
                    String idCita = scanner.nextLine();
                    System.out.print("Ingrese Fecha y Hora de la Cita (yyyy-MM-dd HH:mm): "); // así es el formato de cuardado LOCAL DATE TIME
                    String fechaHoraCita = scanner.nextLine();
                    LocalDateTime fechaHora = LocalDateTime.parse(fechaHoraCita, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                    System.out.print("Ingrese Motivo de la Cita: ");
                    String motivoCita = scanner.nextLine();
                    System.out.print("Ingrese ID del Doctor: ");
                    String doctorId = scanner.nextLine();
                    Doctor doctor = sistema.buscarDoctorPorId(doctorId);
                    if (doctor == null) {
                        System.out.println("Doctor no encontrado");
                        break;
                    }
                    System.out.print("Ingrese ID del Paciente en curso: ");
                    String pacienteId = scanner.nextLine();
                    Paciente paciente = sistema.buscarPacientePorId(pacienteId);
                    if (paciente == null) {
                        System.out.println("Paciente no encontrado.");
                        break;
                    }
                    sistema.crearCita(idCita, fechaHora, motivoCita, doctor, paciente);
                    System.out.println("Cita creada exitosamente");
                    break;
                case 4:
                    sistema.mostrarDoctores();
                    break;
                case 5:
                    sistema.mostrarPacientes();
                    break;
                case 6:
                    sistema.mostrarCitas();
                    break;
                case 7:
                    System.out.println("Saliendo del sistema.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Opción no válida. Intente nuevamente.");
            }
        }
    }
}
