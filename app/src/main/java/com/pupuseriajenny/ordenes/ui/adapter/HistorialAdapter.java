package com.pupuseriajenny.ordenes.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.pupuseriajenny.ordenes.R;
import com.pupuseriajenny.ordenes.data.model.Orden;

import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Collections;
import java.util.Comparator;


public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.ViewHolder> {

    private List<Orden> ordenes;
    private OnOrdenInteractionListener listener;

    // Interfaz para manejar interacciones con cada ítem
    public interface OnOrdenInteractionListener {
        void onEditarOrden(Orden orden);
        void onEliminarOrden(Orden orden);
    }

    // Constructor del adaptador
    public HistorialAdapter(List<Orden> ordenes, OnOrdenInteractionListener listener) {
        this.ordenes = ordenes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historial_pedido, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Orden orden = ordenes.get(position);
        holder.bind(orden); // Llamar al método bind para enlazar datos
    }

    @Override
    public int getItemCount() {
        return ordenes.size();
    }

    // Clase interna ViewHolder para manejar cada ítem
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtClienteOrden, txtFechaOrden, txtTipoOrden, txtEstadoOrden;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtClienteOrden = itemView.findViewById(R.id.nombreCliente);
            txtFechaOrden = itemView.findViewById(R.id.fechaOrden);
            txtTipoOrden = itemView.findViewById(R.id.tipoOrden);
            txtEstadoOrden = itemView.findViewById(R.id.estadoOrden);

            // Evento de clic corto para editar
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onEditarOrden(ordenes.get(position));
                }
            });

            // Evento de clic largo para eliminar
            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onEliminarOrden(ordenes.get(position));
                }
                return true; // Retorna true para indicar que se manejó el evento
            });
        }

        // Método para enlazar datos con la interfaz
        public void bind(Orden orden) {
            txtClienteOrden.setText(orden.getClienteOrden());

            // Formatear fecha al formato deseado
            try {
                // Formato de entrada (ISO 8601 desde la API)
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                // Formato de salida (deseado para mostrar)
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

                // Parsear y formatear fecha
                Date date = inputFormat.parse(orden.getFechaOrden());
                if (date != null) {
                    txtFechaOrden.setText(outputFormat.format(date));
                } else {
                    txtFechaOrden.setText(orden.getFechaOrden()); // Mostrar sin formato si falla
                }
            } catch (Exception e) {
                txtFechaOrden.setText(orden.getFechaOrden()); // Mostrar sin formato si ocurre un error
            }

            txtTipoOrden.setText(orden.getTipoOrden());
            txtEstadoOrden.setText(orden.getEstadoOrden());
        }
    }
}
