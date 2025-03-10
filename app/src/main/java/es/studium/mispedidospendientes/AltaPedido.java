package es.studium.mispedidospendientes;

import android.util.Log;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AltaPedido
{
    OkHttpClient client = new OkHttpClient();

    public AltaPedido(){};
    public boolean crearPedido(String fechaEstimadaPedido, String descripcionPedido, double importePedido, int idTiendaFK)
    {
        boolean correcta = true;
        // Montamos la petición POST
        RequestBody formBody = new FormBody.Builder()
                .add("fechaEstimadaPedido", fechaEstimadaPedido)
                .add("descripcionPedido", descripcionPedido)
                .add("importePedido", String.valueOf(importePedido))
                .add("idTiendaFK", String.valueOf(idTiendaFK))
                .build();
        Request request = new Request.Builder()
                .url("http://192.168.1.142/misPedidos/pedidos.php")
                .post(formBody)
                .build();

        Call call = client.newCall(request);
        try
        {
            Response response = call.execute();
            Log.i("AltaPedido", String.valueOf(response));
            correcta = true;
        }
        catch (IOException e)
        {
            Log.e("Pedido", e.getMessage());
            correcta = false;
        }
        return correcta;
    }
}