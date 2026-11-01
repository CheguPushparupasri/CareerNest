import { useState } from 'react';
import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { GraduationCap, ShieldCheck, ArrowRight, AlertCircle } from 'lucide-react';
import { useAuth } from '../../../app/providers/AuthProvider.jsx';

export function RoleSelectionPage() {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [error, setError] = useState('');

  const roles = [
    {
      id: 'student',
      title: 'Student',
      description: 'Build your career with powerful tools for resume building, project tracking, and skill development.',
      icon: GraduationCap,
      color: 'from-primary-500 to-accent-500',
      bgColor: 'bg-primary-50 dark:bg-primary-950/30',
      textColor: 'text-primary-600 dark:text-primary-400',
      route: '/dashboard',
    },
    {
      id: 'admin',
      title: 'Administrator',
      description: 'Manage users, view analytics, and oversee platform operations with enterprise-grade tools.',
      icon: ShieldCheck,
      color: 'from-secondary-500 to-primary-500',
      bgColor: 'bg-secondary-50 dark:bg-secondary-950/30',
      textColor: 'text-secondary-600 dark:text-secondary-400',
      route: '/admin',
      adminOnly: true,
    },
  ];

  const handleRoleSelect = (role) => {
    setError('');
    if (role.adminOnly && user?.role !== 'ADMIN' && user?.role !== 'SUPER_ADMIN') {
      setError('Administrator access is restricted to admin users only.');
      return;
    }
    navigate(role.route);
  };

  return (
    <div className="min-h-screen bg-background dark:bg-background-dark flex items-center justify-center p-4">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="w-full max-w-4xl"
      >
        <div className="text-center mb-12">
          <motion.h1
            initial={{ opacity: 0, y: -10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.2 }}
            className="text-4xl font-bold text-text dark:text-text-dark mb-4"
          >
            Welcome to CareerNest
          </motion.h1>
          <motion.p
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ delay: 0.3 }}
            className="text-lg text-text-muted dark:text-text-mutedDark"
          >
            Select your role to continue to your personalized workspace
          </motion.p>
          {error && (
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              className="mt-4 inline-flex items-center gap-2 rounded-2xl bg-danger-50 px-4 py-2 text-sm text-danger dark:bg-danger-950/30"
            >
              <AlertCircle size={16} />
              {error}
            </motion.div>
          )}
        </div>

        <div className="grid gap-6 md:grid-cols-2">
          {roles.map((role, index) => {
            const Icon = role.icon;
            return (
              <motion.button
                key={role.id}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.4 + index * 0.1 }}
                onClick={() => handleRoleSelect(role)}
                className="group relative overflow-hidden rounded-3xl border border-border dark:border-borderDark bg-white dark:bg-slate-900 p-8 text-left shadow-premium hover:shadow-premium-lg transition-all duration-300 hover:-translate-y-1"
              >
                <div className={`absolute inset-0 bg-gradient-to-br ${role.color} opacity-0 group-hover:opacity-5 transition-opacity duration-300`} />
                
                <div className={`relative inline-flex rounded-2xl ${role.bgColor} p-4 mb-6 ${role.textColor}`}>
                  <Icon size={32} />
                </div>
                
                <h2 className="relative text-2xl font-bold text-text dark:text-text-dark mb-3">
                  {role.title}
                </h2>
                
                <p className="relative text-text-muted dark:text-text-mutedDark mb-6 leading-relaxed">
                  {role.description}
                </p>
                
                <div className="relative flex items-center gap-2 font-semibold text-primary-600 dark:text-primary-400 group-hover:gap-4 transition-all">
                  Continue as {role.title}
                  <ArrowRight size={20} className="group-hover:translate-x-1 transition-transform" />
                </div>
              </motion.button>
            );
          })}
        </div>

        <motion.p
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 0.6 }}
          className="mt-8 text-center text-sm text-text-muted dark:text-text-mutedDark"
        >
          You can switch roles anytime from your profile settings
        </motion.p>
      </motion.div>
    </div>
  );
}
