package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RegexValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import gal.sdc.usc.wallstreet.Main;
import gal.sdc.usc.wallstreet.components.BotonObservable;
import gal.sdc.usc.wallstreet.components.IconoObservable;
import gal.sdc.usc.wallstreet.model.Empresa;
import gal.sdc.usc.wallstreet.model.Inversor;
import gal.sdc.usc.wallstreet.model.OfertaVenta;
import gal.sdc.usc.wallstreet.model.PropuestaCompra;
import gal.sdc.usc.wallstreet.model.Sociedad;
import gal.sdc.usc.wallstreet.model.SuperUsuario;
import gal.sdc.usc.wallstreet.model.Usuario;
import gal.sdc.usc.wallstreet.model.UsuarioSesion;
import gal.sdc.usc.wallstreet.repository.OfertaVentaDAO;
import gal.sdc.usc.wallstreet.repository.PropuestaCompraDAO;
import gal.sdc.usc.wallstreet.repository.SociedadDAO;
import gal.sdc.usc.wallstreet.repository.SuperUsuarioDAO;
import gal.sdc.usc.wallstreet.repository.UsuarioDAO;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;
import gal.sdc.usc.wallstreet.util.Comprador;
import gal.sdc.usc.wallstreet.util.Comunicador;
import gal.sdc.usc.wallstreet.util.ErrorValidator;
import gal.sdc.usc.wallstreet.util.Iconos;
import gal.sdc.usc.wallstreet.util.Validadores;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class SociedadController extends DatabaseLinker implements Initializable {
    public static final String VIEW = "sociedad";
    public static final Integer HEIGHT = 600;
    public static final Integer WIDTH = 800;
    public static final String TITULO = "Sociedad";

    private final BooleanProperty editando = new SimpleBooleanProperty(false);

    @FXML
    private JFXTabPane tabVentana;

    @FXML
    private JFXTextField txtIdentificador;
    @FXML
    private JFXTextField txtSaldoComunal;
    @FXML
    private JFXTextField txtTolerancia;
    @FXML
    private JFXComboBox<Label> cmbToleranciaUnidad;
    @FXML
    private JFXButton btnSaldo;

    @FXML
    private TableView<PropuestaCompra> tblPropuestas;
    @FXML
    private TableView<UsuarioSesion> tblMiembros;

    @FXML
    private JFXButton btnVolver;
    @FXML
    private JFXButton btnAbandonar;
    @FXML
    private JFXButton btnEditar;

    private ErrorValidator usuarioYaExiste;

    public SociedadController() {
    }

    private void asignarValores() {
        UsuarioSesion us = super.getUsuarioSesion();
        Usuario u = us.getUsuario();
        Sociedad s = u.getSociedad();

        int tolerancia = s.getTolerancia();
        String valor = String.valueOf(tolerancia);
        cmbToleranciaUnidad.getSelectionModel().select(0);
        if (tolerancia % 60 * 24 == 0) {
            valor = String.valueOf(tolerancia / (60 * 24));
            cmbToleranciaUnidad.getSelectionModel().select(2);
        } else if (tolerancia % 60 == 0) {
            valor = String.valueOf(tolerancia / 60);
            cmbToleranciaUnidad.getSelectionModel().select(1);
        }

        txtIdentificador.setText(s.getSuperUsuario().getIdentificador());
        txtTolerancia.setText(valor);
        txtSaldoComunal.setText(s.getSaldoComunal().toString());
    }

    private Integer generarTolerancia() {
        String tipo = cmbToleranciaUnidad.getSelectionModel().getSelectedItem().getId();

        int valor = Integer.parseInt(txtTolerancia.getText());
        if (tipo.equals("hora")) {
            valor = valor * 60;
        } else if (tipo.equals("dia")) {
            valor = valor * 60 * 24;
        }

        return valor;
    }

    private void onBtnEditarGuardar(ActionEvent e) {
        if (!editando.get()) {
            btnEditar.setText("Guardar");
            btnVolver.setText("Cancelar");
            editando.setValue(true);
            // tabVentana.setDisable(true);
            tabVentana.getTabs().forEach(a -> {
                if (a.getId() == null) a.setDisable(true);
            });
        } else {
            if (!txtIdentificador.validate() || !txtTolerancia.validate()) return;
            Usuario u = super.getUsuarioSesion().getUsuario();

            if (!u.getSociedad().getSuperUsuario().getIdentificador().equals(txtIdentificador.getText().toLowerCase())
                    && super.getDAO(SuperUsuarioDAO.class).seleccionar(txtIdentificador.getText().toLowerCase()) != null) {
                if (txtIdentificador.getValidators().size() == 1) txtIdentificador.getValidators().add(usuarioYaExiste);
                txtIdentificador.validate();
                return;
            }

            super.iniciarTransaccion();

            if (!txtIdentificador.getText().toLowerCase()
                    .equals(u.getSociedad().getSuperUsuario().getIdentificador())) {
                super.getDAO(SuperUsuarioDAO.class).actualizarIdentificador(
                        u.getSuperUsuario().getIdentificador(),
                        txtIdentificador.getText().toLowerCase()
                );
            }

            SuperUsuario superUsuario = new SuperUsuario.Builder()
                    .withIdentificador(txtIdentificador.getText().toLowerCase())
                    .build();

            Sociedad s = new Sociedad.Builder(superUsuario)
                    .withSaldoComunal(u.getSociedad().getSaldoComunal())
                    .withTolerancia(generarTolerancia())
                    .build();

            super.getDAO(SociedadDAO.class).actualizar(s);

            if (super.ejecutarTransaccion()) {
                btnEditar.setText("Editar");
                btnVolver.setText("Volver");
                editando.setValue(false);
                // tabVentana.setDisable(false);
                tabVentana.getTabs().forEach(a -> {
                    if (a.getId() == null) a.setDisable(false);
                });
                u.setSociedad(s);
                Main.mensaje("Se han actualizado los datos de la sociedad");
                actualizarTablaPropuestas(s);
            } else {
                Main.mensaje("Error actualizando los datos");
            }
        }
    }

    private void onBtnEditarPropuesta(ActionEvent e) {
        Sociedad s = super.getUsuarioSesion().getUsuario().getSociedad();
        if (super.getDAO(UsuarioDAO.class).getUsuariosPorSociedad(s).size() < 2) {
            Main.mensaje("La sociedad es demasiado pequeña como para realizar compras");
            return;
        }

        Comunicador comunicador = new Comunicador() {
            @Override
            public Object[] getData() {
                return new Object[]{s};
            }

            @Override
            public void onSuccess() {
                actualizarTablaPropuestas(s);
            }

            @Override
            public void onFailure() {
                Main.mensaje("Hubo un error creando la propuesta");
            }
        };

        SociedadPropuestaController.setComunicador(comunicador);
        Main.dialogo(
                SociedadPropuestaController.VIEW,
                SociedadPropuestaController.WIDTH,
                SociedadPropuestaController.HEIGHT,
                SociedadPropuestaController.TITULO
        );
    }

    private void onBtnEditarMiembro(ActionEvent e) {
        Sociedad s = super.getUsuarioSesion().getUsuario().getSociedad();
        Comunicador comunicador = new Comunicador() {
            @Override
            public Object[] getData() {
                return new Object[]{s};
            }

            @Override
            public void onSuccess() {
                actualizarTablaMiembros(s);
            }

            @Override
            public void onFailure() {
                Main.mensaje("Hubo un error invitando al usuario");
            }
        };
        SociedadMiembroController.setComunicador(comunicador);
        Main.dialogo(
                SociedadMiembroController.VIEW,
                SociedadMiembroController.WIDTH,
                SociedadMiembroController.HEIGHT,
                SociedadMiembroController.TITULO
        );
    }

    private void onBtnEditar(ActionEvent e) {
        switch (tabVentana.getSelectionModel().getSelectedIndex()) {
            case 0:
                onBtnEditarGuardar(e);
                break;
            case 1:
                onBtnEditarMiembro(e);
                break;
            case 2:
                onBtnEditarPropuesta(e);
                break;
        }
    }

    private void onBtnVolver(ActionEvent e) {
        if (!editando.get()) {
            Main.ventana(PrincipalController.VIEW, PrincipalController.WIDTH, PrincipalController.HEIGHT, PrincipalController.TITULO);
        } else {
            this.asignarValores();
            editando.setValue(false);
            // tabVentana.setDisable(false)
            tabVentana.getTabs().forEach(a -> {
                if (a.getId() == null) a.setDisable(false);
            });
            btnEditar.setText("Editar");
            btnVolver.setText("Volver");
        }
    }

    private void onBtnSaldo(ActionEvent e) {
        Comunicador comunicador = new Comunicador() {
            @Override
            public Object[] getData() {
                return new Object[]{SociedadController.super.getUsuarioSesion().getUsuario().getSociedad()};
            }

            @Override
            public void onSuccess() {
                asignarValores();
            }

            @Override
            public void onFailure() {
                Main.mensaje("Hubo un error transfiriendo los fondos");
            }
        };
        SociedadSaldoController.setComunicador(comunicador);
        Main.dialogo(
                SociedadSaldoController.VIEW,
                SociedadSaldoController.WIDTH,
                SociedadSaldoController.HEIGHT,
                SociedadSaldoController.TITULO
        );
    }

    private void onBtnAbandonar(ActionEvent e) {
        ConfirmacionController.setComunicador(new Comunicador() {
            @Override
            public Object[] getData() {
                return new Object[]{"¿Estás seguro de abandonar la sociedad?"};
            }

            @Override
            public void onSuccess() {
                Usuario usuario = SociedadController.super.getUsuarioSesion().getUsuario();
                usuario.setSociedad(null);
                if (SociedadController.super.getDAO(UsuarioDAO.class).actualizar(usuario)) {
                    Main.ventana(PrincipalController.VIEW, PrincipalController.WIDTH, PrincipalController.HEIGHT, PrincipalController.TITULO);
                    Main.mensaje("Se ha solicitado la baja en el sistema");
                } else {
                    Main.mensaje("Hubo un error solicitando la baja");
                }
            }
        });
        Main.dialogo(ConfirmacionController.VIEW, ConfirmacionController.WIDTH, ConfirmacionController.HEIGHT, ConfirmacionController.TITULO);
    }

    private void actualizarVentana() {
        switch (tabVentana.getSelectionModel().getSelectedIndex()) {
            case 0:
                btnEditar.setText("Editar");
                btnAbandonar.setVisible(true);
                break;
            case 1:
                btnEditar.setText("Invitar");
                btnAbandonar.setVisible(false);
                break;
            case 2:
                btnEditar.setText("Nueva");
                btnAbandonar.setVisible(false);
                break;
        }
    }

    public void onBtnAccion(PropuestaCompra pc, boolean ejecutar) {
        if (ejecutar) {
            if (super.getDAO(UsuarioDAO.class).getUsuariosPorSociedad(pc.getSociedad()).size() < 2) {
                Main.mensaje("La sociedad es demasiado pequeña como para realizar compras");
                return;
            }

            super.iniciarTransaccion();
            super.getDAO(PropuestaCompraDAO.class).eliminar(pc);

            actualizarTablaPropuestas(pc.getSociedad());

            List<OfertaVenta> ofertas = super.getDAO(OfertaVentaDAO.class).getOfertasVenta(
                    pc.getEmpresa().getUsuario().getSuperUsuario().getIdentificador(),
                    pc.getPrecioMax() == null ? 0.0f : pc.getPrecioMax()
            );

            Integer res = Comprador.comprar(
                    super.getUsuarioSesion().getUsuario().getSociedad(),
                    ofertas,
                    pc.getCantidad()
            );

            if (super.ejecutarTransaccion()) {
                if (res == 0) Main.mensaje("No había participaciones a la venta");
                else Main.mensaje("Se han comprado " + res + " participaciones");
            } else {
                Main.mensaje("Hubo un error realizando la compra");
            }
        } else {
            if (super.getDAO(PropuestaCompraDAO.class).eliminar(pc)) {
                actualizarTablaPropuestas(pc.getSociedad());
                Main.mensaje("Se ha rechazado la propuesta");
            } else {
                Main.mensaje("Hubo un error rechazando la propuesta");
            }
        }
    }

    public BotonObservable extraerBoton(PropuestaCompra pc, Integer tolerancia) {
        JFXButton button = new JFXButton();

        if (super.getUsuarioSesion().getUsuario().getLider()) {
            button.setGraphic(Iconos.icono(FontAwesomeIcon.PLAY_CIRCLE));
            button.setOnAction(e -> this.onBtnAccion(pc, true));
            long t = new Date().getTime() - 1000 * 60 * tolerancia;
            if (pc.getFechaInicio().after(new Date(t))) {
                button.setDisable(true);
            }
        } else {
            button.setGraphic(Iconos.icono(FontAwesomeIcon.USER_TIMES));
            button.setOnAction(e -> this.onBtnAccion(pc, false));
        }

        return new BotonObservable(button);
    }

    public String extraerFecha(Date date) {
        SimpleDateFormat sdc = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        return sdc.format(date);
    }

    private void generarTablaPropuestas() {
        TableColumn<PropuestaCompra, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setPrefWidth(150);
        colFecha.setCellValueFactory((TableColumn.CellDataFeatures<PropuestaCompra, String> param)
                -> new SimpleStringProperty(extraerFecha(param.getValue().getFechaInicio())));

        TableColumn<PropuestaCompra, Number> colCantidad = new TableColumn<>("Cantidad");
        colCantidad.setPrefWidth(150);
        colCantidad.setCellValueFactory((TableColumn.CellDataFeatures<PropuestaCompra, Number> param)
                -> new SimpleIntegerProperty(param.getValue().getCantidad()));

        TableColumn<PropuestaCompra, Number> colPrecioMax = new TableColumn<>("Precio Max");
        colPrecioMax.setPrefWidth(150);
        colPrecioMax.setCellValueFactory((TableColumn.CellDataFeatures<PropuestaCompra, Number> param)
                -> new SimpleFloatProperty(param.getValue().getPrecioMax()));

        TableColumn<PropuestaCompra, String> colEmpresa = new TableColumn<>("Empresa");
        colEmpresa.setPrefWidth(150);
        colEmpresa.setCellValueFactory((TableColumn.CellDataFeatures<PropuestaCompra, String> param)
                -> new SimpleStringProperty(param.getValue().getEmpresa().getNombre()));

        Integer tolerancia = super.getDAO(SociedadDAO.class).seleccionar(
                super.getUsuarioSesion().getUsuario().getSociedad().getSuperUsuario()
        ).getTolerancia();
        TableColumn<PropuestaCompra, Node> colAccion = new TableColumn<>("");
        colAccion.setCellValueFactory((TableColumn.CellDataFeatures<PropuestaCompra, Node> param)
                -> extraerBoton(param.getValue(), tolerancia));

        tblPropuestas.getColumns().addAll(Arrays.asList(colFecha, colCantidad, colPrecioMax, colEmpresa, colAccion));
    }

    private String extraerNombre(UsuarioSesion us) {
        if (us instanceof Inversor) {
            return ((Inversor) us).getNombre() + " " + ((Inversor) us).getApellidos();
        } else if (us instanceof Empresa) {
            return ((Empresa) us).getNombre();
        }
        return us.getUsuario().getSuperUsuario().getIdentificador();
    }

    private String extraerDniCif(UsuarioSesion us) {
        if (us instanceof Inversor) {
            return ((Inversor) us).getDni();
        } else if (us instanceof Empresa) {
            return ((Empresa) us).getCif();
        }
        return us.getUsuario().getSuperUsuario().getIdentificador();
    }

    private void generarTablaMiembros() {
        TableColumn<UsuarioSesion, Node> colLider = new TableColumn<>("");
        // colIdentificador.setPrefWidth(150);
        colLider.setCellValueFactory((TableColumn.CellDataFeatures<UsuarioSesion, Node> param)
                -> param.getValue().getUsuario().getLider() ? new IconoObservable(FontAwesomeIcon.SHIELD) : null);
        colLider.setStyle("-fx-alignment: CENTER;");

        TableColumn<UsuarioSesion, String> colIdentificador = new TableColumn<>("Identificador");
        colIdentificador.setPrefWidth(150);
        colIdentificador.setCellValueFactory((TableColumn.CellDataFeatures<UsuarioSesion, String> param)
                -> new SimpleStringProperty(param.getValue().getUsuario().getSuperUsuario().getIdentificador()));

        TableColumn<UsuarioSesion, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setPrefWidth(150);
        colNombre.setCellValueFactory((TableColumn.CellDataFeatures<UsuarioSesion, String> param)
                -> new SimpleStringProperty(extraerNombre(param.getValue())));

        TableColumn<UsuarioSesion, String> colDniCif = new TableColumn<>("DNI/CIF");
        colDniCif.setPrefWidth(150);
        colDniCif.setCellValueFactory((TableColumn.CellDataFeatures<UsuarioSesion, String> param)
                -> new SimpleStringProperty(extraerDniCif(param.getValue())));

        tblMiembros.getColumns().addAll(Arrays.asList(colLider, colIdentificador, colNombre, colDniCif));
    }

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        if (super.getUsuarioSesion().getUsuario().getSociedad() == null) {
            this.onBtnVolver(null);
            return;
        }

        Usuario u = super.getUsuarioSesion().getUsuario();
        Sociedad s = u.getSociedad();

        if (!u.getLider()) {
            btnEditar.setDisable(true);
        } else {
            btnAbandonar.setDisable(true);
        }

        this.usuarioYaExiste = Validadores.personalizado("Este usuario ya existe");

        RequiredFieldValidator rfv = Validadores.requerido();
        txtIdentificador.getValidators().add(rfv);
        txtTolerancia.getValidators().add(rfv);
        cmbToleranciaUnidad.getValidators().add(rfv);

        RegexValidator rgx = new RegexValidator("Introduce un número válido");
        // rgx.setRegexPattern("^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$");
        rgx.setRegexPattern("^[-0-9]*$");
        txtTolerancia.getValidators().add(rgx);

        txtIdentificador.disableProperty().bind(editando.not());
        txtTolerancia.disableProperty().bind(editando.not());
        cmbToleranciaUnidad.disableProperty().bind(editando.not());

        txtIdentificador.textProperty().addListener((observable, oldValue, newValue) -> {
            // Limitar a 16 caracteres
            if (!newValue.matches("[a-zA-Z0-9_]{0,16}")) {
                txtIdentificador.setText(oldValue);
            }

            // Si hay más de un validador, es porque se ha insertado el "forzado" para mostrar error de
            // usuario ya existe, y por ello, se ha de eliminar cuando se actualice el campo
            if (txtIdentificador.getValidators().size() > 1) {
                txtIdentificador.getValidators().remove(1);
                txtIdentificador.validate();
            }
        });

        tabVentana.getSelectionModel().selectedItemProperty().addListener(listener -> actualizarVentana());

        generarTablaPropuestas();
        generarTablaMiembros();

        actualizarTablaPropuestas(s);
        actualizarTablaMiembros(s);

        Label lblMinuto = new Label("Minutos");
        lblMinuto.setId("minuto");
        cmbToleranciaUnidad.getItems().addAll(lblMinuto);
        Label lblHora = new Label("Horas");
        lblHora.setId("hora");
        cmbToleranciaUnidad.getItems().addAll(lblHora);
        Label lblDia = new Label("Días");
        lblDia.setId("dia");
        cmbToleranciaUnidad.getItems().addAll(lblDia);
        cmbToleranciaUnidad.getSelectionModel().selectFirst();

        btnVolver.setOnAction(this::onBtnVolver);
        btnSaldo.setOnAction(this::onBtnSaldo);
        btnAbandonar.setOnAction(this::onBtnAbandonar);
        btnEditar.setOnAction(this::onBtnEditar);

        // btnVolver.setGraphic(Iconos.icono(FontAwesomeIcon.CHEVRON_LEFT, "0.9em"));

        this.asignarValores();
    }

    private void actualizarTablaPropuestas(Sociedad s) {
        ObservableList<PropuestaCompra> pcs = FXCollections.observableList(
                super.getDAO(PropuestaCompraDAO.class).getPropuestasPorSociedad(s)
        );
        tblPropuestas.setItems(pcs);
    }

    private void actualizarTablaMiembros(Sociedad s) {
        ObservableList<UsuarioSesion> pcs = FXCollections.observableList(
                super.getDAO(UsuarioDAO.class).getUsuariosPorSociedad(s)
        );
        tblMiembros.setItems(pcs);
    }
}
