package br.edu.infnet.ms_historico.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrarHistoricoCommand {
    private Long alunoId;
    private String descricao;
}
