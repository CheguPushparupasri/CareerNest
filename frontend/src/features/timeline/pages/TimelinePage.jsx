import { useEffect, useMemo, useState } from 'react';
import { motion } from 'framer-motion';
import { Plus, Search, Filter, Trash2, Edit, X } from 'lucide-react';
import { timelineService } from '../services/timelineService.js';

export function TimelinePage() {
  const [items, setItems] = useState([]);
  const [query, setQuery] = useState('');
  const [entryType, setEntryType] = useState('');
  const [loading, setLoading] = useState(true);
  const [editingId, setEditingId] = useState(null);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');
  const [form, setForm] = useState({ entryType: 'PROJECT', title: '', description: '', organization: '', entryDate: '' });

  useEffect(() => { loadData(); }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      setError('');
      const data = await timelineService.getAll({ page: 0, size: 20 });
      setItems(data.content || []);
    } catch (error) {
      setError(error.response?.data?.message || 'Unable to load timeline entries.');
    } finally {
      setLoading(false);
    }
  };

  const filteredItems = useMemo(() => {
    return items.filter((item) => {
      const haystack = `${item.title} ${item.description} ${item.organization}`.toLowerCase();
      const matchesQuery = !query || haystack.includes(query.toLowerCase());
      const matchesType = !entryType || item.entryType === entryType;
      return matchesQuery && matchesType;
    });
  }, [items, query, entryType]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setMessage('');
    try {
      if (editingId) await timelineService.update(editingId, form);
      else await timelineService.create(form);
      setMessage(editingId ? 'Timeline entry updated.' : 'Timeline entry added.');
      setEditingId(null);
      setForm({ entryType: 'PROJECT', title: '', description: '', organization: '', entryDate: '' });
      await loadData();
    } catch (error) {
      setError(error.response?.data?.message || 'Unable to save timeline entry.');
    }
  };

  const startEdit = (item) => {
    setEditingId(item.id);
    setForm({ entryType: item.entryType, title: item.title, description: item.description || '',
      organization: item.organization || '', entryDate: item.entryDate ? new Date(item.entryDate).toISOString().slice(0, 10) : '' });
    setError('');
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this timeline entry?')) return;
    try {
      await timelineService.delete(id);
      setMessage('Timeline entry deleted.');
      await loadData();
    } catch (error) {
      setError(error.response?.data?.message || 'Unable to delete timeline entry.');
    }
  };

  return (
    <section className="space-y-6">
      <div className="rounded-xl border border-slate-200 bg-white p-4 shadow-soft dark:border-slate-800 dark:bg-slate-900">
        {error && <p role="alert" className="mb-3 text-sm text-red-600">{error}</p>}
        {message && <p role="status" className="mb-3 text-sm text-green-700">{message}</p>}
        <h2 className="mb-4 text-xl font-semibold">{editingId ? 'Edit timeline entry' : 'Career timeline'}</h2>
        <form onSubmit={handleSubmit} className="grid gap-3 md:grid-cols-2">
          <select className="rounded-lg border border-slate-300 px-3 py-2" value={form.entryType} onChange={(e) => setForm({ ...form, entryType: e.target.value })}>
            <option value="PROJECT">Project</option>
            <option value="INTERNSHIP">Internship</option>
            <option value="CERTIFICATION">Certification</option>
            <option value="ACHIEVEMENT">Achievement</option>
            <option value="LEARNING">Learning</option>
              <option value="ACADEMIC">Academic</option>
              <option value="CODING_PRACTICE">Coding Practice</option>
          </select>
          <input className="rounded-lg border border-slate-300 px-3 py-2" placeholder="Title" value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} required />
          <input className="rounded-lg border border-slate-300 px-3 py-2 md:col-span-2" placeholder="Organization" value={form.organization} onChange={(e) => setForm({ ...form, organization: e.target.value })} />
          <textarea className="rounded-lg border border-slate-300 px-3 py-2 md:col-span-2" rows="3" placeholder="Description" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
          <input type="date" className="rounded-lg border border-slate-300 px-3 py-2" value={form.entryDate} onChange={(e) => setForm({ ...form, entryDate: e.target.value })} />
          <div className="flex justify-end md:col-span-2">
            <button type="submit" className="flex items-center gap-2 rounded-lg bg-brand-600 px-4 py-2 text-white">
              {editingId ? <Edit size={16} /> : <Plus size={16} />} {editingId ? 'Save entry' : 'Add Entry'}
            </button>
          </div>
          {editingId && <button type="button" className="md:col-span-2 inline-flex items-center gap-2 justify-self-end text-sm text-slate-600" onClick={() => { setEditingId(null); setForm({ entryType: 'PROJECT', title: '', description: '', organization: '', entryDate: '' }); }}><X size={15} /> Cancel edit</button>}
        </form>
      </div>

      <div className="rounded-xl border border-slate-200 bg-white p-4 shadow-soft dark:border-slate-800 dark:bg-slate-900">
        <div className="mb-4 flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
          <div className="flex gap-2">
            <div className="flex items-center gap-2 rounded-lg border border-slate-300 px-3 py-2">
              <Search size={16} />
              <input className="bg-transparent outline-none" placeholder="Search" value={query} onChange={(e) => setQuery(e.target.value)} />
            </div>
            <div className="flex items-center gap-2 rounded-lg border border-slate-300 px-3 py-2">
              <Filter size={16} />
              <select className="bg-transparent outline-none" value={entryType} onChange={(e) => setEntryType(e.target.value)}>
                <option value="">All types</option>
                <option value="PROJECT">Project</option>
                <option value="INTERNSHIP">Internship</option>
                <option value="CERTIFICATION">Certification</option>
                <option value="ACHIEVEMENT">Achievement</option>
                <option value="LEARNING">Learning</option>
                <option value="ACADEMIC">Academic</option>
                <option value="CODING_PRACTICE">Coding Practice</option>
              </select>
            </div>
          </div>
        </div>

        {loading ? (
          <div className="space-y-4">{[1,2,3].map((i)=><div key={i} className="h-24 animate-pulse rounded-xl bg-slate-200" />)}</div>
        ) : filteredItems.length === 0 ? (
          <div className="rounded-xl border border-dashed border-slate-300 p-8 text-center text-slate-500">No timeline entries found.</div>
        ) : (
          <div className="space-y-4">
            {filteredItems.map((item) => (
              <motion.div key={item.id} initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} className="rounded-xl border border-slate-200 p-4 dark:border-slate-800">
                <div className="flex items-start justify-between gap-2">
                  <div>
                    <h3 className="font-semibold">{item.title}</h3>
                    <p className="text-sm text-slate-500">{item.organization || 'Career milestone'}</p>
                  </div>
                  <div className="flex items-center gap-2">
                    <span className="rounded-full bg-brand-50 px-2 py-1 text-xs text-brand-700">{item.entryType}</span>
                    <button className="rounded-lg bg-slate-100 p-2" aria-label={`Edit ${item.title}`} onClick={() => startEdit(item)}><Edit size={16} /></button>
                    <button className="rounded-lg bg-slate-100 p-2" onClick={() => handleDelete(item.id)}><Trash2 size={16} /></button>
                  </div>
                </div>
                <p className="mt-2 text-sm text-slate-600">{item.description}</p>
                <p className="mt-2 text-xs text-slate-500">{item.entryDate ? new Date(item.entryDate).toLocaleDateString() : 'No date'}</p>
              </motion.div>
            ))}
          </div>
        )}
      </div>
    </section>
  );
}
