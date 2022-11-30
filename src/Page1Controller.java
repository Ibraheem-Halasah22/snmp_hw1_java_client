import classes_for_table_views.SysGroupRowInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Page1Controller implements Initializable {
    public static final String GET_SYS_GROUP_INFO_PAGE = "http://localhost/snmp-assignment/php-files/get_system_group.php";
    public static final String EDIT_SYS_GROUP_USING_GET = "http://localhost/snmp-assignment/php-files/edit_sys_get.php";
    public static final String EDIT_SYS_GROUP_USING_POST = "http://localhost/snmp-assignment/php-files/edit_sys_post.php";
    String[] sysGroupNames = {"sysDescr", "sysObjectID", "sysUpTime", "sysContact", "sysName",
            "sysLocation", "sysServices"};
    @FXML
    private TableColumn<SysGroupRowInfo, String> descriptionColumn;

    @FXML
    private TextField parametersTextField;

    @FXML
    private TextField sysContactTextField;

    @FXML
    private TableView<SysGroupRowInfo> sysGroupDataTable;

    @FXML
    private TextField sysLocationTextField;

    @FXML
    private TextField urlTextField;

    @FXML
    private TableColumn<SysGroupRowInfo, String> valueColumn;

    @FXML
    private TextField verbTextField;
    BufferedReader reader;

    @FXML
    void fillTableWithSysGroupData(ActionEvent event) throws Exception {
        sysGroupDataTable.getItems().clear();
        verb = "GET";

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setUseCaches(false);
        connection.setConnectTimeout(5000);
        connection.setRequestMethod(verb);
        int status = connection.getResponseCode();
        System.out.println(status);
        if (status > 299) {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText("The response is with code " + status);
            a.show();
            return;
        }
        StringBuffer buffer = new StringBuffer();
        reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        reader.close();

        List<SysGroupRowInfo> list = new ArrayList<>();
        JSONObject object = new JSONObject(buffer.toString());
        for (int i = 0; i < object.length(); i++) {
            list.add(new SysGroupRowInfo(sysGroupNames[i], object.getString(Integer.toString(i))));
        }
        ObservableList<SysGroupRowInfo> observableList = FXCollections.observableList(list);

        descriptionColumn.setCellValueFactory(new PropertyValueFactory<SysGroupRowInfo, String>("description"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<SysGroupRowInfo, String>("value"));
        sysGroupDataTable.setItems(observableList);
        urlTextField.setText(GET_SYS_GROUP_INFO_PAGE);
        parametersTextField.setText("");
        verbTextField.setText(verb);
        System.out.println(buffer);

    }

    @FXML
    void setUsingGet(ActionEvent event) throws Exception {
        String newURL = EDIT_SYS_GROUP_USING_GET + "?sysContact=" +
                URLEncoder.encode(sysContactTextField.getText(), StandardCharsets.UTF_8) +
                "&sysLocation=" + URLEncoder.encode(sysLocationTextField.getText(), StandardCharsets.UTF_8);
        verb = "GET";
        URL modifiedURL = new URL(newURL);
        HttpURLConnection newConnection = (HttpURLConnection) modifiedURL.openConnection();
        newConnection.setUseCaches(false);
        newConnection.setConnectTimeout(5000);
        newConnection.setRequestMethod(verb);

        int status = newConnection.getResponseCode();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if (status >= 299) {
            alert.setContentText("A problem occurred with status code " + status);

        } else {
            alert.setContentText("Edit successfully with status code " + status);
        }
        alert.show();
        urlTextField.setText(newURL);
        parametersTextField.setText("");
        verbTextField.setText(verb);
    }

    @FXML
    void setUsingPost(ActionEvent event) throws Exception {
        String postDataString = "sysContact=" + URLEncoder.encode(sysContactTextField.getText(), StandardCharsets.UTF_8) +
                "&sysLocation=" + URLEncoder.encode(sysLocationTextField.getText(), StandardCharsets.UTF_8);
        byte [] postData = postDataString.getBytes();
        verb = "POST";
        URL postURL = new URL(EDIT_SYS_GROUP_USING_POST);
        HttpURLConnection newConnection = (HttpURLConnection) postURL.openConnection();
        newConnection.setUseCaches(false);
        newConnection.setConnectTimeout(5000);
        newConnection.setRequestMethod(verb);
        newConnection.setDoOutput(true);
        newConnection.setRequestProperty("Content-Length", Integer.toString(postData.length));
        try( DataOutputStream wr = new DataOutputStream( newConnection.getOutputStream())) {
            wr.write( postData );
        }
        int status = newConnection.getResponseCode();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if (status >= 299) {
            alert.setContentText("A problem occurred with status code " + status);

        } else {
            alert.setContentText("Edit successfully with status code " + status);
        }
        alert.show();
        urlTextField.setText(EDIT_SYS_GROUP_USING_POST);
        parametersTextField.setText(postDataString);
        verbTextField.setText(verb);


    }

    @FXML
    void goBack(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("fxmls/main.fxml"))));
        stage.show();
    }

    URL url;
    String verb;

    public Page1Controller() {
        try {
            url = new URL(GET_SYS_GROUP_INFO_PAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        urlTextField.setEditable(false);
        parametersTextField.setEditable(false);
        verbTextField.setEditable(false);
    }
}
