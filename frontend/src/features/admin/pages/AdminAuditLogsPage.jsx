import { useEffect, useState } from 'react';
import { Search, ShieldCheck } from 'lucide-react';
import { Alert } from '../../../components/ui/Alert.jsx';
import { Button } from '../../../components/ui/Button.jsx';
import { Panel } from '../../../components/layout/ui/Panel.jsx';
import { adminService } from '../services/adminService.js';

const PAGE_SIZE = 20;

export function AdminAuditLogsPage() {
  const [logsPage, setLogsPage] = useState(null);
  const [page, setPage] = useState(0);
  const [searchText, setSearchText] = useState('');
  const [query, setQuery] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let active = true;
    setLoading(true);
    setError('');
    adminService.getAuditLogs({ page, size: PAGE_SIZE, ...(query ? { query } : {}) })
      .then((result) => { if (active) setLogsPage(result); })
      .catch((requestError) => { if (active) setError(requestError.response?.data?.message || 'Unable to load audit logs.'); })
      .finally(() => { if (active) setLoading(false); });
    return () => { active = false; };
  }, [page, query]);

  const search = (event) => {
    event.preventDefault();
    setPage(0);
    setQuery(searchText.trim());
  };

  return (
    <section className="space-y-6">
      <header className="flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <h1 className="text-2xl font-semibold text-slate-950 dark:text-slate-50">Audit logs</h1>
          <p className="mt-1 text-sm text-slate-600 dark:text-slate-400">Search recorded platform events.</p>
        </div>
        <form className="flex gap-2" onSubmit={search} role="search">
          <label className="sr-only" htmlFor="admin-audit-query">Search audit logs</label>
          <input
            id="admin-audit-query"
            value={searchText}
            onChange={(event) => setSearchText(event.target.value)}
            placeholder="Event, user, or description"
            className="min-h-11 w-full rounded-lg border border-slate-300 bg-white px-3 text-sm dark:border-slate-700 dark:bg-slate-900 dark:text-slate-100 sm:w-72"
          />
          <Button type="submit" aria-label="Search audit logs"><Search size={18} /></Button>
        </form>
      </header>
      {error && <Alert type="error">{error}</Alert>}
      <Panel className="overflow-hidden p-0">
        {loading ? (
          <p className="p-6 text-sm text-slate-600 dark:text-slate-400">Loading audit logs...</p>
        ) : logsPage?.content?.length ? (
          <div className="overflow-x-auto">
            <table className="w-full min-w-[820px] text-left text-sm">
              <thead className="bg-slate-50 text-xs uppercase text-slate-600 dark:bg-slate-800 dark:text-slate-300">
                <tr>
                  <th className="px-5 py-3 font-medium">Event</th>
                  <th className="px-5 py-3 font-medium">User</th>
                  <th className="px-5 py-3 font-medium">Actor</th>
                  <th className="px-5 py-3 font-medium">IP address</th>
                  <th className="px-5 py-3 font-medium">Time</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-200 dark:divide-slate-800">
                {logsPage.content.map((log) => (
                  <tr key={log.id}>
                    <td className="px-5 py-4">
                      <p className="font-medium text-slate-900 dark:text-slate-100">{log.eventType}</p>
                      {log.eventDescription && <p className="mt-1 max-w-md text-xs text-slate-600 dark:text-slate-400">{log.eventDescription}</p>}
                    </td>
                    <td className="px-5 py-4">{log.userEmail || '-'}</td>
                    <td className="px-5 py-4">{log.actorEmail || '-'}</td>
                    <td className="px-5 py-4">{log.ipAddress || '-'}</td>
                    <td className="whitespace-nowrap px-5 py-4">{log.createdAt ? new Date(log.createdAt).toLocaleString() : '-'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <div className="flex items-center gap-3 p-6 text-sm text-slate-600 dark:text-slate-400">
            <ShieldCheck size={18} aria-hidden="true" /> No audit events found.
          </div>
        )}
        {!loading && logsPage && (
          <footer className="flex items-center justify-between gap-3 border-t border-slate-200 px-5 py-3 dark:border-slate-800">
            <p className="text-sm text-slate-600 dark:text-slate-400">
              {logsPage.totalElements ?? 0} events | Page {(logsPage.number ?? page) + 1} of {Math.max(logsPage.totalPages ?? 1, 1)}
            </p>
            <div className="flex gap-2">
              <Button variant="secondary" disabled={logsPage.first ?? page === 0} onClick={() => setPage((current) => Math.max(0, current - 1))}>Previous</Button>
              <Button variant="secondary" disabled={logsPage.last ?? true} onClick={() => setPage((current) => current + 1)}>Next</Button>
            </div>
          </footer>
        )}
      </Panel>
    </section>
  );
}