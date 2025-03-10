package es.studium.mispedidospendientes;

import android.util.Log;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AltaTienda
{
    OkHttpClient client = new OkHttpClient();

    public AltaTienda(){};
    public boolean crearTienda(String nombreTienda)
    {
        boolean correcta = true;
        // Montamos la petición POST
        RequestBody formBody = new FormBody.Builder()
                .add("nombreTienda", nombreTienda)
                .build();
        Request request = new Request.Builder()
                .url("http://192.168.1.142/misPedidos/tiendas.php")
                .post(formBody)
                .build();

        Call call = client.newCall(request);
        try
        {
            Response response = call.execute();
            Log.i("AltaTienda", String.valueOf(response));
            correcta = true;
        }
        catch (IOException e)
        {
            Log.e("AltaTienda", e.getMessage());
            correcta = false;
        }
        return correcta;
    }
}