package com.mycompany.uposports;

import bbdd.ClienteDAO;
import bbdd.InstalacionDAO;
import bbdd.ReservaDAO;
import clases.Instalacion;
import clases.Reserva;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.WrappedSession;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.servlet.annotation.WebServlet;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Theme("mytheme")
@Title("Reserva")
public class ReservaUI extends UI {
    
    public static ArrayList<Reserva> listaReservas = new ArrayList(); //LISTA DONDE ESTARÁN ALMACENADOS TODAS LAS RESERVAS QUE CREEMOS
    public VerticalLayout layout = new VerticalLayout(); //LAYOUT PRINCIPAL
    final HorizontalLayout layoutHLabelabelTitulo = new HorizontalLayout();//Creamos un layout horizontal
    final HorizontalLayout layoutH2 = new HorizontalLayout();//Creamos un layout horizontal
    final HorizontalLayout layoutH = new HorizontalLayout();//Creamos un layout horizontal

    @Override
    protected void init(VaadinRequest request) {
        layout.removeAllComponents();
        layoutHLabelabelTitulo.removeAllComponents();
        layoutH2.removeAllComponents();
        layoutH.removeAllComponents();
        //RECUPERAMOS LA SESION Y SI NO HAY SESION NOS REDIRIGE A LA PÁGINA DE INICIO DE SESIÓN
        WrappedSession session = getSession().getSession();
        if (session.getAttribute("nombreUsuario") == null) {
            getUI().getPage().setLocation("/inicioSesion");
        }
        
        Label l = new Label("<h1 style='text-weight:bold;margin:auto;    padding-right: 100px;'>UPOSports</h2>", ContentMode.HTML);
        Label labelEntidad = new Label("<h2 style='text-weight:bold;margin:0'>Reservas - </h2>", ContentMode.HTML);
        
        Button buttonAbonos = new Button("Abonos", FontAwesome.MONEY);//Botón para acceder a la entidad abono
        buttonAbonos.addClickListener(e -> {//Acción del botón
            getUI().getPage().setLocation("/Abono");//Accedemos a la entidad abono
        });
        
        Button buttonCliente = new Button("Clientes", FontAwesome.MALE);//Botón para acceder a la entidad instalaciones
        buttonCliente.addClickListener(e -> {//Acción del botón
            getUI().getPage().setLocation("/Cliente");//Accedemos a la entidad abono
        });
        
        Button buttonInstalacion = new Button("Instalaciones", FontAwesome.BUILDING);//Botón para acceder a la entidad instalaciones
        buttonInstalacion.addClickListener(e -> {//Acción del botón
            getUI().getPage().setLocation("/Instalacion");//Accedemos a la entidad abono
        });
        
        Button buttonEmpleados = new Button("Empleados", FontAwesome.USERS);//Botón para acceder a la entidad instalaciones
        buttonEmpleados.addClickListener(e -> {//Acción del botón
            getUI().getPage().setLocation("/Empleado");//Accedemos a la entidad abono
        });
        
        Button buttonMateriales = new Button("Materiales", FontAwesome.ARCHIVE);//Botón para acceder a la entidad instalaciones
        buttonMateriales.addClickListener(e -> {//Acción del botón
            getUI().getPage().setLocation("/Material");//Accedemos a la entidad abono
        });
        
        Button buttonReservas = new Button("Reservas", FontAwesome.CALENDAR);//Botón para acceder a la entidad instalaciones
        buttonReservas.addClickListener(e -> {//Acción del botón
            getUI().getPage().setLocation("/Reserva");//Accedemos a la entidad abono
        });
        
        Button buttonLogout = new Button("Cerrar Sesión", FontAwesome.SIGN_OUT);//Botón para cerrar sesión
        buttonLogout.addClickListener(e -> {//Acción del botón
            VaadinSession.getCurrent().getSession().invalidate();//Eliminamos la sesión
            getUI().getPage().setLocation("/");//Accedemos a la página principal
        });
        
        Button buttonAnunciantes = new Button("Anunciantes", FontAwesome.BELL);//Botón para acceder a la entidad instalaciones
        buttonAnunciantes.addClickListener(e -> {//Acción del botón
            getUI().getPage().setLocation("/Anunciante");//Accedemos a la entidad abono
        });
        
        Button botonAdd = new Button("Crear Reserva", FontAwesome.PLUS_CIRCLE); //BOTÓN PARA AÑADIR CLIENTES
        layoutH2.setMargin(true);
        layoutH2.setSpacing(true);
        layoutH2.addComponents(labelEntidad, botonAdd);
        
        layoutHLabelabelTitulo.addComponent(l);
        layoutH.addComponents(layoutHLabelabelTitulo, buttonReservas, buttonCliente, buttonAbonos, buttonInstalacion, buttonMateriales, buttonEmpleados, buttonAnunciantes, buttonLogout);//Añadimos los componentes al layout horizontal
        layoutH2.setMargin(true);
        layoutH2.setSpacing(true);
        layoutH2.addComponents(labelEntidad, botonAdd);
        layout.addComponents(layoutH, layoutH2);//Añadimos los componentes al layout horizontal

        //CREAMOS UNA TABLA DONDE APARECERÁ LA LISTA DE CLIENTES
        Table tabla = new Table();
        tabla.setSizeFull();
        tabla.addContainerProperty("Fecha Reserva", String.class, null);
        tabla.addContainerProperty("Hora Inicio", String.class, null);
        tabla.addContainerProperty("Hora Fin", String.class, null);
        tabla.addContainerProperty("Nombre Cliente", String.class, null);
        tabla.addContainerProperty("Apellidos Cliente", String.class, null);
        tabla.addContainerProperty("Instalacion", String.class, null);
        tabla.addContainerProperty("Modificar", Button.class, null);
        tabla.addContainerProperty("Eliminar", Button.class, null);
        Iterator it;
        try {
            it = ReservaDAO.consultaReservas().iterator();
            
            int i = 0;
            //BUCLE PARA AÑADIR TODAS LAS RESERVA A LA TABLA
            if (!ReservaDAO.consultaReservas().isEmpty()) {
                while (it.hasNext()) {
                    Button eliminar = new Button("Eliminar", FontAwesome.CLOSE);
                    Button editar = new Button("Modificar", FontAwesome.EDIT);
                    Reserva aux = (Reserva) it.next();
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    DateFormat horaFormat = new SimpleDateFormat("HH:mm");
                    tabla.addItem(new Object[]{dateFormat.format(aux.getInicioReserva()), horaFormat.format(aux.getInicioReserva()),
                        horaFormat.format(aux.getFinReserva()), aux.getCliente().getNombre(), aux.getCliente().getApellidos(), aux.getInstalacion().getNombre(), editar, eliminar}, i);
                    i++;
                    eliminar.addClickListener(e -> {
                        try {
                            //AÑADIMOS EL BOTON DE ELIMINAR POR CADA Reserva
                            ReservaDAO.eliminaReserva(aux);//Elimina la reserva de la bbdd
                        } catch (UnknownHostException ex) {
                            Logger.getLogger(ReservaUI.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        init(request);
                    });
                    
                    editar.addClickListener(e -> { //AÑADIMOS EL BOTON DE EDITAR POR CADA CLIENTE
                        editarReserva(aux, request); //EJECUTA LA FUNCION EDITAR CLIENTE
                    });
                }
                layout.addComponent(tabla);
            }
            layout.setMargin(true);
            layout.setSpacing(true);
            setContent(layout);
            
            botonAdd.addClickListener(e -> {
                try {
                    creaReserva(request);
                } catch (UnknownHostException ex) {
                    Logger.getLogger(ReservaUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        } catch (UnknownHostException ex) {
            Logger.getLogger(ReservaUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void addReserva(Reserva r) {     //METODO QUE AÑADE UNA RESERVA A LA LISTA
        listaReservas.add(r);
    }
    
    public void creaReserva(VaadinRequest request) throws UnknownHostException {
        //CREAMOS UN FORMULARIO PARA QUE EL USUARIO INTRODUZCA LOS DATOS DE LA RESERVA A CREAR
        layout.removeAllComponents();
        HorizontalLayout datos = new HorizontalLayout();
        Label l = new Label("<h2 style='text-weight:bold;'>Nueva Reserva</h2>", ContentMode.HTML);
        layout.addComponent(l);
        DateField inicioReserva = new DateField("Introduzca el inicio de la reserva");
        inicioReserva.setResolution(Resolution.MINUTE);
        DateField finReserva = new DateField("Introduzca el fin de la reserva");
        finReserva.setResolution(Resolution.MINUTE);
        TextField dni = new TextField("DNI cliente:");
        dni.setIcon(FontAwesome.KEY);
        OptionGroup instalacion = new OptionGroup("Instalaciones:");
        ArrayList<Instalacion> listaInstalaciones = InstalacionDAO.mostrarInstalaciones();
        for (int i = 0; i < listaInstalaciones.size(); i++) {
            System.out.println(listaInstalaciones.get(i).getNombre());
            instalacion.addItems(listaInstalaciones.get(i).getNombre());
        }
        datos.addComponents(inicioReserva, finReserva, dni, instalacion);
        datos.setMargin(true);
        datos.setSpacing(true);
        Button enviar = new Button("Guardar", FontAwesome.CHECK);
        
        enviar.addClickListener(e -> {            
            try {
                //UNA VEZ PULSADO EL BOTÓN SE CREA LA RESERVA Y LA AÑADIMOS A LA LISTA
                if (comprobarDatos((Date) inicioReserva.getValue(), (Date) finReserva.getValue()) == true && comprobarCliente(dni.getValue())) {
                    try {
                        Reserva aux = new Reserva();                        
                        aux.setInicioReserva(inicioReserva.getValue());
                        aux.setFinReserva(finReserva.getValue());
                        aux.setCliente(ClienteDAO.buscarCliente(dni.getValue()));
                        aux.setInstalacion(InstalacionDAO.buscarInstalacion((String) instalacion.getValue()));
                        addReserva(aux);
                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        Notification.show("Reserva - Fecha: " + dateFormat.format(inicioReserva.getValue()), "Registrado con éxito",
                                Notification.Type.TRAY_NOTIFICATION);
                        init(request);
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(ReservaUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (UnknownHostException ex) {
                Logger.getLogger(ReservaUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        Button volver = new Button("Cancelar", FontAwesome.CLOSE);
        volver.addClickListener(e -> {
            init(request);
        });
        HorizontalLayout horiz = new HorizontalLayout();
        horiz.addComponents(volver, enviar);
        horiz.setSpacing(true);
        horiz.setMargin(true);
        layout.addComponents(datos, horiz);
        layout.setMargin(true);
        layout.setSpacing(true);
        setContent(layout);
    }
    
    public void editarReserva(Reserva r, VaadinRequest request) {
        layout.removeAllComponents();
        //CREAMOS UN FORMULARIO PARA PODER EDITAR LA RESERVA
        HorizontalLayout datos = new HorizontalLayout();
        Label l = new Label("<h2>Modificar Reserva</h2>", ContentMode.HTML);
        layout.addComponent(l);
        
        DateField inicioReserva = new DateField("Introduzca el inicio de la reserva");
        inicioReserva.setResolution(Resolution.MINUTE);
        inicioReserva.setValue(r.getInicioReserva());
        DateField finReserva = new DateField("Introduzca el fin de la reserva");
        finReserva.setResolution(Resolution.MINUTE);
        finReserva.setValue(r.getFinReserva());
        datos.addComponents(inicioReserva, finReserva);
        datos.setMargin(true);
        datos.setSpacing(true);
        Button enviar = new Button("Modificar", FontAwesome.EDIT);
        //EDITAMOS TODOS LOS CAMPOS QUE HAYA MODIFICADO EL USUARIO Y VOLVEMOS A INSERTAR EL CLIENTE EN LA LISTA
        enviar.addClickListener(e -> {
            if (comprobarDatos((Date) inicioReserva.getValue(), (Date) finReserva.getValue()) == true) {
                Reserva aux;
                try {
                    aux = new Reserva();
                    
                    aux.setInicioReserva(inicioReserva.getValue());
                    aux.setFinReserva(finReserva.getValue());
                    listaReservas.remove(r);
                    addReserva(aux);
                    init(request);
                } catch (UnknownHostException ex) {
                    Logger.getLogger(ReservaUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        Button volver = new Button("Cancelar", FontAwesome.CLOSE);
        //REDIRECCIONA A LA CLASE ReservaUI
        volver.addClickListener(e -> {
            init(request);
        });
        
        HorizontalLayout horiz = new HorizontalLayout();
        horiz.addComponents(volver, enviar);
        
        horiz.setSpacing(true);
        horiz.setMargin(true);
        layout.addComponents(datos, horiz);
        layout.setMargin(true);
        layout.setSpacing(true);
    }

    //Método para comprobar los datos introducidos en los formularios
    protected boolean comprobarDatos(Date ini, Date fin) {
        boolean b = false;//Variable booleana inicializada a false
        try {
            //Comprobamos si algún campo está vacío
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            if ((dateFormat.format(ini).equals(dateFormat.format(fin)))) {
                b = true;
                
            } else {//En caso de campo vacío, mostramos 2 tipos de error uno fijo y otro interactivo (para el proyecto final debatiremos este aspecto)
                //Notificacion de tipo Warning interactiva para el usuario.
                Notification.show("Error Fechas", "La fecha de inicio y fin deben ser iguales",
                        Notification.Type.WARNING_MESSAGE);
            }
            return b;
        } catch (NullPointerException e) {
            
            Notification.show("Error Fechas", "Introduzca la fecha y hora haciendo click en el calendario",
                    Notification.Type.WARNING_MESSAGE);
        }
        return b;
    }
    
    protected boolean comprobarCliente(String dni) throws UnknownHostException {
        if (ClienteDAO.buscarCliente(dni) == null) {
            Notification.show("Cliente no encontrado", "El dni no corresponde con ningún cliente registrado",
                    Notification.Type.WARNING_MESSAGE);
            return false;
        } else {
            return true;
        }        
    }
    
    @WebServlet(urlPatterns = {"/Reserva/*"}, name = "gestionaReservaServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = ReservaUI.class, productionMode = false)
    public static class gestionaReservaServlet extends VaadinServlet {
        
    }
    
}
