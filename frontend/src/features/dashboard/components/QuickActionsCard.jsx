import { motion } from 'framer-motion';
import { ArrowRight } from 'lucide-react';
import { Link } from 'react-router-dom';
import { Panel } from '../../../components/layout/ui/Panel.jsx';

export function QuickActionsCard({ actions, delay = 0 }) {
  return (
    <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} transition={{ delay }}>
      <Panel className="p-5">
        <div className="flex items-center justify-between">
          <div>
            <p className="text-sm font-medium text-slate-500">Launchpad</p>
            <h3 className="text-lg font-semibold text-slate-950 dark:text-slate-50">Quick actions</h3>
          </div>
        </div>

        <div className="mt-4 space-y-2">
          {actions && actions.length > 0 ? actions.map((action, index) => (
            <Link key={index} to={action.link} className="flex items-center justify-between rounded-2xl border border-slate-200/80 p-3 transition-colors hover:bg-slate-50 dark:border-slate-800 dark:hover:bg-slate-800">
              <div className="flex items-center gap-3">
                <div className="rounded-2xl bg-brand-50 p-2 text-brand-600 dark:bg-brand-950/30">
                  {action.icon && typeof action.icon === 'string' ? (
                    <span className="text-lg">{action.icon}</span>
                  ) : action.icon ? (
                    <action.icon size={18} />
                  ) : (
                    <span className="text-lg">⚡</span>
                  )}
                </div>
                <div>
                  <p className="text-sm font-medium text-slate-950 dark:text-slate-50">{action.title}</p>
                  <p className="text-xs text-slate-500">{action.description}</p>
                </div>
              </div>
              <ArrowRight size={16} className="text-slate-400" />
            </Link>
          )) : (
            <p className="text-sm text-slate-500">No quick actions available</p>
          )}
        </div>
      </Panel>
    </motion.div>
  );
}
