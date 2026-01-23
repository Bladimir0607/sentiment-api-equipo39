import React, { createContext, useState, useContext, useEffect } from 'react';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Al cargar la app, ver si hay un token guardado
    const savedToken = localStorage.getItem('token');
    const savedRole = localStorage.getItem('role');
    const savedUsername = localStorage.getItem('username');

    if (savedToken) {
      setUser({ token: savedToken, role: savedRole, username: savedUsername });
    }
    setLoading(false);
  }, []);

  const loginUser = (data) => {
    localStorage.setItem('token', data.token);
    localStorage.setItem('role', data.role);
    localStorage.setItem('username', data.username);
    setUser({ token: data.token, role: data.role, username: data.username });
  };

  const logout = () => {
    localStorage.clear();
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, loginUser, logout, loading }}>
      {!loading && children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);