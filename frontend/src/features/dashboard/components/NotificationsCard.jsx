import { motion } from 'framer-motion';
import { Bell, AlertTriangle, Info, CheckCircle } from 'lucide-react';
import { Panel } from '../../../components/layout/ui/Panel.jsx';

export function NotificationsCard({ notifications, delay = 0 }) {
  const getIcon = (type) => {
    switch (type?.toUpperCase()) {
      case 'HIGH':
      case 'ERROR':
        return AlertTriangle;
      case 'NORMAL':
      case 'INFO':
        return Info;
      case 'SUCCESS':
        return CheckCircle;
      default:
        return Bell;
    }
  };

  const getColor = (type) => {
    switch (type?.toUpperCase()) {
      case 'HIGH':
      case 'ERROR':
        return 'text-red-600 bg-red-50 dark:bg-red-950/30';
      case 'NORMAL':
      case 'INFO':
        return 'text-blue-600 bg-blue-50 dark:bg-blue-950/30';
      case 'SUCCESS':
        return 'text-green-600 bg-green-50 dark:bg-green-950/30';
      default:
        return 'text-slate-600 bg-slate-50 dark:bg-slate-800';
    }
  };

  if (!notifications || notifications.length === 0) {
    return (
      <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} transition={{ delay }}>
        <Panel className="p-5">
          <div className="flex items-center gap-2">
            <Bell size={20} className="text-slate-400" />
            <h3 className="text-lg font-semibold text-slate-950 dark:text-slate-50">Notifications</h3>
          </div>
          <p className="mt-4 text-sm text-slate-500">No new notifications</p>
        </Panel>
      </motion.div>
    );
  }

  return (
    <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} transition={{ delay }}>
      <Panel className="p-5">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2">
          <Bell size={20} className="text-blue-600" />
          <h3 className="text-lg font-semibold text-slate-950 dark:text-slate-50">Notifications</h3>
        </div>
        <span className="rounded-full bg-blue-100 px-2 py-0.5 text-xs font-medium text-blue-700 dark:bg-blue-950 dark:text-blue-300">
          {notifications.length}
        </span>
      </div>
      
      <div className="mt-4 space-y-3">
        {notifications.slice(0, 5).map((notif, index) => {
          const Icon = getIcon(notif.type);
          return (
            <div
              key={notif.id || index}
              className={`flex items-start gap-3 rounded-lg p-3 transition-colors hover:bg-slate-50 dark:hover:bg-slate-800 ${!notif.isRead ? 'bg-slate-50 dark:bg-slate-800' : ''}`}
            >
              <div className={`rounded-lg p-1.5 ${getColor(notif.type)}`}>
                <Icon size={14} />
              </div>
              <div className="flex-1">
                <p className="text-sm font-medium text-slate-950 dark:text-slate-50">{notif.title}</p>
                <p className="mt-0.5 text-xs text-slate-500">{notif.message}</p>
              </div>
            </div>
          );
        })}
      </div>
      </Panel>
    </motion.div>
  );
}
