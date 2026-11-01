import { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import { healthService } from '../services/healthService.js';

export function CareerHealthPage() {
  const [health, setHealth] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const load = async () => {
      try {
        setLoading(true);
        const data = await healthService.getHealth();
        setHealth(data);
      } catch (error) {
        console.error('Failed to load career health', error);
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  if (loading) {
    return <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-soft dark:border-slate-800 dark:bg-slate-900">Loading career health...</div>;
  }

  return (
    <section className="space-y-6">
      <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-soft dark:border-slate-800 dark:bg-slate-900">
        <h2 className="text-2xl font-semibold">Career health score</h2>
        <p className="mt-2 text-slate-500">A personalized view of your readiness, learning momentum, and portfolio strength.</p>
        <div className="mt-6 grid gap-4 lg:grid-cols-[1.2fr_0.8fr]">
          <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} className="rounded-2xl bg-gradient-to-br from-brand-600 to-indigo-600 p-6 text-white">
            <p className="text-sm uppercase tracking-[0.2em] text-brand-100">Overall score</p>
            <div className="mt-3 text-5xl font-bold">{health?.overallScore ?? 0}</div>
            <p className="mt-2 text-brand-100">Placement readiness: {health?.placementReadiness ?? 0}</p>
          </motion.div>
          <div className="grid gap-3">
            {[
              ['Resume', health?.resumeScore ?? 0],
              ['Projects', health?.projectStrength ?? 0],
              ['Learning', health?.learningProgress ?? 0],
              ['Coding', health?.codingConsistency ?? 0],
            ].map(([label, value]) => (
              <div key={label} className="rounded-xl border border-slate-200 p-4 dark:border-slate-800">
                <div className="flex items-center justify-between text-sm text-slate-600">
                  <span>{label}</span>
                  <span className="font-semibold text-slate-950 dark:text-slate-50">{value}</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      <div className="grid gap-6 lg:grid-cols-2">
        <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-soft dark:border-slate-800 dark:bg-slate-900">
          <h3 className="font-semibold">Strengths</h3>
          <ul className="mt-3 space-y-2 text-sm text-slate-600">
            {(health?.strengths ?? []).map((item) => <li key={item}>• {item}</li>)}
          </ul>
        </div>
        <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-soft dark:border-slate-800 dark:bg-slate-900">
          <h3 className="font-semibold">Weaknesses</h3>
          <ul className="mt-3 space-y-2 text-sm text-slate-600">
            {(health?.weaknesses ?? []).map((item) => <li key={item}>• {item}</li>)}
          </ul>
        </div>
      </div>

      <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-soft dark:border-slate-800 dark:bg-slate-900">
        <h3 className="font-semibold">Roadmap</h3>
        <ul className="mt-3 space-y-2 text-sm text-slate-600">
          {(health?.roadmap ?? []).map((item) => <li key={item}>• {item}</li>)}
        </ul>
      </div>
    </section>
  );
}
