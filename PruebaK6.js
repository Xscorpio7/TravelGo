import http from 'k6/http';

export default function () {
    let res = http.get('http://localhost:9090/api/usuarios');   
}