package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;
import gal.sdc.usc.wallstreet.util.ErrorValidator;
import gal.sdc.usc.wallstreet.util.Validadores;
import gal.sdc.usc.wallstreet.util.auth.GoogleAuth;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;

public class OtpController extends DatabaseLinker implements Initializable {
    public static final String VIEW = "otp";
    public static final Integer HEIGHT = 250;
    public static final Integer WIDTH = 500;
    public static final String TITULO = "Verificación";

    private static AccesoController accesoController;

    public static void setAccesoController(AccesoController accesoController) {
        OtpController.accesoController = accesoController;
    }

    @FXML
    public AnchorPane anchor;

    @FXML
    private JFXTextField txtOtp;

    @FXML
    private JFXButton btnCancelar;

    @FXML
    private JFXButton btnConfirmar;

    public OtpController() {
    }

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        txtOtp.textProperty().addListener((observable, oldValue, newValue) -> {
            if (txtOtp.getValidators().size() > 0) {
                txtOtp.getValidators().remove(0);
                txtOtp.validate();
            }

            if (!newValue.matches("\\d{0,6}")) {
                txtOtp.setText(oldValue);
            }
        });

        btnCancelar.setOnAction(event -> {
            accesoController.cancelarOtp();
            anchor.getScene().getWindow().hide();
        });

        ErrorValidator faltanDigitos = Validadores.personalizado("Se necesitan 6 dígitos");
        ErrorValidator codigoIncorrecto = Validadores.personalizado("El código es incorrecto");

        btnConfirmar.setOnAction(event -> {
            if (txtOtp.getText().length() != 6) {
                if (txtOtp.getValidators().size() == 0) txtOtp.getValidators().add(faltanDigitos);
                txtOtp.validate();
                return;
            }

            try {
                if (!GoogleAuth.validarCodigo(accesoController.getUsuario().getOtp(), Long.parseLong(txtOtp.getText()))) {
                    if (txtOtp.getValidators().size() == 0) txtOtp.getValidators().add(codigoIncorrecto);
                    txtOtp.validate();
                    return;
                }

                accesoController.confirmarOtp();
                anchor.getScene().getWindow().hide();
            } catch (NoSuchAlgorithmException | InvalidKeyException e) {
                System.err.println(e.getMessage());
            }
        });
    }
}
