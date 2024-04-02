DROP TABLE Ativo;
DROP TABLE Ordem;

CREATE TABLE IF NOT EXISTS Ativo (
    ticker TEXT PRIMARY KEY,
    nome TEXT,
    quantidade REAL,
    precoMedio REAL,
    totalAtual REAL    
);

CREATE TABLE IF NOT EXISTS Ordem (
    dataOrdem TEXT,
    quantidade REAL,
    preco REAL,
    total REAL,
    tipo TEXT,
    custo REAL NULL,
    tickerAtivo TEXT,
    FOREIGN KEY (tickerAtivo) REFERENCES Ativo(ticker)
);