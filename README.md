# E-Tickets-Shows-Project <img align="center" alt="mateusayres-Java" height="50" width="50" src="https://raw.githubusercontent.com/devicons/devicon/master/icons/java/java-original.svg">

Simple show ticket management project, with connection to SQL database.<br> 
To connect to the database, run the SQL Script Query below 

**********************************************

Projeto de gerenciamento de ingressos para show simples, com conexão com banco de dados SQL.<br> 
Para conectar com o banco de dados, execute o SQL Script Query abaixo.

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
Preview: <br><br>
![E-TicketsShows-Print](https://github.com/mateusayres/e-tickets-shows-project/assets/168099824/a46c3e8b-3be8-443d-9a52-55b11647193b)
