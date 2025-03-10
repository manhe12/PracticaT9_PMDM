package es.studium.mispedidospendientes;

import android.util.Log;
import java.io.IOException;
import java.net.URI;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditarPedido {
    OkHttpClient client = new OkHttpClient();

    EditarPedido() {}

    boolean modificar(int idPedido,String fechaPedidoOriginal ,String fechaEstimadaPedido, String descripcionPedido, double importePedido, boolean estadoPedido, int idTiendaFK) {
        boolean correcta = false;
        HttpUrl.Builder queryUrlBuilder =
                HttpUrl.get(URI.create("http://192.168.1.142/misPedidos/pedidos.php"))
                        .newBuilder();
        queryUrlBuilder.addQueryParameter("idPedido", String.valueOf(idPedido));
        queryUrlBuilder.addQueryParameter("fechaPedido", fechaPedidoOriginal);
        queryUrlBuilder.addQueryParameter("fechaEstimadaPedido", fechaEstimadaPedido);
        queryUrlBuilder.addQueryParameter("descripcionPedido", descripcionPedido);
        queryUrlBuilder.addQueryParameter("importePedido", String.valueOf(importePedido));
        queryUrlBuilder.addQueryParameter("estadoPedido", String.valueOf(estadoPedido));
        queryUrlBuilder.addQueryParameter("idTiendaFK", String.valueOf(idTiendaFK));

        RequestBody formBody = new FormBody.Builder()
                .build();
        Log.i("ModificacionPedido", formBody.toString());
        Request request = new Request.Builder()
                .url(queryUrlBuilder.build())
                .put(formBody)
                .build();
        Log.i("ModificacionPedido", String.valueOf(request));
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            Log.i("ModificacionPedido", String.valueOf(response));
            correcta = response.isSuccessful();
        } catch (IOException e) {
            Log.e("ModificacionPedido", e.getMessage());
            correcta = false;
        }
        return correcta;
    }
}