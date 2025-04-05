package com.rafa.rpggame.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.rafa.rpggame.R;
import com.rafa.rpggame.adapters.ItemAdapter;
import com.rafa.rpggame.models.zones.Exploration;
import com.rafa.rpggame.models.zones.ExplorationStatus;
import com.rafa.rpggame.game.combat.CombatAction;
import com.rafa.rpggame.game.combat.CombatActionType;
import java.util.List;

public class ExplorationResultDialog extends DialogFragment {
    private Exploration exploration;
    private TextView resultTitleText;
    private TextView experienceText;
    private ListView dropsListView;
    private Button closeButton;

    public ExplorationResultDialog(Exploration exploration) {
        this.exploration = exploration;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_exploration_result, null);

        // Inicializar vistas
        resultTitleText = view.findViewById(R.id.result_title_text);
        experienceText = view.findViewById(R.id.experience_text);
        TextView combatLogText = view.findViewById(R.id.combat_log_text);
        dropsListView = view.findViewById(R.id.drops_list_view);
        closeButton = view.findViewById(R.id.close_button);

        // Configurar según resultado
        if (exploration.getStatus() == ExplorationStatus.VICTORY) {
            resultTitleText.setText("¡Victoria!");
            experienceText.setText("Experiencia ganada: " + exploration.getExperienceGained());

            // Mostrar drops
            ItemAdapter adapter = new ItemAdapter(getActivity(), exploration.getDrops());
            dropsListView.setAdapter(adapter);
        } else {
            resultTitleText.setText("Derrota");
            experienceText.setText("No has ganado experiencia");
            dropsListView.setVisibility(View.GONE);
        }

        // Mostrar el log de combate
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("Resumen del combate:\n\n");

        /* Obtener el log de combate (suponiendo que exploration tiene acceso a él)
        List<CombatAction> combatLog = exploration.getCombatLog();
        if (combatLog != null && !combatLog.isEmpty()) {
            for (CombatAction action : combatLog) {
                // Solo mostrar acciones relevantes (ataques, golpes críticos, etc.)
                if (action.getType() == CombatActionType.ATTACK ||
                        action.getType() == CombatActionType.CRITICAL_HIT ||
                        action.getType() == CombatActionType.HEAL ||
                        action.getType() == CombatActionType.COMBAT_END) {

                    logBuilder.append("• ").append(action.getText()).append("\n");
                }
            }
        } else {
            logBuilder.append("No hay registro detallado del combate.");
        }

        combatLogText.setText(logBuilder.toString());*/

        closeButton.setOnClickListener(v -> dismiss());

        builder.setView(view);
        return builder.create();
    }
}