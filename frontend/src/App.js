import React, { useState } from 'react';
import './App.css';

function App() {
  const [text, setText] = useState('');
  const [tokens, setTokens] = useState([]);
  const [symbolTable, setSymbolTable] = useState([]);
  const [errors, setErrors] = useState([]);

  const handleAnalyze = async () => {
    try {
      const response = await fetch('http://localhost:8081/api/analizar', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ codigoFuente: text }),
      });
  
      const data = await response.json();
      console.log('Respuesta del backend:', data); // Esto imprimirá la respuesta en la consola del navegador
  
      if (data.tokens && data.tablaSimbolos && data.errores) {
        setTokens(data.tokens);
        setSymbolTable(Object.values(data.tablaSimbolos));
        setErrors(data.errores);
      } else {
        console.error('La respuesta del backend no tiene el formato esperado:', data);
      }
    } catch (error) {
      console.error('Error al analizar el texto:', error);
    }
  };

  return (
    <div className="App">
      <h1>INGRESE EL TEXTO A ANALIZAR</h1>

      {/* Cuadro de texto */}
      <textarea
        value={text}
        onChange={(e) => setText(e.target.value)}
        placeholder="Escribe tu texto aquí..."
        rows={10}
        cols={50}
      />

      {/* Botón ANALIZAR */}
      <button onClick={handleAnalyze}>ANALIZAR</button>

      {/* Sección de resultados */}
      <div className="results">
        {/* Tokens generados */}
        <div className="tokens">
          <h2>TOKENS GENERADOS</h2>
          <ul>
            {tokens.map((token, index) => (
              <li key={index}>{token.tipo}: {token.valor} (Línea: {token.linea}, Columna: {token.columna})</li>
            ))}
          </ul>
        </div>

        {/* Tabla de símbolos */}
        <div className="symbol-table">
          <h2>TABLA DE SIMBOLOS</h2>
          <table>
            <thead>
              <tr>
                <th>Símbolo</th>
                <th>Tipo</th>
                <th>Línea</th>
                <th>Columna</th>
              </tr>
            </thead>
            <tbody>
              {symbolTable.map((entry, index) => (
                <tr key={index}>
                  <td>{entry.nombre}</td>
                  <td>{entry.tipo}</td>
                  <td>{entry.linea}</td>
                  <td>{entry.columna}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* Errores */}
        <div className="errors">
          <h2>ERRORES</h2>
          <ul>
            {errors.map((error, index) => (
              <li key={index}>{error}</li>
            ))}
          </ul>
        </div>
      </div>

      {/* Espacios para imágenes */}
      <div className="images">
        <div className="image-placeholder">
          <img src="/rarity.PNG" alt="Florecita" />
        </div>
        <div className="image-placeholder">
          <img src="/otra imagen.PNG" alt="Otra imagen" />
        </div>
      </div>
    </div>
  );
}

export default App;