# E-Tickets-Shows-Project <img align="center" alt="mateusayres-Java" height="50" width="50" src="https://raw.githubusercontent.com/devicons/devicon/master/icons/java/java-original.svg">

Simple show ticket management project, with connection to SQL database.<br> 
To connect to the database, run the SQL Script Query below on your DBMS, and change the connection credentials.

**********************************************

Projeto de gerenciamento de ingressos para show simples, com conexão com banco de dados SQL.<br> 
Para conectar com o banco de dados, execute o SQL Script Query abaixo em seu SGBD, e altere as credenciais de conexão.

SQL Script: 
```
CREATE DATABASE eticketsshows;
USE eticketsshows;

CREATE TABLE ingressos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    tipo ENUM('Arquibancada', 'Pista', 'Camarote') NOT NULL,
    valor DECIMAL(10, 2) NOT NULL,
    pagamento ENUM('PIX', 'Cartão de Crédito', 'Boleto') NOT NULL
);

INSERT INTO ingressos (nome, tipo, valor, pagamento) VALUES
('Maria Silva', 'Arquibancada', 150.00, 'PIX'),
('Mateus Ayres', 'Camarote', 350.00, 'PIX'),
('João Pereira', 'Pista', 200.00, 'Cartão de Crédito'),
('Ana Costa', 'Camarote', 350.00, 'Boleto'),
('Carlos Souza', 'Arquibancada', 150.00, 'Boleto'),
('Paula Fernandes', 'Pista', 200.00, 'PIX'),
('Kauã Fonseca', 'Arquibancada', 150.00, 'Cartão de Crédito'),
('Miguel Oliveira', 'Camarote', 350.00, 'Cartão de Crédito');
```

Credentials: 
```
src/
└── br/
    └── com/
        └── eticketsshows/
            └── controller/
                └── Conexao.java ⬅️

        // MySQL database connection URL, includes server address (localhost), port (3306),
        // The database name (eticketsshows) and a parameter to disable the use of SSL (useSSL=false)
        String url = "jdbc:mysql://localhost:3306/eticketsshows?useSSL=false"; ⬅️

        // Username to connect to the database
        String user = "root"; ⬅️

        // User password to connect to the database
        String password = "root"; ⬅️
```
Preview: <br><br>
![e-tickets-shows-print](https://github.com/mateusayres/e-tickets-shows-project/assets/168099824/6cf12983-89ce-43bf-a2d2-44290e75f820)

