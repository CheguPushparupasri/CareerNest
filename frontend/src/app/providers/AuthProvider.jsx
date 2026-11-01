import { createContext, useCallback, useContext, useEffect, useMemo, useRef, useState } from 'react';
import { authService } from '../../features/auth/services/authService.js';
import { tokenStorage } from '../../lib/tokenStorage.js';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const refreshRequest = useRef(null);

  const bootstrap = useCallback(async () => {
    try {
      if (tokenStorage.getAccessToken()) {
        setUser(await authService.me());
      } else {
        refreshRequest.current ||= authService.refresh();
        const response = await refreshRequest.current;
        tokenStorage.setTokens(response);
        setUser(response.user);
      }
    } catch {
      tokenStorage.clear();
      await authService.logout().catch(() => null);
    } finally {
      refreshRequest.current = null;
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    bootstrap();
  }, [bootstrap]);

  const login = async (payload) => {
    const response = await authService.login(payload);
    tokenStorage.setTokens(response);
    setUser(response.user);
    return response.user;
  };

  const logout = async () => {
    await authService.logout().catch(() => null);
    tokenStorage.clear();
    setUser(null);
  };

  const value = useMemo(() => ({
    user,
    loading,
    authenticated: Boolean(user),
    login,
    logout,
    refreshUser: bootstrap,
  }), [user, loading, bootstrap]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used inside AuthProvider');
  }
  return context;
}
