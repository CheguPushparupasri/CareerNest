import { useEffect, useState } from 'react';
import { Activity } from 'lucide-react';
import { Alert } from '../../../components/ui/Alert.jsx';
import { Panel } from '../../../components/layout/ui/Panel.jsx';
import { adminService } from '../services/adminService.js';

const totals = [
  ['Projects', 'totalProjects'],
  ['Resumes', 'totalResumes'],
  ['Certifications', 'totalCertifications'],
  ['Applications', 'totalApplications'],
  ['Learning courses', 'totalLearningCourses'],
  ['Coding profiles', 'totalCodingProfiles'],
];

export function AdminAnalyticsPage() {
  const [overview, setOverview] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    adminService.getOverview()
      .then(setOverview)
      .catch((requestError) => setError(requestError.response?.data?.message || 'Unable to load analytics.'))
      .finally(() => setLoading(false));
  }, []);

  const maximum = overview ? Math.max(1, ...totals.map(([, key]) => Number(overview[key] ?? 0))) : 1;

  return (
    <section className="space-y-6">
      <header>
        <h1 className="text-2xl font-semibold text-slate-950 dark:text-slate-50">Platform analytics</h1>
        <p className="mt-1 text-sm text-slate-600 dark:text-slate-400">Current module totals. Historical trends are not provided by the API.</p>
      </header>
      {error && <Alert type="error">{error}</Alert>}
      <Panel className="p-5">
        {loading ? <p className="text-sm text-slate-600 dark:text-slate-400">Loading analytics...</p> : overview ? (
          <div className="space-y-5">
            {totals.map(([label, key]) => {
              const value = Number(overview[key] ?? 0);
              return (
                <div key={key}>
                  <div className="mb-2 flex items-center justify-between gap-4 text-sm">
                    <span className="text-slate-700 dark:text-slate-300">{label}</span>
                    <span className="font-semibold tabular-nums text-slate-950 dark:text-slate-50">{value.toLocaleString()}</span>
                  </div>
                  <div className="h-2 overflow-hidden rounded-full bg-slate-100 dark:bg-slate-800" role="img" aria-label={`${label}: ${value}`}>
                    <div className="h-full rounded-full bg-secondary-600" style={{ width: `${(value / maximum) * 100}%` }} />
                  </div>
                </div>
              );
            })}
            <p className="flex items-center gap-2 border-t border-slate-200 pt-4 text-xs text-slate-500 dark:border-slate-800 dark:text-slate-400">
              <Activity size={15} aria-hidden="true" /> Counts are live aggregate values from the admin overview endpoint.
            </p>
          </div>
        ) : null}
      </Panel>
    </section>
  );
}