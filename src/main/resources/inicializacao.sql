-- DROP TABLE Ativo;
-- DROP TABLE Ordem;

CREATE TABLE IF NOT EXISTS Ativo (
    ticker TEXT PRIMARY KEY,
    nome TEXT,
    quantidade REAL,
    precoMedio REAL,
    saldoVendas REAL,
    totalComprado REAL 
);

CREATE TABLE IF NOT EXISTS Ordem (
    dataOrdem TEXT,
    quantidade REAL,
    preco REAL,
    taxa REAL,
    tipo TEXT,
    custo REAL NULL,
    tickerAtivo TEXT,
    FOREIGN KEY (tickerAtivo) REFERENCES Ativo(ticker)
);