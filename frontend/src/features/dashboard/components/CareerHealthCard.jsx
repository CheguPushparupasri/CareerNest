import { motion } from 'framer-motion';
import { TrendingUp, AlertCircle } from 'lucide-react';
import { Panel } from '../../../components/layout/ui/Panel.jsx';

export function CareerHealthCard({ health, delay = 0 }) {
  const getScoreColor = (score) => {
    if (score >= 80) return 'text-green-600';
    if (score >= 60) return 'text-amber-600';
    return 'text-red-600';
  };

  const getScoreBg = (score) => {
    if (score >= 80) return 'bg-green-50 dark:bg-green-950/30';
    if (score >= 60) return 'bg-amber-50 dark:bg-amber-950/30';
    return 'bg-red-50 dark:bg-red-950/30';
  };

  return (
    <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} transition={{ delay }}>
      <Panel className="p-5">
        <div className="flex items-center justify-between">
          <div>
            <p className="text-sm font-medium text-slate-500">Career health</p>
            <h3 className="text-lg font-semibold text-slate-950 dark:text-slate-50">Placement readiness</h3>
          </div>
          <div className={`inline-flex rounded-2xl p-2.5 ${getScoreBg(health.overall)}`}>
            <TrendingUp size={20} className={getScoreColor(health.overall)} />
          </div>
        </div>

        <div className="mt-5 flex items-end gap-3">
          <span className={`text-4xl font-semibold ${getScoreColor(health.overall)}`}>{health.overall}</span>
          <span className="text-sm text-slate-500">/ 100</span>
        </div>
        <div className="mt-3 h-2.5 w-full overflow-hidden rounded-full bg-slate-100 dark:bg-slate-800">
          <motion.div initial={{ width: 0 }} animate={{ width: `${health.overall}%` }} transition={{ duration: 1, delay: delay + 0.2 }} className={`h-full rounded-full ${health.overall >= 80 ? 'bg-emerald-500' : health.overall >= 60 ? 'bg-amber-500' : 'bg-red-500'}`} />
        </div>

        <div className="mt-4 grid grid-cols-2 gap-3 text-sm">
          <div className="rounded-2xl bg-slate-50 p-3 dark:bg-slate-800/80">
            <p className="text-xs text-slate-500">Resume</p>
            <p className="mt-1 font-semibold text-slate-950 dark:text-slate-50">{health.resumeScore}%</p>
          </div>
          <div className="rounded-2xl bg-slate-50 p-3 dark:bg-slate-800/80">
            <p className="text-xs text-slate-500">Skills</p>
            <p className="mt-1 font-semibold text-slate-950 dark:text-slate-50">{health.skillScore}%</p>
          </div>
          <div className="rounded-2xl bg-slate-50 p-3 dark:bg-slate-800/80">
            <p className="text-xs text-slate-500">Placement</p>
            <p className="mt-1 font-semibold text-slate-950 dark:text-slate-50">{health.placementReadiness}%</p>
          </div>
          <div className="rounded-2xl bg-slate-50 p-3 dark:bg-slate-800/80">
            <p className="text-xs text-slate-500">Learning</p>
            <p className="mt-1 font-semibold text-slate-950 dark:text-slate-50">{health.learningProgress}%</p>
          </div>
        </div>

        {health.recommendations && health.recommendations.length > 0 && (
          <div className="mt-4 space-y-2">
            <p className="text-xs font-medium uppercase tracking-[0.25em] text-slate-400">Recommendations</p>
            {health.recommendations.slice(0, 2).map((rec, index) => (
              <div key={index} className="flex items-start gap-2 rounded-2xl bg-slate-50 p-2.5 dark:bg-slate-800/80">
                <AlertCircle size={14} className="mt-0.5 shrink-0 text-amber-500" />
                <p className="text-sm text-slate-600 dark:text-slate-400">{rec}</p>
              </div>
            ))}
          </div>
        )}
      </Panel>
    </motion.div>
  );
}
