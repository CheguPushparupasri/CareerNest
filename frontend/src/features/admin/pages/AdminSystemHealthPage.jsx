import { useEffect, useState } from 'react';
import { Activity, CheckCircle2, XCircle } from 'lucide-react';
import { Alert } from '../../../components/ui/Alert.jsx';
import { Panel } from '../../../components/layout/ui/Panel.jsx';
import { adminService } from '../services/adminService.js';

export function AdminSystemHealthPage() {
  const [health, setHealth] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    adminService.getHealth()
      .then(setHealth)
      .catch((requestError) => setError(requestError.response?.data?.message || 'Unable to reach the health endpoint.'))
      .finally(() => setLoading(false));
  }, []);

  const isUp = health?.status === 'UP';

  return (
    <section className="space-y-6">
      <header>
        <h1 className="text-2xl font-semibold text-slate-950 dark:text-slate-50">System health</h1>
        <p className="mt-1 text-sm text-slate-600 dark:text-slate-400">Status reported by the public API health check.</p>
      </header>
      {error && <Alert type="error">{error}</Alert>}
      <Panel className="flex items-start gap-4 p-5">
        {loading ? <Activity size={21} className="animate-pulse text-slate-500" aria-hidden="true" />
          : isUp ? <CheckCircle2 size={21} className="text-green-600" aria-hidden="true" />
            : <XCircle size={21} className="text-red-600" aria-hidden="true" />}
        <div>
          <h2 className="font-medium text-slate-950 dark:text-slate-50">API status</h2>
          <p className="mt-1 text-sm text-slate-600 dark:text-slate-400">
            {loading ? 'Checking...' : health ? health.status : 'Unavailable'}
          </p>
          <p className="mt-3 text-xs text-slate-500 dark:text-slate-400">
            The current health endpoint reports API availability only; it does not expose database, memory, or server-load metrics.
          </p>
        </div>
      </Panel>
    </section>
  );
}