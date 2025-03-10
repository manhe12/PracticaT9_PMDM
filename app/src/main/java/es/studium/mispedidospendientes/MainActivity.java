package es.studium.mispedidospendientes;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PedidoAdapter.OnItemLongClickListener, PedidoAdapter.OnItemClickListener {

    RecyclerView listaPedidos;
    PedidoAdapter adapter;
    List<Pedido> pedidos = new ArrayList<>();
    AltaPedido altaPedido = new AltaPedido();
    BajaPedido bajaPedido = new BajaPedido(); // Instancia de la clase BajaPedido
    EditarPedido editarPedido = new EditarPedido();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listaPedidos = findViewById(R.id.listaPedidos);
        listaPedidos.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PedidoAdapter(this, pedidos);
        adapter.setOnItemLongClickListener(this); // Establecer el listener
        adapter.setOnItemClickListener(this);
        listaPedidos.setAdapter(adapter);

        obtenerPedidos();

        Button btnListarTiendas = findViewById(R.id.btnListarTiendas);
        btnListarTiendas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TiendasActivity.class);
                startActivity(intent);
            }
        });

        Button btnNuevoPedido = findViewById(R.id.btnNuevoPedido);
        btnNuevoPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoNuevoPedido();
            }
        });
    }

    private void obtenerPedidos() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AccesoRemoto accesoRemoto = new AccesoRemoto();
                JSONArray pedidosArray = accesoRemoto.obtenerListadoPedidos();

                if (pedidosArray != null) {
                    pedidos.clear();
                    for (int i = 0; i < pedidosArray.length(); i++) {
                        try {
                            JSONObject pedidoJson = pedidosArray.getJSONObject(i);
                            int idPedido = pedidoJson.getInt("idPedido");
                            String fechaPedido = pedidoJson.getString("fechaPedido");
                            String descripcionPedido = pedidoJson.getString("descripcionPedido");
                            double importePedido = pedidoJson.getDouble("importePedido");
                            String fechaEstimadaPedido = pedidoJson.getString("fechaEstimadaPedido");
                            int estadoPedidoInt = pedidoJson.getInt("estadoPedido"); // Obtener como entero
                            boolean recibido = (estadoPedidoInt == 1); // Convertir a booleano
                            int idTiendaFK = pedidoJson.getInt("idTiendaFK");

                            Pedido pedido = new Pedido(idPedido,fechaPedido ,descripcionPedido, importePedido, fechaEstimadaPedido, recibido, idTiendaFK);
                            pedidos.add(pedido);
                        } catch (JSONException e) {
                            Log.e("MainActivity", "Error al procesar JSON: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }).start();
    }

    private void mostrarDialogoNuevoPedido() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_nuevo_pedido, null);
        builder.setView(dialogView);

        Spinner spinnerTiendas = dialogView.findViewById(R.id.spinnerTiendas);
        EditText etFechaEntrega = dialogView.findViewById(R.id.etFechaEntrega);
        etFechaEntrega.setFocusable(false); // Deshabilitar el foco
        EditText etDescripcion = dialogView.findViewById(R.id.etDescripcion);
        EditText etImporte = dialogView.findViewById(R.id.etImporte);
        Button btnAceptar = dialogView.findViewById(R.id.btnAceptar);
        Button btnCancelar = dialogView.findViewById(R.id.btnCancelar);

        obtenerTiendas(spinnerTiendas);

        etFechaEntrega.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDatePickerDialog(etFechaEntrega);
            }
        });

        AlertDialog dialog = builder.create();

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fechaEstimadaPedido = etFechaEntrega.getText().toString().trim();
                String descripcionPedido = etDescripcion.getText().toString().trim();
                String importeStr = etImporte.getText().toString().trim();
                int tiendaId = (int) spinnerTiendas.getSelectedItemId() + 1;

                if (fechaEstimadaPedido.isEmpty() || descripcionPedido.isEmpty() || importeStr.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                double importePedido;
                try {
                    importePedido = Double.parseDouble(importeStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Importe no válido", Toast.LENGTH_SHORT).show();
                    return;
                }

                crearPedido(fechaEstimadaPedido, descripcionPedido, importePedido, tiendaId);
                dialog.dismiss();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void crearPedido(String fechaEstimadaPedido, String descripcionPedido, double importePedido, int idTiendaFK) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                boolean exito = altaPedido.crearPedido(fechaEstimadaPedido, descripcionPedido, importePedido, idTiendaFK);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (exito) {
                            Toast.makeText(MainActivity.this, "Pedido creado correctamente", Toast.LENGTH_SHORT).show();
                            obtenerPedidos(); // Actualizar la lista de pedidos
                        } else {
                            Toast.makeText(MainActivity.this, "Error al crear pedido", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    private void obtenerTiendas(final Spinner spinnerTiendas) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AccesoRemoto accesoRemoto = new AccesoRemoto();
                JSONArray tiendasArray = accesoRemoto.obtenerListadoTiendas();

                if (tiendasArray != null) {
                    List<String> nombresTiendas = new ArrayList<>();
                    for (int i = 0; i < tiendasArray.length(); i++) {
                        try {
                            JSONObject tienda = tiendasArray.getJSONObject(i);
                            nombresTiendas.add(tienda.getString("nombreTienda"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, nombresTiendas);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerTiendas.setAdapter(adapter);
                        }
                    });
                }
            }
        }).start();
    }

    private void mostrarDatePickerDialog(final EditText etFechaEntrega) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        etFechaEntrega.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }
    @Override
    public void onItemLongClick(int position) {
        Pedido pedido = pedidos.get(position);
        int idPedido = pedido.getIdPedido();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Seguro de Borrar el Pedido Nº " + idPedido + "?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        eliminarPedido(idPedido);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void eliminarPedido(int idPedido) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean exito = bajaPedido.darBaja(idPedido); // Llamar al método darBaja de BajaPedido

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (exito) {
                            Toast.makeText(MainActivity.this, "Pedido eliminado correctamente", Toast.LENGTH_SHORT).show();
                            obtenerPedidos(); // Actualizar la lista de pedidos
                        } else {
                            Toast.makeText(MainActivity.this, "Error al eliminar pedido", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public void onItemClick(int position) {
        Pedido pedido = pedidos.get(position);
        mostrarDialogoEditarPedido(pedido);
    }

    private void mostrarDialogoEditarPedido(Pedido pedido) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_editar_pedido, null);
        builder.setView(dialogView);

        TextView tvPedidoNumero = dialogView.findViewById(R.id.tvPedidoNumero);
        Spinner spinnerTiendas = dialogView.findViewById(R.id.spinnerTiendas);
        EditText etFechaEntrega = dialogView.findViewById(R.id.etFechaEntrega);
        etFechaEntrega.setFocusable(false); // Deshabilitar el foco
        EditText etDescripcion = dialogView.findViewById(R.id.etDescripcion);
        EditText etImporte = dialogView.findViewById(R.id.etImporte);
        Switch switchRecibido = dialogView.findViewById(R.id.switchRecibido);
        Button btnAceptar = dialogView.findViewById(R.id.btnAceptar);
        Button btnCancelar = dialogView.findViewById(R.id.btnCancelar);

        tvPedidoNumero.setText("Pedido Nº " + pedido.getIdPedido());
        obtenerTiendas(spinnerTiendas);
        etFechaEntrega.setText(pedido.getFechaEstimadaPedido());
        etDescripcion.setText(pedido.getDescripcionPedido());
        etImporte.setText(String.valueOf(pedido.getImportePedido()));
        switchRecibido.setChecked(pedido.isRecibido());

        etFechaEntrega.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDatePickerDialog(etFechaEntrega);
            }
        });

        AlertDialog dialog = builder.create();

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fechaEstimadaPedido = etFechaEntrega.getText().toString().trim();
                String descripcionPedido = etDescripcion.getText().toString().trim();
                String importeStr = etImporte.getText().toString().trim();

                if (fechaEstimadaPedido.isEmpty() || descripcionPedido.isEmpty() || importeStr.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                double importePedido;
                try {
                    importePedido = Double.parseDouble(importeStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Importe no válido", Toast.LENGTH_SHORT).show();
                    return;
                }

                int idPedido = pedido.getIdPedido();
                String fechaPedidoOriginal = pedido.getFechaPedido();
                boolean recibido = switchRecibido.isChecked();
                int idTiendaFK = (int) spinnerTiendas.getSelectedItemId() + 1;

                modificarPedido(idPedido, fechaPedidoOriginal, fechaEstimadaPedido, descripcionPedido, importePedido, recibido, idTiendaFK);
                dialog.dismiss();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void modificarPedido(int idPedido,String fechaPedidoOriginal ,String fechaEstimadaPedido, String descripcionPedido, double importePedido, boolean recibido, int idTiendaFK) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean exito = editarPedido.modificar(idPedido,fechaPedidoOriginal ,fechaEstimadaPedido, descripcionPedido, importePedido, recibido, idTiendaFK);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (exito) {
                            Toast.makeText(MainActivity.this, "Pedido modificado correctamente", Toast.LENGTH_SHORT).show();
                            obtenerPedidos(); // Actualizar la lista de pedidos
                        } else {
                            Toast.makeText(MainActivity.this, "Error al modificar pedido", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }
}