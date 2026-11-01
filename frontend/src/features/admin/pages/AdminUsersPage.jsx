import { useEffect, useState } from 'react';
import { Search, Users } from 'lucide-react';
import { Alert } from '../../../components/ui/Alert.jsx';
import { Button } from '../../../components/ui/Button.jsx';
import { Panel } from '../../../components/layout/ui/Panel.jsx';
import { adminService } from '../services/adminService.js';

const PAGE_SIZE = 20;

export function AdminUsersPage() {
  const [usersPage, setUsersPage] = useState(null);
  const [page, setPage] = useState(0);
  const [searchText, setSearchText] = useState('');
  const [query, setQuery] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let active = true;
    setLoading(true);
    setError('');
    adminService.getUsers({ page, size: PAGE_SIZE, ...(query ? { query } : {}) })
      .then((result) => { if (active) setUsersPage(result); })
      .catch((requestError) => { if (active) setError(requestError.response?.data?.message || 'Unable to load users.'); })
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
          <h1 className="text-2xl font-semibold text-slate-950 dark:text-slate-50">Users</h1>
          <p className="mt-1 text-sm text-slate-600 dark:text-slate-400">Browse registered accounts. This API is read-only.</p>
        </div>
        <form className="flex gap-2" onSubmit={search} role="search">
          <label className="sr-only" htmlFor="admin-user-query">Search users</label>
          <input
            id="admin-user-query"
            value={searchText}
            onChange={(event) => setSearchText(event.target.value)}
            placeholder="Name or email"
            className="min-h-11 w-full rounded-lg border border-slate-300 bg-white px-3 text-sm dark:border-slate-700 dark:bg-slate-900 dark:text-slate-100 sm:w-64"
          />
          <Button type="submit" aria-label="Search users"><Search size={18} /></Button>
        </form>
      </header>
      {error && <Alert type="error">{error}</Alert>}
      <Panel className="overflow-hidden p-0">
        {loading ? (
          <div className="p-6 text-sm text-slate-600 dark:text-slate-400">Loading users...</div>
        ) : usersPage?.content?.length ? (
          <div className="overflow-x-auto">
            <table className="w-full min-w-[760px] text-left text-sm">
              <thead className="bg-slate-50 text-xs uppercase text-slate-600 dark:bg-slate-800 dark:text-slate-300">
                <tr>
                  <th className="px-5 py-3 font-medium">User</th>
                  <th className="px-5 py-3 font-medium">Role</th>
                  <th className="px-5 py-3 font-medium">Account</th>
                  <th className="px-5 py-3 font-medium">Email verified</th>
                  <th className="px-5 py-3 font-medium">Joined</th>
                  <th className="px-5 py-3 font-medium">Last login</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-200 dark:divide-slate-800">
                {usersPage.content.map((user) => (
                  <tr key={user.id}>
                    <td className="px-5 py-4">
                      <p className="font-medium text-slate-900 dark:text-slate-100">{user.fullName}</p>
                      <p className="mt-1 text-xs text-slate-600 dark:text-slate-400">{user.email}</p>
                    </td>
                    <td className="px-5 py-4">{user.role}</td>
                    <td className="px-5 py-4">{user.accountStatus}</td>
                    <td className="px-5 py-4">{user.emailVerified ? 'Yes' : 'No'}</td>
                    <td className="px-5 py-4">{user.createdAt ? new Date(user.createdAt).toLocaleDateString() : '-'}</td>
                    <td className="px-5 py-4">{user.lastLoginAt ? new Date(user.lastLoginAt).toLocaleString() : 'Never'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <div className="flex items-center gap-3 p-6 text-sm text-slate-600 dark:text-slate-400">
            <Users size={18} aria-hidden="true" /> No users found.
          </div>
        )}
        {!loading && usersPage && (
          <footer className="flex items-center justify-between gap-3 border-t border-slate-200 px-5 py-3 dark:border-slate-800">
            <p className="text-sm text-slate-600 dark:text-slate-400">
              {usersPage.totalElements ?? 0} users | Page {(usersPage.number ?? page) + 1} of {Math.max(usersPage.totalPages ?? 1, 1)}
            </p>
            <div className="flex gap-2">
              <Button variant="secondary" disabled={usersPage.first ?? page === 0} onClick={() => setPage((current) => Math.max(0, current - 1))}>Previous</Button>
              <Button variant="secondary" disabled={usersPage.last ?? true} onClick={() => setPage((current) => current + 1)}>Next</Button>
            </div>
          </footer>
        )}
      </Panel>
    </section>
  );
}