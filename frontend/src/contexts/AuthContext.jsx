// FINAL AND CORRECTED CODE FOR: src/contexts/AuthContext.jsx

import React, { createContext, useState, useEffect, useContext } from 'react';
import { jwtDecode } from 'jwt-decode';
import api from '../api/axiosConfig';
import { useNavigate } from 'react-router-dom';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(() => localStorage.getItem('token') || null);
  const navigate = useNavigate();

  useEffect(() => {
    const storedToken = localStorage.getItem('token');
    if (storedToken) {
      try {
        const decoded = jwtDecode(storedToken);
        
        const userRoles = decoded.role || (decoded.authorities ? decoded.authorities.map(a => a.authority) : []);
        
        setUser({
          email: decoded.sub,
          roles: userRoles,
        });
        setToken(storedToken);
      } catch (error) {
        console.error("Invalid token on initial load:", error);
        logout();
      }
    }
  }, []);

  const login = async (email, password) => {
    try {
      const response = await api.post('/auth/login', { email, password });
      const newToken = response.data;
      
      localStorage.setItem('token', newToken);
      setToken(newToken);

      const decoded = jwtDecode(newToken);
      
      const userRoles = decoded.role || (decoded.authorities ? decoded.authorities.map(a => a.authority) : []);
      
      setUser({
        email: decoded.sub,
        roles: userRoles,
      });

      navigate('/');
    } catch (error) {
      console.error('Login failed:', error);
      alert('Login failed! Please check your credentials.');
    }
  };
  
  const register = async (name, email, password, role = 'USER') => {
    try {
      await api.post('/auth/register', {
        name: name,
        email: email,
        password: password,
        role: role
      });

      navigate('/login');
      alert('Registration successful! Please log in.');
    } catch (error) {
      console.error('Registration failed:', error);
      alert('Registration failed! The backend reported an error.');
    }
  };

  const logout = () => {
    setUser(null);
    setToken(null);
    localStorage.removeItem('token');
    navigate('/login');
  };
  
  const isAdmin = () => {
    return user && user.roles && user.roles.includes('ROLE_ADMIN');
  };

  return (
    <AuthContext.Provider value={{ user, token, login, register, logout, isAdmin }}>
      {children}
    </AuthContext.Provider> // <-- THE FIX IS HERE
  );
};

export const useAuth = () => {
  return useContext(AuthContext);
};