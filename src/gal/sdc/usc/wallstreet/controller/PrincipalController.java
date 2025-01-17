package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import gal.sdc.usc.wallstreet.Main;
import gal.sdc.usc.wallstreet.model.OfertaVenta;
import gal.sdc.usc.wallstreet.model.Participacion;
import gal.sdc.usc.wallstreet.model.Usuario;
import gal.sdc.usc.wallstreet.model.UsuarioTipo;
import gal.sdc.usc.wallstreet.repository.OfertaVentaDAO;
import gal.sdc.usc.wallstreet.repository.ParticipacionDAO;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;
import gal.sdc.usc.wallstreet.util.Comunicador;
import gal.sdc.usc.wallstreet.util.Iconos;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;


public class PrincipalController extends DatabaseLinker {
    public static final String VIEW = "principal";
    public static final Integer HEIGHT = 555;
    public static final Integer WIDTH = 867;
    public static final String TITULO = "Ventana principal";
    Scene scene;
    Usuario usuario;
    List<Participacion> participacionesUsuario;
    List<OfertaVenta> ofertaVentaUsuario;
    @FXML
    private JFXButton buttonParticipaciones;
    @FXML
    private JFXButton buttonPagos;
    @FXML
    private JFXButton buttonComprar;
    @FXML
    private JFXButton buttonVender;
    @FXML
    private JFXButton buttonMostrarMas;
    @FXML
    private TableView<Participacion> tablaParticipaciones;
    @FXML
    private TableView<OfertaVenta> tablaOfertasVenta;
    @FXML
    private TableColumn<Participacion, String> colEmpresa;
    @FXML
    private TableColumn<Participacion, Integer> colCantidad;
    @FXML
    private TableColumn<OfertaVenta, String> colEmpresa2;
    @FXML
    private TableColumn<OfertaVenta, Float> colPrecio;
    @FXML
    private TableColumn<OfertaVenta, Integer> colNParticipaciones;
    @FXML
    private Label txtSaldo;
    @FXML
    private Menu buttonPerfil;
    @FXML
    private Menu buttonEstadisticas;
    @FXML
    private MenuItem buttonCerrarSesion;
    @FXML
    private MenuItem buttonVerPerfil;
    @FXML
    private MenuItem buttonSociedad;

    @FXML
    public void initialize() {
        Group root = new Group();
        scene = new Scene(root, WIDTH, HEIGHT);
        usuario = super.getUsuarioSesion().getUsuario();
        ofertaVentaUsuario = super.getDAO(OfertaVentaDAO.class).getOfertasVentaPorUsuario(usuario.getSuperUsuario().getIdentificador(), 6);
        participacionesUsuario = super.getDAO(ParticipacionDAO.class).getParticipacionesPorUsuario(usuario.getSuperUsuario().getIdentificador(), 6);

        seleccionVentana(super.getTipoUsuario().equals(UsuarioTipo.EMPRESA));
        mostrarSaldo();

        gestionTablaParticipaciones(participacionesUsuario);
        gestionTablaOfertas(ofertaVentaUsuario);

        if (usuario.getSociedad() == null) {
            buttonSociedad.setText("Nueva sociedad");
        }

        buttonPerfil.setGraphic(Iconos.icono(FontAwesomeIcon.USERS, "2.5em"));
        buttonVerPerfil.setGraphic(Iconos.icono(FontAwesomeIcon.USER));
        buttonVerPerfil.setOnAction(e -> {
            Main.ventana(PerfilController.VIEW, PerfilController.WIDTH, PerfilController.HEIGHT, PerfilController.TITULO);
        });
        buttonEstadisticas.setGraphic(Iconos.icono(FontAwesomeIcon.BAR_CHART, "2.5em"));
        buttonCerrarSesion.setGraphic(Iconos.icono(FontAwesomeIcon.POWER_OFF));
        buttonSociedad.setGraphic(Iconos.icono(FontAwesomeIcon.SHARE_ALT));

        buttonCerrarSesion.setOnAction(event -> {
            super.cerrarSesion();
            Main.ventana(AccesoController.VIEW, AccesoController.WIDTH, AccesoController.HEIGHT, AccesoController.TITULO);
        });

        buttonComprar.setOnAction(e -> {
            Main.ventana(VCompraController.VIEW, VCompraController.WIDTH, VCompraController.HEIGHT, VCompraController.TITULO);
        });
        buttonPagos.setOnAction(event -> {
            Main.ventana(PagosController.VIEW, PagosController.WIDTH, PagosController.HEIGHT, PagosController.TITULO);
        });

        buttonVender.setOnAction(e -> {
            Main.ventana(VVentaController.VIEW, VVentaController.WIDTH, VVentaController.HEIGHT, VVentaController.TITULO);
        });

        buttonMostrarMas.setOnAction(event -> {
            Main.ventana(CarteraController.VIEW, CarteraController.WIDTH, CarteraController.HEIGHT, CarteraController.TITULO);
        });

        // No se puede poner un listener de click al menú, pero sí a su imagen
        buttonEstadisticas.getGraphic().setOnMouseClicked(event -> {
            Main.ventana(EstadisticasController.VIEW, EstadisticasController.WIDTH, EstadisticasController.HEIGHT, EstadisticasController.TITULO);
        });

        buttonParticipaciones.setOnAction(e -> {
            Main.ventana(PartEmpresaController.VIEW, PartEmpresaController.WIDTH, PartEmpresaController.HEIGHT, PartEmpresaController.TITULO);
        });

        buttonSociedad.setOnAction(this::onBtnSociedad);
    }

