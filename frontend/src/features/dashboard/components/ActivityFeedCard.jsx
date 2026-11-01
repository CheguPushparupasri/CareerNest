import { motion } from 'framer-motion';
import { Activity, Clock } from 'lucide-react';
import { Panel } from '../../../components/layout/ui/Panel.jsx';

export function ActivityFeedCard({ activities, delay = 0 }) {
  const formatTime = (dateString) => {
    const date = new Date(dateString);
    const now = new Date();
    const diffMs = now - date;
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMs / 3600000);
    const diffDays = Math.floor(diffMs / 86400000);

    if (diffMins < 1) return 'Just now';
    if (diffMins < 60) return `${diffMins}m ago`;
    if (diffHours < 24) return `${diffHours}h ago`;
    if (diffDays < 7) return `${diffDays}d ago`;
    return date.toLocaleDateString();
  };

  if (!activities || activities.length === 0) {
    return (
      <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} transition={{ delay }}>
        <Panel className="p-5">
          <div className="flex items-center gap-2">
            <Activity size={20} className="text-slate-400" />
            <h3 className="text-lg font-semibold text-slate-950 dark:text-slate-50">Recent activity</h3>
          </div>
          <p className="mt-4 text-sm text-slate-500">No recent activity</p>
        </Panel>
      </motion.div>
    );
  }

  return (
    <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} transition={{ delay }}>
      <Panel className="p-5">
      <div className="flex items-center gap-2">
        <Activity size={20} className="text-green-600" />
        <h3 className="text-lg font-semibold text-slate-950 dark:text-slate-50">Recent Activity</h3>
      </div>
      
      <div className="mt-4 space-y-3">
        {activities.map((activity, index) => (
          <div key={index} className="flex items-start gap-3">
            <div className="mt-1 rounded-full bg-green-100 p-1.5 dark:bg-green-950/30">
              <Clock size={12} className="text-green-600" />
            </div>
            <div className="flex-1">
              <p className="text-sm font-medium text-slate-950 dark:text-slate-50">{activity.title}</p>
              <p className="mt-0.5 text-xs text-slate-500">{activity.description}</p>
              <p className="mt-1 text-xs text-slate-400">{formatTime(activity.timestamp)}</p>
            </div>
          </div>
        ))}
      </div>
      </Panel>
    </motion.div>
  );
}
