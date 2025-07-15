import './App.css';
import { Routes, Route } from 'react-router-dom';
import Home from './pages/public/Home.jsx';
import Login from './pages/public/Login.jsx';
import SignUp from './pages/public/SignUp.jsx';
import Register from './pages/public/Register.jsx';
import LoginAdmin from './pages/admin/LoginAdmin.jsx';
import Dashboard from './pages/admin/Dashboard.jsx';
import Error404 from './pages/public/Error404.jsx'
import UserProfile from './pages/public/UserProfile.jsx';

function App() {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/login" element={<Login />} />
      <Route path="/signup" element={<SignUp />} />
      <Route path="/register" element={<Register />} />
      <Route path="/admin/login" element={<LoginAdmin />} />
      <Route path="/admin/dashboard" element={<Dashboard />} />
      <Route path="/UserProfile" element={<UserProfile/>} />
      <Route path="*" element={<Error404/>}/>
      
     
      
     
     
    </Routes>
  );
}

export default App;