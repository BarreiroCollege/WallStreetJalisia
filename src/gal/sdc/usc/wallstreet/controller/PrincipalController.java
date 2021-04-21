package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import gal.sdc.usc.wallstreet.Main;
import gal.sdc.usc.wallstreet.model.*;
import gal.sdc.usc.wallstreet.repository.OfertaVentaDAO;
import gal.sdc.usc.wallstreet.repository.ParticipacionDAO;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;
import gal.sdc.usc.wallstreet.util.Iconos;
import gal.sdc.usc.wallstreet.util.TipoUsuario;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;


public class PrincipalController extends DatabaseLinker {
    public static final String VIEW = "principal";
    public static final Integer HEIGHT = 551;
    public static final Integer WIDTH = 730;

    @FXML
    private JFXButton buttonPerfilUsuario;
    @FXML
    private JFXButton buttonParticipaciones;
    @FXML
    private JFXButton buttonPagos;
    @FXML
    private JFXButton buttonComprar;
    @FXML
    private JFXButton buttonVender;

    @FXML
    private JFXButton buttonMostrarMasParticipaciones;

    @FXML
    private JFXButton buttonMostrarMasOfertas;

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
    private Menu buttonPerfil;

    @FXML
    private Menu buttonEstadisticas;

    @FXML
    private MenuItem buttonCerrarSesion;
    @FXML
    private MenuItem buttonVerPerfil;


    Scene scene;
    Usuario usuario;
    List<Participacion> participacionesUsuario;
    List<OfertaVenta> ofertaVentaUsuario;

    @FXML
    public void initialize(){
        Group root = new Group();
        scene = new Scene(root, 683, 551);
        switch (super.getTipoUsuario()) {
            case EMPRESA:
                Empresa empresa = super.getEmpresa();
                usuario = empresa.getUsuario();
                break;
            case INVERSOR:
                Inversor inversor = super.getInversor();
                usuario = inversor.getUsuario();
                break;
        }
        ofertaVentaUsuario = super.getDAO(OfertaVentaDAO.class).getOfertasVentaPorUsuario(usuario.getIdentificador());
        participacionesUsuario = super.getDAO(ParticipacionDAO.class).getParticipacionesPorUsuario(usuario.getIdentificador());

        seleccionVentana(super.getTipoUsuario().equals(TipoUsuario.INVERSOR));
        gestionTablaParticipaciones(participacionesUsuario);
        gestionTablaOfertas(ofertaVentaUsuario);


        buttonPerfil.setGraphic(Iconos.icono(FontAwesomeIcon.USERS, "2.5em"));
        buttonVerPerfil.setGraphic(Iconos.icono(FontAwesomeIcon.USER));
        buttonEstadisticas.setGraphic(Iconos.icono(FontAwesomeIcon.BAR_CHART, "2.5em"));
        buttonCerrarSesion.setGraphic(Iconos.icono(FontAwesomeIcon.POWER_OFF));

        buttonCerrarSesion.setOnAction(event -> {
            Main.setScene(AccesoController.VIEW, AccesoController.WIDTH, AccesoController.HEIGHT);

        });
        /*
        buttonMostrarMasParticipaciones.setOnAction(e -> Main.setScene(MMParticipacionesController.VIEW, MMParticipacionesController.WIDTH, MMParticipacionesController.HEIGHT));
        buttonMostrarMasOfertas.setOnAction(e -> Main.setScene(MMOfertasVentaController.VIEW, MMOfertasVentaController.WIDTH, MMOfertasVentaController.HEIGHT));
         */
    }


    public void seleccionVentana(boolean empresa){
        if(!empresa){
            buttonPagos.setVisible(false);
            buttonParticipaciones.setVisible(false);
            buttonParticipaciones.setDisable(false);
            buttonPagos.setDisable(false);

            buttonVender.setLayoutX(buttonParticipaciones.getLayoutX());
            buttonVender.setLayoutY(buttonParticipaciones.getLayoutY());
        }
    }

    public void gestionTablaParticipaciones(List<Participacion> ofertaParticipaciones){
        //ofertaParticipaciones = new ArrayList<>();

        ObservableList<Participacion> participaciones = FXCollections.observableArrayList(ofertaParticipaciones);
        tablaParticipaciones.setItems(participaciones);

       //Declaramos el nombre de las columnas
        colEmpresa.setCellValueFactory(cellData -> Bindings.createObjectBinding(() -> cellData.getValue().getEmpresa().getNombre()));
        colCantidad.setCellValueFactory(new PropertyValueFactory<Participacion, Integer>("cantidad"));

    }

    public void gestionTablaOfertas(List<OfertaVenta> ofertaVentaList){
        //ofertaVentaList = new ArrayList<>();

        ObservableList<OfertaVenta> ofertasVenta = FXCollections.observableArrayList(ofertaVentaList);
        tablaOfertasVenta.setItems(ofertasVenta);

        //Declaramos el nombre de las columnas

        colEmpresa2.setCellValueFactory(cellData -> Bindings.createObjectBinding(() -> cellData.getValue().getEmpresa().getNombre()));
        colPrecio.setCellValueFactory(new PropertyValueFactory<OfertaVenta, Float>("precioVenta"));
        colNParticipaciones.setCellValueFactory(new PropertyValueFactory<OfertaVenta, Integer>("numParticipaciones"));

    }

}