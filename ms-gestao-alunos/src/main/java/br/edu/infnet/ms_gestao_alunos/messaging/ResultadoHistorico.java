package br.edu.infnet.ms_gestao_alunos.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultadoHistorico {

    private Long alunoId;
    private boolean confirmado;
    private String motivo;
}
