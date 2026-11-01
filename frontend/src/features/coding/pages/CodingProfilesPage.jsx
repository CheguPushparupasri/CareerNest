import { useEffect, useMemo, useState } from 'react';
import { motion } from 'framer-motion';
import { Plus, Search, Code2, Trash2, Edit, X, Activity } from 'lucide-react';
import { codingService } from '../services/codingService.js';
import { timelineService } from '../../timeline/services/timelineService.js';

export function CodingProfilesPage() {
  const [items, setItems] = useState([]);
  const [query, setQuery] = useState('');
  const [platform, setPlatform] = useState('');
  const [loading, setLoading] = useState(true);
  const [editingId, setEditingId] = useState(null);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');
  const [form, setForm] = useState({ platform: 'GitHub', username: '', profileUrl: '', rating: '', problemsSolved: '', streak: '', badgeCount: '' });
  const [practice, setPractice] = useState({ title: '', problems: 1, difficulty: 'MIXED', practicedAt: new Date().toISOString().slice(0, 10) });

  useEffect(() => { loadData(); }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      setError('');
      const data = await codingService.getAll({ page: 0, size: 20 });
      setItems(data.content || []);
    } catch (error) {
      setError(error.response?.data?.message || 'Unable to load coding profiles.');
    } finally {
      setLoading(false);
    }
  };

  const filteredItems = useMemo(() => {
    return items.filter((item) => {
      const haystack = `${item.platform} ${item.username}`.toLowerCase();
      const matchesQuery = !query || haystack.includes(query.toLowerCase());
      const matchesPlatform = !platform || item.platform === platform;
      return matchesQuery && matchesPlatform;
    });
  }, [items, query, platform]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setMessage('');
    const payload = { ...form, rating: Number(form.rating) || 0, problemsSolved: Number(form.problemsSolved) || 0, streak: Number(form.streak) || 0, badgeCount: Number(form.badgeCount) || 0 };
    try {
      if (editingId) await codingService.update(editingId, payload);
      else await codingService.create(payload);
      setMessage(editingId ? 'Coding profile updated.' : 'Coding profile added.');
      setEditingId(null);
      setForm({ platform: 'GitHub', username: '', profileUrl: '', rating: '', problemsSolved: '', streak: '', badgeCount: '' });
      await loadData();
    } catch (error) {
      setError(error.response?.data?.message || 'Unable to save coding profile.');
    }
  };

  const startEdit = (item) => {
    setEditingId(item.id);
    setForm({ platform: item.platform, username: item.username, profileUrl: item.profileUrl || '',
      rating: item.rating ?? '', problemsSolved: item.problemsSolved ?? '', streak: item.streak ?? '', badgeCount: item.badgeCount ?? '' });
    setError('');
  };

  const logPractice = async (event) => {
    event.preventDefault();
    setError('');
    setMessage('');
    try {
      await timelineService.create({
        entryType: 'CODING_PRACTICE',
        title: practice.title || `${practice.difficulty} coding practice`,
        description: `Solved ${Number(practice.problems)} problem(s); difficulty: ${practice.difficulty}.`,
        organization: 'Manual practice log',
        entryDate: practice.practicedAt,
      });
      setMessage('Practice session saved to your career timeline.');
      setPractice({ ...practice, title: '', problems: 1 });
    } catch (requestError) {
      setError(requestError.response?.data?.message || 'Unable to save practice session.');
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this coding profile?')) return;
    try {
      await codingService.delete(id);
      setMessage('Coding profile deleted.');
      await loadData();
    } catch (error) {
      setError(error.response?.data?.message || 'Unable to delete coding profile.');
    }
  };

  return (
    <section className="space-y-6">
      <div className="rounded-xl border border-slate-200 bg-white p-4 shadow-soft dark:border-slate-800 dark:bg-slate-900">
        {error && <p role="alert" className="mb-3 text-sm text-red-600">{error}</p>}
        {message && <p role="status" className="mb-3 text-sm text-green-700">{message}</p>}
        <h2 className="mb-4 text-xl font-semibold">{editingId ? 'Edit coding profile' : 'Coding profiles'}</h2>
        <form onSubmit={handleSubmit} className="grid gap-3 md:grid-cols-2 xl:grid-cols-3">
          <select className="rounded-lg border border-slate-300 px-3 py-2" value={form.platform} onChange={(e) => setForm({ ...form, platform: e.target.value })}>
            <option value="GitHub">GitHub</option>
            <option value="LeetCode">LeetCode</option>
            <option value="HackerRank">HackerRank</option>
            <option value="CodeChef">CodeChef</option>
            <option value="Codeforces">Codeforces</option>
          </select>
          <input className="rounded-lg border border-slate-300 px-3 py-2" placeholder="Username" value={form.username} onChange={(e) => setForm({ ...form, username: e.target.value })} required />
          <input className="rounded-lg border border-slate-300 px-3 py-2" placeholder="Profile URL" value={form.profileUrl} onChange={(e) => setForm({ ...form, profileUrl: e.target.value })} />
          <input type="number" className="rounded-lg border border-slate-300 px-3 py-2" placeholder="Rating" value={form.rating} onChange={(e) => setForm({ ...form, rating: e.target.value })} />
          <input type="number" className="rounded-lg border border-slate-300 px-3 py-2" placeholder="Problems solved" value={form.problemsSolved} onChange={(e) => setForm({ ...form, problemsSolved: e.target.value })} />
          <input type="number" className="rounded-lg border border-slate-300 px-3 py-2" placeholder="Streak" value={form.streak} onChange={(e) => setForm({ ...form, streak: e.target.value })} />
          <input type="number" className="rounded-lg border border-slate-300 px-3 py-2" placeholder="Badge count" value={form.badgeCount} onChange={(e) => setForm({ ...form, badgeCount: e.target.value })} />
          <div className="md:col-span-2 xl:col-span-3 flex justify-end">
            <button type="submit" className="flex items-center gap-2 rounded-lg bg-brand-600 px-4 py-2 text-white">
              {editingId ? <Edit size={16} /> : <Plus size={16} />} {editingId ? 'Save profile' : 'Add Profile'}
            </button>
          </div>
          {editingId && <button type="button" className="md:col-span-2 xl:col-span-3 inline-flex items-center gap-2 justify-self-end text-sm text-slate-600" onClick={() => { setEditingId(null); setForm({ platform: 'GitHub', username: '', profileUrl: '', rating: '', problemsSolved: '', streak: '', badgeCount: '' }); }}><X size={15} /> Cancel edit</button>}
        </form>
      </div>

      <div className="rounded-xl border border-slate-200 bg-white p-4 shadow-soft dark:border-slate-800 dark:bg-slate-900">
        <h2 className="mb-4 flex items-center gap-2 text-lg font-semibold"><Activity size={18} /> Log coding practice</h2>
        <form onSubmit={logPractice} className="grid gap-3 md:grid-cols-2 xl:grid-cols-4">
          <input className="rounded-lg border border-slate-300 px-3 py-2" placeholder="Session title (optional)" value={practice.title} onChange={(event) => setPractice({ ...practice, title: event.target.value })} />
          <input aria-label="Problems solved" type="number" min="1" max="1000" required className="rounded-lg border border-slate-300 px-3 py-2" value={practice.problems} onChange={(event) => setPractice({ ...practice, problems: Number(event.target.value) })} />
          <select aria-label="Difficulty" className="rounded-lg border border-slate-300 px-3 py-2" value={practice.difficulty} onChange={(event) => setPractice({ ...practice, difficulty: event.target.value })}>
            <option value="EASY">Easy</option><option value="MEDIUM">Medium</option><option value="HARD">Hard</option><option value="MIXED">Mixed</option>
          </select>
          <input aria-label="Practice date" type="date" required className="rounded-lg border border-slate-300 px-3 py-2" value={practice.practicedAt} onChange={(event) => setPractice({ ...practice, practicedAt: event.target.value })} />
          <button type="submit" className="rounded-lg bg-brand-600 px-4 py-2 text-white md:col-span-2 xl:col-span-4">Save practice session</button>
        </form>
      </div>

      <div className="rounded-xl border border-slate-200 bg-white p-4 shadow-soft dark:border-slate-800 dark:bg-slate-900">
        <div className="mb-4 flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
          <div className="flex gap-2">
            <div className="flex items-center gap-2 rounded-lg border border-slate-300 px-3 py-2">
              <Search size={16} />
              <input className="bg-transparent outline-none" placeholder="Search" value={query} onChange={(e) => setQuery(e.target.value)} />
            </div>
            <select className="rounded-lg border border-slate-300 px-3 py-2" value={platform} onChange={(e) => setPlatform(e.target.value)}>
              <option value="">All platforms</option>
              <option value="GitHub">GitHub</option>
              <option value="LeetCode">LeetCode</option>
              <option value="HackerRank">HackerRank</option>
              <option value="CodeChef">CodeChef</option>
              <option value="Codeforces">Codeforces</option>
            </select>
          </div>
        </div>

        {loading ? (
          <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">{[1,2,3].map((i)=><div key={i} className="h-36 animate-pulse rounded-xl bg-slate-200" />)}</div>
        ) : filteredItems.length === 0 ? (
          <div className="rounded-xl border border-dashed border-slate-300 p-8 text-center text-slate-500">No coding profiles found.</div>
        ) : (
          <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
            {filteredItems.map((item) => (
              <motion.div key={item.id} initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} className="rounded-xl border border-slate-200 p-4 dark:border-slate-800">
                <div className="mb-3 flex items-start justify-between gap-2">
                  <div>
                    <h3 className="font-semibold">{item.platform}</h3>
                    <p className="text-sm text-slate-500">{item.username}</p>
                  </div>
                  <span className="rounded-full bg-brand-50 px-2 py-1 text-xs text-brand-700">{item.rating || 0}</span>
                </div>
                <div className="grid gap-2 text-sm text-slate-600">
                  <div className="flex items-center gap-2"><Code2 size={14} />{item.problemsSolved || 0} problems solved</div>
                  <div>Streak: {item.streak || 0}</div>
                  <div>Badges: {item.badgeCount || 0}</div>
                </div>
                <div className="mt-4 flex justify-end gap-2">
                  <button type="button" className="rounded-lg bg-slate-100 p-2" aria-label={`Edit ${item.platform} profile`} onClick={() => startEdit(item)}><Edit size={16} /></button>
                  <button className="rounded-lg bg-slate-100 p-2" onClick={() => handleDelete(item.id)}><Trash2 size={16} /></button>
                </div>
              </motion.div>
            ))}
          </div>
        )}
      </div>
    </section>
  );
}
