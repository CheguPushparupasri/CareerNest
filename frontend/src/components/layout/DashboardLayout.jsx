import { Link, Outlet, useLocation, useNavigate } from 'react-router-dom';
import { Bell, Briefcase, ChevronRight, Compass, FolderKanban, LogOut, Moon, Search, Sun, UserRound, BookOpen, Code2, FileText, Sparkles, Settings, ShieldCheck, TrendingUp, Award, CalendarDays, Clock3 } from 'lucide-react';
import { useEffect, useMemo, useState } from 'react';
import { Button } from '../ui/Button.jsx';
import { useAuth } from '../../app/providers/AuthProvider.jsx';
import { useTheme } from '../../app/providers/ThemeProvider.jsx';
import { notificationService } from '../../features/notifications/services/notificationService.js';

const navItems = [
  { label: 'Overview', to: '/dashboard', icon: Compass },
  { label: 'Projects', to: '/projects', icon: FolderKanban },
  { label: 'Resumes', to: '/resumes', icon: FileText },
  { label: 'Certifications', to: '/certifications', icon: Award },
  { label: 'Internships', to: '/internships', icon: Briefcase },
  { label: 'Learning', to: '/learning', icon: BookOpen },
  { label: 'Coding', to: '/coding-profiles', icon: Code2 },
  { label: 'Timeline', to: '/timeline', icon: Clock3 },
  { label: 'Profile', to: '/profile-setup', icon: UserRound },
  { label: 'Notifications', to: '/notifications', icon: CalendarDays },
  { label: 'Career Health', to: '/career-health', icon: TrendingUp },
  { label: 'Assistant', to: '/assistant', icon: Sparkles },
  { label: 'Admin', to: '/admin', icon: ShieldCheck, adminOnly: true },
];

export function DashboardLayout() {
  const { user, logout } = useAuth();
  const { theme, toggleTheme } = useTheme();
  const navigate = useNavigate();
  const location = useLocation();
  const [collapsed, setCollapsed] = useState(false);
  const [unreadCount, setUnreadCount] = useState(0);

  useEffect(() => {
    let active = true;
    notificationService.getCount()
      .then((count) => { if (active) setUnreadCount(count); })
      .catch(() => { if (active) setUnreadCount(0); });
    return () => { active = false; };
  }, [location.pathname]);

  const filteredNavItems = navItems.filter(item => !item.adminOnly || (user?.role === 'ADMIN' || user?.role === 'SUPER_ADMIN'));

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  const activeLabel = useMemo(() => navItems.find((item) => location.pathname.startsWith(item.to))?.label ?? 'Overview', [location.pathname]);

  return (
    <div className="min-h-screen bg-transparent text-slate-950 dark:text-slate-50">
      <header className="sticky top-0 z-20 border-b border-slate-200/80 bg-white/80 backdrop-blur-xl dark:border-slate-800/80 dark:bg-slate-950/80">
        <div className="mx-auto flex max-w-7xl items-center justify-between px-4 py-3 sm:px-6 lg:px-8">
          <div className="flex items-center gap-3">
            <button onClick={() => setCollapsed((value) => !value)} className="rounded-xl border border-slate-200 p-2 text-slate-600 transition hover:bg-slate-100 dark:border-slate-700 dark:text-slate-300 dark:hover:bg-slate-800">
              <ChevronRight size={18} className={collapsed ? 'rotate-180' : ''} />
            </button>
            <Link to="/dashboard" className="flex items-center gap-3">
              <div className="flex h-10 w-10 items-center justify-center rounded-2xl bg-gradient-to-br from-brand-600 via-violet-600 to-cyan-500 text-white shadow-glow">
                <Briefcase size={18} />
              </div>
              <div>
                <p className="text-sm font-semibold tracking-wide text-slate-900 dark:text-slate-100">CareerNest</p>
                <p className="text-xs text-slate-500">Premium talent OS</p>
              </div>
            </Link>
          </div>
          <div className="flex items-center gap-2">
            <label className="hidden items-center gap-2 rounded-xl border border-slate-200 bg-slate-50 px-3 py-2 text-sm text-slate-500 sm:flex dark:border-slate-700 dark:bg-slate-900/70">
              <Search size={16} />
              <input className="w-36 bg-transparent outline-none" placeholder="Search" />
            </label>
            <Link to="/notifications" className="relative rounded-xl border border-slate-200 p-2 text-slate-600 transition hover:bg-slate-100 dark:border-slate-700 dark:text-slate-300 dark:hover:bg-slate-800" aria-label={`Notifications, ${unreadCount} unread`}>
              <Bell size={18} />
              {unreadCount > 0 && <span className="absolute -right-1 -top-1 min-w-4 rounded-full bg-red-600 px-1 text-center text-[10px] leading-4 text-white">{unreadCount > 99 ? '99+' : unreadCount}</span>}
            </Link>
            <button className="rounded-xl border border-slate-200 p-2 text-slate-600 transition hover:bg-slate-100 dark:border-slate-700 dark:text-slate-300 dark:hover:bg-slate-800" onClick={toggleTheme} aria-label="Toggle dark mode">
              {theme === 'dark' ? <Sun size={18} /> : <Moon size={18} />}
            </button>
            <div className="flex items-center gap-2 rounded-xl border border-slate-200 bg-slate-50 px-2 py-1.5 dark:border-slate-700 dark:bg-slate-900/70">
              <div className="flex h-8 w-8 items-center justify-center rounded-full bg-gradient-to-br from-brand-500 to-cyan-500 text-sm font-semibold text-white">
                {user?.fullName?.charAt(0) || 'U'}
              </div>
              <div className="hidden sm:block">
                <p className="text-sm font-medium text-slate-900 dark:text-slate-100">{user?.fullName || 'Student'}</p>
                <p className="text-xs text-slate-500">{activeLabel}</p>
              </div>
            </div>
            <Button variant="secondary" onClick={handleLogout}><LogOut size={16} className="mr-2" />Logout</Button>
          </div>
        </div>
      </header>
      <div className="mx-auto grid max-w-7xl gap-6 px-4 py-6 sm:px-6 lg:grid-cols-[260px_1fr] lg:px-8">
        <aside className={`rounded-3xl border border-slate-200/80 bg-white/80 p-3 shadow-soft backdrop-blur dark:border-slate-800/80 dark:bg-slate-900/70 ${collapsed ? 'lg:w-[88px]' : ''}`}>
          <div className="mb-4 flex items-center justify-between rounded-2xl bg-slate-50 px-3 py-3 dark:bg-slate-800/70">
            <div>
              <p className="text-xs uppercase tracking-[0.3em] text-slate-400">Workspace</p>
              <p className="text-sm font-semibold text-slate-900 dark:text-slate-100">Daily Focus</p>
            </div>
            <Settings size={16} className="text-slate-400" />
          </div>
          <nav className="space-y-1">
            {filteredNavItems.map((item) => {
              const Icon = item.icon;
              const active = location.pathname.startsWith(item.to) && (item.to === '/dashboard' ? location.pathname === '/dashboard' : true);
              return (
                <Link key={item.label} to={item.to} className={`flex items-center gap-3 rounded-2xl px-3 py-2.5 text-sm font-medium transition ${active ? 'bg-gradient-to-r from-brand-600 to-violet-600 text-white shadow-glow' : 'text-slate-600 hover:bg-slate-100 dark:text-slate-300 dark:hover:bg-slate-800'}`}>
                  <Icon size={18} />
                  {!collapsed && <span>{item.label}</span>}
                </Link>
              );
            })}
          </nav>
        </aside>
        <main className="min-w-0">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
