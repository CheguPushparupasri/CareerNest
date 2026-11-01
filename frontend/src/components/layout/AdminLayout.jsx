import { useState } from 'react';
import { Outlet, Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../../app/providers/AuthProvider.jsx';
import { useTheme } from '../../app/providers/ThemeProvider.jsx';
import { 
  LayoutDashboard, 
  Users, 
  BarChart3, 
  FileText, 
  ShieldCheck, 
  Activity, 
  Bell, 
  Search, 
  Moon, 
  Sun, 
  LogOut, 
  Settings, 
  ChevronDown,
  Menu,
  X
} from 'lucide-react';

const adminNavItems = [
  { label: 'Dashboard', to: '/admin', icon: LayoutDashboard },
  { label: 'Users', to: '/admin/users', icon: Users },
  { label: 'Analytics', to: '/admin/analytics', icon: BarChart3 },
  { label: 'Reports', to: '/admin/reports', icon: FileText },
  { label: 'Audit Logs', to: '/admin/audit-logs', icon: ShieldCheck },
  { label: 'System Health', to: '/admin/system-health', icon: Activity },
  { label: 'Settings', to: '/admin/settings', icon: Settings },
];

export function AdminLayout() {
  const { user, logout } = useAuth();
  const { theme, toggleTheme } = useTheme();
  const navigate = useNavigate();
  const location = useLocation();
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [notificationsOpen, setNotificationsOpen] = useState(false);

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  const activeLabel = adminNavItems.find((item) => location.pathname.startsWith(item.to))?.label ?? 'Dashboard';

  return (
    <div className="min-h-screen bg-background dark:bg-background-dark">
      {/* Sidebar Overlay */}
      {sidebarOpen && (
        <div 
          className="fixed inset-0 z-50 bg-black/50 lg:hidden"
          onClick={() => setSidebarOpen(false)}
        />
      )}

      {/* Sidebar */}
      <aside className={`fixed inset-y-0 left-0 z-50 w-72 transform border-r border-border dark:border-borderDark bg-white dark:bg-slate-900 transition-transform lg:static lg:translate-x-0 ${sidebarOpen ? 'translate-x-0' : '-translate-x-full'}`}>
        <div className="flex h-full flex-col">
          {/* Logo */}
          <div className="flex items-center gap-3 border-b border-border dark:border-borderDark p-6">
            <div className="flex h-12 w-12 items-center justify-center rounded-2xl bg-gradient-to-br from-secondary-500 to-primary-500 shadow-glow">
              <ShieldCheck className="text-white" size={24} />
            </div>
            <div>
              <h1 className="text-xl font-bold text-text dark:text-text-dark">CareerNest</h1>
              <p className="text-xs text-text-muted dark:text-text-mutedDark">Admin Console</p>
            </div>
          </div>

          {/* Navigation */}
          <nav className="flex-1 overflow-y-auto p-4">
            <ul className="space-y-1">
              {adminNavItems.map((item) => {
                const Icon = item.icon;
                const isActive = location.pathname === item.to || (item.to !== '/admin' && location.pathname.startsWith(item.to));
                return (
                  <li key={item.to}>
                    <Link
                      to={item.to}
                      className={`flex items-center gap-3 rounded-2xl px-4 py-3 text-sm font-medium transition-all ${
                        isActive
                          ? 'bg-gradient-to-r from-secondary-500 to-primary-500 text-white shadow-glow'
                          : 'text-text-muted dark:text-text-mutedDark hover:bg-slate-50 dark:hover:bg-slate-800'
                      }`}
                      onClick={() => setSidebarOpen(false)}
                    >
                      <Icon size={18} />
                      {item.label}
                    </Link>
                  </li>
                );
              })}
            </ul>
          </nav>

          {/* User Section */}
          <div className="border-t border-border dark:border-borderDark p-4">
            <div className="flex items-center gap-3 rounded-2xl bg-slate-50 dark:bg-slate-800/80 p-3">
              <div className="flex h-10 w-10 items-center justify-center rounded-full bg-gradient-to-br from-secondary-500 to-primary-500 text-white font-semibold">
                {user?.fullName?.charAt(0) || 'A'}
              </div>
              <div className="flex-1 min-w-0">
                <p className="truncate text-sm font-medium text-text dark:text-text-dark">{user?.fullName || 'Admin'}</p>
                <p className="truncate text-xs text-text-muted dark:text-text-mutedDark">{user?.role || 'ADMIN'}</p>
              </div>
            </div>
          </div>
        </div>
      </aside>

      {/* Main Content */}
      <div className="flex flex-1 flex-col">
        {/* Topbar */}
        <header className="flex items-center justify-between border-b border-border dark:border-borderDark bg-white dark:bg-slate-900 px-6 py-4">
          <div className="flex items-center gap-4">
            <button
              onClick={() => setSidebarOpen(true)}
              className="lg:hidden rounded-2xl p-2 text-text-muted hover:bg-slate-50 dark:text-text-mutedDark dark:hover:bg-slate-800"
            >
              <Menu size={24} />
            </button>

            <div className="relative hidden sm:block">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-text-muted" size={18} />
              <input
                type="text"
                placeholder="Search users, logs..."
                className="w-80 rounded-2xl border border-border dark:border-borderDark bg-slate-50 dark:bg-slate-800 py-2.5 pl-10 pr-4 text-sm focus:outline-none focus:ring-2 focus:ring-secondary-500"
              />
            </div>
          </div>

          <div className="flex items-center gap-3">
            <button
              onClick={toggleTheme}
              className="rounded-2xl p-2.5 text-text-muted hover:bg-slate-50 dark:text-text-mutedDark dark:hover:bg-slate-800 transition-colors"
            >
              {theme === 'dark' ? <Sun size={20} /> : <Moon size={20} />}
            </button>
            
            <div className="relative">
              <button
                onClick={() => setNotificationsOpen(!notificationsOpen)}
                className="relative rounded-2xl p-2.5 text-text-muted hover:bg-slate-50 dark:text-text-mutedDark dark:hover:bg-slate-800 transition-colors"
              >
                <Bell size={20} />
                <span className="absolute right-1 top-1 h-2 w-2 rounded-full bg-danger" />
              </button>
              
              {notificationsOpen && (
                <div className="absolute right-0 top-full mt-2 w-80 rounded-2xl border border-border dark:border-borderDark bg-white dark:bg-slate-900 shadow-premium-lg p-4">
                  <div className="flex items-center justify-between mb-3">
                    <h3 className="font-semibold text-text dark:text-text-dark">Notifications</h3>
                    <span className="text-xs text-secondary-600 cursor-pointer hover:underline">Mark all read</span>
                  </div>
                  <div className="space-y-3">
                    {[1, 2, 3].map((i) => (
                      <div key={i} className="flex gap-3 p-2 rounded-xl hover:bg-slate-50 dark:hover:bg-slate-800 cursor-pointer">
                        <div className="h-8 w-8 rounded-full bg-secondary-100 dark:bg-secondary-950/30 flex items-center justify-center">
                          <Bell size={14} className="text-secondary-600" />
                        </div>
                        <div className="flex-1">
                          <p className="text-sm font-medium text-text dark:text-text-dark">New admin notification</p>
                          <p className="text-xs text-text-muted dark:text-text-mutedDark">System update available</p>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </div>

            <div className="relative">
              <button className="flex items-center gap-2 rounded-2xl p-2 hover:bg-slate-50 dark:hover:bg-slate-800 transition-colors">
                <div className="h-8 w-8 rounded-full bg-gradient-to-br from-secondary-500 to-primary-500 text-white font-semibold flex items-center justify-center">
                  {user?.fullName?.charAt(0) || 'A'}
                </div>
                <ChevronDown size={16} className="text-text-muted dark:text-text-mutedDark hidden sm:block" />
              </button>
            </div>

            <button
              onClick={handleLogout}
              className="rounded-2xl p-2.5 text-text-muted hover:bg-danger-50 dark:text-text-mutedDark dark:hover:bg-danger-950/30 hover:text-danger transition-colors"
              title="Logout"
            >
              <LogOut size={20} />
            </button>
          </div>
        </header>

        {/* Page Content */}
        <main className="flex-1 overflow-y-auto p-6">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
