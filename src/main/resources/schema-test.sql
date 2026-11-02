  CREATE DATABASE task_manager_test;

  USE task_manager_test;

  CREATE TABLE tarefas (
      id BIGINT AUTO_INCREMENT PRIMARY KEY,
      titulo VARCHAR(100) NOT NULL,
      descricao TEXT,
      concluida BOOLEAN NOT NULL DEFAULT FALSE,
      tarefa_pai_id BIGINT,
      CONSTRAINT fk_tarefa_pai
          FOREIGN KEY (tarefa_pai_id)
          REFERENCES tarefas(id)
          ON DELETE CASCADE
  );

  GRANT ALL PRIVILEGES ON task_manager_test.*
  TO 'taskmanager_user'@'localhost';