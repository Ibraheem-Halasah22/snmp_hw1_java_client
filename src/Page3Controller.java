import classes_for_table_views.SysGroupRowInfo;
import classes_for_table_views.TCPConnsTableRow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class Page3Controller {
    public static final String GET_TCP_CONNS_TABLE = "http://localhost/snmp-assignment/php-files/get_tcp_conns_table.php";
    @FXML
    private TableColumn<TCPConnsTableRow, String> localIPColumn;

    @FXML
    private TableColumn<TCPConnsTableRow, String> localPortColumn;

    @FXML
    private TableColumn<TCPConnsTableRow, Integer> noColumn;

    @FXML
    private TableColumn<TCPConnsTableRow, String> remoteIPColumn;

    @FXML
    private TableColumn<TCPConnsTableRow, String> remotePortColumn;

    @FXML
    private TableColumn<TCPConnsTableRow, String> statusColumn;

    @FXML
    private TextField statusTextField;

    @FXML
    private TableView<TCPConnsTableRow> tcpConnsTable;

    @FXML
    void getDataAsync(ActionEvent event) throws Exception{
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(GET_TCP_CONNS_TABLE)).build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(this::fillTCPConnsTable)
                .join();
        statusTextField.setText("Got Asynchronously");
    }

    @FXML
    void getDataSync(ActionEvent event) throws Exception{
        HttpURLConnection connection = (HttpURLConnection) new URL(GET_TCP_CONNS_TABLE).openConnection();
        connection.setUseCaches(false);
        connection.setConnectTimeout(5000);
        connection.setRequestMethod("GET");
        int status = connection.getResponseCode();
        System.out.println(status);
        if (status > 299) {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText("The response is with code " + status);
            a.show();
            return;
        }
        StringBuffer buffer = new StringBuffer();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        reader.close();
        String jsonResponse = buffer.toString();
        fillTCPConnsTable(jsonResponse);

        statusTextField.setText("Got Synchronously");
        System.out.println(buffer);
    }

    @FXML
    void goBack(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("fxmls/main.fxml"))));
        stage.show();
    }
    private void fillTCPConnsTable(String jsonResponse){
        tcpConnsTable.getItems().clear();
        List<TCPConnsTableRow> list = new ArrayList<>();
        JSONObject object = new JSONObject(jsonResponse);
        int n = object.length();
        for (int i = 0; i < n/5; i++) {
            list.add(new TCPConnsTableRow(i, object.getString(Integer.toString(i)),
                    object.getString(Integer.toString(i + (n/5))), object.getString(Integer.toString(i + 2 * (n/5))),
                    object.getString(Integer.toString(i + 3 * (n/5))), object.getString(Integer.toString(i + 4 * (n/5)))));
        }
        ObservableList<TCPConnsTableRow> observableList = FXCollections.observableList(list);

        noColumn.setCellValueFactory(new PropertyValueFactory<TCPConnsTableRow, Integer>("no"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<TCPConnsTableRow, String>("status"));
        localIPColumn.setCellValueFactory(new PropertyValueFactory<TCPConnsTableRow, String>("localIP"));
        localPortColumn.setCellValueFactory(new PropertyValueFactory<TCPConnsTableRow, String>("localPort"));
        remoteIPColumn.setCellValueFactory(new PropertyValueFactory<TCPConnsTableRow, String>("remoteIP"));
        remotePortColumn.setCellValueFactory(new PropertyValueFactory<TCPConnsTableRow, String>("remotePort"));
        tcpConnsTable.setItems(observableList);
    }
}