    public void onBtnSociedad(ActionEvent e) {
        if (super.getUsuarioSesion().getUsuario().getSociedad() == null) {
            Comunicador comunicador = new Comunicador() {
                @Override
                public void onSuccess() {
                    onBtnSociedad(null);
                }
            };

            SociedadNuevaController.setComunicador(comunicador);
            Main.dialogo(
                    SociedadNuevaController.VIEW,
                    SociedadNuevaController.WIDTH,
                    SociedadNuevaController.HEIGHT,
                    SociedadNuevaController.TITULO
            );
        } else {
            Main.ventana(
                    SociedadController.VIEW,
                    SociedadController.WIDTH,
                    SociedadController.HEIGHT,
                    SociedadController.TITULO
            );
        }
    }

    public void seleccionVentana(boolean empresa) {
        if (!empresa) {
            buttonPagos.setVisible(false);
            buttonParticipaciones.setVisible(false);
            buttonParticipaciones.setDisable(false);
            buttonPagos.setDisable(false);

            buttonVender.setLayoutX(buttonParticipaciones.getLayoutX());
            buttonVender.setLayoutY(buttonParticipaciones.getLayoutY());
        }
    }

    public void gestionTablaParticipaciones(List<Participacion> ofertaParticipaciones) {
        //ofertaParticipaciones = new ArrayList<>();

        ObservableList<Participacion> participaciones = FXCollections.observableArrayList(ofertaParticipaciones);
        tablaParticipaciones.setItems(participaciones);

        //Declaramos el nombre de las columnas
        colEmpresa.setCellValueFactory(cellData -> Bindings.createObjectBinding(() -> cellData.getValue().getEmpresa().getNombre()));
        colEmpresa.setStyle("-fx-alignment: CENTER;");
        colCantidad.setCellValueFactory(new PropertyValueFactory<Participacion, Integer>("cantidad"));
        colCantidad.setStyle("-fx-alignment: CENTER;");
    }

    public void gestionTablaOfertas(List<OfertaVenta> ofertaVentaList) {
        //ofertaVentaList = new ArrayList<>();

        ObservableList<OfertaVenta> ofertasVenta = FXCollections.observableArrayList(ofertaVentaList);
        tablaOfertasVenta.setItems(ofertasVenta);

        //Declaramos el nombre de las columnas

        colEmpresa2.setCellValueFactory(cellData -> Bindings.createObjectBinding(() -> cellData.getValue().getEmpresa().getNombre()));
        colEmpresa2.setStyle("-fx-alignment: CENTER;");
        colPrecio.setCellValueFactory(new PropertyValueFactory<OfertaVenta, Float>("precioVenta"));
        colPrecio.setStyle("-fx-alignment: CENTER;");
        colNParticipaciones.setCellValueFactory(new PropertyValueFactory<OfertaVenta, Integer>("numParticipaciones"));
        colNParticipaciones.setStyle("-fx-alignment: CENTER;");

    }

    public void mostrarSaldo() {
        txtSaldo.setText(usuario.getSaldo() - usuario.getSaldoBloqueado() + " €");
    }


}
