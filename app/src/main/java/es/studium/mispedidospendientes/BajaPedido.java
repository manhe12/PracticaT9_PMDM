package es.studium.mispedidospendientes;

import android.util.Log;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BajaPedido
{
    OkHttpClient client = new OkHttpClient();

    BajaPedido() {}

    boolean darBaja(int id) {
        boolean correcta = false; // Inicializar a false
        Request request = new Request.Builder()
                .url("http://192.168.1.142/misPedidos/pedidos.php" + "?idPedido=" + id)
                .delete()
                .build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            Log.i("BajaRemota", String.valueOf(response));

            if (response.isSuccessful()) {
                correcta = true;
            } else {
                Log.e("BajaRemota", "Error en la respuesta del servidor: " + response.code());
                correcta = false;
            }
        } catch (IOException e) {
            Log.e("BajaRemota", e.getMessage());
            correcta = false;
        }
        return correcta;
    }
}