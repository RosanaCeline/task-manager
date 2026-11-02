package com.capacita.irede.controller;

import com.capacita.irede.exceptions.TaskTitleTooLongException;
import com.capacita.irede.exceptions.TitleEmptyOrNullException;
import com.capacita.irede.model.Task;
import com.capacita.irede.model.TaskPrioritaria;
import com.capacita.irede.repository.TaskRepository;
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

import java.sql.SQLException;
import java.util.Optional;

public class MainController {
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

    private final TaskRepository taskRepository = new TaskRepository();

    /**
     * Inicializa os componentes da interface, configura seus comportamentos
     * e carrega as tarefas armazenadas no banco de dados.
     */
    @FXML
    public void initialize() throws SQLException {
        treeTarefas.setCellFactory(tv -> new TreeCell<>() {

            /**
             * Atualiza a apresentação de cada tarefa na árvore, destacando seu estado de conclusão e sua descrição.
             */
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
                        texto += "\n    " + descricao;
                    }

                    setText(texto);
                    if (task.isConcluida()) {
                        getStyleClass().add("tarefa-concluida");
                    }
                }
            }
        });

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

        cbTarefaPai.disableProperty().bind(rbSubtarefa.selectedProperty().not());

        btnAdicionar.disableProperty().bind(Bindings.createBooleanBinding(
                () -> txtTitulo.getText().isBlank()
                        || (rbSubtarefa.isSelected() && cbTarefaPai.getValue() == null),
                txtTitulo.textProperty(),
                rbSubtarefa.selectedProperty(),
                cbTarefaPai.valueProperty()));

        btnConcluir.disableProperty().bind(
                treeTarefas.getSelectionModel().selectedItemProperty().isNull());
        btnRemover.disableProperty().bind(
                treeTarefas.getSelectionModel().selectedItemProperty().isNull());

        listarTarefas();
    }

    /**
     * Cria uma tarefa principal ou uma subtarefa, salva no banco de dados
     * e atualiza a interface após a operação.
     */
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
                taskRepository.adicionar(subtarefa);

                mostrarStatus("Subtarefa '" + subtarefa.getTitulo() + "' adicionada à tarefa '" + pai.getTitulo() + "'.");
            } else {
                Task task = new Task(titulo, descricao);
                taskRepository.adicionar(task);

                mostrarStatus("Tarefa '" + task.getTitulo() + "' adicionada com sucesso!");
            }

            limparFormulario();
            listarTarefas();
        } catch (TaskTitleTooLongException e) {
            mostrarStatus(e.getMessage());
        } catch (TitleEmptyOrNullException e) {
            mostrarStatus("Erro ao salvar tarefa: " + e.getMessage());
        } catch (SQLException e) {
            mostrarStatus("Erro ao acessar o banco de dados.");
            e.printStackTrace();
        }
    }

    /**
     * Recupera as tarefas do banco de dados e atualiza a árvore
     * e a lista de tarefas principais disponíveis para seleção.
     */
    @FXML
    public void listarTarefas() throws SQLException {
        boolean apenasPendentes = chkApenasPendentes.isSelected();

        TreeItem<Task> raiz = new TreeItem<>();
        for (Task task : taskRepository.getTarefas()) {
            TreeItem<Task> item = criarItem(task, apenasPendentes);
            if (item != null) {
                raiz.getChildren().add(item);
            }
        }
        treeTarefas.setRoot(raiz);
        cbTarefaPai.setItems(FXCollections.observableArrayList(taskRepository.getTarefas()));
    }

    /**
     * Marca como concluída a tarefa selecionada, atualiza o banco de dados
     * e recarrega a lista apresentada na interface.
     */
    @FXML
    public void concluirTarefa() throws SQLException {
        TreeItem<Task> selecionado = treeTarefas.getSelectionModel().getSelectedItem();

        if (selecionado == null) {
            mostrarStatus("Selecione uma tarefa na lista.");
            return;
        }

        Task task = selecionado.getValue();
        taskRepository.concluir(task);
        mostrarStatus("'" + task.getTitulo() + "' concluída!");
        listarTarefas();
    }

    /**
     * Solicita a confirmação do usuário antes de remover a tarefa selecionada
     * e atualiza a árvore após a exclusão.
     */
    @FXML
    public void removerTarefa() throws SQLException {
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

        taskRepository.remover(task);

        mostrarStatus("'" + task.getTitulo() + "' removida.");
        listarTarefas();
    }

    // ===== Métodos auxiliares =====

    /**
     * Cria recursivamente os itens da árvore, organizando as subtarefas
     * e aplicando o filtro de tarefas pendentes quando solicitado.
     *
     * @param task tarefa que será representada na árvore
     * @param apenasPendentes indica se tarefas concluídas devem ser ocultadas
     * @return item da árvore ou null quando a tarefa não deve ser exibida
     */
    private TreeItem<Task> criarItem(Task task, boolean apenasPendentes) {
        TreeItem<Task> item = new TreeItem<>(task);
        item.setExpanded(true);

        for (TaskPrioritaria sub : task.getSubtarefas()) {
            TreeItem<Task> filho = criarItem(sub, apenasPendentes);
            if (filho != null) {
                item.getChildren().add(filho);
            }
        }

        boolean visivel = !apenasPendentes || !task.isConcluida() || !item.getChildren().isEmpty();
        return visivel ? item : null;
    }

    /**
     * Limpa os campos do formulário e devolve o foco ao campo de título.
     */
    private void limparFormulario() {
        txtTitulo.clear();
        txtDescricao.clear();
        txtTitulo.requestFocus();
    }

    /**
     * Exibe uma mensagem de retorno ao usuário na área de status da interface.
     *
     * @param mensagem texto que será apresentado
     */
    private void mostrarStatus(String mensagem) {
        lblStatus.setText(mensagem);
    }
}