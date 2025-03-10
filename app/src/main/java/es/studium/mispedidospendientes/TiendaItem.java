package es.studium.mispedidospendientes;

public class TiendaItem {
    private int idTienda;
    private String nombreTienda;

    public TiendaItem(int idTienda, String nombreTienda) {
        this.idTienda = idTienda;
        this.nombreTienda = nombreTienda;
    }

    public int getIdTienda() {
        return idTienda;
    }

    @Override
    public String toString() {
        return nombreTienda; // Devolvemos el nombre de la tienda
    }
}