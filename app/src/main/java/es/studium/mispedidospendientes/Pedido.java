package es.studium.mispedidospendientes;

public class Pedido {
    private int idPedido;
    private String fechaPedido; // Asegúrate de tener este atributo
    private String descripcionPedido;
    private double importePedido;
    private String fechaEstimadaPedido;
    private boolean recibido;
    private int idTiendaFK;

    public Pedido(int idPedido,String fechaPedido ,String descripcionPedido, double importePedido, String fechaEstimadaPedido, boolean estadoPedido, int idTiendaFK) {
        this.idPedido = idPedido;
        this.fechaPedido = fechaPedido; // Asigna la fecha
        this.descripcionPedido = descripcionPedido;
        this.importePedido = importePedido;
        this.fechaEstimadaPedido = fechaEstimadaPedido;
        this.recibido = estadoPedido;
        this.idTiendaFK = idTiendaFK;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public String getDescripcionPedido() {
        return descripcionPedido;
    }

    public double getImportePedido() {
        return importePedido;
    }

    public String getFechaEstimadaPedido() {
        return fechaEstimadaPedido;
    }

    public boolean isRecibido() {
        return recibido;
    }

    public int getIdTiendaFK() {
        return idTiendaFK;
    }

    public String getFechaPedido() {
        return fechaPedido;
    }
}