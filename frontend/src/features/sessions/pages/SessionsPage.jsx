import { useEffect, useState } from 'react';
import { Button } from '../../../components/ui/Button.jsx';
import { Alert } from '../../../components/ui/Alert.jsx';
import { authService } from '../../auth/services/authService.js';

export function SessionsPage() {
  const [sessions, setSessions] = useState([]);
  const [error, setError] = useState('');

  const load = () => authService.sessions().then(setSessions).catch((err) => setError(err.response?.data?.message || 'Unable to load sessions'));

  useEffect(() => {
    load();
  }, []);

  const revoke = async (id) => {
    await authService.revokeSession(id);
    load();
  };

  return (
    <section className="rounded-xl border border-slate-200 bg-white p-5 shadow-soft dark:border-slate-800 dark:bg-slate-900">
      <h1 className="text-2xl font-bold">Active sessions</h1>
      <p className="mt-1 text-sm text-slate-500 dark:text-slate-400">Review devices that can refresh your CareerNest login.</p>
      {error ? <div className="mt-4"><Alert type="error">{error}</Alert></div> : null}
      <div className="mt-6 space-y-3">
        {sessions.map((session) => (
          <div key={session.id} className="flex flex-col justify-between gap-3 rounded-lg border border-slate-200 p-4 dark:border-slate-800 md:flex-row md:items-center">
            <div>
              <p className="font-semibold">{session.deviceName}</p>
              <p className="text-sm text-slate-500">{session.ipAddress} · Last used {new Date(session.lastUsedAt).toLocaleString()}</p>
            </div>
            <Button variant="danger" onClick={() => revoke(session.id)}>Revoke</Button>
          </div>
        ))}
        {!sessions.length ? <p className="text-sm text-slate-500">No active sessions found.</p> : null}
      </div>
    </section>
  );
}
