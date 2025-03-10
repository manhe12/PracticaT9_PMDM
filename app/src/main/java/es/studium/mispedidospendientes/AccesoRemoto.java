package es.studium.mispedidospendientes;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
public class AccesoRemoto
{
    // Crear una instancia de OkHttpClient
    OkHttpClient client = new OkHttpClient();

    AccesoRemoto() {} // Constructor

    public JSONArray obtenerListadoTiendas() {
        JSONArray resultado = new JSONArray();
        Request request = new Request.Builder()
                .url("http://192.168.1.142/misPedidos/tiendas.php") // Reemplaza con la URL de tu API
                .build();

        try {
            // Realizar la solicitud
            Response response = client.newCall(request).execute();

            // Procesar la respuesta
            if (response.isSuccessful()) {
                resultado = new JSONArray(response.body().string());
            } else {
                Log.e("AccesoRemoto", "Error al obtener tiendas: " + response.message());
            }
        } catch (IOException e) {
            Log.e("AccesoRemoto", "Error de IO: " + e.getMessage());
        } catch (JSONException e) {
            Log.e("AccesoRemoto", "Error JSON: " + e.getMessage());
        }
        return resultado;
    }

    public JSONArray obtenerListadoPedidos() {
        JSONArray resultado = new JSONArray();
        Request request = new Request.Builder()
                .url("http://192.168.1.142/misPedidos/pedidos.php") // Reemplaza con la URL de tu API
                .build();

        try {
            // Realizar la solicitud
            Response response = client.newCall(request).execute();

            // Procesar la respuesta
            if (response.isSuccessful()) {
                resultado = new JSONArray(response.body().string());
            } else {
                Log.e("AccesoRemoto", "Error al obtener pedidos: " + response.message());
            }
        } catch (IOException e) {
            Log.e("AccesoRemoto", "Error de IO: " + e.getMessage());
        } catch (JSONException e) {
            Log.e("AccesoRemoto", "Error JSON: " + e.getMessage());
        }
        return resultado;
    }

}