import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import { motion } from 'framer-motion';
import { Panel } from '../../../components/layout/ui/Panel.jsx';

export function ActivityChart({ data, delay = 0 }) {
  return (
    <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} transition={{ delay }}>
      <Panel className="p-5">
        <div className="flex items-center justify-between">
          <div>
            <p className="text-sm font-medium text-slate-500">Momentum</p>
            <h3 className="text-lg font-semibold text-slate-950 dark:text-slate-50">Activity trend</h3>
          </div>
          <div className="rounded-full bg-brand-50 px-3 py-1 text-sm font-medium text-brand-600 dark:bg-brand-950/30">+12.4%</div>
        </div>
        <div className="mt-4 h-52">
          <ResponsiveContainer width="100%" height="100%">
            <BarChart data={data}>
              <CartesianGrid strokeDasharray="3 3" className="stroke-slate-200 dark:stroke-slate-800" />
              <XAxis dataKey="label" className="text-xs text-slate-500" tick={{ fill: 'currentColor' }} />
              <YAxis className="text-xs text-slate-500" tick={{ fill: 'currentColor' }} />
              <Tooltip contentStyle={{ backgroundColor: 'rgb(15 23 42)', border: '1px solid rgb(51 65 85)', borderRadius: '0.75rem' }} itemStyle={{ color: 'rgb(226 232 240)' }} />
              <Bar dataKey="value" fill="url(#activityGradient)" radius={[6, 6, 0, 0]} />
              <defs>
                <linearGradient id="activityGradient" x1="0" x2="0" y1="0" y2="1">
                  <stop offset="0%" stopColor="#4f46e5" />
                  <stop offset="100%" stopColor="#06b6d4" />
                </linearGradient>
              </defs>
            </BarChart>
          </ResponsiveContainer>
        </div>
      </Panel>
    </motion.div>
  );
}
