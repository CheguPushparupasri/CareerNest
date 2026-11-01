import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../../app/providers/AuthProvider.jsx';

export function ProtectedRoute() {
  const { authenticated, loading } = useAuth();
  if (loading) {
    return <div className="flex min-h-screen items-center justify-center bg-slate-50 text-slate-600 dark:bg-slate-950 dark:text-slate-300">Loading CareerNest...</div>;
  }
  return authenticated ? <Outlet /> : <Navigate to="/login" replace />;
}
