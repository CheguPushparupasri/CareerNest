import { useEffect, useMemo, useState } from 'react';
import { motion } from 'framer-motion';
import { Plus, Search, BookOpen, Trash2, Edit, X } from 'lucide-react';
import { learningService } from '../services/learningService.js';

export function LearningPage() {
  const [items, setItems] = useState([]);
  const [query, setQuery] = useState('');
  const [status, setStatus] = useState('');
  const [loading, setLoading] = useState(true);
  const [editingId, setEditingId] = useState(null);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');
  const [form, setForm] = useState({ title: '', provider: '', category: '', status: 'PLANNED', progress: 0, goal: '', notes: '' });

  useEffect(() => { loadData(); }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const data = await learningService.getAll({ page: 0, size: 20 });
      setItems(data.content || []);
    } catch (error) {
      setError(error.response?.data?.message || 'Unable to load courses.');
    } finally {
      setLoading(false);
    }
  };

  const filteredItems = useMemo(() => {
    return items.filter((item) => {
      const haystack = `${item.title} ${item.category} ${item.status}`.toLowerCase();
      const matchesQuery = !query || haystack.includes(query.toLowerCase());
      const matchesStatus = !status || item.status === status;
      return matchesQuery && matchesStatus;
    });
  }, [items, query, status]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setMessage('');
    try {
      if (editingId) await learningService.update(editingId, form);
      else await learningService.create(form);
      setMessage(editingId ? 'Course updated.' : 'Course added.');
      setEditingId(null);
      setForm({ title: '', provider: '', category: '', status: 'PLANNED', progress: 0, goal: '', notes: '' });
      await loadData();
    } catch (error) {
      setError(error.response?.data?.message || 'Unable to save course.');
    }
  };

  const startEdit = (item) => {
    setEditingId(item.id);
    setForm({ title: item.title, provider: item.provider || '', category: item.category || '',
      status: item.status, progress: item.progress ?? 0, goal: item.goal || '', notes: item.notes || '' });
    setError('');
    setMessage('');
  };

  const adjustProgress = async (item, amount) => {
    const progress = Math.max(0, Math.min(100, (item.progress || 0) + amount));
    const updated = { title: item.title, provider: item.provider, category: item.category,
      status: progress === 100 ? 'COMPLETED' : item.status === 'COMPLETED' ? 'IN_PROGRESS' : item.status,
      progress, goal: item.goal, notes: item.notes };
    try {
      await learningService.update(item.id, updated);
      setMessage('Course progress saved.');
      await loadData();
    } catch (requestError) {
      setError(requestError.response?.data?.message || 'Unable to update course progress.');
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this course?')) return;
    try {
      await learningService.delete(id);
      setMessage('Course deleted.');
      await loadData();
    } catch (error) {
      setError(error.response?.data?.message || 'Unable to delete course.');
    }
  };

  return (
    <section className="space-y-6">
      <div className="rounded-xl border border-slate-200 bg-white p-4 shadow-soft dark:border-slate-800 dark:bg-slate-900">
        {error && <p role="alert" className="mb-3 text-sm text-red-600">{error}</p>}
        {message && <p role="status" className="mb-3 text-sm text-green-700">{message}</p>}
        <h2 className="mb-4 text-xl font-semibold">{editingId ? 'Edit course' : 'Learning tracker'}</h2>
        <form onSubmit={handleSubmit} className="grid gap-3 md:grid-cols-2 xl:grid-cols-3">
          <input className="rounded-lg border border-slate-300 px-3 py-2" placeholder="Course title" value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} required />
          <input className="rounded-lg border border-slate-300 px-3 py-2" placeholder="Provider" value={form.provider} onChange={(e) => setForm({ ...form, provider: e.target.value })} />
          <input className="rounded-lg border border-slate-300 px-3 py-2" placeholder="Category" value={form.category} onChange={(e) => setForm({ ...form, category: e.target.value })} />
          <select className="rounded-lg border border-slate-300 px-3 py-2" value={form.status} onChange={(e) => setForm({ ...form, status: e.target.value })}>
            <option value="PLANNED">Planned</option>
            <option value="IN_PROGRESS">In Progress</option>
            <option value="COMPLETED">Completed</option>
          </select>
          <label className="space-y-1 text-sm">Progress (%)<input type="number" min="0" max="100" className="block w-full rounded-lg border border-slate-300 px-3 py-2" value={form.progress} onChange={(e) => setForm({ ...form, progress: Number(e.target.value) })} /></label>
          <input className="rounded-lg border border-slate-300 px-3 py-2" placeholder="Goal" value={form.goal} onChange={(e) => setForm({ ...form, goal: e.target.value })} />
          <textarea className="rounded-lg border border-slate-300 px-3 py-2 md:col-span-2 xl:col-span-3" placeholder="Notes" value={form.notes} onChange={(e) => setForm({ ...form, notes: e.target.value })} />
          <div className="md:col-span-2 xl:col-span-3 flex justify-end">
            <button type="submit" className="flex items-center gap-2 rounded-lg bg-brand-600 px-4 py-2 text-white">
              {editingId ? <Edit size={16} /> : <Plus size={16} />} {editingId ? 'Save course' : 'Add Course'}
            </button>
          </div>
          {editingId && <button type="button" className="md:col-span-2 xl:col-span-3 inline-flex items-center gap-2 justify-self-end text-sm text-slate-600" onClick={() => { setEditingId(null); setForm({ title: '', provider: '', category: '', status: 'PLANNED', progress: 0, goal: '', notes: '' }); }}><X size={15} /> Cancel edit</button>}
        </form>
      </div>

      <div className="rounded-xl border border-slate-200 bg-white p-4 shadow-soft dark:border-slate-800 dark:bg-slate-900">
        <div className="mb-4 flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
          <div className="flex gap-2">
            <div className="flex items-center gap-2 rounded-lg border border-slate-300 px-3 py-2">
              <Search size={16} />
              <input className="bg-transparent outline-none" placeholder="Search" value={query} onChange={(e) => setQuery(e.target.value)} />
            </div>
            <select className="rounded-lg border border-slate-300 px-3 py-2" value={status} onChange={(e) => setStatus(e.target.value)}>
              <option value="">All statuses</option>
              <option value="PLANNED">Planned</option>
              <option value="IN_PROGRESS">In Progress</option>
              <option value="COMPLETED">Completed</option>
            </select>
          </div>
        </div>

        {loading ? (
          <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">{[1,2,3].map((i)=><div key={i} className="h-36 animate-pulse rounded-xl bg-slate-200" />)}</div>
        ) : filteredItems.length === 0 ? (
          <div className="rounded-xl border border-dashed border-slate-300 p-8 text-center text-slate-500">No courses found.</div>
        ) : (
          <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
            {filteredItems.map((item) => (
              <motion.div key={item.id} initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} className="rounded-xl border border-slate-200 p-4 dark:border-slate-800">
                <div className="mb-3 flex items-start justify-between gap-2">
                  <div>
                    <h3 className="font-semibold">{item.title}</h3>
                    <p className="text-sm text-slate-500">{item.provider || 'Self paced'}</p>
                  </div>
                  <span className="rounded-full bg-brand-50 px-2 py-1 text-xs text-brand-700">{item.status}</span>
                </div>
                <p className="mb-3 text-sm text-slate-600">{item.category || 'General'}</p>
                <div className="mb-3 h-2 rounded-full bg-slate-100">
                  <div className="h-2 rounded-full bg-brand-600" style={{ width: `${Math.min(item.progress, 100)}%` }} />
                </div>
                <p className="text-sm text-slate-500">{item.goal || 'Keep learning consistently.'}</p>
                  <div className="mt-4 flex items-center justify-between">
                    <div className="flex items-center gap-2">
                      <button type="button" className="rounded-lg border px-2 py-1 text-sm" disabled={item.progress <= 0} onClick={() => adjustProgress(item, -10)} aria-label="Decrease progress">-10</button>
                      <button type="button" className="rounded-lg border px-2 py-1 text-sm" disabled={item.progress >= 100} onClick={() => adjustProgress(item, 10)} aria-label="Increase progress">+10</button>
                    </div>
                    <div className="flex gap-2">
                      <button type="button" className="rounded-lg bg-slate-100 p-2" aria-label={`Edit ${item.title}`} onClick={() => startEdit(item)}><Edit size={16} /></button>
                  <button className="rounded-lg bg-slate-100 p-2" onClick={() => handleDelete(item.id)}><Trash2 size={16} /></button>
                    </div>
                </div>
              </motion.div>
            ))}
          </div>
        )}
      </div>
    </section>
  );
}
