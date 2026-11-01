import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../../app/providers/AuthProvider.jsx';

export function AdminRoute() {
  const { user } = useAuth();

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (user.role !== 'ADMIN' && user.role !== 'SUPER_ADMIN') {
    return <Navigate to="/dashboard" replace />;
  }

  return <Outlet />;
}
