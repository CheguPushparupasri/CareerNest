import { motion } from 'framer-motion';
import { BookOpen } from 'lucide-react';
import { Panel } from '../../../components/layout/ui/Panel.jsx';

export function LearningProgressCard({ progress, delay = 0 }) {
  return (
    <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} transition={{ delay }}>
      <Panel className="p-5">
        <div className="flex items-center gap-2">
          <div className="rounded-2xl bg-brand-50 p-2 text-brand-600 dark:bg-brand-950/30">
            <BookOpen size={18} />
          </div>
          <div>
            <p className="text-sm font-medium text-slate-500">Growth track</p>
            <h3 className="text-lg font-semibold text-slate-950 dark:text-slate-50">Learning progress</h3>
          </div>
        </div>

        <div className="mt-4 space-y-3">
          {progress.map((item, index) => (
            <div key={item.skill}>
              <div className="flex items-center justify-between text-sm">
                <span className="text-slate-600 dark:text-slate-400">{item.skill}</span>
                <span className="font-medium text-slate-900 dark:text-slate-100">{item.progress}%</span>
              </div>
              <div className="mt-2 h-2 w-full overflow-hidden rounded-full bg-slate-100 dark:bg-slate-800">
                <motion.div initial={{ width: 0 }} animate={{ width: `${item.progress}%` }} transition={{ duration: 0.8, delay: delay + index * 0.1 }} className="h-full rounded-full bg-gradient-to-r from-brand-600 to-cyan-500" />
              </div>
            </div>
          ))}
        </div>
      </Panel>
    </motion.div>
  );
}
