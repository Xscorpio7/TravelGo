const express = require('express');
const app = express();


app.get('/', (req, res) => {
  res.sendFile(__dirname + '/principal.html');
});


app.use((req, res, next) => {
  res.status(404).sendFile(__dirname + '/src/error404.html');
});


app.use((err, req, res, next) => {
  console.error(err.stack); 
  res.status(500).sendFile(__dirname + '/src/error500.html');
});

app.listen(3000, () => {
  console.log('Servidor en http://localhost:3000');
});
