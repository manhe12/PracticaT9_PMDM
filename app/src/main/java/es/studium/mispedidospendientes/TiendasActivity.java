package es.studium.mispedidospendientes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class TiendasActivity extends AppCompatActivity implements TiendaAdapter.OnItemLongClickListener, TiendaAdapter.OnItemClickListener {

    RecyclerView listaTiendas;
    TiendaAdapter adapter;
    List<Tienda> tiendas = new ArrayList<>();
    AccesoRemoto accesoRemoto = new AccesoRemoto();
    AltaTienda altaTienda = new AltaTienda();
    BajaTienda bajaTienda = new BajaTienda();
    EditarTienda editarTienda = new EditarTienda();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiendas);

        listaTiendas = findViewById(R.id.listaTiendas);
        listaTiendas.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TiendaAdapter(this, tiendas);
        adapter.setOnItemLongClickListener(this);
        adapter.setOnItemClickListener(this);
        listaTiendas.setAdapter(adapter);

        obtenerTiendas();

        Button btnNuevaTienda = findViewById(R.id.btnNuevaTienda);
        btnNuevaTienda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoNuevaTienda();
            }
        });
    }

    private void obtenerTiendas() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONArray tiendasArray = accesoRemoto.obtenerListadoTiendas();

                if (tiendasArray != null) {
                    tiendas.clear();
                    for (int i = 0; i < tiendasArray.length(); i++) {
                        try {
                            JSONObject tiendaJson = tiendasArray.getJSONObject(i);
                            int idTienda = tiendaJson.getInt("idTienda");
                            String nombreTienda = tiendaJson.getString("nombreTienda");
                            tiendas.add(new Tienda(idTienda, nombreTienda));
                        } catch (JSONException e) {
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

    private void mostrarDialogoNuevaTienda() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_nueva_tienda, null);
        builder.setView(dialogView);

        EditText etNombreTienda = dialogView.findViewById(R.id.etNombreTienda);
        Button btnAceptar = dialogView.findViewById(R.id.btnAceptar);
        Button btnCancelar = dialogView.findViewById(R.id.btnCancelar);

        AlertDialog dialog = builder.create();

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombreTienda = etNombreTienda.getText().toString().trim();

                if (nombreTienda.isEmpty()) {
                    Toast.makeText(TiendasActivity.this, "Por favor, ingrese el nombre de la tienda", Toast.LENGTH_SHORT).show();
                    return;
                }

                crearTienda(nombreTienda);
                dialog.dismiss();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    private void crearTienda(String nombreTienda) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean exito = altaTienda.crearTienda(nombreTienda);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (exito) {
                            Toast.makeText(TiendasActivity.this, "Tienda creada correctamente", Toast.LENGTH_SHORT).show();
                            obtenerTiendas(); // Actualizar la lista de tiendas
                        } else {
                            Toast.makeText(TiendasActivity.this, "Error al crear tienda", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public void onItemLongClick(int position) {
        Tienda tienda = tiendas.get(position);
        int idTienda = tienda.getIdTienda();
        String nombreTienda = tienda.getNombreTienda();

        Log.d("TiendasActivity", "ID de la tienda a eliminar: " + idTienda);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Seguro de Borrar la Tienda " + nombreTienda + "?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Eliminar la tienda si el usuario hace clic en Aceptar
                        eliminarTienda(idTienda);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Cerrar el diálogo si el usuario hace clic en Cancelar
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void eliminarTienda(int idTienda) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean exito = bajaTienda.darBaja(idTienda);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (exito) {
                            Toast.makeText(TiendasActivity.this, "Tienda eliminada correctamente", Toast.LENGTH_SHORT).show();
                            obtenerTiendas();
                        } else {
                            Toast.makeText(TiendasActivity.this, "Error al eliminar tienda", Toast.LENGTH_SHORT).show();
                            Log.e("TiendasActivity", "Error al eliminar tienda con ID: " + idTienda);
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public void onItemClick(int position) {
        Tienda tienda = adapter.getTienda(position);
        mostrarDialogoEditarTienda(tienda);
    }

    private void mostrarDialogoEditarTienda(Tienda tienda) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_editar_tienda, null);
        builder.setView(dialogView);

        EditText etNombreTienda = dialogView.findViewById(R.id.etNombreTienda);
        etNombreTienda.setText(tienda.getNombreTienda()); // Establecer el nombre actual
        Button btnAceptar = dialogView.findViewById(R.id.btnAceptar);
        Button btnCancelar = dialogView.findViewById(R.id.btnCancelar);


        AlertDialog dialog = builder.create();

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nuevoNombre = etNombreTienda.getText().toString().trim();

                if (nuevoNombre.isEmpty()) {
                    Toast.makeText(TiendasActivity.this, "Por favor, ingrese el nombre de la tienda", Toast.LENGTH_SHORT).show();
                    return;
                }

                modificarTienda(tienda, nuevoNombre);
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

    private void modificarTienda(Tienda tienda, String nuevoNombre) {
        Log.d("TiendasActivity", "ID de la tienda a modificar: " + tienda.getIdTienda()); // Agregar esta línea
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean exito = editarTienda.modificar(tienda.getIdTienda(), nuevoNombre); // Pasar idTienda y nuevoNombre

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (exito) {
                            Toast.makeText(TiendasActivity.this, "Tienda modificada correctamente", Toast.LENGTH_SHORT).show();
                            obtenerTiendas();
                        } else {
                            Toast.makeText(TiendasActivity.this, "Error al modificar tienda", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }
}