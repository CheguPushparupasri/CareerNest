import { motion } from 'framer-motion';
import { Panel } from '../../../components/layout/ui/Panel.jsx';

export function DashboardCard({ title, value, unit, description, color, icon: Icon, delay = 0 }) {
  const colorClasses = {
    blue: 'text-brand-600 bg-brand-50 dark:bg-brand-950/30',
    teal: 'text-cyan-600 bg-cyan-50 dark:bg-cyan-950/30',
    amber: 'text-amber-600 bg-amber-50 dark:bg-amber-950/30',
    green: 'text-emerald-600 bg-emerald-50 dark:bg-emerald-950/30',
    purple: 'text-violet-600 bg-violet-50 dark:bg-violet-950/30',
    red: 'text-red-600 bg-red-50 dark:bg-red-950/30',
  };

  return (
    <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} transition={{ delay }}>
      <Panel hover className="p-5">
        <div className={`inline-flex rounded-2xl p-2.5 ${colorClasses[color] || colorClasses.blue}`}>
          {Icon && <Icon size={20} />}
        </div>
        <p className="mt-4 text-sm text-slate-500 dark:text-slate-400">{title}</p>
        <p className="mt-1 text-2xl font-semibold text-slate-950 dark:text-slate-50">
          {value}{unit && <span className="ml-1 text-lg font-normal text-slate-500">{unit}</span>}
        </p>
        <p className="mt-2 text-xs text-slate-400">{description}</p>
      </Panel>
    </motion.div>
  );
}
