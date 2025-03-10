package es.studium.mispedidospendientes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder> {

    private Context context;
    private List<Pedido> pedidos;
    private OnItemLongClickListener longClickListener;
    private OnItemClickListener clickListener;

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public PedidoAdapter(Context context, List<Pedido> pedidos) {
        this.context = context;
        this.pedidos = pedidos;
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pedido, parent, false);
        return new PedidoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
        Pedido pedido = pedidos.get(position);
        holder.descripcionPedido.setText(pedido.getDescripcionPedido());
        holder.importePedido.setText("Importe: " + String.valueOf(pedido.getImportePedido()) + " €");
        holder.fechaEstimadaPedido.setText("Fecha estimada: " + pedido.getFechaEstimadaPedido());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (longClickListener != null) {
                    longClickListener.onItemLongClick(holder.getAdapterPosition());
                }
                return true;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onItemClick(holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }

    public Pedido getPedido(int position) {
        return pedidos.get(position);
    }

    public static class PedidoViewHolder extends RecyclerView.ViewHolder {
        TextView descripcionPedido;
        TextView importePedido;
        TextView fechaEstimadaPedido;

        public PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            descripcionPedido = itemView.findViewById(R.id.descripcionPedido);
            importePedido = itemView.findViewById(R.id.importePedido);
            fechaEstimadaPedido = itemView.findViewById(R.id.fechaEstimadaPedido);
        }
    }
}