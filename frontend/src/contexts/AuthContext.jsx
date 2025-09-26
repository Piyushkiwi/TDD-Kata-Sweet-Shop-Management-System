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
        // The 'sub' claim usually holds the username (email in your case)
        // Spring Security roles are often in an 'authorities' or 'roles' array. Let's assume 'roles'.
        const userRoles = decoded.roles || (decoded.authorities ? decoded.authorities.map(a => a.authority) : []);
        
        setUser({
          email: decoded.sub,
          roles: userRoles,
        });
        setToken(storedToken);
      } catch (error) {
        console.error("Invalid token:", error);
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
      const userRoles = decoded.roles || (decoded.authorities ? decoded.authorities.map(a => a.authority) : []);
      
      setUser({
        email: decoded.sub,
        roles: userRoles,
      });

      navigate('/'); // Redirect to homepage after login
    } catch (error) {
      console.error('Login failed:', error);
      // You can add state to show an error message to the user
      alert('Login failed! Please check your credentials.');
    }
  };
  
 // In src/contexts/AuthContext.jsx

const register = async (name, email, password, role = 'USER') => {
  try {
    // THE OBJECT BELOW IS THE MOST IMPORTANT PART.
    // Make sure it sends name, email, password, and role correctly.
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
    // Let's improve the error message based on the new error
    alert('Registration failed! The backend reported an error. Please check the console.');
  }
};

  const logout = () => {
    setUser(null);
    setToken(null);
    localStorage.removeItem('token');
    navigate('/login');
  };
  
  // Helper function to check for Admin role
  const isAdmin = () => {
    return user && user.roles.includes('ROLE_ADMIN');
  };

  return (
    <AuthContext.Provider value={{ user, token, login, register, logout, isAdmin }}>
      {children}
    </AuthContext.Provider>
  );
};

// Custom hook to use the auth context easily
export const useAuth = () => {
  return useContext(AuthContext);
};