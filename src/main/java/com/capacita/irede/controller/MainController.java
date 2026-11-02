package com.capacita.irede.controller;

import com.capacita.irede.exceptions.TitleEmptyOrNullException;
import com.capacita.irede.model.Task;
import com.capacita.irede.model.TaskPrioritaria;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.util.StringConverter;

import java.util.Optional;

public class MainController {

    // ===== Componentes do FXML (os nomes precisam ser iguais aos fx:id) =====
    @FXML private CheckBox chkApenasPendentes;
    @FXML private TreeView<Task> treeTarefas;
    @FXML private Button btnConcluir;
    @FXML private Button btnRemover;

    @FXML private RadioButton rbSubtarefa;
    @FXML private ComboBox<Task> cbTarefaPai;
    @FXML private TextField txtTitulo;
    @FXML private TextArea txtDescricao;

    @FXML private Label lblStatus;
    @FXML private Button btnAdicionar;

    // Reaproveita a sua classe de lógica
    private final TaskManager<Task> taskManager = new TaskManager<>();

    // Chamado automaticamente pelo JavaFX depois de carregar o FXML
    @FXML
    public void initialize() {
        // Como cada tarefa aparece na árvore
        treeTarefas.setCellFactory(tv -> new TreeCell<>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                getStyleClass().remove("tarefa-concluida");

                if (empty || task == null) {
                    setText(null);
                } else {
                    String texto = (task.isConcluida() ? "✔ " : "○ ") + task.getTitulo();

                    String descricao = task.getDescricao();
                    if (descricao != null && !descricao.isBlank()) {
                        texto += "\n    " + descricao;   // descrição na linha de baixo
                    }

                    setText(texto);
                    if (task.isConcluida()) {
                        getStyleClass().add("tarefa-concluida");
                    }
                }
            }
        });

        // Como cada tarefa aparece no ComboBox
        cbTarefaPai.setConverter(new StringConverter<>() {
            @Override
            public String toString(Task task) {
                return task == null ? "" : task.getTitulo();
            }

            @Override
            public Task fromString(String texto) {
                return null;
            }
        });

        // ComboBox só habilitado quando "Subtarefa" estiver marcada
        cbTarefaPai.disableProperty().bind(rbSubtarefa.selectedProperty().not());

        // Botão só habilitado quando há um título válido (campo obrigatório)
        btnAdicionar.disableProperty().bind(Bindings.createBooleanBinding(
                () -> txtTitulo.getText().isBlank()
                        || (rbSubtarefa.isSelected() && cbTarefaPai.getValue() == null),
                txtTitulo.textProperty(),
                rbSubtarefa.selectedProperty(),
                cbTarefaPai.valueProperty()));

        // Botões só habilitados quando há uma tarefa selecionada
        btnConcluir.disableProperty().bind(
                treeTarefas.getSelectionModel().selectedItemProperty().isNull());
        btnRemover.disableProperty().bind(
                treeTarefas.getSelectionModel().selectedItemProperty().isNull());

        listarTarefas();
    }

    // Botão "Adicionar"
    @FXML
    public void criarTarefa() {
        String titulo = txtTitulo.getText();
        String descricao = txtDescricao.getText();

        try {
            if (rbSubtarefa.isSelected()) {
                Task pai = cbTarefaPai.getValue();

                if (pai == null) {
                    mostrarStatus("Selecione a tarefa principal.");
                    return;
                }

                TaskPrioritaria subtarefa = new TaskPrioritaria(titulo, descricao, pai);
                pai.adicionarSubtarefa(subtarefa);
                mostrarStatus("Subtarefa '" + subtarefa.getTitulo()
                        + "' adicionada à tarefa '" + pai.getTitulo() + "'.");
            } else {
                Task task = new Task(titulo, descricao);
                taskManager.adicionar(task);
                mostrarStatus("Tarefa '" + task.getTitulo() + "' adicionada com sucesso!");
            }

            limparFormulario();
            listarTarefas();

        } catch (TitleEmptyOrNullException e) {
            mostrarStatus("Erro ao salvar tarefa: " + e.getMessage());
        }
    }

    // Atualiza a árvore (também é chamado pelo checkbox "Mostrar apenas pendentes")
    @FXML
    public void listarTarefas() {
        boolean apenasPendentes = chkApenasPendentes.isSelected();

        TreeItem<Task> raiz = new TreeItem<>();
        for (Task task : taskManager.getTarefas()) {
            TreeItem<Task> item = criarItem(task, apenasPendentes);
            if (item != null) {
                raiz.getChildren().add(item);
            }
        }
        treeTarefas.setRoot(raiz);

        // O ComboBox sempre lista todas as tarefas principais, independente do filtro
        cbTarefaPai.setItems(FXCollections.observableArrayList(taskManager.getTarefas()));
    }

    // Botão "Marcar como concluída"
    @FXML
    public void concluirTarefa() {
        TreeItem<Task> selecionado = treeTarefas.getSelectionModel().getSelectedItem();

        if (selecionado == null) {
            mostrarStatus("Selecione uma tarefa na lista.");
            return;
        }

        Task task = selecionado.getValue();
        taskManager.concluir(task);
        mostrarStatus("'" + task.getTitulo() + "' concluída!");
        listarTarefas();
    }

    // Botão "Remover"
    @FXML
    public void removerTarefa() {
        TreeItem<Task> selecionado = treeTarefas.getSelectionModel().getSelectedItem();

        if (selecionado == null) {
            mostrarStatus("Selecione uma tarefa na lista.");
            return;
        }

        Task task = selecionado.getValue();

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
                "Remover '" + task.getTitulo() + "'?", ButtonType.YES, ButtonType.NO);
        confirmacao.setHeaderText(null);
        confirmacao.setTitle("Confirmar remoção");

        Optional<ButtonType> resposta = confirmacao.showAndWait();
        if (resposta.isEmpty() || resposta.get() != ButtonType.YES) {
            return;
        }

        TreeItem<Task> paiItem = selecionado.getParent();

        if (paiItem == treeTarefas.getRoot()) {
            taskManager.remover(task);                        // tarefa principal
        } else {
            paiItem.getValue().getSubtarefas().remove(task);  // subtarefa
        }

        mostrarStatus("'" + task.getTitulo() + "' removida.");
        listarTarefas();
    }

    // ===== Métodos auxiliares =====

    // Monta o item da árvore com as subtarefas dentro (recursivo)
    private TreeItem<Task> criarItem(Task task, boolean apenasPendentes) {
        TreeItem<Task> item = new TreeItem<>(task);
        item.setExpanded(true);

        for (TaskPrioritaria sub : task.getSubtarefas()) {
            TreeItem<Task> filho = criarItem(sub, apenasPendentes);
            if (filho != null) {
                item.getChildren().add(filho);
            }
        }

        // Com o filtro ligado, esconde tarefas concluídas (a não ser que tenham subtarefa pendente)
        boolean visivel = !apenasPendentes || !task.isConcluida() || !item.getChildren().isEmpty();
        return visivel ? item : null;
    }

    private void limparFormulario() {
        txtTitulo.clear();
        txtDescricao.clear();
        txtTitulo.requestFocus();
    }

    private void mostrarStatus(String mensagem) {
        lblStatus.setText(mensagem);
    }
}